package com.ball.ball.service;

import com.ball.ball.entity.NoWinDataInfo;

import java.util.List;

/**
 * @ author Mr. Hao
 * @ date 2022-11-26   16:28
 */
public interface NoWinDataService {
    void insertBatch(List<NoWinDataInfo> noWinDataInfos);

    List<NoWinDataInfo> getByHistoryData(List<String> list);

    void batchDelete(List<Integer> idList);

    List<NoWinDataInfo> getAll(int startIndex,int endIndex);

    void updateById(NoWinDataInfo noWinDataInfoList);

    void batchUpdateById(List<NoWinDataInfo> noWinDataInfoListNew);
}
