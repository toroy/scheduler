package com.clubfactory.platform.scheduler.spi.param;

/**
 * @author xiejiajun
 */
public class NullParameter implements IParameters {
    @Override
    public boolean checkParameters() {
        return true;
    }
}
