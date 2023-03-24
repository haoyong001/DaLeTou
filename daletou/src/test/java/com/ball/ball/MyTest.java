package com.ball.ball;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @ author Mr. Hao
 * @ date 2022-11-25   23:19
 */
public class MyTest {

    public static void main(String[] args) {
        String ball = "02,08,15,17,26;02,08";
        String redBall = "02,05,09,15,17,26:02,06,08";
        String[] redBallArray = {"02","05","09","15","17","26"};
        String[] blueBallArray = {"02","06","08"};
        ArrayList<String> redList = new ArrayList<>();
        redList.add("02");
        redList.add("08");
        redList.add("15");
        redList.add("17");
        redList.add("26");
        List<String> strings = redList.subList(4, 5);
        System.out.println(strings);
        AtomicInteger sameRedBall = new AtomicInteger(0);
        Arrays.stream(redBallArray).forEach(b ->{
            boolean contains = redList.contains(b);
            if(contains){
                sameRedBall.getAndIncrement();
            }
        });

        System.out.println(sameRedBall.get());
    }
}
