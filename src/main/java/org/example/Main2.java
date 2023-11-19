package org.example;
import java.util.concurrent.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main2 {

    static Logger logger = LogManager.getLogger(Main2.class);

    public static void main(String[] args) {
        int maxThreads = 5; // 自定义的最大线程数

        // 创建线程池
        ExecutorService executor = new ThreadPoolExecutor(
                maxThreads,
                maxThreads,
                0L,
                TimeUnit.MILLISECONDS,
                new SynchronousQueue<Runnable>()
        );

        // 创建 Callable 任务
        Callable<String> task = () -> {
            // 模拟耗时操作
            Thread.sleep(1000);
            return "Task return";
        };

        // 提交任务
        for (int i = 0; i < maxThreads + 1; i++) {
            try {
                Future<String> future = executor.submit(task);
                logger.info("Task submitted");
                logger.info(future.get());
                logger.info("Task completed");
            } catch (RejectedExecutionException e) {
                logger.info("Task rejected");
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        // 关闭线程池
        executor.shutdown();
    }
}
