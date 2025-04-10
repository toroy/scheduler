package com.clubfactory.platform.scheduler.server.leader;

import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.clubfactory.platform.scheduler.common.constant.DateFormatPattern;
import com.clubfactory.platform.scheduler.common.util.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.utils.ThreadUtils;

import com.clubfactory.platform.scheduler.core.service.impl.MachineService;
import com.clubfactory.platform.scheduler.core.service.impl.TaskService;
import com.clubfactory.platform.scheduler.core.utils.SpringBean;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ListenerWorkService implements Runnable {

	private CuratorFramework client;
	
	private PathChildrenCache cache;
	
	private String path;
	
	private TaskService taskService;
	
	private MachineService machineService;
	
	private Object mutex = new Object();
	
	ListenerWorkService(CuratorFramework client, String path) {
		this.client = client;
		this.path = path;
		initCache(client, path);
		init();
	}
	
	private void initCache(CuratorFramework client, String path) {
		this.cache = new PathChildrenCache(client, path, true);
		try {
			cache.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void init() {
		taskService = SpringBean.getBean(TaskService.class);
		machineService = SpringBean.getBean(MachineService.class);
	}
	
	@Override
	public void run() {
		
		cache = new PathChildrenCache(client, path, true, ThreadUtils.newGenericThreadFactory("Master-Main-Thread"));
        try {

        	cache.start();
        	cache.getListenable().addListener(new PathChildrenCacheListener() {
                @Override
                public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                    switch (event.getType()) {
                        case CHILD_REMOVED:
                            String workPath = event.getData().getPath();
                            log.info("remove work request, workPath: {}", workPath);
                            String ip = getIp(workPath);
                            if (StringUtils.isNotBlank(ip)) {
                            	// 重新查下节点是否存在，如果不存在，再转移（针对重启环境）
	                           Long ctime = getCtime(cache.getCurrentData(),ip);
	                            // 转移修改时间早于节点创建时间的task
	                            resume(ip, ctime);
	                            // 没有节点存活，则调度机状态改为不可用
	                            if (ctime == null) {
	                            	machineService.disableByIp(ip);
	                            	log.info("disable machine type ip: {}", ip);
	                            }
	                            log.info("remove work ip: {}", ip);
                            }
                            break;
                        case CHILD_UPDATED: break;
                        case CHILD_ADDED:
                        	workPath = event.getData().getPath();
                        	log.info("add work request, workPath: {}", workPath);
                        	ip = getIp(workPath);
                        	if (StringUtils.isNotBlank(ip)) {
                        		// 调度机状态改为可用
                            	machineService.enableByIp(ip);
                            	log.info("add work ip: {}", ip);
                            }
                        	break;
                        default:
                            break;
                    }
                }
            });
        } catch (Exception e) {
            log.error("monitor worker failed : " + e.getMessage(), e);
        }
        
		synchronized (mutex) {
			try {
				mutex.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private Long getCtime(List<ChildData> currentData, String ip) {	
		for (ChildData data : currentData) {
			if (data.getPath().contains(ip)) {
				return data.getStat().getCtime();
			}
		}
		return null;
	}
	
	private String getIp(String workPath) {
		String ma = "(\\d{1,3}\\.){3}\\d{1,3}";
		Matcher m = Pattern.compile(ma).matcher(workPath);
		while (m.find()) {
			return m.group();
		}
		return null;
	}
	
	private void resume(String ip, Long ctime) {
		String endTime = null;
		if (ctime != null) {
			Date date = new Date(ctime);
			endTime = DateUtil.format(date, DateFormatPattern.YYYY_MM_DD_HH_MM_SS);
		}
		taskService.initReadyByWorkFailed(ip, endTime);
	}
	
	
}
