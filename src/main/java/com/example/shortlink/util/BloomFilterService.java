package com.example.shortlink.util;

import com.example.shortlink.mapper.ShortLinkMapper;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.Charset;
import java.util.List;

@Service
public class BloomFilterService implements InitializingBean {

    @Autowired
    private ShortLinkMapper shortLinkMapper;

    private BloomFilter<String> bloomFilter;

    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            System.out.println(">>> 开始初始化布隆过滤器...");
            bloomFilter = BloomFilter.create(
                    Funnels.stringFunnel(Charset.defaultCharset()),
                    100000,
                    0.01
            );
            List<String> allShortCodes = shortLinkMapper.selectAllShortCodes();
            System.out.println(">>> 从数据库加载了 " + allShortCodes.size() + " 个短码");
            for (String code : allShortCodes) {
                bloomFilter.put(code);
            }
            System.out.println(">>> 布隆过滤器初始化完成，已加载 " + allShortCodes.size() + " 个短码");
        } catch (Exception e) {
            System.err.println(">>> 布隆过滤器初始化异常：");
            e.printStackTrace();
        }
    }

    public boolean mightContain(String shortCode) {
        if (bloomFilter == null) {
            System.err.println(">>> 警告：布隆过滤器未初始化，mightContain 返回 false");
            return false;
        }
        return bloomFilter.mightContain(shortCode);
    }

    public void put(String shortCode) {
        if (bloomFilter == null) {
            System.err.println(">>> 警告：布隆过滤器未初始化，无法添加短码：" + shortCode);
            return;
        }
        bloomFilter.put(shortCode);
    }
}