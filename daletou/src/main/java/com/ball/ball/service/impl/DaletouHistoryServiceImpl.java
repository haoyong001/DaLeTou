package com.ball.ball.service.impl;

import com.ball.ball.dao.DaletouHistoryDao;
import com.ball.ball.entity.DaletouHistory;
import com.ball.ball.service.DaletouHistoryService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ author Mr. Hao
 * @ date 2022-11-24   16:40
 */
@Service
public class DaletouHistoryServiceImpl implements DaletouHistoryService {

    @Resource
    private DaletouHistoryDao daletouHistoryDao;

    @Override
    public List<DaletouHistory> findAll() {
        return daletouHistoryDao.findAll();
    }

    @Override
    public void batchSave(List<DaletouHistory> daletouHistory) {
        daletouHistoryDao.batchSave(daletouHistory);
    }
}
