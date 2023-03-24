package com.ball.ball.winrules;

import cn.hutool.core.date.DateTime;
import com.alibaba.druid.util.StringUtils;
import com.ball.ball.entity.DaletouHistory;
import com.ball.ball.entity.NoWinDataInfo;
import com.ball.ball.entity.WinDataInfo;
import com.ball.ball.service.NoWinDataService;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *  6+3复试投注计算中奖规则
 * @ author Mr. Hao
 * @ date 2022-11-23   17:36
 */
public class DaletouWinRules {
    /**
     * 三等奖：投注号码与当期开奖号码中的五个前区号码相同，即中奖； 三等奖：单注奖金固定为10000元。
     * 四等奖：投注号码与当期开奖号码中的任意四个前区号码及两个后区号码相同，即中奖；四等奖：单注奖金固定为3000元。
     * 五等奖：投注号码与当期开奖号码中的任意四个前区号码及任意一个后区号码相同，即中奖；五等奖：单注奖金固定为300元。
     *六等奖：投注号码与当期开奖号码中的任意三个前区号码及两个后区号码相同，即中奖； 六等奖：单注奖金固定为200元。
     *七等奖：投注号码与当期开奖号码中的任意四个前区号码相同，即中奖；七等奖：单注奖金固定为100元。
     *八等奖：投注号码与当期开奖号码中的任意三个前区号码及任意一个后区号码相同，或者任意两个前区号码及两个后区号码相同，即中奖；
     *八等奖：单注奖金固定为15元。
     *
     *九等奖：投注号码与当期开奖号码中的任意三个前区号码相同，或者任意一个前区号码及两个后区号码相同，
     * 或者任意两个前区号码及任意一个后区号码相同，或者两个后区号码相同，即中奖。九等奖：单注奖金固定为5元。
     */



    /**
     * sixAndThree  2,8,10,11,24,30:1,7,8
     * @param sixAndThree
     * @return
     */
    public static Map<String, Object> getWinMoney(String sixAndThree, List<DaletouHistory> daletouHistoryList){
        Map<String, Object> winMoney = isWinMoney(sixAndThree, daletouHistoryList);
        WinDataInfo winDataInfo =null;
        Integer money = Integer.valueOf(String.valueOf(winMoney.get("money")));
        //先判断是否满足最低奖，如果不满足，直接抛弃
        if(money != 0){
            winDataInfo = new WinDataInfo();
            //一等奖判断，只记录次数，不算金额
            int theFirstPrizeCount = Integer.valueOf(String.valueOf(winMoney.get("theFirstPrizeCount")));
            //二等奖判断，只记录次数，不算金额
            int secondAwardCount = Integer.valueOf(String.valueOf(winMoney.get("secondAwardCount")));
            //将一等奖,二等奖中奖次数与彩票组合信息保存进数据库
            winDataInfo.setRedAndBlue(sixAndThree);
            String his = StringUtils.isEmpty(String.valueOf(winMoney.get("his")))?"没有中一等奖":String.valueOf(winMoney.get("his"));
            winDataInfo.setHistoryData(his);
            winDataInfo.setTheFirstPrizeCount(theFirstPrizeCount);
            winDataInfo.setSecondAwardCount(secondAwardCount);
            winDataInfo.setMoneyCount(money);
            winDataInfo.setDateTime(DateTime.now().toString("yyyy-MM-dd HH:mm:ss"));
        }
        Map<String, Object> data = new HashMap<>();
        data.put("winDataInfo",winDataInfo);
        return data;
    }

