package com.example.shortlink.mapper;

import com.example.shortlink.entity.ShortLink;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ShortLinkMapper {
    int insert(ShortLink shortLink);
    ShortLink selectByShortCode(@Param("shortCode") String shortCode);
    int incrementClickCount(@Param("shortCode") String shortCode);
    List<String> selectAllShortCodes();
}
