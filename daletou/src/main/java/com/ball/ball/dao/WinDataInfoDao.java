package com.ball.ball.dao;

import com.ball.ball.entity.WinDataInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @ author Mr. Hao
 * @ date 2022-11-24   16:57
 */
@Mapper
public interface WinDataInfoDao {
    void saveOne(WinDataInfo winMoney);

    void insertBatch(List<WinDataInfo> winDataInfoList);

    List<WinDataInfo> getByHistoryData(List<String> list);
}
