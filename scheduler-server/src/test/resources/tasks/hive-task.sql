select * from hive_test;
select '${hivevar:param1}' as test1,'${param2}' as test2, '${hiveconf:hivesysconf}' as conf1;