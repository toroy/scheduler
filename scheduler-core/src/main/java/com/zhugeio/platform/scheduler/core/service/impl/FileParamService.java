package com.zhugeio.platform.scheduler.core.service.impl;

import com.zhugeio.platform.scheduler.common.util.Assert;
import com.zhugeio.platform.scheduler.core.vo.FileParamVO;
import com.zhugeio.platform.scheduler.dal.dao.FileParamMapper;
import com.zhugeio.platform.scheduler.dal.po.FileParam;
import org.apache.commons.collections.CollectionUtils;
import org.apache.curator.shaded.com.google.common.collect.Maps;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * TODO
 *
 * @author zhoulijiang
 * @date 2025/6/15 17:06
 **/
@Service
public class FileParamService extends BaseNewService<FileParamVO, FileParam> {

    @Resource
    FileParamMapper fileParamMapper;

    @PostConstruct
    public void init() {
        setBaseMapper(fileParamMapper);
    }

    public Map<Long, String> getFileParamMap(List<Long> fileParamIds) {
        List<FileParamVO> fileParamVos = listByIds(fileParamIds);
        if (CollectionUtils.isEmpty(fileParamVos)) {
            return Maps.newHashMap();
        }
        return fileParamVos.stream().collect(Collectors.toMap(FileParam::getId, FileParam::getFileName));
    }

    public List<FileParamVO> listByIds(List<Long> fileParamIds) {
        Assert.collectionNotEmpty(fileParamIds, "脚本id列表");
        FileParam fileParam = new FileParam();
        fileParam.setIds(fileParamIds);
        List<FileParamVO> fileParamVos = this.list(fileParam);
        return fileParamVos;
    }

    /**
     * 根据ID获取脚本信息
     *
     * @param fileParamIds
     * @return
     */
    public FileParamVO getFileParamInfoById(Long fileParamIds) {
        FileParam fileParam = new FileParam();
        fileParam.setId(fileParamIds);
        fileParam.setIsDeleted(false);
        return this.get(fileParam);
    }
}


