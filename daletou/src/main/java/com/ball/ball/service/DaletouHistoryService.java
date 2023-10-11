package com.ball.ball.service;

import com.ball.ball.entity.DaletouHistory;

import java.util.List;

/**
 * @ author Mr. Hao
 * @ date 2022-11-24   16:05
 */
public interface DaletouHistoryService {
    List<DaletouHistory> findAll();

    void batchSave(List<DaletouHistory> daletouHistory);
}
