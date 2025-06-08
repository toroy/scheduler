package com.zhugeio.platform.scheduler.server.worker.monitor;

import com.zhugeio.platform.scheduler.server.BaseTest;
import com.zhugeio.platform.scheduler.core.utils.PropertyUtils;
import com.zhugeio.platform.scheduler.core.vo.TaskVO;
import com.zhugeio.platform.scheduler.core.monitor.YarnTaskMonitor;
import com.zhugeio.platform.scheduler.server.config.CommonProperties;
import lombok.Setter;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class MonitorTest extends BaseTest {

    @Autowired
    private YarnTaskMonitor yarnTaskMonitor;

    @Autowired
    @Setter
    private CommonProperties commonProperties;


    @Test
    public void add() throws Exception {
        PropertyUtils.init(commonProperties.getProperties());

        TaskVO taskVO = new TaskVO();
        taskVO.setId(10294737L);
        yarnTaskMonitor.monitor(taskVO, "application_1587265231805_240032", null);
    }
}
