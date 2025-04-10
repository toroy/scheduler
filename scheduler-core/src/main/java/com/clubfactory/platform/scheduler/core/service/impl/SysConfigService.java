package com.clubfactory.platform.scheduler.core.service.impl;

import com.clubfactory.platform.scheduler.common.util.Assert;
import com.clubfactory.platform.scheduler.core.vo.SysConfigVO;
import com.clubfactory.platform.scheduler.dal.dao.SysConfigMapper;
import com.clubfactory.platform.scheduler.dal.enums.ConfigType;
import com.clubfactory.platform.scheduler.dal.po.SysConfig;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author xiejiajun
 */
@Service
public class SysConfigService {

    @Resource
    private SysConfigMapper sysConfigMapper;



    public int save(SysConfig sysConfig){
        Assert.notNull(sysConfig);
        Assert.notNull(sysConfig.getApplyHost(),"配置应用主机");
        Assert.notNull(sysConfig.getParamKey(),"配置key");
        Assert.notNull(sysConfig.getParamValue(),"配置value");
        Assert.notNull(sysConfig.getConfigType(),"配置适应范围");
        return sysConfigMapper.save(sysConfig);
    }

    public int edit(SysConfig sysConfig){
        Assert.notNull(sysConfig);
        Date updateTime = new Date();
        SysConfig condition = SysConfig.builder()
                .id(sysConfig.getId()).build();
        Map<String,Object> updateParams = Maps.newHashMap();
        if (StringUtils.isNotBlank(sysConfig.getParamKey())){
            updateParams.put("param_key",sysConfig.getParamKey());
        }
        if (StringUtils.isNotBlank(sysConfig.getParamValue())){
            updateParams.put("param_val",sysConfig.getParamValue());
        }
        return sysConfigMapper.update(updateParams,updateTime,condition);
    }

    public int remove(Long id){
        Assert.notNull(id,"配置ID");
        SysConfig sysConfig = SysConfig.builder().id(id).build();
        return sysConfigMapper.delete(sysConfig);
    }

    public List<SysConfigVO> listValidConf(String host, ConfigType configType){
        Assert.notNull(host,"服务器IP");
        Assert.notNull(configType,"服务类型");
        SysConfig sysConfig = SysConfig.builder()
                .applyHost("all")
                .configType(configType)
                .build();

        List<SysConfigVO> configVOList = Lists.newLinkedList();
        sysConfigMapper.listValid(sysConfig)
                .stream()
                .filter(po -> StringUtils.isNotBlank(po.getParamValue()) && StringUtils.isNotBlank(po.getParamKey()))
                .forEach(po ->
                        configVOList.add(
                                SysConfigVO.builder()
                                .paramKey(po.getParamKey())
                                .paramValue(po.getParamValue())
                                .build()
                        )

                );

        sysConfig.setApplyHost(host);
        sysConfigMapper.listValid(sysConfig)
                .stream()
                .filter(po -> StringUtils.isNotBlank(po.getParamValue()) && StringUtils.isNotBlank(po.getParamKey()))
                .forEach(po ->
                        configVOList.add(
                                SysConfigVO.builder()
                                        .paramKey(po.getParamKey())
                                        .paramValue(po.getParamValue())
                                        .build()
                        )

                );
        return configVOList;
    }
}
