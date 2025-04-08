package com.clubfactory.platform.scheduler.dal.dao;

import com.clubfactory.platform.scheduler.dal.po.UserInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserInfoMapper extends BaseMapper<UserInfo>{

    List<UserInfo> listLoginUserInfos(UserInfo po);

}