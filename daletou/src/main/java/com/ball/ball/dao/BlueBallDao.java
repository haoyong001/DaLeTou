package com.ball.ball.dao;

import com.ball.ball.entity.BlueBallInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @ author Mr. Hao
 * @ date 2022-11-22   20:26
 */
@Mapper
public interface BlueBallDao {
    void insertOne(BlueBallInfo blueBallInfo);

    List<BlueBallInfo> findAll();
}
