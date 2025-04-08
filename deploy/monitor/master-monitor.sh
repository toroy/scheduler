#!/bin/bash


base_dir=$(cd `dirname $0`;pwd)
root_dir=${base_dir}/../
logs_dir=${base_dir}/../../logs/monitor/
monitor_log=${logs_dir}/master-server-monitor.`date +'%Y%m'`.log

source ${root_dir}/common/common.sh
create_dir ${logs_dir}



pid=`fetch_pid 'MasterServer'`
if [[ -z "${pid}" ]];then
    log_warn "MasterServer is down , try start up now ..." >> ${monitor_log}
     /bin/bash ${root_dir}/master-daemon.sh "start" >> ${monitor_log}
else
    log_info "MasterServer is  running, skip it..." >> ${monitor_log}
fi