    private static Map<String,Object> isWinMoney(String sixAndThree, List<DaletouHistory> daletouHistoryList) {
        String[] split = sixAndThree.split(":");
        String redBall = split[0];
        String blueBall = split[1];
        String[] redBallArray = redBall.split(",");
        String[] blueBallArray = blueBall.split(",");
        AtomicInteger winCount = new AtomicInteger(0);
        AtomicInteger theFirstPrizeCount = new AtomicInteger(0);
        AtomicInteger secondAwardCount = new AtomicInteger(0);
        Map<String,Object> map = new HashMap<>();
        StringBuilder builder = new StringBuilder();

        daletouHistoryList.stream().forEach(d ->{
            //获取红球相同个数  2,8,10,11,24,30：01，09
            int sameRedBallCount = getSameRedBallCount(redBallArray,d);
            int sameBlueBallCount = getSameBlueBallCount(blueBallArray,d);
            //前区6+3规则
            if((sameRedBallCount == 0 && sameBlueBallCount < 2) || (sameRedBallCount == 1 && sameBlueBallCount < 2) || (sameRedBallCount == 2 && sameBlueBallCount ==0)){
                //未中奖
                winCount.addAndGet(0);
            }else{
                //判断是否中一等奖，记录次数
                AtomicInteger winTimes = new AtomicInteger(0);
                if(sameRedBallCount ==5 && sameBlueBallCount ==2){
                    theFirstPrizeCount.getAndIncrement();
                    //二等奖次数  投注号码与当期开奖号码中的五个前区号码及任意一个后区号码相同，即中奖；
                    secondAwardCount.addAndGet(2);
                    String hisData = d.getRedOne() + "," + d.getRedTwo() + "," +d.getRedThree()+ "," +d.getRedFour()+ "," +d.getRedFive() +";" +
                            d.getBlueOne() + "," + d.getBlueTwo() + "||";
                    builder.append(hisData);
                }
                //判断是否中二等奖，记录次数
                if(sameRedBallCount ==5 && sameBlueBallCount ==1){
                    secondAwardCount.addAndGet(1);
                }
                //根据中球个数，计算中奖金额
                int money = addWinMoney(sameRedBallCount,sameBlueBallCount);
                winCount.addAndGet(money);
            }

        });
        map.put("theFirstPrizeCount",theFirstPrizeCount.get());
        map.put("secondAwardCount",secondAwardCount.get());
        map.put("his",builder.toString());
        map.put("money",winCount.get());
        return map;

    }


