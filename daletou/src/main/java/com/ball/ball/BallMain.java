package com.ball.ball;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * @ author Mr. Hao
 * @ date 2022-11-22   20:46
 */
@SpringBootApplication
@EnableConfigServer
public class BallMain {

    public static void main(String[] args) {
            SpringApplication.run(BallMain.class,args);
        }
}
