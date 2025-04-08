package com.clubfactory.platform.scheduler.server.common;

import com.clubfactory.platform.scheduler.core.IStoppable;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author xiejiajun
 */
public abstract class AbstractServer implements IStoppable {


    /**
     *  object lock
     */
    protected final Object lock = new Object();

    /**
     * whether or not to close the state
     */
    protected volatile boolean terminated = false;


    /**
     *  heartbeat interval, unit second
     */
    protected int heartBeatInterval;




    /**
     *  blocking implement
     * @throws InterruptedException
     */
    public void awaitTermination() throws InterruptedException {
        synchronized (lock) {
            while (!terminated) {
                lock.wait();
            }
        }
    }


    /**
     * Callback used to run the bean.
     * @param args incoming main method arguments
     * @throws Exception on error
     */
    public abstract void run(String... args) throws Exception;

    /**
     * gracefully stop
     * @param cause why stopping
     */
    @Override
    public abstract void stop(String cause);
}

