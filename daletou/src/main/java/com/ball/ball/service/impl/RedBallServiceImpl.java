package com.ball.ball.service.impl;


import com.ball.ball.dao.RedBallDao;
import com.ball.ball.entity.RedBallInfo;
import com.ball.ball.service.RedBallService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ author Mr. Hao
 * @ date 2022-11-22   17:26
 */
@Service
public class RedBallServiceImpl implements RedBallService {

    @Resource
    private RedBallDao redBallDao;

    @Override
    public void insertBatch(List<RedBallInfo> redBallInfoList) {
        int size = redBallInfoList.size();
        if(size > 100000){
            int k = size / 1000;
            for(int a =0;a <= k;a++){
                int p = (a + 1) * 1000;
                p = p>size?size:p;
                List<RedBallInfo> subList = redBallInfoList.subList(a * 1000, p);
                redBallDao.insertBatch(subList);
            }
        }else{
            redBallDao.insertBatch(redBallInfoList);
        }

    }

    @Override
    public String getOne(String redBall) {
        //RedBallInfo redBallInfo = redBallDao.selectById(1);
        RedBallInfo redBallInfo = redBallDao.selectByRedBall(redBall);
        return redBallInfo == null?"无数据":"主键id:" + redBallInfo.getId() + ",红球组合：" + redBallInfo.getRedBall();
    }

    @Override
    public List<RedBallInfo> queryByStartAndEndIdx(int startIdx, int endIdx) {
        return redBallDao.queryByStartAndEndIdx(startIdx,endIdx);
    }
}
