CREATE TABLE sc_task_statistic
(
    id bigint PRIMARY KEY NOT NULL AUTO_INCREMENT COMMENT '自增id',
    failed_task_count INT COMMENT '失败任务数',
    task_date timestamp COMMENT '任务执行日期',
    succeed_task_count INT COMMENT '成功任务数',
    delay_task_count INT COMMENT '延迟任务数',
    depart_id BIGINT COMMENT '任务所属团队ID',
    type VARCHAR(256) COMMENT '任务类型',
    statistic_dim VARCHAR(256) COMMENT '任务统计维度',
    succeed_rate VARCHAR(256) COMMENT '任务成功率',
    total_task INT COMMENT '总任务数',
    depart_name VARCHAR(256) COMMENT '任务所属部门',
    create_time timestamp null COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 AUTO_INCREMENT=1;


