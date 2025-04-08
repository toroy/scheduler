package com.clubfactory.platform.scheduler.core.service.impl;

import com.clubfactory.platform.common.util.Assert;
import com.clubfactory.platform.scheduler.core.utils.StringUtil;
import com.clubfactory.platform.scheduler.core.vo.UserGroupRelVO;
import com.clubfactory.platform.scheduler.core.vo.UserInfoVO;
import com.clubfactory.platform.scheduler.dal.dao.UserInfoMapper;
import com.clubfactory.platform.scheduler.dal.enums.AlarmNoticeTypeEnum;
import com.clubfactory.platform.scheduler.dal.enums.AlarmTypeEnum;
import com.clubfactory.platform.scheduler.dal.po.UserGroupRel;
import com.clubfactory.platform.scheduler.dal.po.UserInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserInfoService extends BaseNewService<UserInfoVO,UserInfo> {

    @Resource
    UserInfoMapper userInfoMapper;
    @Resource
    UserGroupRelService userGroupRelService;

    @PostConstruct
    public void init(){
        setBaseMapper(userInfoMapper);
    }

    public Map<AlarmNoticeTypeEnum, List<String>> getNoticeMapByGroupId(Long groupId) {
        Assert.notNull(groupId);
        // 通过组id查询所有联系人Id
        UserGroupRel userGroupRel = new UserGroupRel();
        userGroupRel.setGroupId(groupId);
        userGroupRel.setIsDeleted(false);
        List<UserGroupRelVO> userGroupRels = userGroupRelService.list(userGroupRel);
        if (CollectionUtils.isEmpty(userGroupRels)) {
            return Maps.newHashMap();
        }

        // 通过联系人id查询所有联系人信息
        List<Long> userIds = userGroupRels.stream().map(UserGroupRelVO::getUserInfoId).collect(Collectors.toList());
        UserInfo userInfo = new UserInfo();
        userInfo.setIsDeleted(false);
        userInfo.setIds(userIds);
        List<UserInfoVO> userInfoVOS = this.list(userInfo);

        // 分类组装数据
        Map<AlarmNoticeTypeEnum, List<String>> noticeTypeMap = Maps.newHashMap();
        List<String> emails = userInfoVOS.stream().filter(user -> StringUtils.isNotBlank(user.getEmail()))
                .map(UserInfo::getEmail)
                .distinct()
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(emails)) {
            noticeTypeMap.put(AlarmNoticeTypeEnum.EMAIL, emails);
        }
        List<String> ims = userInfoVOS.stream().filter(user -> StringUtils.isNotBlank(user.getImRobot()))
                .map(UserInfo::getImRobot)
                .distinct()
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(ims)) {
            noticeTypeMap.put(AlarmNoticeTypeEnum.IM, ims);
        }
        List<String> phones = userInfoVOS.stream().filter(user -> StringUtils.isNotBlank(user.getPhoneNo()))
                .map(UserInfo::getPhoneNo)
                .distinct()
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(phones)) {
            noticeTypeMap.put(AlarmNoticeTypeEnum.PHONE_NO, phones);
        }
        return noticeTypeMap;
    }
}
