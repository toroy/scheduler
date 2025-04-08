package com.clubfactory.platform.scheduler.dal.enums;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.clubfactory.platform.common.constant.DateFormatPattern;
import com.clubfactory.platform.common.util.Assert;
import com.clubfactory.platform.common.util.DateUtil;
import com.clubfactory.platform.scheduler.dal.dto.SchedulerTimeDto;
import com.clubfactory.platform.scheduler.dal.dto.TaskTimeDto;
import com.google.common.collect.Lists;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum JobCycleTypeEnum implements IEnum {

	REAL_TIME("实时任务") {
		@Override
		public List<TaskTimeDto> listTaskTimes(SchedulerTimeDto time, Date nowDate) {
			return listTaskTimes(null, null, null);
		}

		@Override
		public List<TaskTimeDto> listTaskTimes(SchedulerTimeDto time, Date startDate, Date endDate) {
			TaskTimeDto taskTimeDto = new TaskTimeDto();
			taskTimeDto.setStartTime(new Date());
			return Lists.newArrayList(Lists.newArrayList(Lists.newArrayList(taskTimeDto)));
		}

		@Override
		public String getSchedulerTime(SchedulerTimeDto time) {
			return "实时";
		}

		@Override
		public Boolean isNotAllowCreateByDay(SchedulerTimeDto time, Date nowDate) {
			return false;
		}

		@Override
		public Date getNewDateByMinusNum(Date date, Integer num) {
			return null;
		}

		@Override
		public Date getNewDateByPlusNum(Date date, Integer num) {
			return null;
		}
	},
	MINUTES("分钟任务") {
		@Override
		public List<TaskTimeDto> listTaskTimes(SchedulerTimeDto time, Date nowDate) {
			Date endDate = DateUtil.getMaxOfDay(nowDate);
			// 将调度时间转换为对应的实例时间
			Date startDate = DateUtil.getMinOfDay(nowDate);
			long timestamp = startDate.getTime();
			int min = Optional.ofNullable(time.getMinute()).orElse(5);
			int sec = Optional.ofNullable(time.getSecond()).orElse(0);
			
			// 调度时间
			long dateTimestamp = timestamp +  min * MINUTE_VAL + sec * SECOND_VAL;
			while (dateTimestamp < nowDate.getTime()) {
				dateTimestamp = dateTimestamp + min * MINUTE_VAL;
			}
			timestamp = dateTimestamp - min * MINUTE_VAL - sec * SECOND_VAL;
			return this.listTaskTimes(time, new Date(timestamp), endDate);
		}
		
		public List<TaskTimeDto> listTaskTimes(SchedulerTimeDto time, Date startDate, Date endDate) {
			long timestamp = DateUtil.getMinOfDay(startDate).getTime();
			long startTimestamp = startDate.getTime();
			int min = Optional.ofNullable(time.getMinute()).orElse(5);
			int sec = Optional.ofNullable(time.getSecond()).orElse(0);
			long maxDay = endDate.getTime();
			
			if (min <= 0) {
				return Lists.newArrayList();
			}
			
			List<TaskTimeDto> dates = Lists.newLinkedList();
			// 实例时间小于当天最晚时间
			while (maxDay > timestamp) {
				long dateTimestamp = timestamp + min * MINUTE_VAL + sec * SECOND_VAL;
				// 开始时间大于等于当前时间 并且 小于等于结束时间
				if (startTimestamp <= timestamp
						&& timestamp <= maxDay) {
					TaskTimeDto timeDto = new TaskTimeDto();
					timeDto.setTaskTime(new Date(timestamp));
					timeDto.setStartTime(new Date(dateTimestamp));
					dates.add(timeDto);
				}
				timestamp = min * MINUTE_VAL + timestamp;
			}
			return dates;
		}

		@Override
		public String getSchedulerTime(SchedulerTimeDto time) {
			return String.format("%s */%s * * * ?", getValue(time.getSecond()), getValue(time.getMinute()));
		}

		@Override
		public Boolean isNotAllowCreateByDay(SchedulerTimeDto time, Date nowDate) {
			return false;
		}

		@Override
		public Date getNewDateByMinusNum(Date date, Integer num) {
			LocalDateTime localDate = DateUtil.getLocalDateTime(date);
			LocalDateTime minusLocalDate = localDate.minusMinutes(num);
			// 算昨日最大的周期
			if (num > 0) {
				LocalDateTime minDay = localDate.with(LocalTime.MIN);
				if (minusLocalDate.isBefore(minDay)) {
					int cycleNum = 24 * 60 % num == 0 ? 24 * 60 / num - 1 : 24 * 60 / num;
					LocalDateTime maxOfDayDur = minDay.minusDays(1).plusHours(cycleNum * num);
					return DateUtil.getDate(maxOfDayDur);
				}
			}
			// 算明日最小的周期
			if (num < 0) {
				LocalDateTime minOfDayDurPlus = localDate.with(LocalTime.MIN).plusDays(1);
				if (minusLocalDate.isAfter(minOfDayDurPlus)) {
					LocalDateTime minOfDayDur = minOfDayDurPlus;
					return DateUtil.getDate(minOfDayDur);
				}
			}
			return DateUtil.getDate(minusLocalDate);
		}

		@Override
		public Date getNewDateByPlusNum(Date date, Integer num) {
			return this.getNewDateByMinusNum(date, -num);
		}
	},
	HOURS("小时任务") {
		@Override
		public List<TaskTimeDto> listTaskTimes(SchedulerTimeDto time, Date nowDate) {
			// 将调度时间转换为对应的实例时间
			Date endDate = DateUtil.getMaxOfDay(nowDate);
			Date startDate = DateUtil.getMinOfDay(nowDate);
			long timestamp = startDate.getTime();
			int hour = Optional.ofNullable(time.getHour()).orElse(1);
			int min = Optional.ofNullable(time.getMinute()).orElse(0);
			int sec = Optional.ofNullable(time.getSecond()).orElse(0);
			
			// 调度时间
			long dateTimestamp = timestamp + hour * HOUR_VAL + min * MINUTE_VAL + sec * SECOND_VAL;
			while (dateTimestamp < nowDate.getTime()) {
				dateTimestamp = dateTimestamp + hour * HOUR_VAL;
			}
			timestamp = dateTimestamp - hour * HOUR_VAL - min * MINUTE_VAL - sec * SECOND_VAL;
			return this.listTaskTimes(time, new Date(timestamp), endDate);
		}

		@Override
		public String getSchedulerTime(SchedulerTimeDto time) {
			return String.format("%s %s */%s * * ?", getValue(time.getSecond()), getValue(time.getMinute()), getValue(time.getHour()));
		}

		@Override
		public Boolean isNotAllowCreateByDay(SchedulerTimeDto time, Date nowDate) {
			return false;
		}

		@Override
		public List<TaskTimeDto> listTaskTimes(SchedulerTimeDto time, Date startDate, Date endDate) {
			long timestamp = DateUtil.getMinOfDay(startDate).getTime();
			long startTimestamp = startDate.getTime();
			int hour = Optional.ofNullable(time.getHour()).orElse(1);
			int min = Optional.ofNullable(time.getMinute()).orElse(0);
			int sec = Optional.ofNullable(time.getSecond()).orElse(0);
			long maxDay = endDate.getTime();
			
			if (hour <= 0) {
				return Lists.newArrayList();
			}
			
			List<TaskTimeDto> dates = Lists.newLinkedList();
			while (maxDay > timestamp ) {
				long dateTimestamp = timestamp + hour * HOUR_VAL + min * MINUTE_VAL + sec * SECOND_VAL;
				// 开始时间大于等于当前时间 并且 小于等于结束时间
				if (timestamp <= maxDay
						&& timestamp >= startTimestamp) {
					TaskTimeDto timeDto = new TaskTimeDto();
					timeDto.setTaskTime(new Date(timestamp));
					timeDto.setStartTime(new Date(dateTimestamp));
					dates.add(timeDto);
				}
				timestamp = hour * HOUR_VAL + timestamp;
			}
			return dates;
		}

		// 算明日最小的小时周期，如果今天最大的小时周期 + 周期值 > 明天凌晨，改算法
		// 新算法->今日凌晨 减去 1 天 = 明日最小的小时周期
		// 算昨日最大的小时周期，如果今天最小的小时周期 - 周期值 < 今天凌晨，改算法
		// 昨日凌晨 + 周期 * 周期数 = 昨日的最大小时周期
		@Override
		public Date getNewDateByMinusNum(Date date, Integer num) {
			LocalDateTime localDate = DateUtil.getLocalDateTime(date);
			LocalDateTime minusLocalDate = localDate.minusHours(num);
			// 算昨日最大的周期
			if (num > 0) {
				LocalDateTime minDay = localDate.with(LocalTime.MIN);
				if (minusLocalDate.isBefore(minDay)) {
					// 当天的最大周期日期
					int cycleNum = 24 % num == 0 ? 24 / num - 1 : 24 / num;
					LocalDateTime maxOfDayDur = minDay.minusDays(1).plusHours(cycleNum * num);
					return DateUtil.getDate(maxOfDayDur);
				}
			}
			// 算明日最小的周期
			if (num < 0) {
				LocalDateTime minOfDayDurPlus = localDate.with(LocalTime.MIN).plusDays(1);
				if (minusLocalDate.isAfter(minOfDayDurPlus)) {
					LocalDateTime minOfDayDur = minOfDayDurPlus;
					return DateUtil.getDate(minOfDayDur);
				}
			}
			return DateUtil.getDate(minusLocalDate);
		}

		@Override
		public Date getNewDateByPlusNum(Date date, Integer num) {
			return this.getNewDateByMinusNum(date, -num);
		}

	},
	DAY("天任务") {
		@Override
		public List<TaskTimeDto> listTaskTimes(SchedulerTimeDto time, Date nowDate) {
			int hour = Optional.ofNullable(time.getHour()).orElse(0);
			int min = Optional.ofNullable(time.getMinute()).orElse(0);
			Date startDate = DateUtil.getMinOfDay(nowDate);
			long dateTimestamp = startDate.getTime() + hour * HOUR_VAL + min * MINUTE_VAL;
			
			if (nowDate.getTime() <= dateTimestamp) {
				Date endDate = DateUtil.getMaxOfDay(nowDate);
				// 调度时间转实例时间
				return this.listTaskTimes(time, startDate, endDate);
			}
			return Lists.newArrayList();
		}

		@Override
		public String getSchedulerTime(SchedulerTimeDto time) {
			return String.format("%s %s %s * * ?", getValue(time.getSecond()), getValue(time.getMinute()), getValue(time.getHour()));
		}

		@Override
		public Boolean isNotAllowCreateByDay(SchedulerTimeDto time, Date nowDate) {
			return false;
		}

		@Override
		public List<TaskTimeDto> listTaskTimes(SchedulerTimeDto time, Date startDate, Date endDate) {
			long timestamp = DateUtil.getMinOfDay(startDate).getTime();
			long startTimestamp = startDate.getTime();
			int hour = Optional.ofNullable(time.getHour()).orElse(0);
			int min = Optional.ofNullable(time.getMinute()).orElse(0);
			long maxDay = endDate.getTime();
			
			List<TaskTimeDto> dates = Lists.newLinkedList();
			while (maxDay > timestamp) {
				long dateTimestamp = timestamp + hour * HOUR_VAL + min * MINUTE_VAL;
				// 开始时间大于等于当前时间 并且 小于等于结束时间
				if (startTimestamp <= timestamp
						&& timestamp <= maxDay) {
					TaskTimeDto timeDto = new TaskTimeDto();
					timeDto.setTaskTime(new Date(timestamp));
					timeDto.setStartTime(new Date(dateTimestamp));
					dates.add(timeDto);
				}
				timestamp = DAY_VAL + timestamp;
			}
			return dates;
		}

		@Override
		public Date getNewDateByMinusNum(Date date, Integer num) {
			LocalDate localDate = DateUtil.getLocalDate(date);
			LocalDate minusLocalDate = localDate.minusDays(num);;
			return DateUtil.getDate(minusLocalDate);
		}

		@Override
		public Date getNewDateByPlusNum(Date date, Integer num) {
			return this.getNewDateByMinusNum(date, -num);
		}
	},
	DAYT1("T+1任务") {
		@Override
		public List<TaskTimeDto> listTaskTimes(SchedulerTimeDto time, Date nowDate) {
			Date endDate = DateUtil.getMaxOfDay(nowDate);
			Date startDate = DateUtil.getMinOfDay(nowDate);
			return this.listTaskTimes(time, startDate, endDate);
		}

		@Override
		public String getSchedulerTime(SchedulerTimeDto time) {
			return String.format("%s %s %s * * ?", getValue(time.getSecond()), getValue(time.getMinute()), getValue(time.getHour()));
		}

		@Override
		public Boolean isNotAllowCreateByDay(SchedulerTimeDto time, Date nowDate) {
			return false;
		}

		@Override
		public List<TaskTimeDto> listTaskTimes(SchedulerTimeDto time, Date startDate, Date endDate) {
			long timestamp = DateUtil.getMinOfDay(startDate).getTime();
			long startTimestamp = startDate.getTime();
			int hour = Optional.ofNullable(time.getHour()).orElse(0);
			int min = Optional.ofNullable(time.getMinute()).orElse(0);
			long maxDay = endDate.getTime();
			
			List<TaskTimeDto> dates = Lists.newLinkedList();
			while (maxDay > timestamp) {
				long dateTimestamp = timestamp  + DAY_VAL + hour * HOUR_VAL + min * MINUTE_VAL;
				// 开始时间大于等于当前时间 并且 小于等于结束时间
				if (startTimestamp <= timestamp
						&& timestamp <= maxDay) {
					TaskTimeDto timeDto = new TaskTimeDto();
					timeDto.setTaskTime(new Date(timestamp));
					timeDto.setStartTime(new Date(dateTimestamp));
					dates.add(timeDto);
				}
				timestamp = DAY_VAL + timestamp;
			}
			return dates;
		}

		@Override
		public Date getNewDateByMinusNum(Date date, Integer num) {
			LocalDate localDate = DateUtil.getLocalDate(date);
			LocalDate minusLocalDate = localDate.minusDays(num);;
			return DateUtil.getDate(minusLocalDate);
		}

		@Override
		public Date getNewDateByPlusNum(Date date, Integer num) {
			return this.getNewDateByMinusNum(date, -num);
		}
	},
	WEEK("周任务") {
		@Override
		public List<TaskTimeDto> listTaskTimes(SchedulerTimeDto time, Date nowDate) {
			Date endDate = DateUtil.getMaxOfDay(nowDate);
			return this.listTaskTimes(time, nowDate, endDate);
		}

		@Override
		public String getSchedulerTime(SchedulerTimeDto time) {
			return String.format("%s %s %s * * %s", getValue(time.getSecond()), getValue(time.getMinute()), getValue(time.getHour()), getValue(time.getWeek()));
		}

		@Override
		public Boolean isNotAllowCreateByDay(SchedulerTimeDto time, Date nowDate) {
			int dayOfWeek = DateUtil.getLocalDate(nowDate).getDayOfWeek().getValue();
			if (time.getWeek().equals(dayOfWeek)) {
				return false;
			} else {
				return true;
			}
		}

		@Override
		public List<TaskTimeDto> listTaskTimes(SchedulerTimeDto time, Date startDate, Date endDate) {
			long timestamp = DateUtil.getMinOfDay(startDate).getTime();
			
			int hour = Optional.ofNullable(time.getHour()).orElse(0);
			int min = Optional.ofNullable(time.getMinute()).orElse(0);
			int sec = Optional.ofNullable(time.getSecond()).orElse(0);
			long maxDay = endDate.getTime();
			long week = time.getWeek();
			
			List<TaskTimeDto> dates = Lists.newLinkedList();
			while(endDate.getTime() > timestamp) {
				long dateTimestamp = timestamp + hour * HOUR_VAL + min * MINUTE_VAL + sec * SECOND_VAL;
				LocalDate localDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(dateTimestamp), ZoneId.systemDefault()).toLocalDate();
				long tasktimestamp = timestamp  - 7 * DAY_VAL;
				if (tasktimestamp <= maxDay
						&& localDate.getDayOfWeek().getValue() == week) {
					TaskTimeDto timeDto = new TaskTimeDto();
					timeDto.setTaskTime(new Date(tasktimestamp));
					timeDto.setStartTime(new Date(dateTimestamp));
					dates.add(timeDto);
				}
				timestamp = DAY_VAL + timestamp;
			}
			return dates;
		}

		@Override
		public Date getNewDateByMinusNum(Date date, Integer num) {
			LocalDate localDate = DateUtil.getLocalDate(date);
			LocalDate minusLocalDate = localDate.minusWeeks(num);;
			return DateUtil.getDate(minusLocalDate);
		}

		@Override
		public Date getNewDateByPlusNum(Date date, Integer num) {
			return this.getNewDateByMinusNum(date, -num);
		}
	},
	MONTH("月任务") {
		@Override
		public List<TaskTimeDto> listTaskTimes(SchedulerTimeDto time, Date nowDate) {
			Date endDate = DateUtil.getMaxOfDay(nowDate);
			return this.listTaskTimes(time, nowDate, endDate);
		}

		@Override
		public String getSchedulerTime(SchedulerTimeDto time) {
			return String.format("%s %s %s %s * ?", getValue(time.getSecond()), getValue(time.getMinute()), getValue(time.getHour()), getValue(time.getDay()));
		}

		@Override
		public Boolean isNotAllowCreateByDay(SchedulerTimeDto time, Date nowDate) {
			int monthOfWeek = DateUtil.getLocalDate(nowDate).getDayOfMonth();
			if (time.getDay().equals(monthOfWeek)) {
				return false;
			} else {
				return true;
			}
		}

		@Override
		public List<TaskTimeDto> listTaskTimes(SchedulerTimeDto time, Date startDate, Date endDate) {
			long timestamp = DateUtil.getMinOfDay(startDate).getTime();
			int hour = Optional.ofNullable(time.getHour()).orElse(0);
			int min = Optional.ofNullable(time.getMinute()).orElse(0);
			int day = Optional.ofNullable(time.getDay()).orElse(1);
			long maxDay = endDate.getTime();
			
			List<TaskTimeDto> dates = Lists.newLinkedList();
			while(endDate.getTime() > timestamp) {
				// 调度时间
				long dateTimestamp = timestamp + hour * HOUR_VAL + min * MINUTE_VAL;
				LocalDate localDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(dateTimestamp), ZoneId.systemDefault()).toLocalDate();
				if (timestamp <= maxDay
						&& localDate.getDayOfMonth() == day) {
					TaskTimeDto timeDto = new TaskTimeDto();
					LocalDate minusMonths = DateUtil.getLocalDate(new Date(timestamp)).minusMonths(1);
					LocalDate firstDayOfMonth = LocalDate.of(minusMonths.getYear(),minusMonths.getMonth(),1);
					long taskTimestamp = DateUtil.getDate(firstDayOfMonth).getTime();
					timeDto.setTaskTime(new Date(taskTimestamp));
					timeDto.setStartTime(new Date(dateTimestamp));
					dates.add(timeDto);
				}
				// 一天天加
				timestamp = DAY_VAL + timestamp;
			}
			return dates;
		}

		@Override
		public Date getNewDateByMinusNum(Date date, Integer num) {
			LocalDate localDate = DateUtil.getLocalDate(date);
			LocalDate minusLocalDate = localDate.minusMonths(num);;
			return DateUtil.getDate(minusLocalDate);
		}

		@Override
		public Date getNewDateByPlusNum(Date date, Integer num) {
			return this.getNewDateByMinusNum(date, -num);
		}
	};

	private String desc;
	
	private static final long SECOND_VAL = 1000;
	private static final long MINUTE_VAL = SECOND_VAL * 60;
	private static final long HOUR_VAL = MINUTE_VAL * 60;
	private static final long DAY_VAL = HOUR_VAL * 24;
	private static final int DAY_BY_MINUTE = 24 * 60;
	private static final int DAY_BY_HOUR = 24;
	
	@SuppressWarnings("unused")
	private static Integer getValue(Integer val) {
		if (val == null) {
			return 0;
		}
		return val;
	}
	
	public static Boolean checkIsDepends(JobCycleTypeEnum typeEnum, JobCycleTypeEnum typeParentEnum) {
		Assert.notNull(typeEnum, "子节点类型不能为空");
		Assert.notNull(typeParentEnum, "父节点类型不能为空");
		
		if ( (typeParentEnum == JobCycleTypeEnum.MONTH && typeEnum == JobCycleTypeEnum.WEEK)
				|| (typeParentEnum == JobCycleTypeEnum.MONTH && typeEnum == JobCycleTypeEnum.DAY)
				|| (typeParentEnum == JobCycleTypeEnum.MONTH && typeEnum == JobCycleTypeEnum.DAYT1)
				|| (typeParentEnum == JobCycleTypeEnum.MONTH && typeEnum == JobCycleTypeEnum.HOURS)
				|| (typeParentEnum == JobCycleTypeEnum.MONTH && typeEnum == JobCycleTypeEnum.MINUTES)
				|| (typeParentEnum == JobCycleTypeEnum.WEEK && typeEnum == JobCycleTypeEnum.DAYT1)
				|| (typeParentEnum == JobCycleTypeEnum.WEEK && typeEnum == JobCycleTypeEnum.DAY)
				|| (typeParentEnum == JobCycleTypeEnum.WEEK && typeEnum == JobCycleTypeEnum.MONTH)
				|| (typeParentEnum == JobCycleTypeEnum.WEEK && typeEnum == JobCycleTypeEnum.MINUTES)
				|| (typeParentEnum == JobCycleTypeEnum.WEEK && typeEnum == JobCycleTypeEnum.HOURS)
				|| (typeParentEnum == JobCycleTypeEnum.DAY && typeEnum == JobCycleTypeEnum.HOURS)
				|| (typeParentEnum == JobCycleTypeEnum.DAYT1 && typeEnum == JobCycleTypeEnum.HOURS)
				|| (typeParentEnum == JobCycleTypeEnum.HOURS && typeEnum == JobCycleTypeEnum.WEEK)
				|| (typeParentEnum == JobCycleTypeEnum.HOURS && typeEnum == JobCycleTypeEnum.MONTH)
				|| (typeParentEnum == JobCycleTypeEnum.DAY && typeEnum == JobCycleTypeEnum.MINUTES)
				|| (typeParentEnum == JobCycleTypeEnum.DAYT1 && typeEnum == JobCycleTypeEnum.MINUTES)
				|| (typeParentEnum == JobCycleTypeEnum.MINUTES && typeEnum == JobCycleTypeEnum.WEEK)
				|| (typeParentEnum == JobCycleTypeEnum.MINUTES && typeEnum == JobCycleTypeEnum.MONTH)) {
			return false;
		}
		return true;
	}

	/**
	 * 生成实例时间和调度时间, 调度时间转换成实例时间，再查listTaskTimes(SchedulerTimeDto time, Date startDate, Date endDate)
	 * 
	 * @param time 用户配置的调度信息
	 * @param nowDate 当前调度时间
	 * @return 实例时间和调度时间列表
	 */
	public abstract List<TaskTimeDto> listTaskTimes(SchedulerTimeDto time, Date nowDate);
	
	/**
	 * 生成实例时间和调度时间, 次日生成，还是当日生成？
	 * 
	 * @param time 用户配置的调度信息
	 * @param startDate 当前实例开始时间
	 * @param endDate 当前实例结束时间
	 * @return 实例时间和调度时间列表
	 */
	public abstract List<TaskTimeDto> listTaskTimes(SchedulerTimeDto time, Date startDate, Date endDate);
	
	public abstract String getSchedulerTime(SchedulerTimeDto time);
	
	/**
	 * 当前时间-时间周期，获取最新日期，小时，分钟类型如果跨天，返回前一天凌晨+最大周期时间
	 * 
	 * @param date 当前时间
	 * @param num 时间周期
	 * @return
	 */
	public abstract Date getNewDateByMinusNum(Date date, Integer num);
	
	/**
	 * 当前时间+时间周期，获取最新日期，小时，分钟类型如果跨天，返回明天凌晨
	 * 
	 * @param date 当前时间
	 * @param num 时间周期
	 * @return
	 */
	public abstract Date getNewDateByPlusNum(Date date, Integer num);
	
	/**
	 * 当日是否允许创建
	 * 
	 * @param time
	 * @return
	 */
	public abstract Boolean isNotAllowCreateByDay(SchedulerTimeDto time, Date nowDate);

	
	public static void main(String[] args) {
		
//		Date nowDate = DateUtil.getDate(LocalDateTime.now().with(LocalTime.MIN).plusHours(0));
//		System.out.println(DateUtil.format(nowDate, DateFormatPattern.YYYY_MM_DD_HH_MM_SS));
//		
//		JobCycleTypeEnum jobCycle = JobCycleTypeEnum.HOURS;
//		Date date = jobCycle.getNewDateByMinuteNum(nowDate, 4);
//		System.out.println(DateUtil.format(date, DateFormatPattern.YYYY_MM_DD_HH_MM_SS));
		
		System.out.println(LocalDate.now().toString());
		System.out.println(LocalDate.now().getDayOfWeek().getValue());
		
		SchedulerTimeDto time = new SchedulerTimeDto();
		time.setDay(29);
		time.setHour(0);
		time.setMinute(0);
		time.setSecond(0);
		time.setWeek(2);
		
		System.out.println(String.format("week: %s, day: %s, hour %s, minute %s, sec : %s", time.getWeek(), time.getDay(), time.getHour(), time.getMinute(), time.getSecond()));
		
		//Date nowDate = DateUtil.getDate(LocalDateTime.now());
		Date nowDate = DateUtil.getMinOfDay(DateUtil.getDate(LocalDateTime.now().plusDays(1)));
		Date endDate = DateUtil.getMaxOfDay(DateUtil.getDate(LocalDate.now().plusDays(1)));
		
		System.out.println(String.format("start: %s, end: %s", DateUtil.format(nowDate, DateFormatPattern.YYYY_MM_DD_HH_MM_SS),DateUtil.format(endDate, DateFormatPattern.YYYY_MM_DD_HH_MM_SS)));
		
		JobCycleTypeEnum jobCycle = JobCycleTypeEnum.MONTH;
		//System.out.println("isNotAllow:"+jobCycle.isNotAllowCreateByDay(time, nowDate));
		List<TaskTimeDto> taskTimes = jobCycle.listTaskTimes(time, nowDate, endDate);
		//List<TaskTimeDto> taskTimes = jobCycle.listTaskTimes(time, nowDate);
		for (TaskTimeDto date : taskTimes) {
			System.out.println(jobCycle.name()  + ", taskTimes : " + DateUtil.format(date.getTaskTime(), DateFormatPattern.YYYY_MM_DD_HH_MM_SS));
			System.out.println(jobCycle.name()  + ", startTimes : " + DateUtil.format(date.getStartTime(), DateFormatPattern.YYYY_MM_DD_HH_MM_SS));
		}
		
		System.out.println("--------------------------------------");
		
	}

}
