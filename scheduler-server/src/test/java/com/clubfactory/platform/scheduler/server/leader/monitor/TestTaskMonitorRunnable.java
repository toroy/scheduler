package com.clubfactory.platform.scheduler.server.leader.monitor;


import com.clubfactory.platform.scheduler.server.Application;
import com.clubfactory.platform.scheduler.server.leader.runnable.TaskMonitorRunnable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class TestTaskMonitorRunnable {

    @Autowired
    private TaskMonitorRunnable taskMonitorRunnable;

    @Test
    public void runnable() throws Exception {
//        taskMonitorRunnable.run();
        ExecutorService es = Executors.newFixedThreadPool(1);
        es.submit(taskMonitorRunnable);
        es.awaitTermination(99999L, TimeUnit.SECONDS);
    }

}
