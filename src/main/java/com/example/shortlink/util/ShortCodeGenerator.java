package com.example.shortlink.util;

import cn.hutool.core.lang.Snowflake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ShortCodeGenerator {

    @Autowired
    private Snowflake snowflake;

    private static final String BASE62 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public String generate() {
        long id = snowflake.nextId();
        String code = encodeBase62(id);
        System.out.println("Generated short code: " + code + ", length: " + code.length()); // 添加这行
        return code;
    }
    private String encodeBase62(long num){
        StringBuilder sb = new StringBuilder();
        while (num > 0){
            int remainder = (int) (num % 62);
            sb.append(BASE62.charAt(remainder));
            num /= 62;
        }
        String code = sb.reverse().toString();
        return String.format("%-6s",code).replace(' ','0');
    }
}
