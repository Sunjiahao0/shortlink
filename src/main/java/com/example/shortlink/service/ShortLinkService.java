package com.example.shortlink.service;

import com.example.shortlink.entity.ShortLink;
import com.example.shortlink.mapper.ShortLinkMapper;
import com.example.shortlink.util.BloomFilterService;
import com.example.shortlink.util.ShortCodeGenerator;
import cn.hutool.core.lang.Snowflake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
public class ShortLinkService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private BloomFilterService bloomFilterService;

    @Autowired
    private ShortLinkMapper shortLinkMapper;

    @Autowired
    private ShortCodeGenerator codeGenerator;

    @Autowired
    private Snowflake snowflake;

    @Transactional
    public String createShortLink(String longUrl, String customCode) {
        String shortCode;

        // 如果提供了自定义短码
        if (customCode != null && !customCode.trim().isEmpty()) {
            // 校验格式：4-16位字母数字
            if (!customCode.matches("^[a-zA-Z0-9]{4,16}$")) {
                throw new IllegalArgumentException("自定义短码必须是4-16位字母数字组合");
            }
            // 检查是否已存在（先快速布隆过滤器）
            if (bloomFilterService.mightContain(customCode)) {
                // 布隆过滤器可能存在，需要精确查库
                ShortLink existing = shortLinkMapper.selectByShortCode(customCode);
                if (existing != null) {
                    throw new IllegalArgumentException("自定义短码已存在，请换一个");
                }
            }
            shortCode = customCode;
        } else {
            // 未提供则自动生成
            shortCode = codeGenerator.generate();
        }

        // 创建实体
        ShortLink shortLink = new ShortLink();
        shortLink.setId(snowflake.nextId());
        shortLink.setShortCode(shortCode);
        shortLink.setLongUrl(longUrl);
        shortLink.setCreatedAt(new Date());
        shortLink.setClickCount(0);

        // 存入数据库
        shortLinkMapper.insert(shortLink);
        // 写入 Redis 缓存
        redisTemplate.opsForValue().set("short:" + shortCode, longUrl, 7, TimeUnit.DAYS);
        // 添加到布隆过滤器
        bloomFilterService.put(shortCode);

        return "http://short.domain/" + shortCode;
    }

    public String getLongUrl(String shortCode) {
        // 1. 先查 Redis
        String longUrl = redisTemplate.opsForValue().get("short:" + shortCode);
        if (longUrl != null) {
            return longUrl;
        }

        // 2. Redis 中没有，查数据库
        ShortLink shortLink = shortLinkMapper.selectByShortCode(shortCode);
        if (shortLink == null) {
            return null;
        }

        longUrl = shortLink.getLongUrl();
        // 3. 回写 Redis（设置过期时间）
        redisTemplate.opsForValue().set("short:" + shortCode, longUrl, 7, TimeUnit.DAYS);

        return longUrl;
    }

    // 异步更新点击次数
    public void incrementClickCount(String shortCode) {
        CompletableFuture.runAsync(() -> {
            shortLinkMapper.incrementClickCount(shortCode);
        });
    }

    public boolean isValidUrl(String url) {
        return url != null && (url.startsWith("http://") || url.startsWith("https://"));
    }
}