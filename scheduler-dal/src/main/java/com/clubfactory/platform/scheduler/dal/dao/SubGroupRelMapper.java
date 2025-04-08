package com.clubfactory.platform.scheduler.dal.dao;

import com.clubfactory.platform.scheduler.dal.po.AlertSub;
import com.clubfactory.platform.scheduler.dal.po.GroupInfo;
import com.clubfactory.platform.scheduler.dal.po.SubGroupRel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SubGroupRelMapper extends BaseMapper<SubGroupRel>{

    /**
     * 根据联系人组ID获取改组的所有订阅列表
     * @param groupInfoId
     * @return
     */
    List<AlertSub> selectSubInfosByGroupInfoId(@Param("groupInfoId") Long groupInfoId);

    /**
     * 根据订阅ID获取该订阅信息所关联的联系人组列表
     * @param subscribeId
     * @return
     */
    List<GroupInfo> selectGroupInfosBySubscribeId(@Param("subscribeId") Long subscribeId);
}