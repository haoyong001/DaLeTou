package com.ball.ball.service.impl;

import com.ball.ball.dao.BlueBallDao;
import com.ball.ball.entity.BlueBallInfo;
import com.ball.ball.service.BlueBallService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ author Mr. Hao
 * @ date 2022-11-22   20:24
 */
@Service
public class BlueBallServiceImpl implements BlueBallService {


    @Resource
    private BlueBallDao blueBallDao;

    @Override
    public void insertBatch(List<BlueBallInfo> blueBallInfoList) {
        blueBallInfoList.stream().forEach(b ->{
            blueBallDao.insertOne(b);
        });

    }

    @Override
    public List<BlueBallInfo> findAll() {
        return blueBallDao.findAll();
    }
}
