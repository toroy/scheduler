package com.zhugeio.platform.scheduler.core.service.impl;

import com.zhugeio.platform.scheduler.common.util.Assert;
import com.zhugeio.platform.scheduler.core.service.IAlarmService;
import com.zhugeio.platform.scheduler.core.vo.AlarmVO;
import com.zhugeio.platform.scheduler.dal.dao.AlarmMapper;
import com.zhugeio.platform.scheduler.dal.enums.AlarmTypeEnum;
import com.zhugeio.platform.scheduler.dal.po.Alarm;
import org.springframework.stereotype.Service;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Service("alarmService")
public class AlarmService extends BaseNewService<AlarmVO, Alarm> implements IAlarmService {

    @Resource
    AlarmMapper alarmMapper;

    @Override
    @PostConstruct
    public void init(){
        setBaseMapper(alarmMapper);
    }

    @Override
    public void sendServerStoppedAlert(int alertGroupId, String host, String serverType) {

    }
    
    public List<AlarmVO> listByJobIds(List<Long> jobId, AlarmTypeEnum type) {
    	Assert.collectionNotEmpty(jobId, "任务id");
    	
    	Alarm alarm = new Alarm();
    	alarm.setIds(jobId);
    	alarm.setType(type);
    	alarm.setIsDeleted(false);
    	alarm.setQueryListFieldName("job_id");
    	return this.list(alarm);
    }
    
    public List<AlarmVO> listDelayByJobIds(List<Long> jobId) {
    	Assert.collectionNotEmpty(jobId, "任务id");
    	
    	Alarm alarm = new Alarm();
    	alarm.setIds(jobId);
    	alarm.setType(AlarmTypeEnum.DELAY);
    	alarm.setQueryListFieldName("job_id");
    	alarm.setIsDeleted(false);
    	return this.list(alarm);
    }

}
