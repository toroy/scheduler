package com.clubfactory.platform.scheduler.dal.dao;

import com.clubfactory.platform.scheduler.dal.po.GroupInfo;
import com.clubfactory.platform.scheduler.dal.po.UserGroupRel;
import com.clubfactory.platform.scheduler.dal.po.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserGroupRelMapper extends BaseMapper<UserGroupRel>{

    /**
     * 根据联系人ID查询其拥有的组列表
     * @param userInfoId
     * @return
     */
    List<GroupInfo> selectGroupsByUserId(@Param("userInfoId") Long userInfoId);


    /**
     * 根据联系人组ID查询其拥有的联系人列表
     * @param groupInfoId
     * @return
     */
    List<UserInfo> selectUserInfosByGroupId(@Param("groupInfoId") Long groupInfoId);

    /**
     * 根据组ID列表查询所有相关的联系人列表
     * @param groupIds
     * @return
     */
    List<UserInfo> selectUserInfosByGroupIds(@Param("groupIds") List<Long> groupIds);

    /**
     * 根据联系人ID列表查询所有相关的联系人组列表
     * @param userIds
     * @return
     */
    List<GroupInfo> selectGroupInfosByUserIds(@Param("userIds") List<Long> userIds);
}