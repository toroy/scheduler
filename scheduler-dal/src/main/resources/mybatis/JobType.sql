CREATE TABLE `sc_job_type` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `plugin_dir` varchar(256) DEFAULT NULL,
  `plugin_name` varchar(256) DEFAULT NULL,
  `plugin_desc` varchar(256) DEFAULT NULL,
  `plugin_clazz` varchar(256) DEFAULT NULL,
  `plugin_emr_clazz` varchar(256) DEFAULT NULL,
  `create_time` timestamp NULL DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT NULL,
  `plugin_alias` varchar(256) DEFAULT NULL,
  `category` varchar(255) DEFAULT NULL,
  `is_stream` tinyint(1) DEFAULT '0',
  `is_cluster_job` tinyint(1) DEFAULT '0',
  `is_support_lineage` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_name` (`plugin_name`,`category`) USING HASH
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

insert into `sc_job_type`(category,plugin_dir, plugin_name, plugin_alias, plugin_desc, plugin_clazz, plugin_emr_clazz,
create_time, update_time,is_stream,is_support_lineage,is_cluster_job)
values ('CAL', null, 'HIVE', 'Hive任务', '计算型HIVE任务', 'com.clubfactory.platform.scheduler.engine.task.builtin.HiveTask',
'com.clubfactory.platform.scheduler.engine.task.builtin.emr.EmrHiveTask','2020-11-16 14:33:00','2020-11-16 14:33:00', 0, 1, 1),
('CAL', null, 'SPARK', 'Spark任务', '计算型Spark任务', 'com.clubfactory.platform.scheduler.engine.task.builtin.SparkTask',
'com.clubfactory.platform.scheduler.engine.task.builtin.emr.EmrSparkTask','2020-11-16 14:33:00','2020-11-16 14:33:00', 0, 0, 0),
('CAL', null, 'SPARK_STREAMING', 'SparkStreaming任务', '计算型SparkStreaming任务', 'com.clubfactory.platform.scheduler.engine.task.builtin.SparkStreamingTask',
'com.clubfactory.platform.scheduler.engine.task.builtin.emr.EmrSparkStreamingTask','2020-11-16 14:33:00','2020-11-16 14:33:00', 1, 0, 0),
('CAL', null, 'JAVA', 'Java任务', '计算型Java任务', 'com.clubfactory.platform.scheduler.engine.task.builtin.JavaTask',
null,'2020-11-16 14:33:00','2020-11-16 14:33:00', 0, 0, 0),
('CAL', null, 'SHELL', 'Shell任务', '计算型Shell任务', 'com.clubfactory.platform.scheduler.engine.task.builtin.ShellTask',
'com.clubfactory.platform.scheduler.engine.task.builtin.emr.EmrShellTask','2020-11-16 14:33:00','2020-11-16 14:33:00', 0, 0, 0),
('CAL', null, 'PYTHON', 'Python任务', '计算型Python任务', 'com.clubfactory.platform.scheduler.engine.task.builtin.PythonTask',
null,'2020-11-16 14:33:00','2020-11-16 14:33:00', 0, 0, 0),
('COLLECT', null, 'PYTHON', 'Python任务', '采集型Python任务', 'com.clubfactory.platform.scheduler.engine.task.builtin.DataPullTask',
null,'2020-11-16 14:33:00','2020-11-16 14:33:00', 0, 0, 0),
('COLLECT', null, 'SPARK_STREAMING', 'SparkStreaming任务', 'SparkStreaming流式采集任务', 'com.clubfactory.platform.scheduler.engine.task.builtin.SparkStreamingEtlTask',
'com.clubfactory.platform.scheduler.engine.task.builtin.emr.EmrSparkStreamingEtlTask','2020-11-16 14:33:00','2020-11-16 14:33:00', 1, 0, 0),
('REFLUE', null, 'PYTHON', 'Python任务', '回流型Python任务', 'com.clubfactory.platform.scheduler.engine.task.builtin.DataPushTask',
null,'2020-11-16 14:33:00','2020-11-16 14:33:00', 0, 0, 0),
('REFLUE', null, 'KAFKA', 'Kafka任务', '回流型Kafka任务', 'com.clubfactory.platform.scheduler.engine.task.builtin.DataPushTask',
null,'2020-11-16 14:33:00','2020-11-16 14:33:00', 0, 0, 0)
,('CAL', '/home/ubuntu/opt/scheduler-server/plugins', 'FLINK', 'Flink任务', 'Flink Batch计算任务', 'com.clubfactory.platform.scheduler.task.FlinkBatchTask',
'com.clubfactory.platform.scheduler.task.EmrFlinkBatchTask','2020-11-16 14:33:00','2020-11-16 14:33:00', 0, 0, 0)
,('CAL', '/home/ubuntu/opt/scheduler-server/plugins', 'FLINK_STREAMING', 'FlinkStream任务', 'FlinkStream计算任务', 'com.clubfactory.platform.scheduler.task.FlinkStreamTask',
'com.clubfactory.platform.scheduler.task.EmrFlinkStreamTask','2020-11-16 14:33:00','2020-11-16 14:33:00', 0, 0, 0)


;


