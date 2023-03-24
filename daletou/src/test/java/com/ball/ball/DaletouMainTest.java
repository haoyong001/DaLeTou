package com.ball.ball;

import cn.hutool.core.date.DateTime;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.ball.ball.blueball.BlueBallEnum;
import com.ball.ball.entity.BlueBallInfo;
import com.ball.ball.entity.RedBallInfo;
import com.ball.ball.redball.RedBallEnum;
import com.ball.ball.service.BlueBallService;
import com.ball.ball.service.RedBallService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @ author Mr. Hao
 * @ date 2022-11-22   17:29
 */
@SpringBootTest(classes = BallMain.class)
@RunWith(SpringRunner.class)
public class DaletouMainTest {

    @Resource
    private RedBallService redBallService;

    @Resource
    private BlueBallService blueBallService;

    @Test
    public void createBall(){
        //获取6红球集合
        long start = System.currentTimeMillis();
        Set<String[]> sixRedBallForAll = RedBallEnum.getSixRedBallForAll();
        System.out.println("6个红球共有组合："+ sixRedBallForAll.size() + "种");
        JSON red = JSONUtil.parse(sixRedBallForAll);
        List<RedBallInfo> redBallInfoList = new ArrayList<>();
        sixRedBallForAll.stream().forEach(r ->{
            RedBallInfo redBallInfo = new RedBallInfo();
            redBallInfo.setCreateTime(DateTime.now().toString("yyyy-MM-dd"));
            String redBall = r[0]+ "," + r[1]+ "," + r[2]+ "," + r[3]+ "," + r[4]+ "," + r[5];
            redBallInfo.setRedBall(redBall);
            redBallInfoList.add(redBallInfo);
        });
        //保存红球进数据库
        redBallService.insertBatch(redBallInfoList);

        List<BlueBallInfo> blueBallInfoList = new ArrayList<>();
        Set<String[]> threeBlueBallForAll = BlueBallEnum.getThreeBlueBallForAll();
        System.out.println("3个蓝球共有组合："+ threeBlueBallForAll.size() + "种");
        JSON blue = JSONUtil.parse(threeBlueBallForAll);
        //保存篮球进数据库
        threeBlueBallForAll.stream().forEach(b ->{
            String threeBlueBall = b[0]+ "," + b[1]+ ","+ b[2];
            BlueBallInfo blueBallInfo = new BlueBallInfo();
            blueBallInfo.setBlueBall(threeBlueBall);
            blueBallInfo.setCreateTime(DateTime.now().toString("yyyy-MM-dd"));
            blueBallInfoList.add(blueBallInfo);
        });
        blueBallService.insertBatch(blueBallInfoList);

        //红球+篮球所有组合
//        List<String> sixAndThree = new ArrayList<String>();
//        threeBlueBallForAll.stream().forEach(b ->{
//            sixRedBallForAll.stream().forEach(r ->{
//                String sixAndThreeString = r[0]+ "," + r[1]+ "," + r[2]+ "," + r[3]+ "," + r[4]+ "," + r[5] + ":" + b[0]+ "," + b[1]+ ","+ b[2];
//                //System.out.println(sixAndThreeString);
//                sixAndThree.add(sixAndThreeString);
//            });
//        });
        //357095200
//        System.out.println("红球+篮球共有组合："+ sixAndThree.size() + "种");
        long end = System.currentTimeMillis();
        System.out.println("总共计算耗时：" + (end-start)/1000 + "秒");
    }
}
