#!/bin/bash

base_dir=$(cd `dirname $0`;pwd)
conf_dir=${base_dir}/../conf
source ${base_dir}/common/common.sh
export SCHEDULER_OPTS="-Xms1024m -Xmx1024m -Dworker.config.dir=${conf_dir}"

command_list="start stop status restart"
if [[ "${command_list}" =~ $1 ]]; then
   /bin/bash ${base_dir}/scheduler-daemon.sh worker-server $@
else
    log_error "Usage: sh `basename $0` <start|stop|status|restart> [debug]"
    exit 1
fi
