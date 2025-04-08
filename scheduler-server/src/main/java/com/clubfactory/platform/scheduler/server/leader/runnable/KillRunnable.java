package com.clubfactory.platform.scheduler.server.leader.runnable;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.curator.framework.CuratorFramework;

import com.clubfactory.platform.scheduler.core.enums.CommandType;
import com.clubfactory.platform.scheduler.core.vo.ScriptVO;
import com.clubfactory.platform.scheduler.dal.enums.TaskStatusEnum;
import com.clubfactory.platform.scheduler.dal.po.Task;
import com.clubfactory.platform.scheduler.core.dto.TaskDto;

/**
 * @author zhoulijiang
 */
@Slf4j
public class KillRunnable extends SchedulerBaseService implements Runnable {

	public KillRunnable(CuratorFramework client, String taskPath) {
		super.init();
		super.client = client;
		super.taskPath = taskPath;
	}
	
	@Override
	public void run() {
		// killing请求
		sendByStatus(TaskStatusEnum.KILLING);
		// 正在运行的，强制成功的请求
		sendByStatus(TaskStatusEnum.MANUAL_SUCCESS);
	}

	private void sendByStatus(TaskStatusEnum status) {
		List<Task> tasks = taskService.listByStatus(status);
		if (CollectionUtils.isEmpty(tasks)) {
			return;
		}
		Map<Long, Integer> versionMap = super.getVersionMap(tasks);
		List<ScriptVO> scriptVOs =  super.listScript(tasks);
		
		Map<Long, String> fileNameMap = scriptVOs.stream().collect(Collectors.toMap(ScriptVO::getId, ScriptVO::getFileName));
		Map<Long, Long> scriptUserMap = scriptVOs.stream().collect(Collectors.toMap(ScriptVO::getId, ScriptVO::getCreateUser));
		
		// 更新
		for (Task task : tasks) {
			log.info("status: {}, ip: {}, taskId:{}", status.name(), task.getIp(), task.getId());
			String path = super.getPath(task.getIp(), task.getId());
			TaskDto taskDto = new TaskDto();
			taskDto.setCommandType(CommandType.SCHEDULER);
			taskDto.setStatus(status);
			taskDto.setVersion(versionMap.get(task.getId()));
			taskDto.setFileName(fileNameMap.get(task.getScriptId()));
			taskDto.setScriptUserId(scriptUserMap.get(task.getScriptId()));
			super.setZkData(path, taskDto);
		}
	}







}
