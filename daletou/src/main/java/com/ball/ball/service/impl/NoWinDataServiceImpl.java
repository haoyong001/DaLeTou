package com.ball.ball.service.impl;

import com.ball.ball.dao.NoWinDataDao;
import com.ball.ball.entity.NoWinDataInfo;
import com.ball.ball.service.NoWinDataService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ author Mr. Hao
 * @ date 2022-11-26   16:28
 */
@Service
public class NoWinDataServiceImpl implements NoWinDataService {

    @Resource
    private NoWinDataDao noWinDataDao;

    @Override
    public void insertBatch(List<NoWinDataInfo> noWinDataInfos) {
        long start = System.currentTimeMillis();
        int size = noWinDataInfos.size();
        if(size > 50000){
            int k = size / 10000;
            System.out.println("未中奖数据插入数据库开始，大小size:"+size +",大概循环次数k:"+k);
            for(int a =0;a <= k;a++){
                int p = (a + 1) * 10000;
                int q = a * 10000;
                p = p>size?size:p;
                if(q < size){
                    List<NoWinDataInfo> subList = noWinDataInfos.subList(q, p);
                    noWinDataDao.insertBatch(subList);
                }

            }
        }else{
            System.out.println("未中奖数据插入数据库开始，大小size:"+size);
            noWinDataDao.insertBatch(noWinDataInfos);
        }
        long end = System.currentTimeMillis();
        System.out.println("未中奖数据插入数据库结束，耗时:"+ (end - start)/1000/60 + "分钟");
    }
}
