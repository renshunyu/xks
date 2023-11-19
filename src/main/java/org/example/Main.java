package org.example;
import java.util.Random;
import java.util.concurrent.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class Main {

    public static void main(String[] args) {
        int n = 1;  // 设置你希望的线程池大小
        ThreadPoolExecutor executor = new ThreadPoolExecutor(n, n, 0L, TimeUnit.MILLISECONDS,
                new SynchronousQueue<Runnable>(),
                new ThreadPoolExecutor.AbortPolicy());

        int userNum = 2;
        ThreadPoolExecutor users =  new ThreadPoolExecutor(userNum, userNum, 0L, TimeUnit.MILLISECONDS,
                new SynchronousQueue<Runnable>(),
                new ThreadPoolExecutor.AbortPolicy());
        //executor.execute(new Task(625, 625));
        // 提交20个任务到线程池
        for (int i = 0; i < 1; i++) {
            try {
                users.execute(new User(i,executor));
            } catch (RejectedExecutionException e) {
                System.out.println("User " + i + " was rejected.");
            }
        }

        // 关闭线程池
//        users.shutdown();
//        executor.shutdown();
    }

    static class Task implements Runnable {
        private int taskId;

        private int time;

        public Task(int id, int time) {
            this.taskId = id;
            this.time = time;
        }

        @Override
        public void run() {
            String threadName = Thread.currentThread().getName();
            System.out.println("Hello from Task " + taskId + " and Thread " + threadName + "start");
            try {
                Random rand = new Random();
                Thread.sleep(this.time);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Hello from Task " + taskId + " and Thread " + threadName + "end");
        }
    }
    static class User implements Runnable {
        private static final Logger logger = LogManager.getLogger(User.class);

        private int taskId;

        private ThreadPoolExecutor executor;

        public User(int id,ThreadPoolExecutor executor) {
            this.taskId = id;
            this.executor = executor;
        }

        @Override
        public void run() {
            String threadName = Thread.currentThread().getName();
            Random rand = new Random();
            while (true) {
                logger.info("User "+taskId + " start");
                System.out.println("User " + taskId + " think start");
                try {
                    Thread.sleep((long) Math.abs(1000 + 500 * rand.nextGaussian()));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("User " + taskId + " think end");
                System.out.println("User " + taskId + " and User " + threadName + "start");
                try {
                    this.executor.execute(new Task(this.taskId, (int) Math.round(Math.abs(1000 + 500 * rand.nextGaussian()))));

                } catch (RejectedExecutionException e) {
                    System.out.println("User " + this.taskId + " was rejected.");
                }
                //Thread.sleep((long) Math.abs(1000 + 500 * rand.nextGaussian()));
                System.out.println("User " + taskId + " and User " + threadName + "end");
            }
        }
    }
}

