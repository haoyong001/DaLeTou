package com.ball.ball.service;

import com.ball.ball.entity.SixAndThreeEntity;

import java.util.List;

/**
 * @ author Mr. Hao
 * @ date 2023-10-13   3:57
 */
public interface SixAndThreeService {
    void batchSave(List<SixAndThreeEntity> list);

    List<SixAndThreeEntity> findAll(int startIdx,int endIdx);
}
