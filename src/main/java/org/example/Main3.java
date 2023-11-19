package org.example;
import java.util.Random;
import java.util.concurrent.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class Main3 {
    private static final Logger logger = LogManager.getLogger(Main3.class);

    public static void main(String[] args) {
        int maxThreads = 16;  // 设置你希望的线程池大小

        ExecutorService executor = new ThreadPoolExecutor(
                maxThreads,
                maxThreads,
                0L,
                TimeUnit.MILLISECONDS,
                new SynchronousQueue<Runnable>()
        );

        int userNum = 48;
        ThreadPoolExecutor users =  new ThreadPoolExecutor(userNum, userNum, 0L, TimeUnit.MILLISECONDS,
                new SynchronousQueue<Runnable>(),
                new ThreadPoolExecutor.AbortPolicy());
        //executor.execute(new Task(625, 625));
        // 提交20个任务到线程池
        for (int i = 0; i < userNum; i++) {
            try {
                users.execute(new User(i,executor));
            } catch (RejectedExecutionException e) {
                logger.info("User " + i + " was rejected.");
            }
        }

        // 关闭线程池
//        users.shutdown();
//        executor.shutdown();
    }

    static class Task<T> implements Callable<T> {
        private static final Logger logger = LogManager.getLogger(Task.class);
        private int taskId;

        private int time;

        public Task(int id, int time) {
            this.taskId = id;
            this.time = time;
        }

        @Override
        public T call() {
            String threadName = Thread.currentThread().getName();
            //logger.info("Hello from Task " + taskId + " and Thread " + threadName + "start");
            try {
                Random rand = new Random();
                Thread.sleep(this.time);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            //logger.info("Hello from Task " + taskId + " and Thread " + threadName + "end");
            return (T) "return";
        }
    }
    static class User implements Runnable {
        private static final Logger logger = LogManager.getLogger(User.class);

        private int taskId;

        private ExecutorService executor;

        public User(int id,ExecutorService executor) {
            this.taskId = id;
            this.executor = executor;
        }

        @Override
        public void run() {
            String threadName = Thread.currentThread().getName();
            Random rand = new Random();
            while (true) {
                //logger.info("User "+taskId + " start");
                //logger.info("User " + taskId + " think start");
                try {
                    Thread.sleep((long) Math.abs(3000 + 2500 * rand.nextGaussian()));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                //logger.info("User " + taskId + " think end");
                //logger.info("User " + taskId + " and User " + threadName + "start");
                try {
                    Future<String> future = this.executor.submit(new Task(this.taskId, (int) Math.round(Math.abs(1000 + 500 * rand.nextGaussian()))));
                    logger.info("User "+taskId + " "+future.get());

                } catch (RejectedExecutionException e) {
                    logger.error("User " + this.taskId + " was rejected.");
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                //Thread.sleep((long) Math.abs(1000 + 500 * rand.nextGaussian()));
                //logger.info("User " + taskId + " and User " + threadName + "end");
            }
        }
    }
}

