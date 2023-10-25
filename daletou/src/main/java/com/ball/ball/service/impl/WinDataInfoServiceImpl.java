package com.ball.ball.service.impl;

import com.ball.ball.dao.WinDataInfoDao;
import com.ball.ball.entity.WinDataInfo;
import com.ball.ball.service.WinDataInfoService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ author Mr. Hao
 * @ date 2022-11-24   16:47
 */
@Service
public class WinDataInfoServiceImpl implements WinDataInfoService {

    @Resource
    private WinDataInfoDao winDataInfoDao;

    @Override
    public void saveOne(WinDataInfo winMoney) {
        winDataInfoDao.saveOne(winMoney);
    }

    @Override
    public void insertBatch(List<WinDataInfo> winDataInfoList) {
        long start = System.currentTimeMillis();
        int size = winDataInfoList.size();
        if(size > 100000){
            int k = size / 10000;
            System.out.println("已中奖插入数据库开始，大小size:"+size +",大概循环次数k:"+k);
            for(int a =0;a <= k;a++){
                int p = (a + 1) * 10000;
                int q = a * 10000;
                p = p>size?size:p;
                if(q < size){
                    List<WinDataInfo> subList = winDataInfoList.subList(q, p);
                    winDataInfoDao.insertBatch(subList);
                }

            }
        }else{
            System.out.println("已中奖插入数据库开始，大小size:"+size);
            if(size >0){
                winDataInfoDao.insertBatch(winDataInfoList);
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("已中奖插入数据库结束，耗时:"+ (end - start)/1000/60 + "分钟");
    }

    @Override
    public List<WinDataInfo> getByHistoryData(List<String> list) {
        return winDataInfoDao.getByHistoryData(list);
    }
}
