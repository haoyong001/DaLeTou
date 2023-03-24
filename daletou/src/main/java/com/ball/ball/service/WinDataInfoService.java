package com.ball.ball.service;

import com.ball.ball.entity.WinDataInfo;

import java.util.List;

/**
 * @ author Mr. Hao
 * @ date 2022-11-24   16:27
 */
public interface WinDataInfoService {
    void saveOne(WinDataInfo winMoney);

    void insertBatch(List<WinDataInfo> winDataInfoList);
}
