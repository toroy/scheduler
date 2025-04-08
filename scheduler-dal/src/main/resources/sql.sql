alter table sc_job_online
	add job_type varchar(16) default "NORMAL" null comment '任务类型， NORMAL:正常任务，DQC:DQC任务';


alter table sc_job
	add job_type varchar(16) default "NORMAL" null comment '任务类型， NORMAL:正常任务，DQC:DQC任务';


alter table sc_task
	add job_type varchar(16) default "NORMAL" null comment '任务类型， NORMAL:正常任务，DQC:DQC任务';


alter table sc_alarm
	add user_group_id long null comment '用户联系人组id';

INSERT INTO scheduler.sc_sys_config (apply_host, param_key, param_val, param_desc, config_type, create_time, update_time) VALUES ('all', 'dqc.number.script.id', '1867', 'dqc数值型脚本', 'WEB', null, null);
INSERT INTO scheduler.sc_sys_config (apply_host, param_key, param_val, param_desc, config_type, create_time, update_time) VALUES ('all', 'dqc.rolling.script.id', '1868', 'dqc波动型脚本', 'WEB', null, null);
INSERT INTO scheduler.sc_sys_config (apply_host, param_key, param_val, param_desc, config_type, create_time, update_time) VALUES ('all', 'dqc.task.not.block.time', '15:59:59', '没有阻塞实例的最早执行时间', 'MASTER', null, null);
INSERT INTO scheduler.sc_sys_config (apply_host, param_key, param_val, param_desc, config_type, create_time, update_time) VALUES ('all', 'dqc.task.not.block.time.end', '20:00:00', '没有阻塞实例的最晚执行时间', 'MASTER', null, null);

INSERT INTO scheduler.sc_token (is_deleted, description, value, create_user, update_user, create_time, update_time) VALUES (0, '搜索团队', 'gaia9QrxctkSGPfmJRYb', 124, 124, null, null);