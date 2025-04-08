package com.clubfactory.platform.scheduler.core.service.impl;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.curator.shaded.com.google.common.collect.Lists;
import org.springframework.stereotype.Service;

import com.clubfactory.platform.scheduler.core.vo.TaskDependsVO;
import com.clubfactory.platform.scheduler.dal.dao.TaskDependsMapper;
import com.clubfactory.platform.scheduler.dal.po.TaskDepends;

@Service
public class TaskDependsService extends BaseNewService<TaskDependsVO,TaskDepends> {

    @Resource
    TaskDependsMapper taskDependsMapper;

    @PostConstruct
    public void init(){
        setBaseMapper(taskDependsMapper);
    }
    

    public List<TaskDependsVO> listByIds(List<Long> ids) {
    	if (CollectionUtils.isEmpty(ids)) {
    		return Lists.newArrayList();
    	}
    	TaskDepends depends = new TaskDepends();
    	depends.setIds(ids);
    	depends.setQueryListFieldName("task_id");
    	List<TaskDependsVO> dependVos = this.list(depends);
    	if (CollectionUtils.isEmpty(dependVos)) {
    		return Lists.newArrayList();
    	}
    	
    	return dependVos;
    }

	public List<TaskDependsVO> listByParentIds(List<Long> ids) {
		if (CollectionUtils.isEmpty(ids)) {
			return Lists.newArrayList();
		}
		TaskDepends depends = new TaskDepends();
		depends.setIds(ids);
		depends.setQueryListFieldName("parent_id");
		return this.list(depends);
	}
}
