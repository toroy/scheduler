#!/bin/bash
. /etc/profile
if [[ -f ~/.bash_profile ]];then
    . ~/.bash_profile
fi
if [[ -f ~/.bashrc ]]; then
   . ~/.bashrc
fi

## 打印INFO日志
function log_info() {
    msg="$@"
    echo "[`date +'%Y-%m-%d %H:%M:%S'`] - [INFO] - ${msg}"
}

function log_info_e() {
    echo -e "[`date +'%Y-%m-%d %H:%M:%S'`] - [INFO] - ${@}"
}

## 打印警告日志
function log_warn() {
   echo "[`date +'%Y-%m-%d %H:%M:%S'`] - [WARN] - ${@}"
}

## 打印错误日志
function log_error() {
   echo "[`date +'%Y-%m-%d %H:%M:%S'`] - [ERROR] - ${@}"
}

## 判断上一条命令是否执行成功
function assert_status(){
    if [[ $? != 0 ]]; then
       msg="$@"
       if [[ -z ${msg} ]]; then
           msg="执行出错"
       fi
       log_error ${msg}
    fi
}

## 创建文件夹
function create_dir() {
    dir="$1"
    if [[ ! -z ${dir} && ! -d ${dir} ]]; then
        mkdir -p ${dir}
    fi
}

## 根据服务名称匹配出pid
function fetch_pid() {
    pid=`ps -ef | grep "${*}" | grep -v 'grep' | awk '{print $2}'`
	echo ${pid}
}


## 字符串转为大写
function to_upper() {
    echo "$@" | tr 'a-z' 'A-Z'
}

## 字符串转为小写
function to_lower() {
    echo "$@" | tr 'A-Z' 'a-z'
}