alter table sc_job_online
	add job_type varchar(16) default "NORMAL" null comment '任务类型， NORMAL:正常任务，DQC:DQC任务';


alter table sc_job
	add job_type varchar(16) default "NORMAL" null comment '任务类型， NORMAL:正常任务，DQC:DQC任务';


alter table sc_task
	add job_type varchar(16) default "NORMAL" null comment '任务类型， NORMAL:正常任务，DQC:DQC任务';

alter table sc_task
	add cycle_type varchar(16) default "NORMAL" null comment '任务类型， NORMAL:正常任务，DQC:DQC任务';

alter table sc_task
	add retry_max int default null;

 alter table sc_task add notice_count int;

alter table sc_task add is_notice bool;

alter table sc_task add script_id int;

alter table sc_task add retry_dur int;

alter table sc_alarm
	add user_group_id long null comment '用户联系人组id';

alter table sc_machine add is_self bool;

alter table sc_job_online add args_param varchar(255) default null;
 alter table sc_job_online add main_class varchar(255) default null;
 alter table sc_job_online add program_type varchar(16) default null;
 alter table sc_job_online add deploy_mode int default null;
 alter table sc_job_online add target_table varchar(255) default null;
 alter table sc_job_online add job_id int;
  alter table sc_job_online add is_running bool;
  alter table sc_job_online add db_target_id int;
  alter table sc_job_online add time_out int;
  alter table sc_job_online add source_table  varchar(255) default null;
  alter table sc_job_online add db_source_id int;
alter table sc_job_online add split_pk varchar(32);
 alter table sc_job_online add increment_column varchar(255);
 alter table sc_job_online add run_count int;
 alter table sc_job_online add target_columns varchar(255);
 alter table sc_job_online add source_columns varchar(255);
 alter table sc_job_online add storage_format varchar(64);
 alter table sc_job_online add increment_type varchar(8);
  alter table sc_job_online add where_sql varchar(255);
  alter table sc_job_online add group_id int;
  alter table sc_job_online add project_id int;
  alter table sc_job_online add project_type_id int;
  alter table sc_job_online add job_type varchar(8);
   alter table sc_task_monitor add retry_counts int;
   alter table sc_task_monitor add max_retry_num int;



INSERT INTO scheduler.sc_sys_config (apply_host, param_key, param_val, param_desc, config_type, create_time, update_time) VALUES ('all', 'dqc.number.script.id', '1867', 'dqc数值型脚本', 'WEB', null, null);
INSERT INTO scheduler.sc_sys_config (apply_host, param_key, param_val, param_desc, config_type, create_time, update_time) VALUES ('all', 'dqc.rolling.script.id', '1868', 'dqc波动型脚本', 'WEB', null, null);
INSERT INTO scheduler.sc_sys_config (apply_host, param_key, param_val, param_desc, config_type, create_time, update_time) VALUES ('all', 'dqc.task.not.block.time', '15:59:59', '没有阻塞实例的最早执行时间', 'MASTER', null, null);
INSERT INTO scheduler.sc_sys_config (apply_host, param_key, param_val, param_desc, config_type, create_time, update_time) VALUES ('all', 'dqc.task.not.block.time.end', '20:00:00', '没有阻塞实例的最晚执行时间', 'MASTER', null, null);

INSERT INTO scheduler.sc_token (is_deleted, description, value, create_user, update_user, create_time, update_time) VALUES (0, '搜索团队', 'gaia9QrxctkSGPfmJRYb', 124, 124, null, null);