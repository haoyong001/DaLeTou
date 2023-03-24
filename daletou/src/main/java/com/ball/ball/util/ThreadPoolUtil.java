package com.ball.ball.util;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @ author Mr. Hao
 * @ date 2022-11-25   20:58
 */
public class ThreadPoolUtil {

    public static ThreadPoolExecutor getThreadPoolExcutor(String threadName, int corePoolSize, int maxPoolSize, int queue) {
        //ThreadFactory threadFactory = new ThreadFactoryBuilder().setNamePrefix(threadName + "-%d").build();
        return new ThreadPoolExecutor(corePoolSize,maxPoolSize,0L, TimeUnit.MILLISECONDS,new LinkedBlockingDeque<>(queue));
    }

}
