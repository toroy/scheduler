package com.bigdata.platform.scheduler.server.mapper;

import com.alibaba.fastjson.JSON;
import com.bigdata.platform.scheduler.server.BaseTest;
import com.bigdata.platform.scheduler.dal.dao.DqcRuleMapper;
import com.bigdata.platform.scheduler.dal.po.DqcRule;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

public class DqcMapperTest extends BaseTest {

    @Resource
    DqcRuleMapper dqcRuleMapper;

    @Test
    public void listTargetTaskIdsTest() {
        List<Map<String, Object>> test = dqcRuleMapper.listTargetTaskIds(new DqcRule());
        System.out.println(JSON.toJSONString(test));
    }
}
