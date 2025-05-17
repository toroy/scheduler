package com.bigdata.platform.scheduler.dal.dao;

import com.bigdata.platform.scheduler.dal.po.DqcRule;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface DqcRuleMapper extends BaseMapper<DqcRule>{

    List<Map<String, Object>> listTargetTaskIds(DqcRule dqcRule);

    Integer countTargetTaskIds(DqcRule dqcRule);

    int editIfNull(DqcRule dqcRule);
}