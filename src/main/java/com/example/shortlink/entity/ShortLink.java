package com.example.shortlink.entity;

import lombok.Data;
import java.util.Date;

@Data
public class ShortLink {
    private Long id;               // 雪花算法ID
    private String shortCode;       // 短码
    private String longUrl;         // 原始URL
    private Date createdAt;         // 创建时间
    private Date expiredAt;         // 过期时间（可留空）
    private Integer clickCount;     // 点击次数
}
