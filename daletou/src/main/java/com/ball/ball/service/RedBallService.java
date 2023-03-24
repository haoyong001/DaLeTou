package com.ball.ball.service;

import com.ball.ball.entity.RedBallInfo;

import java.util.List;

/**
 * @ author Mr. Hao
 * @ date 2022-11-22   17:01
 */
public interface RedBallService {
    void insertBatch(List<RedBallInfo> redBallInfoList);

    String getOne(String redBall);

    List<RedBallInfo> queryByStartAndEndIdx(int startIdx, int endIdx);
}
