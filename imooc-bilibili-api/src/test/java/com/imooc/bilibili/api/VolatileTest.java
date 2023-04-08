package com.imooc.bilibili.api;

import lombok.extern.slf4j.Slf4j;

import java.util.logging.Logger;

/**
 * <p>在开始处详细描述该类的作用</p>
 * <p>描述请遵循 javadoc 规范</p>
 *
 * @author Soxhwhat
 * @date 2023/4/18 10:23
 * @update [序号][日期YYYY-MM-DD] [更改人姓名][变更描述]
 */

// Java Program to demonstrate the
// use of Volatile Keyword in Java
@Slf4j
public class VolatileTest {
    private static volatile int MY_INT = 0;

    public static void main(String[] args) {
        new ChangeListener().start();
        new ChangeMaker().start();
    }

    static class ChangeListener extends Thread {
        @Override
        public void run() {
            int local_value = MY_INT;
            while (local_value < 5) {
                if (local_value != MY_INT) {
                    log.info(
                            // "Got Change for MY_INT : {}",括号里面是MY_INT
                            "Got Change for MY_INT : {}", MY_INT
                    );
                    local_value = MY_INT;
                }
            }
        }
    }

    static class ChangeMaker extends Thread {
        @Override
        public void run() {
            int local_value = MY_INT;
            while (MY_INT < 5) {
                log.info(
                        "Incrementing MY_INT to {}", local_value + 1
                );
                MY_INT = ++local_value;
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}