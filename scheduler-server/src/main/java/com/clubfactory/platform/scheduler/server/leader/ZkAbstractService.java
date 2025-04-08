package com.clubfactory.platform.scheduler.server.leader;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;

import com.alibaba.fastjson.JSON;
import com.clubfactory.platform.scheduler.core.dto.TaskDto;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class ZkAbstractService {
	
	protected CuratorFramework client;
	protected String taskPath;
	protected String workPath;
	

	protected void setZkData(String path, TaskDto taskDto) {
		if (taskDto != null && taskDto.getVersion() == null) {
			log.error("task version is null, filename: {}, scriptUserId: {}, status: {}, command: {}， path: {}"
					, taskDto.getFileName(), taskDto.getScriptUserId(), taskDto.getStatus(), taskDto.getCommandType(), path);
		}
		
		try {
			if (client.checkExists().forPath(path) != null) {
				client.setData().forPath(path, JSON.toJSONString(taskDto).getBytes());
			} else {
				client.create().withMode(CreateMode.EPHEMERAL).forPath(path, JSON.toJSONString(taskDto).getBytes());
			}
		} catch (Exception e) {
			log.error("插入zk数据失败", e);
		}
	}
	
	protected Boolean deleteZkPath(String path) {
		try {
			client.delete().forPath(path);
		} catch (Exception e) {
			e.printStackTrace();
			return true;
		}
		return false;
	}
	
	protected Boolean isExist(String path) {
		
		try {
			if (client.checkExists().forPath(path) != null) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			log.error("path: {}", path, e);
			return false;
		}
	}
	
	protected Boolean isHasChild(String path, String checkChild) {
		
		try {
			if (client.checkExists().forPath(path) != null) {
				List<String> childs = client.getChildren().forPath(path);
				if (CollectionUtils.isEmpty(childs)) {
					return false;
				}
				for (String child : childs) {
					if (child.contains(checkChild)) {
						return true;
					}
				}
				return false;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	protected List<String> listChildrens(String ip) {
		String path = String.format("%s/%s", taskPath, ip);
		List<String> childs = Lists.newArrayList();
		try {
			childs = client.getChildren().forPath(path);
		} catch (Exception e) {
			log.error("zookeeper error",e);
			
			try {
				client.create().withMode(CreateMode.PERSISTENT).forPath(path);
			} catch (Exception se) {
				log.error("create zookeeper node error",se);
			}
		}
		return childs;
	}
	
	protected String getPath(String ip, Long id) {
		return String.format("%s/%s/%s", taskPath, ip, id);
	}
}
