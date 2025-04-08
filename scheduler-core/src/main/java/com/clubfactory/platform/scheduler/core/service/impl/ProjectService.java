package com.clubfactory.platform.scheduler.core.service.impl;

import com.clubfactory.platform.scheduler.core.vo.ProjectVO;
import com.clubfactory.platform.scheduler.dal.dao.ProjectMapper;
import com.clubfactory.platform.scheduler.dal.po.Project;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;

@Service
public class ProjectService extends BaseNewService<ProjectVO, Project> {

    @Resource
    ProjectMapper projectMapper;

    @PostConstruct
    public void init() {
        setBaseMapper(projectMapper);
    }

    public List<String> getProjectNames(){
        return projectMapper.getProjectNames();
    }


}
