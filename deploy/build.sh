#!/bin/bash

base_dir=$(cd `dirname $0`;pwd)
project_root_dir=${base_dir}/../


source ${base_dir}/common/common.sh


env=""
if [[ $# -eq 1 ]] ; then
    echo -e "unknown param $1"
    echo -e "usage: sh `basename $0` [--env env]"
    echo -e "       --env：指定打包时选择的springboot profile"
    echo -e "       eg: ./`basename $0` --env prod"
    exit 1
fi
while [[ $# -ge 2 ]];do
    case "$1" in
            "--env") env=$2; shift 2;;
            *) echo -e "unknown param $1"
               echo -e "usage: sh `basename $0` [--env env]"
               echo -e "       --env：指定打包时选择的springboot profile"
               echo -e "       eg: ./`basename $0` --env prod"
               exit 1 ; break;;
    esac
done

if [[ -z "${env}" ]]; then
    env="dev"
fi
log_info "当前打包环境为: ${env}"

cd ${project_root_dir} && mvn clean deploy -DskipTests -U -P${env}

if [[ $? = 0 ]]; then
    log_info "compile success ..."
else
    log_error "compile failed ..."
    exit 1
fi