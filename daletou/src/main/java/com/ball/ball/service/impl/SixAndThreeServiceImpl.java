package com.ball.ball.service.impl;

import com.ball.ball.dao.SixAndThreeDao;
import com.ball.ball.entity.SixAndThreeEntity;
import com.ball.ball.service.SixAndThreeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ author Mr. Hao
 * @ date 2023-10-13   3:59
 */
@Service
public class SixAndThreeServiceImpl implements SixAndThreeService {

    @Resource
    private SixAndThreeDao sixAndThreeDao;

    @Override
    public void batchSave(List<SixAndThreeEntity> list) {
        sixAndThreeDao.batchSave(list);
    }

    @Override
    public List<SixAndThreeEntity> findAll(int startIdx,int endIdx) {
        return sixAndThreeDao.findAll(startIdx,endIdx);
    }
}
