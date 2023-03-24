package com.ball.ball.blueball;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @ author Mr. Hao
 * @ date 2022-11-21   20:55
 */
public enum BlueBallEnum {
    one("01"),
    two("02"),
    three("03"),
    four("04"),
    five("05"),
    six("06"),
    seven("07"),
    eight("08"),
    nine("09"),
    ten("10"),
    eleven("11"),
    twelve("12"),

    ;

    private String blueBall;

    BlueBallEnum(String blueBall) {
        this.blueBall = blueBall;
    }
    /**
     * 获取3个篮球所有组合
     */
    public static Set<String []> getThreeBlueBallForAll(){
        BlueBallEnum[] values = BlueBallEnum.values();
        //蓝球集合
        List<String> ballList = new ArrayList<String>();
        for(BlueBallEnum blueBall:values){
            ballList.add(blueBall.blueBall);
        }
        System.out.println("蓝球池：" + ballList);
        //从12个蓝球选出所有3个蓝球的组合
        Set<String []> blueBallList = new HashSet<String[]>();
        for(int i0 = 0;i0<=11;i0++){
            String [] ballInteger = new String[3];
            //第一个球位
            ballInteger[0] = ballList.get(i0);
            for(int i1 = i0 +1;i1<=11;i1++){
                //第二个球位
                if(i1 != i0){
                    ballInteger[1] = ballList.get(i1);
                    for(int i2 = i1 +1;i2<=11;i2++){
                        //第三个球位
                        if(i2 !=i0 && i2 !=i1){
                            ballInteger[2] = ballList.get(i2);
                            blueBallList.add(ballInteger);
                            ballInteger = new String[3];
                            //第一个球位
                            ballInteger[0] = ballList.get(i0);
                            //第二个球位
                            ballInteger[1] = ballList.get(i1);
                            //第三个球位
                            ballInteger[2] = ballList.get(i2);

                        }
                    }
                }
            }

        }
        return blueBallList;
    }

}
