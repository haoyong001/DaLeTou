package com.ball.ball.dao;

import com.ball.ball.entity.DaletouHistory;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @ author Mr. Hao
 * @ date 2022-11-24   16:42
 */
@Mapper
public interface DaletouHistoryDao {
    List<DaletouHistory> findAll();

    void batchSave(List<DaletouHistory> daletouHistory);
}
