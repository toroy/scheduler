#!/bin/sh

task_log_dir=""
if [[ -z "${task_log_dir}" ]]; then
    exit 1
fi

## 删除15天前的日志文件
find ${task_log_dir} -mtime +7 -name "task_*.log" -exec rm -rf {} \;
exit 0