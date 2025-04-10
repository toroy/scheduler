package com.clubfactory.platform.scheduler.core.service.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.clubfactory.platform.scheduler.common.util.Assert;
import org.apache.commons.collections.CollectionUtils;
import org.apache.curator.shaded.com.google.common.collect.Maps;
import org.springframework.stereotype.Service;

import com.clubfactory.platform.scheduler.core.vo.ScriptVO;
import com.clubfactory.platform.scheduler.dal.dao.ScriptMapper;
import com.clubfactory.platform.scheduler.dal.po.Script;

@Service
public class ScriptService extends BaseNewService<ScriptVO,Script> {

    @Resource
    ScriptMapper scriptMapper;

    @PostConstruct
    public void init(){
        setBaseMapper(scriptMapper);
    }

    public Map<Long, String> getScriptMap(List<Long> scriptIds) {
    	List<ScriptVO> scriptVos = listByIds(scriptIds);
    	if (CollectionUtils.isEmpty(scriptVos)) {
    		return Maps.newHashMap();
    	}
    	return scriptVos.stream().collect(Collectors.toMap(Script::getId, Script::getFileName));
    }
    
	public List<ScriptVO> listByIds(List<Long> scriptIds) {
		Assert.collectionNotEmpty(scriptIds, "脚本id列表");
    	Script script = new Script();
    	script.setIds(scriptIds);
    	List<ScriptVO> scriptVos = this.list(script);
		return scriptVos;
	}

    /**
     * 根据ID获取脚本信息
     * @param scriptId
     * @return
     */
    public ScriptVO getScriptInfoById(Long scriptId){
        Script script = new Script();
        script.setId(scriptId);
        script.setIsDeleted(false);
        return  this.get(script);
    }
    
    
}
