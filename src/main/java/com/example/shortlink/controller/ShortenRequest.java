package com.example.shortlink.controller;

import lombok.Data;

@Data
public class ShortenRequest {
    private String url;
    private String customCode;
}