package com.clubfactory.platform.scheduler.common.utils;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.clubfactory.platform.common.constant.DateFormatPattern;
import com.clubfactory.platform.common.util.DateUtil;
import com.clubfactory.platform.scheduler.dal.dto.SchedulerTimeDto;
import com.clubfactory.platform.scheduler.dal.enums.JobCycleTypeEnum;
import com.clubfactory.platform.scheduler.dal.po.JobOnline;
import com.clubfactory.platform.scheduler.dal.po.Task;
import com.clubfactory.platform.scheduler.dal.po.TaskDepends;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TaskDependsUtil {
	
	public static List<TaskDepends> genSelfDependsByJob(JobOnline jobOnline, List<Task> tasks, List<Task> parentTasks) {
		List<TaskDepends> taskDepends = Lists.newArrayList();
		if (CollectionUtils.isEmpty(tasks)) {
			return taskDepends;
		}
		
		generateDependsBySelf(tasks, taskDepends);
		
		if (CollectionUtils.isEmpty(parentTasks)) {
			return taskDepends;
		}
		JobCycleTypeEnum cycleType = jobOnline.getCycleType();
		if (JobCycleTypeEnum.DAY == cycleType) {
			genTaskDependsByLocal(tasks, taskDepends, parentTasks, 1, JobCycleTypeEnum.DAY);
		} else if (JobCycleTypeEnum.DAYT1 == cycleType) {
			genTaskDependsByLocal(tasks, taskDepends, parentTasks, 1, JobCycleTypeEnum.DAY);
		} else if (JobCycleTypeEnum.HOURS == cycleType) {
			SchedulerTimeDto schedulerTimeDto = JSON.parseObject(jobOnline.getSchedulerTime(), SchedulerTimeDto.class);
			genTaskDependsByLocal(tasks, taskDepends, parentTasks, schedulerTimeDto.getHour(), JobCycleTypeEnum.HOURS);
		} else if (JobCycleTypeEnum.MINUTES == cycleType) {
			SchedulerTimeDto schedulerTimeDto = JSON.parseObject(jobOnline.getSchedulerTime(), SchedulerTimeDto.class);
			genTaskDependsByLocal(tasks, taskDepends, parentTasks, schedulerTimeDto.getMinute(), JobCycleTypeEnum.MINUTES);
		} else if (JobCycleTypeEnum.WEEK == cycleType) {
			genTaskDependsByLocal(tasks, taskDepends, parentTasks, 1, JobCycleTypeEnum.WEEK);
		} else if (JobCycleTypeEnum.MONTH == cycleType) {
			genTaskDependsByLocal(tasks, taskDepends, parentTasks, 1, JobCycleTypeEnum.MONTH);
		} 
		return taskDepends;
	}
	
	public static List<TaskDepends> genAllDependsByCycleType(JobOnline jobOnline, JobOnline parentJobOnline, List<Task> tasks, List<Task> parentTasks) {
		List<TaskDepends> taskDepends = Lists.newArrayList();
		if (CollectionUtils.isEmpty(tasks)) {
			return taskDepends;
		}
		JobCycleTypeEnum cycleType = jobOnline.getCycleType();
		JobCycleTypeEnum parentCycleType = parentJobOnline.getCycleType();
		if (JobCycleTypeEnum.DAY == cycleType) {
			if (JobCycleTypeEnum.DAY == parentCycleType) {
				genDependByOneDay(tasks, parentTasks, taskDepends);
			} else if (JobCycleTypeEnum.DAYT1 == parentCycleType) {
				genDependByOneDay(tasks, parentTasks, taskDepends);
			} else if (JobCycleTypeEnum.HOURS == parentCycleType) {
				SchedulerTimeDto schedulerTimeDto = JSON.parseObject(parentJobOnline.getSchedulerTime(), SchedulerTimeDto.class);
				genAllDependByDurAndDay(tasks, parentTasks, taskDepends, schedulerTimeDto.getHour(), JobCycleTypeEnum.HOURS);
			} else if (JobCycleTypeEnum.MINUTES == parentCycleType) {
				SchedulerTimeDto schedulerTimeDto = JSON.parseObject(parentJobOnline.getSchedulerTime(), SchedulerTimeDto.class);
				genAllDependByDurAndDay(tasks, parentTasks, taskDepends, schedulerTimeDto.getMinute(), JobCycleTypeEnum.MINUTES);
			}
		} else if (JobCycleTypeEnum.DAYT1 == cycleType) {
			if (JobCycleTypeEnum.DAY == parentCycleType) {
				genDependByOneDay(tasks, parentTasks, taskDepends);
			} else if (JobCycleTypeEnum.DAYT1 == parentCycleType) {
				genDependByOneDay(tasks, parentTasks, taskDepends);
			} else if (JobCycleTypeEnum.HOURS == parentCycleType) {
				SchedulerTimeDto schedulerTimeDto = JSON.parseObject(parentJobOnline.getSchedulerTime(), SchedulerTimeDto.class);
				genAllDependByDurAndDay(tasks, parentTasks, taskDepends, schedulerTimeDto.getHour(), JobCycleTypeEnum.HOURS);
			} else if (JobCycleTypeEnum.MINUTES == parentCycleType) {
				SchedulerTimeDto schedulerTimeDto = JSON.parseObject(parentJobOnline.getSchedulerTime(), SchedulerTimeDto.class);
				genAllDependByDurAndDay(tasks, parentTasks, taskDepends, schedulerTimeDto.getMinute(), JobCycleTypeEnum.MINUTES);
			}
		} else if (JobCycleTypeEnum.WEEK == cycleType) {
			if (CollectionUtils.isEmpty(parentTasks)) {
				return taskDepends;
			}
			if (JobCycleTypeEnum.DAY == parentCycleType) {
				genAllDependByWeek(tasks, parentTasks, taskDepends);
			} else if (JobCycleTypeEnum.DAYT1 == parentCycleType) {
				genAllDependByWeek(tasks, parentTasks, taskDepends);
			} else if (JobCycleTypeEnum.WEEK == parentCycleType) {
				genWeekDependByWeek(tasks, parentTasks, taskDepends);
			}
		} else if (JobCycleTypeEnum.MONTH == cycleType) {
			if (CollectionUtils.isEmpty(parentTasks)) {
				return taskDepends;
			}
			if (JobCycleTypeEnum.DAY == parentCycleType) {
				genAllDependByMonth(tasks, parentTasks, taskDepends);
			} else if (JobCycleTypeEnum.DAYT1 == parentCycleType) {
				genAllDependByMonth(tasks, parentTasks, taskDepends);
			} else if (JobCycleTypeEnum.MONTH == cycleType) {
				genDependByOneDay(tasks, parentTasks, taskDepends);
			}
		} else if (JobCycleTypeEnum.HOURS == cycleType) {
			SchedulerTimeDto schedulerTimeDto = JSON.parseObject(jobOnline.getSchedulerTime(), SchedulerTimeDto.class);
			if (JobCycleTypeEnum.HOURS == parentCycleType) {
				genAllDependByDur(tasks, parentTasks, taskDepends, schedulerTimeDto.getHour(), JobCycleTypeEnum.HOURS);
			} else if (JobCycleTypeEnum.MINUTES == parentCycleType) {
				genAllDependByDur(tasks, parentTasks, taskDepends, schedulerTimeDto.getHour(), JobCycleTypeEnum.HOURS);
			}
		} else if (JobCycleTypeEnum.MINUTES == cycleType) {
			SchedulerTimeDto schedulerTimeDto = JSON.parseObject(jobOnline.getSchedulerTime(), SchedulerTimeDto.class);
			if (JobCycleTypeEnum.HOURS == parentCycleType) {
				genAllDependByDur(tasks, parentTasks, taskDepends, schedulerTimeDto.getMinute(), JobCycleTypeEnum.MINUTES);
			} else if (JobCycleTypeEnum.MINUTES == parentCycleType) {
				genAllDependByDur(tasks, parentTasks, taskDepends, schedulerTimeDto.getMinute(), JobCycleTypeEnum.MINUTES);
			}
		}
		return taskDepends;
		
	}
	

	/**
	 * 生成同周期依赖
	 * 
	 * @param jobOnline 子任务
	 * @param parentJobOnline 父任务
	 * @param tasks 子节点列表
	 * @param parentTasks  父节点列表 （定时生成：实时生成+数据库查询（周，月）；页面操作都是数据库查询）
	 * @return
	 */
	public static List<TaskDepends> genSameDependsByCycleType(JobOnline jobOnline, JobOnline parentJobOnline, List<Task> tasks, List<Task> parentTasks) {
		List<TaskDepends> taskDepends = Lists.newArrayList();
		JobCycleTypeEnum cycleType = jobOnline.getCycleType();
		JobCycleTypeEnum parentCycleType = parentJobOnline.getCycleType();
		if (JobCycleTypeEnum.DAY == cycleType) {
			if (JobCycleTypeEnum.DAY == parentCycleType) {
				genDependByOneDay(tasks, parentTasks, taskDepends);
			} else if (JobCycleTypeEnum.DAYT1 == parentCycleType) {
				genDependByOneDay(tasks, parentTasks, taskDepends);
			} else if (JobCycleTypeEnum.HOURS == parentCycleType) {
				SchedulerTimeDto schedulerTimeDto = JSON.parseObject(parentJobOnline.getSchedulerTime(), SchedulerTimeDto.class);
				genSameDependByDurAndDay(tasks, parentTasks, taskDepends, schedulerTimeDto.getHour(), JobCycleTypeEnum.HOURS);
			} else if (JobCycleTypeEnum.MINUTES == parentCycleType) {
				SchedulerTimeDto schedulerTimeDto = JSON.parseObject(parentJobOnline.getSchedulerTime(), SchedulerTimeDto.class);
				genSameDependByDurAndDay(tasks, parentTasks, taskDepends, schedulerTimeDto.getHour(), JobCycleTypeEnum.HOURS);
			}
		} else if (JobCycleTypeEnum.DAYT1 == cycleType) {
			if (JobCycleTypeEnum.DAY == parentCycleType) {
				genDependByOneDay(tasks, parentTasks,  taskDepends);
			} else if (JobCycleTypeEnum.DAYT1 == parentCycleType) {
				genDependByOneDay(tasks, parentTasks, taskDepends);
			} else if (JobCycleTypeEnum.HOURS == parentCycleType) {
				SchedulerTimeDto schedulerTimeDto = JSON.parseObject(parentJobOnline.getSchedulerTime(), SchedulerTimeDto.class);
				genSameDependByDurAndDay(tasks, parentTasks, taskDepends, schedulerTimeDto.getHour(), JobCycleTypeEnum.HOURS);
			} else if (JobCycleTypeEnum.MINUTES == parentCycleType) {
				SchedulerTimeDto schedulerTimeDto = JSON.parseObject(parentJobOnline.getSchedulerTime(), SchedulerTimeDto.class);
				genSameDependByDurAndDay(tasks, parentTasks, taskDepends, schedulerTimeDto.getHour(), JobCycleTypeEnum.HOURS);
			}
		} else if (JobCycleTypeEnum.WEEK == cycleType) {
			if (JobCycleTypeEnum.DAY == parentCycleType) {
				genSameDependByWeek(tasks, parentTasks, taskDepends);
			} else if (JobCycleTypeEnum.DAYT1 == parentCycleType) {
				genSameDependByWeek(tasks, parentTasks, taskDepends);
			} else if (JobCycleTypeEnum.WEEK == parentCycleType) {
				genWeekDependByWeek(tasks, parentTasks, taskDepends);
			}
		} else if (JobCycleTypeEnum.MONTH == cycleType) {
			if (JobCycleTypeEnum.DAY == parentCycleType) {
				genSameDependByMonth(tasks, parentTasks, taskDepends);
			} else if (JobCycleTypeEnum.DAYT1 == parentCycleType) {
				genSameDependByMonth(tasks, parentTasks, taskDepends);
			} else if (JobCycleTypeEnum.MONTH == cycleType) {
				genDependByOneDay(tasks, parentTasks, taskDepends);
			}
		} else if (JobCycleTypeEnum.HOURS == cycleType) {
			SchedulerTimeDto schedulerTimeDto = JSON.parseObject(jobOnline.getSchedulerTime(), SchedulerTimeDto.class);
			if (JobCycleTypeEnum.HOURS == parentCycleType) {
				genSameDependByDur(tasks, parentTasks,  taskDepends, schedulerTimeDto.getHour(), JobCycleTypeEnum.HOURS);
			} else if (JobCycleTypeEnum.MINUTES == parentCycleType) {
				genSameDependByDur(tasks, parentTasks, taskDepends, schedulerTimeDto.getHour(), JobCycleTypeEnum.HOURS);
			} 
		} else if (JobCycleTypeEnum.MINUTES == cycleType) {
			SchedulerTimeDto schedulerTimeDto = JSON.parseObject(jobOnline.getSchedulerTime(), SchedulerTimeDto.class);
			if (JobCycleTypeEnum.HOURS == parentCycleType) {
				genSameDependByDur(tasks, parentTasks, taskDepends, schedulerTimeDto.getMinute(), JobCycleTypeEnum.MINUTES);
			} else if (JobCycleTypeEnum.MINUTES == parentCycleType) {
				genSameDependByDur(tasks, parentTasks, taskDepends, schedulerTimeDto.getMinute(), JobCycleTypeEnum.MINUTES);
			}
		}
		return taskDepends;
	}


	/**
	 * 生成上一周期依赖
	 * TODO 暂时只支持天任务依赖天任务的逻辑
	 * @param jobOnline 子任务
	 * @param parentJobOnline 父任务
	 * @param tasks 子节点列表
	 * @param parentTasks  父节点列表 （定时生成：实时生成+数据库查询（周，月）；页面操作都是数据库查询）
	 * @return
	 */
	public static List<TaskDepends> genPrevDependsByCycleType(JobOnline jobOnline, JobOnline parentJobOnline, List<Task> tasks, List<Task> parentTasks) {
		List<TaskDepends> taskDepends = Lists.newArrayList();
		JobCycleTypeEnum cycleType = jobOnline.getCycleType();
		JobCycleTypeEnum parentCycleType = parentJobOnline.getCycleType();
		if (JobCycleTypeEnum.DAY == cycleType) {
			if (JobCycleTypeEnum.DAY == parentCycleType) {
				genPrevDependByDay(tasks, parentTasks, taskDepends);
			}
		}
		return taskDepends;
	}


	private static void genAllDependByDur(List<Task> tasks, List<Task> parentTasks, List<TaskDepends> taskDepends, Integer num, JobCycleTypeEnum jobCycleTypeEnum) {
		if (CollectionUtils.isEmpty(tasks) || CollectionUtils.isEmpty(parentTasks)) {
			return;
		}
		// task必须是子
		for (Task task : tasks) {
			Date nextTaskTime = jobCycleTypeEnum.getNewDateByPlusNum(task.getTaskTime(), num);
			// 取范围内所有的id, 左闭右开
			for (Task parentTask : parentTasks) {
				Date parentTaskTime = parentTask.getTaskTime();
				if ((parentTaskTime.after(task.getTaskTime()) || parentTaskTime.equals(task.getTaskTime()))
						&& parentTaskTime.before(nextTaskTime)) {
					log.debug("parent:{}, sub:{}, nextSub:{}"
							, DateUtil.format(parentTaskTime, DateFormatPattern.YYYY_MM_DD_HH_MM_SS)
							, DateUtil.format(task.getTaskTime(), DateFormatPattern.YYYY_MM_DD_HH_MM_SS)
							, DateUtil.format(nextTaskTime, DateFormatPattern.YYYY_MM_DD_HH_MM_SS));
					taskDepends.add(addTaskDepends(task.getId(), parentTask.getId(), task.getCreateUser()));
				}
			}
		}
	}

	private static void genAllDependByWeek(List<Task> tasks, List<Task> parentTasks, List<TaskDepends> taskDepends) {
		if (CollectionUtils.isEmpty(tasks) || CollectionUtils.isEmpty(parentTasks)) {
			return;
		}
		for (Task task : tasks) {
			Date date = DateUtil.getDate(DateUtil.getLocalDate(task.getTaskTime()).plusWeeks(1));
			for (Task parentTask : parentTasks) {
				Date parentTaksTime = parentTask.getTaskTime();
				log.debug("sub:{}, parent:{}"
						, DateUtil.format(task.getTaskTime(), DateFormatPattern.YYYY_MM_DD_HH_MM_SS)
						, DateUtil.format(parentTaksTime, DateFormatPattern.YYYY_MM_DD_HH_MM_SS));
				if ((parentTaksTime.after(task.getTaskTime()) || parentTaksTime.equals(task.getTaskTime()))
						&& parentTaksTime.before(date)) {
					log.debug("sub:{}, parent:{}"
							, DateUtil.format(task.getTaskTime(), DateFormatPattern.YYYY_MM_DD_HH_MM_SS)
							, DateUtil.format(parentTaksTime, DateFormatPattern.YYYY_MM_DD_HH_MM_SS));
					taskDepends.add(addTaskDepends(task.getId(), parentTask.getId(), task.getCreateUser()));
				}
			}
		}
	}
	
	private static void genWeekDependByWeek(List<Task> tasks, List<Task> parentTasks, List<TaskDepends> taskDepends) {
		if (CollectionUtils.isEmpty(tasks) || CollectionUtils.isEmpty(parentTasks)) {
			return;
		}
		for (Task task : tasks) {
			Date taskTime = task.getTaskTime();
			for (Task parentTask : parentTasks) {
				Date date = DateUtil.getDate(DateUtil.getLocalDate(parentTask.getTaskTime()).plusWeeks(1));
				Date parentTaksTime = parentTask.getTaskTime();
				if ((taskTime.after(parentTaksTime) || taskTime.equals(parentTaksTime))
						&& taskTime.before(date)) {
					log.debug("sub:{}, parent:{}"
							, DateUtil.format(taskTime, DateFormatPattern.YYYY_MM_DD_HH_MM_SS)
							, DateUtil.format(parentTaksTime, DateFormatPattern.YYYY_MM_DD_HH_MM_SS));
					taskDepends.add(addTaskDepends(task.getId(), parentTask.getId(), task.getCreateUser()));
				}
			}
		}
	}
	
	private static void genAllDependByMonth(List<Task> tasks, List<Task> parentTasks, List<TaskDepends> taskDepends) {
		if (CollectionUtils.isEmpty(tasks) || CollectionUtils.isEmpty(parentTasks)) {
			return;
		}
		for (Task task : tasks) {
			Date startDate = task.getTaskTime();
			Date endDate = DateUtil.getDate(DateUtil.getLocalDate(task.getTaskTime()).plusMonths(1));
			for (Task parentTask : parentTasks) {
				if ((parentTask.getTaskTime().after(startDate) 
						|| parentTask.getTaskTime().equals(startDate))
						&& parentTask.getTaskTime().before(endDate)) {
					log.debug("all, sub:{}, parent:{}"
							, DateUtil.format(task.getTaskTime(), DateFormatPattern.YYYY_MM_DD_HH_MM_SS)
							, DateUtil.format(parentTask.getTaskTime(), DateFormatPattern.YYYY_MM_DD_HH_MM_SS));
					taskDepends.add(addTaskDepends(task.getId(), parentTask.getId(), task.getCreateUser()));
				}
			}
		}
	}

	private static void genSameDependByDurAndDay(List<Task> tasks, List<Task> parentTasks, List<TaskDepends> taskDepends,
			Integer num, JobCycleTypeEnum jobCycleTypeEnum) {
		if (CollectionUtils.isEmpty(tasks) || CollectionUtils.isEmpty(parentTasks)) {
			return;
		}
		for (Task task : tasks) {
			Date endDate = DateUtil.getDate(DateUtil.getLocalDate(task.getTaskTime()).plusDays(1));
			Date startDate = task.getTaskTime();
			// 取范围内最大的id, 左闭右开
			Task maxTask = null;
			for (Task parentTask : parentTasks) {
				if ((parentTask.getTaskTime().after(startDate) || parentTask.getTaskTime().equals(startDate))
						&& parentTask.getTaskTime().before(endDate)) {
					
					if (maxTask == null) {
						maxTask = parentTask;
					}
					// 比大小
					if (parentTask.getTaskTime().after(maxTask.getTaskTime())) {
						maxTask = parentTask;
					}
				}
			}
			if (maxTask != null) {
				log.debug("parentTask:{}, pre:{}"
						, DateUtil.format(maxTask.getTaskTime(), DateFormatPattern.YYYY_MM_DD_HH_MM_SS)
						, DateUtil.format(task.getTaskTime(), DateFormatPattern.YYYY_MM_DD_HH_MM_SS));
				taskDepends.add(addTaskDepends(task.getId(), maxTask.getId(), task.getCreateUser()));
			}
		}
	}
	
	private static void genAllDependByDurAndDay(List<Task> tasks, List<Task> parentTasks, List<TaskDepends> taskDepends,
			Integer num, JobCycleTypeEnum jobCycleTypeEnum) {
		if (CollectionUtils.isEmpty(tasks) || CollectionUtils.isEmpty(parentTasks)) {
			return;
		}
		for (Task task : tasks) {
			Date endDate = DateUtil.getDate(DateUtil.getLocalDate(task.getTaskTime()).plusDays(1));
			Date startDate = task.getTaskTime();
			// 取范围内最大的id, 左闭右开
			for (Task parentTask : parentTasks) {
				if ((parentTask.getTaskTime().after(startDate) || parentTask.getTaskTime().equals(startDate))
						&& parentTask.getTaskTime().before(endDate)) {
					log.debug("parentTask:{}, pre:{}"
							, DateUtil.format(parentTask.getTaskTime(), DateFormatPattern.YYYY_MM_DD_HH_MM_SS)
							, DateUtil.format(task.getTaskTime(), DateFormatPattern.YYYY_MM_DD_HH_MM_SS));
					taskDepends.add(addTaskDepends(task.getId(), parentTask.getId(), task.getCreateUser()));
				}
			}
		}
	}


	private static void genDependByOneDay(List<Task> tasks,  List<Task> parentTasks, List<TaskDepends> taskDepends) {
		if (CollectionUtils.isEmpty(tasks) || CollectionUtils.isEmpty(parentTasks)) {
			return;
		}
		for (Task task : tasks) {
			for (Task parentTask : parentTasks) {
				if (task.getTaskTime().equals(parentTask.getTaskTime())) {
					log.debug("sub:{}, parent:{}"
							, DateUtil.format(task.getTaskTime(), DateFormatPattern.YYYY_MM_DD_HH_MM_SS)
							, DateUtil.format(parentTask.getTaskTime(), DateFormatPattern.YYYY_MM_DD_HH_MM_SS));
					taskDepends.add(addTaskDepends(task.getId(), parentTask.getId(), task.getCreateUser()));
				}
			}
		}
	}

	private static void genPrevDependByDay(List<Task> tasks,  List<Task> parentTasks, List<TaskDepends> taskDepends) {
		if (CollectionUtils.isEmpty(tasks) || CollectionUtils.isEmpty(parentTasks)) {
			return;
		}
		for (Task task : tasks) {
			// 计算昨天
			Date date = DateUtil.getDate(DateUtil.getLocalDate(task.getTaskTime()).minusDays(1));
			for (Task parentTask : parentTasks) {
				if (date.equals(parentTask.getTaskTime())) {
					log.debug("sub:{}, parent:{}"
							, DateUtil.format(task.getTaskTime(), DateFormatPattern.YYYY_MM_DD_HH_MM_SS)
							, DateUtil.format(parentTask.getTaskTime(), DateFormatPattern.YYYY_MM_DD_HH_MM_SS));
					taskDepends.add(addTaskDepends(task.getId(), parentTask.getId(), task.getCreateUser()));
				}
			}
		}
	}
	
	private static void genSameDependByWeek(List<Task> tasks,  List<Task> parentTasks, List<TaskDepends> taskDepends) {
		if (CollectionUtils.isEmpty(tasks) || CollectionUtils.isEmpty(parentTasks)) {
			return;
		}
		for (Task task : tasks) {
			Date date = DateUtil.getDate(DateUtil.getLocalDate(task.getTaskTime()).plusWeeks(1).minusDays(1));
			for (Task parentTask : parentTasks) {
				if (date.equals(parentTask.getTaskTime())) {
					log.debug("sub:{}, parent:{}"
							, DateUtil.format(task.getTaskTime(), DateFormatPattern.YYYY_MM_DD_HH_MM_SS)
							, DateUtil.format(parentTask.getTaskTime(), DateFormatPattern.YYYY_MM_DD_HH_MM_SS));
					taskDepends.add(addTaskDepends(task.getId(), parentTask.getId(), task.getCreateUser()));
				}
			}
		}
	}
	
	private static void genSameDependByDur(List<Task> tasks, List<Task> parentTasks, List<TaskDepends> taskDepends, int num, JobCycleTypeEnum jobCycleTypeEnum) {
		if (CollectionUtils.isEmpty(tasks) || CollectionUtils.isEmpty(parentTasks)) {
			return;
		}
		for (Task task : tasks) {
			Date nextTask = jobCycleTypeEnum.getNewDateByPlusNum(task.getTaskTime(), num);
			// 取范围内最大的id, 左闭右开
			Task maxTask = null;
			for (Task parentTask : parentTasks) {
				if ((parentTask.getTaskTime().after(task.getTaskTime()) || parentTask.getTaskTime().equals(task.getTaskTime()))
						&& parentTask.getTaskTime().before(nextTask)) {
					
					if (maxTask == null) {
						maxTask = parentTask;
					}
					// 比大小
					if (parentTask.getTaskTime().after(maxTask.getTaskTime())) {
						maxTask = parentTask;
					}
				}
			}
			if (maxTask != null) {
				log.debug("parentTask:{}, task:{}, nextTask:{}"
						, DateUtil.format(maxTask.getTaskTime(), DateFormatPattern.YYYY_MM_DD_HH_MM_SS)
						, DateUtil.format(task.getTaskTime(), DateFormatPattern.YYYY_MM_DD_HH_MM_SS)
						, DateUtil.format(nextTask, DateFormatPattern.YYYY_MM_DD_HH_MM_SS));
				taskDepends.add(addTaskDepends(task.getId(), maxTask.getId(), task.getCreateUser()));
			}
		}
	}
	
	private static void genSameDependByMonth(List<Task> tasks, List<Task> parentTasks,  List<TaskDepends> taskDepends) {
		if (CollectionUtils.isEmpty(tasks) || CollectionUtils.isEmpty(parentTasks)) {
			return;
		}
		for (Task task : tasks) {
			Date date = DateUtil.getDate(DateUtil.getLocalDate(task.getTaskTime()).plusMonths(1).minusDays(1));
			for (Task parentTask : parentTasks) {
				if (date.equals(parentTask.getTaskTime())) {
					log.debug("same, sub:{}, parent:{}"
							, DateUtil.format(task.getTaskTime(), DateFormatPattern.YYYY_MM_DD_HH_MM_SS)
							, DateUtil.format(parentTask.getTaskTime(), DateFormatPattern.YYYY_MM_DD_HH_MM_SS));
					taskDepends.add(addTaskDepends(task.getId(), parentTask.getId(), task.getCreateUser()));
				}
			}
		}
	}
	
	
	private static void generateDependsBySelf(List<Task> tasks, List<TaskDepends> taskDepends) {
		for (int i = 1; i < tasks.size(); i++) {
			Task task = tasks.get(i);
			if (task == null) {
				continue;
			}
			if (task.getId().equals(tasks.get(i-1).getId())) {
				continue;
			}
			taskDepends.add(addTaskDepends(task.getId(), tasks.get(i-1).getId(), task.getCreateUser()));
		}
	}
	
	private static void genTaskDependsByLocal(List<Task> tasks, List<TaskDepends> taskDepends, List<Task> parentTasks, Integer num, JobCycleTypeEnum jobCycleType) {
		Task newTask = tasks.get(tasks.size()-1);
		Task oldTask = tasks.get(0);
		Date newDate = jobCycleType.getNewDateByPlusNum(newTask.getTaskTime(), num);
		Date oldDate = jobCycleType.getNewDateByMinusNum(oldTask.getTaskTime(), num);
		generateTaskDepends(taskDepends, parentTasks, oldTask, oldDate, newTask, newDate);
	}

	private static void generateTaskDepends(List<TaskDepends> taskDepends, List<Task> parentTasks, Task oldTask, Date oldDay,
			Task newTask, Date newDay) {
		if (CollectionUtils.isEmpty(parentTasks) || oldTask == null) {
			return;
		}
		for (Task parentTask : parentTasks) {
			if (parentTask.getTaskTime().equals(oldDay)) {
				log.debug("sub:{}, parent:{}, old:{}, new:{}"
						, DateUtil.format(oldTask.getTaskTime(), DateFormatPattern.YYYY_MM_DD_HH_MM_SS)
						, DateUtil.format(parentTask.getTaskTime(), DateFormatPattern.YYYY_MM_DD_HH_MM_SS)
						, DateUtil.format(oldDay, DateFormatPattern.YYYY_MM_DD_HH_MM_SS)
						, DateUtil.format(newDay, DateFormatPattern.YYYY_MM_DD_HH_MM_SS));
				if (oldTask.getId().equals(parentTask.getId())) {
					continue;
				}
				taskDepends.add(addTaskDepends(oldTask.getId(), parentTask.getId(), oldTask.getCreateUser()));
			}
		}
	}

	public static TaskDepends addTaskDepends(Long taskId, Long parentId, Long userId) {
		TaskDepends taskDepends = new TaskDepends();
		taskDepends.setTaskId(taskId);
		taskDepends.setParentId(parentId);
		taskDepends.setCreateUser(userId);
		taskDepends.setUpdateUser(userId);
		return taskDepends;
	}
	
	public static List<Task> mergeTaskById(List<Task> tasks1, List<Task> tasks2, List<Task> tasks3) {
		List<Task> allTasks = Lists.newArrayList();
		Set<Long> taskIds = Sets.newHashSet();
		distinctTasks(tasks1, allTasks, taskIds);
		distinctTasks(tasks2, allTasks, taskIds);
		distinctTasks(tasks3, allTasks, taskIds);
		return allTasks;
	}

	private static void distinctTasks(List<Task> tasks, List<Task> allTasks, Set<Long> taskIds) {
		if (CollectionUtils.isEmpty(tasks)) {
			return;
		}
		for (Task task : tasks) {
			if (taskIds.contains(task.getId())) {
				continue;
			}
			allTasks.add(task);
			taskIds.add(task.getId());
		}
	}
}
