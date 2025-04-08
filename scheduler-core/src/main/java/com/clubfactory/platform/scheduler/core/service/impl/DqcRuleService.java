package com.clubfactory.platform.scheduler.core.service.impl;

import com.clubfactory.platform.scheduler.core.vo.DqcRuleVO;
import com.clubfactory.platform.scheduler.dal.dao.DqcRuleMapper;
import com.clubfactory.platform.scheduler.dal.po.DqcRule;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DqcRuleService extends BaseNewService<DqcRuleVO,DqcRule> {

    @Resource
    DqcRuleMapper dqcRuleMapper;

    @PostConstruct
    public void init(){
        setBaseMapper(dqcRuleMapper);
    }

    /**
     * 获取是否阻塞的dqc的任务id列表
     *
     * @param jobIds 关联任务id
     * @return
     */
    public Map<Boolean, Set<Long>> getDqcJobIdMap(List<Long> jobIds) {
        if (CollectionUtils.isEmpty(jobIds)) {
            return Maps.newHashMap();
        }
        List<DqcRuleVO> dqcRuleVOS = listByRelJobIds(jobIds);
        if (CollectionUtils.isEmpty(dqcRuleVOS)) {
            return Maps.newHashMap();
        }

        return dqcRuleVOS.stream().collect(Collectors.groupingBy(DqcRule::getIsBlock, Collectors.mapping(DqcRuleVO::getJobId, Collectors.toSet())));
    }

    /**
     * 获取是否阻塞的dqc的列表对象
     *
     * @param jobIds
     * @return Map<Boolean, List<DqcRule>>
     */
    public Map<Boolean, List<DqcRule>> getIsBlockMap(List<Long> jobIds) {
        if (CollectionUtils.isEmpty(jobIds)) {
            return Maps.newHashMap();
        }
        List<DqcRuleVO> dqcRuleVOS = listByRelJobIds(jobIds);
        if (CollectionUtils.isEmpty(dqcRuleVOS)) {
            return Maps.newHashMap();
        }
        return dqcRuleVOS.stream().collect(Collectors.groupingBy(DqcRule::getIsBlock));
    }

    public List<DqcRuleVO> listByRelJobIds(List<Long> jobIds) {
        if (CollectionUtils.isEmpty(jobIds)) {
            return Lists.newArrayList();
        }
        DqcRule dqcRule = new DqcRule();
        dqcRule.setIsDeleted(false);
        dqcRule.setIds(jobIds);
        dqcRule.setQueryListFieldName("rel_job_id");
        return this.list(dqcRule);
    }

    public List<Long> listNotBlockJobId() {
        DqcRule dqcRule = new DqcRule();
        dqcRule.setIsDeleted(false);
        dqcRule.setIsBlock(false);
        List<DqcRuleVO> vos = this.list(dqcRule);
        if (CollectionUtils.isEmpty(vos)) {
            return Lists.newArrayList();
        }
        return vos.stream().map(DqcRule::getJobId).collect(Collectors.toList());
    }

    public List<Long> blockRelJobId(List<Long> relJobIds) {
        List<DqcRuleVO> vos = listByRelJobIds(relJobIds);
        if (CollectionUtils.isEmpty(vos)) {
            return Lists.newArrayList();
        }
        return vos.stream().filter(vo -> BooleanUtils.isTrue(vo.getIsBlock())).map(DqcRuleVO::getRelJobId).collect(Collectors.toList());
    }
}