    private static int addWinMoney(int sameRedBallCount, int sameBlueBallCount) {
        AtomicInteger winCountOneBall = new AtomicInteger(0);
        //复试6+3
        if(sameBlueBallCount == 0){
            //没有相同的蓝球
            if(sameRedBallCount == 3){
                //九等奖，单注奖金固定为5元   获奖注数3    3+0
                winCountOneBall.addAndGet(15);
            }else if(sameRedBallCount == 4){
                //七等奖，获奖注数3    投注号码与当期开奖号码中的任意四个前区号码相同，即中奖；七等奖：单注奖金固定为100元。  4+0
                winCountOneBall.addAndGet(300);
                //九等奖注数 12
                winCountOneBall.addAndGet(60);
            }else if(sameRedBallCount == 5){
                //三等奖，获奖注数1   投注号码与当期开奖号码中的五个前区号码相同，即中奖；奖金固定为10000元。  七等奖注数15       5+0
                winCountOneBall.addAndGet(10000);
                winCountOneBall.addAndGet(1500);
            }
        }else if(sameBlueBallCount == 1){
            //有一个相同的蓝球
            if(sameRedBallCount == 2){
                // 2+1 任意两个前区号码及任意一个后区号码相同 九等奖注数8
                winCountOneBall.addAndGet(40);
            }else if(sameRedBallCount == 3){
                //3+1 九等奖注数15   八等奖：投注号码与当期开奖号码中的任意三个前区号码及任意一个后区号码相同 15元  注数6
                winCountOneBall.addAndGet(75);
                winCountOneBall.addAndGet(90);
            }else if(sameRedBallCount == 4){
                //4+1  五等奖：注数：4  单注奖金固定为300元。  七等奖注数6
                winCountOneBall.addAndGet(1200);
                winCountOneBall.addAndGet(600);
            }else if(sameRedBallCount == 5){
                //5+1  三等奖：投注号码与当期开奖号码中的五个前区号码相同，注数3； 三等奖：单注奖金固定为10000元
                winCountOneBall.addAndGet(30000);
                //五等奖注数10： 投注号码与当期开奖号码中的任意四个前区号码及任意一个后区号码相同，单注奖金固定为300元
                winCountOneBall.addAndGet(3000);
                //七等奖注数15   投注号码与当期开奖号码中的任意四个前区号码相同，即中奖；七等奖：单注奖金固定为100元
                winCountOneBall.addAndGet(1500);
            }
        }else if(sameBlueBallCount == 2){
            //两个相同蓝球
            if(sameRedBallCount == 0){
                //0+2 九等奖注数6
                winCountOneBall.addAndGet(30);
            }else if(sameRedBallCount == 1){
                //1+2   任意一个前区号码及两个后区号码相同5   或者两个后区号码相同6  九等奖注数11
                winCountOneBall.addAndGet(55);
            }else if(sameRedBallCount == 2){
                //2+2  八等奖注数4 任意两个前区号码及两个后区号码相同15元
                winCountOneBall.addAndGet(60);
                //任意一个前区号码及两个后区号码相同2   任意两个前区号码及任意一个后区号码相同8  九等奖注数10
                winCountOneBall.addAndGet(50);
            }else if(sameRedBallCount == 3){
                //3+2  六等奖注数3：投注号码与当期开奖号码中的任意三个前区号码及两个后区号码相同，即中奖； 六等奖：单注奖金固定为200元
                winCountOneBall.addAndGet(600);
                //八等奖注数9：投注号码与当期开奖号码中的任意三个前区号码及任意一个后区号码相同6，或者任意两个前区号码及两个后区号码相同3 15
                winCountOneBall.addAndGet(135);
                //九等奖注数8：投注号码与当期开奖号码中的任意三个前区号码相同2，或者任意两个前区号码及任意一个后区号码相同6 单注奖金固定为5元
                winCountOneBall.addAndGet(40);
            }else if(sameRedBallCount == 4){
                //4+2  投注号码与当期开奖号码中的任意四个前区号码及两个后区号码相同，即中奖；四等奖注数2：单注奖金固定为3000元。
                winCountOneBall.addAndGet(6000);
                //五等奖注数4：投注号码与当期开奖号码中的任意四个前区号码及任意一个后区号码相同，即中奖；五等奖：单注奖金固定为300元。
                winCountOneBall.addAndGet(1200);
                //六等奖注数4：投注号码与当期开奖号码中的任意三个前区号码及两个后区号码相同，即中奖； 六等奖：单注奖金固定为200元。
                winCountOneBall.addAndGet(800);
                //八等奖注数8：投注号码与当期开奖号码中的任意三个前区号码及任意一个后区号码相同 15元
                winCountOneBall.addAndGet(120);
            }else if(sameRedBallCount == 5){
                //5+2 四等奖注数5：投注号码与当期开奖号码中的任意四个前区号码及两个后区号码相同，即中奖；四等奖：单注奖金固定为3000元。
                winCountOneBall.addAndGet(15000);
                //五等奖注数10：投注号码与当期开奖号码中的任意四个前区号码及任意一个后区号码相同，即中奖；五等奖：单注奖金固定为300元。
                winCountOneBall.addAndGet(3000);
            }
        }

        return winCountOneBall.get();
    }

    private static int getSameBlueBallCount(String[] blueBallArray, DaletouHistory d) {
        //篮球相同个数 最大值2
        AtomicInteger sameBlueBall = new AtomicInteger();
        ArrayList<String> blueList = new ArrayList<>();
        blueList.add(d.getBlueOne());
        blueList.add(d.getBlueTwo());
        Arrays.stream(blueBallArray).forEach(ball ->{
            boolean contains = blueList.contains(ball);
            if(contains){
                sameBlueBall.getAndIncrement();
            }
        });
        return sameBlueBall.get();
    }

    private static int getSameRedBallCount(String[] redBallArray, DaletouHistory d) {
        String redOne = d.getRedOne();
        String redTwo = d.getRedTwo();
        String redThree = d.getRedThree();
        String redFour = d.getRedFour();
        String redFive = d.getRedFive();
        ArrayList<String> redList = new ArrayList<>();
        redList.add(redOne);
        redList.add(redTwo);
        redList.add(redThree);
        redList.add(redFour);
        redList.add(redFive);
        //红球相同个数 最大值5
        AtomicInteger sameRedBall = new AtomicInteger(0);
        Arrays.stream(redBallArray).forEach(ball ->{
            boolean contains = redList.contains(ball);
            if(contains){
                //System.out.println("历史中奖号码：" + redList + ",包含数字：" + ball);
                sameRedBall.getAndIncrement();
            }
        });
        return sameRedBall.get();
    }

}
