#!/bin/bash

base_dir=$(cd `dirname $0`;pwd)
lib_dir=${base_dir}/../libs
logs_dir=${base_dir}/../logs
dump_dir=${base_dir}/../dumps
web_jar_name="scheduler-web-server.jar"
engine_jar_name="scheduler-server.jar"
std_out="false"


source ${base_dir}/common/common.sh

create_dir ${logs_dir}
create_dir ${dump_dir}


SCHEDULER_OPTS="-server $SCHEDULER_OPTS"

if [[ $# < 2 ]]; then
    log_error "Usage: sh `basename $0` <master-server|worker-server|logger-server|web-server> <start|stop|status|restart> [debug]"
    exit 1
fi

std_err=${logs_dir}/scheduler-${1}.out
pid_file=${base_dir}/scheduler-${1}.pid

if [[ $# > 2 ]]; then
    spring_profile=`to_lower $3`
    SCHEDULER_OPTS="
    ${SCHEDULER_OPTS} \
    -Dspring.profiles.active=${spring_profile}
    "
    log_info "当前启动环境为${spring_profile}..."
fi

if [[ $# > 3 ]]; then
    debug_port=$4
    SCHEDULER_OPTS="
    ${SCHEDULER_OPTS} \
    -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=${debug_port}
    "
    log_info "debug 模式已开启, 端口为${debug_port}..."
    if [[ ${debug_port} == "stdout" ]]; then
        std_out="true"
        log_warn "已开启stdout日志输出，请注意磁盘占用监控..."
    fi

fi


SCHEDULER_OPTS="${SCHEDULER_OPTS} -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=${dump_dir}/${1}_heapdump.hprof"
function get_pid() {
    case $1 in
        "web-server")
            grep_str="${web_jar_name}"
        ;;
        "master-server")
            grep_str="MasterServer"
        ;;
        "worker-server")
            grep_str="WorkerServer"
        ;;
        "logger-server")
            grep_str="LoggerServer"
        ;;
        *)
        log_error "Usage: sh `basename $0` <master-server|worker-server|logger-server|web-server> <start|stop|status|restart> [debug]"
        ;;
    esac
    pid=`fetch_pid "${grep_str}"`
	echo ${pid}
}

function status() {
   pid=`get_pid ${1}`
   if [[ -z "${pid}" ]]; then
        log_info_e "scheduler-${1} is \033[31m stopped\033[0m ..."
   else
        log_info_e "scheduler-${1} is \033[32m running\033[0m ..."
   fi
}


function start() {
   server_type="$1"
   start_params=""
   shift 2
   case ${server_type} in
        "web-server")
            jar_name="${web_jar_name}"
            start_params="$@"
        ;;
        "master-server")
            jar_name="${engine_jar_name}"
            start_params="MasterServer $@"
        ;;
        "worker-server")
            jar_name="${engine_jar_name}"
            start_params="WorkerServer $@"
        ;;
        "logger-server")
            jar_name="${engine_jar_name}"
            start_params="LoggerServer $@"
        ;;
        *)
        log_error "Usage: sh `basename $0` <master-server|worker-server|logger-server|web-server> <start|stop|status|restart> [debug]"
        exit 1
        ;;
   esac
   log_info "starting scheduler-${server_type} ..."
   if [[ ${std_out} == "true" ]]; then
      nohup java ${SCHEDULER_OPTS} \
       -jar ${lib_dir}/${jar_name} \
       ${start_params} \
       --logging.path=${logs_dir}  \
       > ${std_err}  2>&1 &
       pid=$!
       echo ${pid} > ${pid_file}
   else
      nohup java ${SCHEDULER_OPTS} \
       -jar ${lib_dir}/${jar_name} \
       ${start_params} \
       --logging.path=${logs_dir}  \
       > /dev/null 2>&1 &
       pid=$!
       echo ${pid} > ${pid_file}
   fi

}


function stop() {
    log_info "stopping scheduler-${1} ..."
	kill  "`get_pid ${1}`"
}



start_pid=`get_pid ${1}`
case "$2" in
    "start")
        if [[ ! -z  "${start_pid}" ]]; then
			log_info "scheduler-${1} is already running ,skipped ..."
	    else
		    start $@
	    fi
    ;;
    "stop")
        if [[  -z  "${start_pid}" ]]; then
			log_info "scheduler-${1} had been stopped, skipped..."
		else
			stop $1
		fi
    ;;
    "status")
        status $1
    ;;
    "restart")
        if [[  -z  "${start_pid}" ]]; then
			start $@
		else
			stop $1 && start $@
		fi
    ;;
    *)
        log_error "Usage: sh `basename $0` <master-server|worker-server|logger-server|web-server> <start|stop|status|restart> [debug]"
    ;;
esac




