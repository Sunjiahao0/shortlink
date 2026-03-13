package com.example.shortlink.controller;

import com.example.shortlink.service.ShortLinkService;
import com.example.shortlink.util.BloomFilterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class ShortLinkController {

    @Autowired
    private BloomFilterService bloomFilterService;

    @Autowired
    private ShortLinkService shortLinkService;

    @PostMapping("/shorten")
    public Result<String> shorten(@RequestBody ShortenRequest request) {
        if (!shortLinkService.isValidUrl(request.getUrl())) {
            return Result.error("URL格式不正确，必须以http://或https://开头");
        }
        try {
            String shortUrl = shortLinkService.createShortLink(request.getUrl(), request.getCustomCode());
            return Result.success(shortUrl);
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/{shortCode}")
    public void redirect(@PathVariable String shortCode, HttpServletResponse response) throws IOException {
        if (!bloomFilterService.mightContain(shortCode)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        String longUrl = shortLinkService.getLongUrl(shortCode);
        if (longUrl == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        // 异步更新点击次数，不影响重定向
        shortLinkService.incrementClickCount(shortCode);
        response.sendRedirect(longUrl);
    }
}