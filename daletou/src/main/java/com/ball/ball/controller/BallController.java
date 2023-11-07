package com.ball.ball.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.druid.util.StringUtils;
import com.ball.ball.blueball.BlueBallEnum;
import com.ball.ball.dto.HistoryDaLeTouDto;
import com.ball.ball.entity.*;
import com.ball.ball.redball.RedBallEnum;
import com.ball.ball.service.*;
import com.ball.ball.winrules.DaletouWinRules;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

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

    @Resource
    private SixAndThreeService sixAndThreeService;


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
        sixRedBallForAll.forEach(r ->{
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
        threeBlueBallForAll.forEach(b ->{
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
    public String forecastWinBall(){
        //@RequestParam int startIdx,@RequestParam int endIdx
        String mes = "";

        long start = System.currentTimeMillis();
//        //查询红球组合
//        List<RedBallInfo> redBallInfoList = redBallService.queryByStartAndEndIdx(startIdx,endIdx);
//        //查询全部蓝球组合
//        List<BlueBallInfo> blueBallInfoList = blueBallService.findAll();
        //查询全部6+3组合
        Boolean flag = true;
        int page = 1;
        int pageSize = 100000;
        //查询全部真实开奖记录
        List<DaletouHistory> daletouHistoryList = daletouHistoryService.findAll();
        while (flag){
            mes = "";
            int startIdx = (page -1) * pageSize;
            int endIdx = page * pageSize;
            List<SixAndThreeEntity> sixAndThreeEntityList = sixAndThreeService.findAll(startIdx,endIdx);
            int size = sixAndThreeEntityList.size();
            System.out.println("查询6+3组合获取数量：" + size);
            if(sixAndThreeEntityList == null || size == 0){
                flag = false;
                break;
            }
            page ++;

            AtomicInteger insertCount = new AtomicInteger(0);
            List<WinDataInfo> winDataInfoList = new ArrayList<>();
            List<NoWinDataInfo> allNoWinData = new ArrayList<>();
            sixAndThreeEntityList.forEach(r ->{
                String sixAndThree = r.getBall();
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
                        if(theFirstPrizeCount >0){
                            //一等奖
                            winDataInfoList.add(winMoney);
                            insertCount.incrementAndGet();
                        }else if(secondAwardCount >0){
                            //二等奖
                            winDataInfoList.add(winMoney);
                            insertCount.incrementAndGet();
                        }else if(moneyCount > 10000){
                            //总奖金金额 大于一万的留下保存
                            winDataInfoList.add(winMoney);
                            insertCount.incrementAndGet();
                        }else{
                            NoWinDataInfo noWinDataInfo = new NoWinDataInfo();
                            noWinDataInfo.setNoWinNum(sixAndThree);
                            getOddAndEvenNum(noWinDataInfo,sixAndThree);
                            noWinDataInfo.setMoneyCount(moneyCount);
                            allNoWinData.add(noWinDataInfo);
//                            System.out.println("符合noWinDataInfo数据：组合：" + sixAndThree + "金额：" + moneyCount);
                        }
                    }else{
                        //将未中奖的6+3组合保存进数据库
                        NoWinDataInfo noWinDataInfo = new NoWinDataInfo();
                        noWinDataInfo.setNoWinNum(sixAndThree);
                        getOddAndEvenNum(noWinDataInfo,sixAndThree);
                        noWinDataInfo.setMoneyCount(0);
                        allNoWinData.add(noWinDataInfo);
                    }
                }
                if(allNoWinData.size() >= 100000){
                    noWinDataService.insertBatch(allNoWinData);
                    allNoWinData.clear();
                    System.out.println("noWinDataInfo插入数据库");
                }
                if(winDataInfoList.size() >= 100000){
                    winDataInfoService.insertBatch(winDataInfoList);
                    winDataInfoList.clear();
                }

            });
            mes += (",winDataInfo共产生符合要求的数据：" + insertCount.get() + "条，");
            long end2 = System.currentTimeMillis();
            System.out.println("运算结束，耗时："+ (end2-start)/1000/60 +"分，winDataInfo共产生符合要求的数据：" + insertCount.get() + "条，开始入库操作！");
            if(winDataInfoList.size()>0){
                winDataInfoService.insertBatch(winDataInfoList);
            }

            //保存未中奖数据
            if(allNoWinData.size()>0){
                noWinDataService.insertBatch(allNoWinData);
            }
            long end = System.currentTimeMillis();
            System.out.println("大数据运算结束，总耗时：" + (end - start)/1000/60 + "分");
            mes += ("大数据运算结束，总耗时：" + (end - start)/1000/60 + "分");
        }

        System.out.println(mes);
        return "运算结束";
    }

    private void getOddAndEvenNum(NoWinDataInfo noWinDataInfo, String sixAndThree) {
        String[] split = sixAndThree.split(":");
        String redBallArr = split[0];
        String[] redBall = redBallArr.split(",");
        AtomicInteger odd = new AtomicInteger(0);
        AtomicInteger even = new AtomicInteger(0);
        Arrays.stream(redBall).forEach(r->{
            Integer num = Integer.valueOf(r);
            if(num % 2 == 0){
                //偶数
                even.getAndIncrement();
            }else{
                //奇数
                odd.getAndIncrement();
            }
        });
        noWinDataInfo.setOdd(odd.get());
        noWinDataInfo.setEven(even.get());
    }

    /**
     *获取历史开奖记录  并写入数据库
     * https://webapi.sporttery.cn/gateway/lottery/getHistoryPageListV1.qry?gameNo=85&provinceId=0&pageSize=3000&isVerify=1&pageNo=1
     */
    @GetMapping("/getHistoryPageList")
    public String getHistoryPageList(@RequestParam int pageSize){
        //pageSize  目前已开奖开奖期数
        List<DaletouHistory> daletouHistoryServiceAll = daletouHistoryService.findAll();
        int size = daletouHistoryServiceAll.size();
        if(pageSize > size){
            pageSize = pageSize - size;
        }else{
            return "传参有误，数据库已有数据" + size + "条，请检查参数，或者清理数据库数据！";
        }
        int pageNo = 1;
        String url = "https://webapi.sporttery.cn/gateway/lottery/getHistoryPageListV1.qry?gameNo=85&provinceId=0&isVerify=1&";
        String param = "pageSize="+ pageSize +"&pageNo=" + pageNo;
        url += param;
        String jsonStr = HttpUtil.get(url);
        JSONObject jsonObject = new JSONObject(jsonStr);
        Object value = jsonObject.get("value");
        JSONObject jsonObjectValue = new JSONObject(value);
        HistoryDaLeTouDto historyDaLeTouDtoList = JSONUtil.toBean(jsonObjectValue,HistoryDaLeTouDto.class);
        int num = 0;
        if(historyDaLeTouDtoList != null && historyDaLeTouDtoList.getList() != null && historyDaLeTouDtoList.getList().size()>0){
            num = historyDaLeTouDtoList.getList().size();
            List<DaletouHistory> daletouHistory = toDaletouHistory(historyDaLeTouDtoList.getList());
            daletouHistoryService.batchSave(daletouHistory);
        }
        return "已获取开奖数据："+ num +"期";
    }

    private List<DaletouHistory> toDaletouHistory(List<HistoryDaLeTouEntity> historyDaLeTouDtoList) {
        List<DaletouHistory> daletouHistoryList = new ArrayList<>();
        historyDaLeTouDtoList.forEach(h ->{
            DaletouHistory daletouHistory1 = new DaletouHistory();
            daletouHistory1.setIssueNo(h.getLotteryDrawNum());
            daletouHistory1.setDateTime(h.getLotteryDrawTime());
            String lotteryDrawResult = h.getLotteryDrawResult();
            String[] split = lotteryDrawResult.split(" ");
            daletouHistory1.setRedOne(split[0]);
            daletouHistory1.setRedTwo(split[1]);
            daletouHistory1.setRedThree(split[2]);
            daletouHistory1.setRedFour(split[3]);
            daletouHistory1.setRedFive(split[4]);
            daletouHistory1.setBlueOne(split[5]);
            daletouHistory1.setBlueTwo(split[6]);
            daletouHistoryList.add(daletouHistory1);
        });
        return daletouHistoryList;
    }

    @GetMapping("/getWinSixAndThree")
    public String getWinSixAndThree(@RequestParam String daletou){
//        //查询所有红球组合
//        List<RedBallInfo> redBallInfoList = redBallService.queryByStartAndEndIdx(1,1623160);
//        //查询全部蓝球组合
//        List<BlueBallInfo> blueBallInfoList = blueBallService.findAll();
        //查询全部6+3组合
        long t1 = System.currentTimeMillis();
        AtomicInteger page = new AtomicInteger(1);
        int pageSize = 100000;
        AtomicReference<Boolean> flag = new AtomicReference<>(true);
        //查询全部真实开奖记录
        DaletouHistory daletouHistory1 = new DaletouHistory();
        String[] split = daletou.split(",");
        daletouHistory1.setRedOne(split[0]);
        daletouHistory1.setRedTwo(split[1]);
        daletouHistory1.setRedThree(split[2]);
        daletouHistory1.setRedFour(split[3]);
        daletouHistory1.setRedFive(split[4]);
        daletouHistory1.setBlueOne(split[5]);
        daletouHistory1.setBlueTwo(split[6]);
        List<DaletouHistory> daletouHistoryList = new ArrayList<>();
        daletouHistoryList.add(daletouHistory1);
        while (flag.get()){
            CountDownLatch countDownLatch = new CountDownLatch(6);
            for (int i = 1; i <= 6; i++) {
                new Thread(() -> {
                    System.out.println(Thread.currentThread().getName() + "\t" + "开始");
                    int startIdx = (page.get() -1) * pageSize;
                    int endIdx = page.get() * pageSize;
                    List<SixAndThreeEntity> sixAndThreeEntityList = sixAndThreeService.findAll(startIdx,endIdx);
                    int size = sixAndThreeEntityList.size();
                    if(size == 0){
                        flag.set(false);
                    }

                    List<WinDataInfo> winDataInfoList = new ArrayList<>();
                    List<String> list = new ArrayList<>();
                    AtomicReference<String> kjhm = new AtomicReference<>("");
                    sixAndThreeEntityList.forEach(r ->{
                        String sixAndThree = r.getBall();
                        Map<String, Object> map = DaletouWinRules.getWinMoney(sixAndThree, daletouHistoryList);
                        WinDataInfo winMoney = (WinDataInfo) map.get("winDataInfo");
                        if(winMoney != null){
                            int moneyCount = winMoney.getMoneyCount();
                            if(moneyCount != 0){
                                String redAndBlue = winMoney.getRedAndBlue();
                                int theFirstPrizeCount = winMoney.getTheFirstPrizeCount();
                                int secondAwardCount = winMoney.getSecondAwardCount();
                                String historyData=winMoney.getHistoryData();
                                if(theFirstPrizeCount >0){
                                    kjhm.set(historyData);
                                    System.out.println("开奖号码："+ historyData +",大乐透投注号码：" + redAndBlue +",一等奖中奖次数："+ theFirstPrizeCount +
                                            ",二等奖中奖次数：" + secondAwardCount + ",除一二等奖外，总奖金金额：" + moneyCount);
                                }
                                if(theFirstPrizeCount >0){
                                    //一等奖
                                    list.add(redAndBlue);
                                }
                            }
                        }
                    });
                    StringBuilder str = new StringBuilder();
                    str.append("计算开奖组合结束，共产生："+ list.size() +"条数据.");
                    if(list.size()>0){
                        winDataInfoList = winDataInfoService.getByHistoryData(list);
                        int size2 = winDataInfoList.size();
                        str.append("经过查询以往开奖记录winDataInfoList，筛选出" + size2 + "条复合预期的数据。") ;

                        List<Integer> mon = new ArrayList<>();
                        List<Integer> idList = new ArrayList<>();
                        List<WinDataInfo> winDataInfoListNew = new ArrayList<>();
                        winDataInfoList.forEach(w ->{
                            String redAndBlue = w.getRedAndBlue();
                            int moneyCount = w.getMoneyCount();
                            str.append("6+3组合：" + redAndBlue + ",历史中奖金额：" + moneyCount + "元。");
                            System.out.println("winDataInfoList主键"+ w.getId() +"开奖记录:6+3组合："+ redAndBlue + ",历史中奖金额：" + moneyCount + "元。");
                            mon.add(moneyCount);
                        });
                        List<NoWinDataInfo> noWinDataInfoList = noWinDataService.getByHistoryData(list);
                        int size1 = noWinDataInfoList.size();
                        str.append("经过查询以往开奖记录NoWinDataInfo，筛选出" + size1 + "条复合预期的数据。") ;
                        noWinDataInfoList.forEach(n ->{
                            String noWinNum = n.getNoWinNum();
                            WinDataInfo winDataInfo = new WinDataInfo();
                            winDataInfo.setRedAndBlue(noWinNum);
                            winDataInfo.setHistoryData(kjhm.get());
                            //数量无所谓 标记作用
                            winDataInfo.setTheFirstPrizeCount(1);
                            winDataInfo.setSecondAwardCount(2);
                            winDataInfo.setMoneyCount(18000);
                            winDataInfo.setDateTime(DateTime.now().toString("yyyy-MM-dd HH:mm:ss"));
                            winDataInfoListNew.add(winDataInfo);
                            int moneyCount = n.getMoneyCount();
                            str.append("6+3组合：" + noWinNum + ",历史中奖金额：" + moneyCount + "元。");
                            int id = n.getId();
                            idList.add(id);
                            System.out.println("NoWinDataInfo主键："+ id +",开奖记录:6+3组合："+ noWinNum + ",历史中奖金额：" + moneyCount + "元。");
                            mon.add(moneyCount);
                        });
                        Collections.sort(mon);//默认排序(从小到大)
                        str.append("规律标志：" + mon.toString());
                        System.out.println("开奖总结：" + str.toString());
                        //将未中奖表的数据删除  并保存进已中奖表
                        winDataInfoService.insertBatch(winDataInfoListNew);
                        noWinDataService.batchDelete(idList);
                    }
                    countDownLatch.countDown();
                }, ("线程：" + i)).start();
                page.getAndIncrement();
            }
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        long t2 = System.currentTimeMillis();
        return "开奖记录调整数据完毕，耗时：" + ((t2-t1)/1000) + "秒";
    }

    @GetMapping("createSixAndThreeBall")
    public String createSixAndThreeBall(){
        //查询所有红球组合
        List<RedBallInfo> redBallInfoList = redBallService.queryByStartAndEndIdx(1,1623160);
        //查询全部蓝球组合
        List<BlueBallInfo> blueBallInfoList = blueBallService.findAll();
        redBallInfoList.forEach(r ->{
            List<SixAndThreeEntity> list = new ArrayList<>();
            String redBall = r.getRedBall();
            blueBallInfoList.forEach(b ->{
                SixAndThreeEntity sixAndThreeEntity = new SixAndThreeEntity();
                String blueBall = b.getBlueBall();
                String sixAndThree = redBall + ":" + blueBall;
                sixAndThreeEntity.setBall(sixAndThree);
                list.add(sixAndThreeEntity);
            });
            sixAndThreeService.batchSave(list);

        });
        return "6+3组合数据完毕";
    }

    /**
     * 计算未中奖的6+3组合  奇数和偶数个数  从小到大
     */
    @GetMapping("getOddAndEvenNumber")
    public String getOddAndEvenNumber(){
        int pageNum = 35606;
        int pageSize = 10000;
        Boolean flag = true;
        while(flag){
            int startIndex = (pageNum - 1) * pageSize;
            int endIndex = pageNum * pageSize;
            long t1 = System.currentTimeMillis();
            List<NoWinDataInfo> noWinDataInfoList = noWinDataService.getAll(startIndex,endIndex);
            long t2 = System.currentTimeMillis();
            System.out.println("从小到大:查询数据：" + pageSize + "条，耗时：" + (t2 - t1));
            if(noWinDataInfoList == null || noWinDataInfoList.size() ==0){
                flag = false;
                break;
            }
            System.out.println("从小到大:第" + pageNum + "次运算开始");
            pageNum ++;
            List<NoWinDataInfo> noWinDataInfoListNew = new ArrayList<>();
            long t3 = System.currentTimeMillis();
            noWinDataInfoList.forEach(n ->{
                String noWinNum = n.getNoWinNum();
                String[] split = noWinNum.split(":");
                String redBallArr = split[0];
                String[] redBall = redBallArr.split(",");
                AtomicInteger odd = new AtomicInteger(0);
                AtomicInteger even = new AtomicInteger(0);
                Arrays.stream(redBall).forEach(r->{
                    Integer num = Integer.valueOf(r);
                    if(num % 2 == 0){
                        //偶数
                        even.getAndIncrement();
                    }else{
                        //奇数
                        odd.getAndIncrement();
                    }
                });
                n.setOdd(odd.get());
                n.setEven(even.get());
                noWinDataInfoListNew.add(n);
            });
            long t4 = System.currentTimeMillis();
            System.out.println("从小到大:奇偶运算耗时：" + (t4-t3));
            noWinDataService.batchUpdateById(noWinDataInfoListNew);
            long t5 = System.currentTimeMillis();
            System.out.println("从小到大:插入数据库耗时：" + (t5-t4));
        }
        return "从小到大:奇数偶数运算结束！";
    }

    /**
     * 计算未中奖的6+3组合  奇数和偶数个数  从大到小
     */
    @GetMapping("getOddAndEvenNumberDesc")
    public String getOddAndEvenNumberDesc(){
        //35605 8246
        int pageNum = 35606;
        int pageSize = 10000;
        AtomicReference<Boolean> flag = new AtomicReference<>(true);
        while(flag.get()){
            int startIndex = (pageNum - 1) * pageSize;
            int endIndex = pageNum * pageSize;
            long t1 = System.currentTimeMillis();
            List<NoWinDataInfo> noWinDataInfoList = noWinDataService.getAll(startIndex,endIndex);
            long t2 = System.currentTimeMillis();
            System.out.println("从大到小:查询数据：" + pageSize + "条，耗时：" + (t2 - t1));
            if(noWinDataInfoList == null || noWinDataInfoList.size() ==0){
                flag.set(false);
                break;
            }
            System.out.println("从大到小:第" + pageNum + "次运算开始");
            pageNum --;
            List<NoWinDataInfo> noWinDataInfoListNew = new ArrayList<>();
            long t3 = System.currentTimeMillis();
            for(NoWinDataInfo n : noWinDataInfoList){
                int odd1 = n.getOdd();
                int even1 = n.getEven();
                if(odd1 != 0 && even1 != 0){
                    flag.set(false);
                    continue;
                }
                String noWinNum = n.getNoWinNum();
                String[] split = noWinNum.split(":");
                String redBallArr = split[0];
                String[] redBall = redBallArr.split(",");
                AtomicInteger odd = new AtomicInteger(0);
                AtomicInteger even = new AtomicInteger(0);
                Arrays.stream(redBall).forEach(r->{
                    Integer num = Integer.valueOf(r);
                    if(num % 2 == 0){
                        //偶数
                        even.getAndIncrement();
                    }else{
                        //奇数
                        odd.getAndIncrement();
                    }
                });
                n.setOdd(odd.get());
                n.setEven(even.get());
                noWinDataInfoListNew.add(n);
            }
            long t4 = System.currentTimeMillis();
            System.out.println("从大到小:奇偶运算耗时：" + (t4-t3));
            noWinDataService.batchUpdateById(noWinDataInfoListNew);
            long t5 = System.currentTimeMillis();
            System.out.println("从大到小:插入数据库耗时：" + (t5-t4));
        }
        return "从大到小:奇数偶数运算结束！";
    }


    /**
     * 根据条件选择合适的6+3组合
     */
    @GetMapping("getDaletouCombination")
    public String getDaletouCombination(@RequestParam(required = false) String redBall,
            @RequestParam(required = false) String blueBall,@RequestParam(required = false) Integer odd,
                                                           @RequestParam(required = false) Integer even,
                                                           @RequestParam Integer moneyStart,
                                                           @RequestParam Integer moneyEnd
                                                            ){
        long t1 = System.currentTimeMillis();
        String[] redBallSplit = null;
        if(!StringUtils.isEmpty(redBall)){
            redBallSplit = redBall.split(",");
        }
        String[] blueBallSplit = null;
        if(!StringUtils.isEmpty(blueBall)){
            blueBallSplit = blueBall.split(",");
        }
        Map<Integer,String> map = new HashMap<>();
        AtomicInteger pageNum = new AtomicInteger(1);
        int pageSize = 100000;
        AtomicReference<Boolean> flag1 = new AtomicReference<>(true);
        String[] finalRedBallSplit = redBallSplit;
        String[] finalBlueBallSplit = blueBallSplit;
        while(flag1.get()){
            CountDownLatch countDownLatch = new CountDownLatch(6);
            for (int i = 1; i <= 6; i++) {
                int finalPageNum = pageNum.get();
                new Thread(() -> {
                    int startIndex = (finalPageNum - 1) * pageSize;
                    int endIndex = finalPageNum * pageSize;
                    System.out.println(Thread.currentThread().getName() + "\t" + "开始,起始位置：" +startIndex + ",结束位置：" + endIndex);
                    List<NoWinDataInfo> noWinDataServiceAll = noWinDataService.getAll(startIndex,endIndex);
                    if(noWinDataServiceAll == null || noWinDataServiceAll.size() ==0){
                        flag1.set(false);
                    }

                    noWinDataServiceAll.forEach(n ->{
                        String noWinNum = n.getNoWinNum();
                        String[] noWinSplit = noWinNum.split(":");
                        String redBall6 = noWinSplit[0];
                        String BlueBall3 = noWinSplit[1];
                        AtomicBoolean flag = new AtomicBoolean(true);
                        if(finalRedBallSplit != null){
                            Arrays.stream(finalRedBallSplit).forEach(r ->{
                                if(!redBall6.contains(r)){
                                    flag.set(false);
                                }
                            });
                        }
                        if(finalBlueBallSplit != null && flag.get()){
                            Arrays.stream(finalBlueBallSplit).forEach(b ->{
                                if(!BlueBall3.contains(b)){
                                    flag.set(false);
                                }
                            });
                        }
                        int odd1 = n.getOdd();
                        if(odd1 < odd && flag.get()){
                            flag.set(false);
                        }
                        int even1 = n.getEven();
                        if(even1 < even && flag.get()){
                            flag.set(false);
                        }
                        int moneyCount = n.getMoneyCount();
                        if((moneyCount < moneyStart || moneyCount > moneyEnd) && flag.get()){
                            flag.set(false);
                        }
                        if(flag.get()){
                            int id = n.getId();
                            map.put(id,noWinNum);
                        }
                    });

                    countDownLatch.countDown();
                }, ("线程：" + i)).start();
                pageNum.getAndIncrement();
                System.out.println("第" + pageNum.get() + "次运算开始");
            }
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        long t2 = System.currentTimeMillis();
        System.out.println("根据条件选择合适的6+3组合结束,耗时：" + ((t2-t1)/1000) + "秒");
        return JSONUtil.toJsonStr(map);
    }

}
