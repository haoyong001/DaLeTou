package com.ball.ball.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.ball.ball.blueball.BlueBallEnum;
import com.ball.ball.entity.*;
import com.ball.ball.redball.RedBallEnum;
import com.ball.ball.service.*;
import com.ball.ball.winrules.DaletouWinRules;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @ author Mr. Hao
 * @ date 2022-11-23   0:41
 */
@RestController
public class BallController {
    @Resource
    private RedBallService redBallService;

    @Resource
    private BlueBallService blueBallService;

    @Resource
    private DaletouHistoryService daletouHistoryService;

    @Resource
    private WinDataInfoService winDataInfoService;

    @Resource
    private NoWinDataService noWinDataService;


    @GetMapping("/getOne")
    public String getOne(@RequestParam String redBall){
        return redBallService.getOne(redBall);
    }

    @GetMapping("/createRedBall")
    public String createRedBall(){
        //获取6红球集合
        String mes ="";
        long start = System.currentTimeMillis();
        Set<String[]> sixRedBallForAll = RedBallEnum.getSixRedBallForAll();
        mes += "6个红球共有组合："+ sixRedBallForAll.size() + "种";
        System.out.println("6个红球共有组合："+ sixRedBallForAll.size() + "种");
        //JSON red = JSONUtil.parse(sixRedBallForAll);
        List<RedBallInfo> redBallInfoList = new ArrayList<>();
        sixRedBallForAll.stream().forEach(r ->{
                RedBallInfo redBallInfo = new RedBallInfo();
                redBallInfo.setCreateTime(DateTime.now().toString("yyyy-MM-dd HH:mm:ss"));
                String redBall = r[0]+ "," + r[1]+ "," + r[2]+ "," + r[3]+ "," + r[4]+ "," + r[5];
                redBallInfo.setRedBall(redBall);
                redBallInfoList.add(redBallInfo);
        });
        System.out.println("开始插入数据库------------------------------------------");
        //保存红球进数据库
        redBallService.insertBatch(redBallInfoList);
        long end = System.currentTimeMillis();
        System.out.println("总共计算耗时：" + (end-start)/1000 + "秒");
        mes += (",总共计算及入库耗时：" + (end-start)/1000 + "秒,插入数据库：" + redBallInfoList.size() +"条");
        return mes;
    }

    @GetMapping("/createBlueBall")
    public String createBlueBall(){
        long start = System.currentTimeMillis();
        String mes ="";
        List<BlueBallInfo> blueBallInfoList = new ArrayList<>();
        Set<String[]> threeBlueBallForAll = BlueBallEnum.getThreeBlueBallForAll();
        System.out.println("3个蓝球共有组合："+ threeBlueBallForAll.size() + "种");
        mes += "3个蓝球共有组合："+ threeBlueBallForAll.size() + "种";
        JSON blue = JSONUtil.parse(threeBlueBallForAll);
        //保存篮球进数据库
        threeBlueBallForAll.stream().forEach(b ->{
            String threeBlueBall = b[0]+ "," + b[1]+ ","+ b[2];
            BlueBallInfo blueBallInfo = new BlueBallInfo();
            blueBallInfo.setBlueBall(threeBlueBall);
            blueBallInfo.setCreateTime(DateTime.now().toString("yyyy-MM-dd HH:mm:ss"));
            blueBallInfoList.add(blueBallInfo);
        });
        blueBallService.insertBatch(blueBallInfoList);
        long end = System.currentTimeMillis();
        System.out.println("总共计算耗时：" + (end-start)/1000 + "秒");
        mes += (",总共计算及入库耗时：" + (end-start)/1000 + "秒");
        return mes;
    }

    @GetMapping("/forecastWinBall")
    public String forecastWinBall(@RequestParam int startIdx,@RequestParam int endIdx){
        String mes = "运算开始索引startIdx：" + startIdx + ",结束索引：" + endIdx;
        System.out.println("大数据运算开始---开始主键："+startIdx+"-------------结束主键：" + endIdx );
        long start = System.currentTimeMillis();
        //查询红球组合
        List<RedBallInfo> redBallInfoList = redBallService.queryByStartAndEndIdx(startIdx,endIdx);
        //查询全部蓝球组合
        List<BlueBallInfo> blueBallInfoList = blueBallService.findAll();
        //查询全部真实开奖记录
        List<DaletouHistory> daletouHistoryList = daletouHistoryService.findAll();
        AtomicInteger insertCount = new AtomicInteger(0);
        List<WinDataInfo> winDataInfoList = new ArrayList<>();
        List<NoWinDataInfo> allNoWinData = new ArrayList<>();
        redBallInfoList.stream().forEach(r ->{
            String redBall = r.getRedBall();
            blueBallInfoList.stream().forEach(b ->{
                String blueBall = b.getBlueBall();
                String sixAndThree = redBall + ":" + blueBall;
                Map<String, Object> map = DaletouWinRules.getWinMoney(sixAndThree, daletouHistoryList);
                WinDataInfo winMoney = (WinDataInfo) map.get("winDataInfo");
                if(winMoney != null){
                    int moneyCount = winMoney.getMoneyCount();
                    if(moneyCount != 0){
                        String redAndBlue = winMoney.getRedAndBlue();
                        int theFirstPrizeCount = winMoney.getTheFirstPrizeCount();
                        int secondAwardCount = winMoney.getSecondAwardCount();
                        if(theFirstPrizeCount >0 && secondAwardCount >0){
                            System.out.println("大乐透投注号码：" + redAndBlue +",一等奖中奖次数："+ theFirstPrizeCount +
                                    ",二等奖中奖次数：" + secondAwardCount + ",除一二等奖外，总奖金金额：" + moneyCount);
                        }
                        winDataInfoList.add(winMoney);
                        insertCount.incrementAndGet();
                    }else{
                        //将未中奖的6+3组合保存进数据库
                        NoWinDataInfo noWinDataInfo = new NoWinDataInfo();
                        noWinDataInfo.setNoWinNum(sixAndThree);
                        allNoWinData.add(noWinDataInfo);
                    }
                }
            });
        });
        mes += (",共产生符合要求的数据：" + insertCount.get() + "条，");
        long end2 = System.currentTimeMillis();
        System.out.println("运算结束，耗时："+ (end2-start)/1000/60 +"分，共产生符合要求的数据：" + insertCount.get() + "条，开始入库操作！");
        winDataInfoService.insertBatch(winDataInfoList);
        //保存未中奖数据
        if(allNoWinData != null && allNoWinData.size()>0){
            noWinDataService.insertBatch(allNoWinData);
        }
        long end = System.currentTimeMillis();
        System.out.println("大数据运算结束，总耗时：" + (end - start)/1000/60 + "分");
        mes += ("大数据运算结束，总耗时：" + (end - start)/1000/60 + "分");
        return mes;
    }

}
