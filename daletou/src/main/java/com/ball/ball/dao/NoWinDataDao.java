package com.ball.ball.dao;

import com.ball.ball.entity.NoWinDataInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @ author Mr. Hao
 * @ date 2022-11-26   16:29
 */
@Mapper
public interface NoWinDataDao {
    void insertBatch(List<NoWinDataInfo> subList);

    List<NoWinDataInfo> getByHistoryData(List<String> list);

    void batchDelete(List<Integer> idList);

    List<NoWinDataInfo> getAll(int startIndex,int endIndex);

    void updateById(NoWinDataInfo n);

    void batchUpdateById(List<NoWinDataInfo> noWinDataInfoList);
}
