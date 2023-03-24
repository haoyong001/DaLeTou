package com.ball.ball.service;

import com.ball.ball.entity.BlueBallInfo;

import java.util.List;

/**
 * @ author Mr. Hao
 * @ date 2022-11-22   20:24
 */
public interface BlueBallService {
    void insertBatch(List<BlueBallInfo> blueBallInfoList);

    List<BlueBallInfo> findAll();
}
