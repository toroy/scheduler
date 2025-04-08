#!/bin/bash

base_dir=$(cd `dirname $0`;pwd)
project_root_dir=${base_dir}/../
package_dir="${project_root_dir}/scheduler-server/target"

source ${base_dir}/common/common.sh

function usage() {
    echo -e "usage: sh `basename $0` <-p [pre|prod]>"
    echo -e "       -p：发布环境 [pre | prod]"
    echo -e "       eg: ./`basename $0` -p prod"
    exit 0
}

while getopts ":hp:" opt
do
    case ${opt} in
        h)
        usage;;
        p)
        env="$OPTARG";;
        ?)
        usage;;
    esac
done



if [[ -z "${env}" ]] || [[ "${env}" != "pre" && "${env}" != "prod" ]]; then
    usage
fi
log_info "当前部署环境为: ${env}"
case "${env}" in
    "pre")
     s3_base="s3://cfdp/tmp/scheduler-pre/"
     ;;
     "prod")
     s3_base="s3://cfdp/tmp/scheduler/"
     ;;
     *)
     usage
esac


cd ${package_dir} && aws s3 cp scheduler-server.jar ${s3_base}
#
if [[ $? = 0 ]]; then
    log_info "upload success ..."
else
    log_error "upload failed ..."
    exit 1
fi