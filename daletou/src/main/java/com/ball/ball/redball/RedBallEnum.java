package com.ball.ball.redball;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @ author Mr. Hao
 * @ date 2022_11_21   21:03
 */
public enum RedBallEnum {

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
    thirteen("13"),
    fourteen("14"),
    fifteen("15"),
    sixteen("16"),
    seventeen("17"),
    eighteen("18"),
    nineteen("19"),
    twenty("20"),
    twenty_one("21"),
    twenty_two("22"),
    twenty_three("23"),
    twenty_four("24"),
    twenty_five("25"),
    twenty_six("26"),
    twenty_seven("27"),
    twenty_eight("28"),
    twenty_nine("29"),
    thirty("30"),
    thirty_one("31"),
    thirty_two("32"),
    thirty_three("33"),
    thirty_four("34"),
    thirty_five("35"),

    ;

    private String redBall;


    RedBallEnum(String redBall) {
        this.redBall = redBall;
    }

    /**
     * 获取6个红球的所有可能组合
     */
    public static Set<String []> getSixRedBallForAll(){
        RedBallEnum[] values = RedBallEnum.values();
        //红球集合
        List<String> ballList = new ArrayList<String>();
        for(RedBallEnum redBall:values){
            ballList.add(redBall.redBall);
        }
        System.out.println("红球池：" + ballList);
        //从35个红球选出所有6个红球的组合
        Set<String []> redBallList = new HashSet<String[]>();
        for(int i0 = 0;i0<=34;i0++){
            String [] ballInteger = new String[6];
            //第一个球位
            ballInteger[0] = ballList.get(i0);
            for(int i1 = i0 +1;i1<=34;i1++){
                //第二个球位
                if(i1 != i0){
                    ballInteger[1] = ballList.get(i1);
                    for(int i2 = i1 +1;i2<=34;i2++){
                        //第三个球位
                        if(i2 !=i0 && i2 !=i1){
                            ballInteger[2] = ballList.get(i2);
                            for(int i3 = i2 +1;i3<=34;i3++){
                                //第四个球位
                                if(i3 != i2 && i3 != i1 && i3 != i0){
                                    ballInteger[3] = ballList.get(i3);
                                    for(int i4 = i3 +1;i4<=34;i4++){
                                        //第五个球位
                                        if(i4!=i3 && i4 != i2 && i4 != i1 && i4 != i0){
                                            ballInteger[4] = ballList.get(i4);
                                            for(int i5 = i4 +1;i5<=34;i5++){
                                                //第六个球位
                                                if(i5!=i4 && i5!=i3 && i5 != i2 && i5 != i1 && i5 != i0){
                                                    ballInteger[5] = ballList.get(i5);
                                                    redBallList.add(ballInteger);
                                                    ballInteger = new String[6];
                                                    //第一个球位
                                                    ballInteger[0] = ballList.get(i0);
                                                    //第二个球位
                                                    ballInteger[1] = ballList.get(i1);
                                                    //第三个球位
                                                    ballInteger[2] = ballList.get(i2);
                                                    //第四个球位
                                                    ballInteger[3] = ballList.get(i3);
                                                    //第五个球位
                                                    ballInteger[4] = ballList.get(i4);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }
        return redBallList;
    }
}
