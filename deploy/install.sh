#!/bin/bash

base_dir=$(cd `dirname $0`;pwd)
project_root_dir=${base_dir}/../
dist_dir=${project_root_dir}/dist
common_dir=${base_dir}/common

source ${common_dir}/common.sh


install_dir=""
if [[ $# = 1 ]]; then
    echo -e "unknown param $1"
    echo -e "usage: sh `basename $0` --install_dir [install_dir]"
    echo -e "       --install_dir：指定安装服务的根目录"
    echo -e "       eg: ./`basename $0` --install_dir /opt/scheduler"
    exit 1
fi
while [[ $# -ge 2 ]];do
    case "$1" in
            "--install_dir") install_dir=$2; shift 2;;
            *) echo -e "unknown param $1"
               echo -e "usage: sh `basename $0` --install_dir [install_dir]"
               echo -e "       --install_dir：指定安装服务的根目录"
               echo -e "       eg: ./`basename $0` --install_dir /opt/scheduler"
               exit 1 ; break;;
    esac
done

if [[ -z ${install_dir} ]]; then
    install_dir="${dist_dir}"
fi


install_libs=${install_dir}/scheduler-server/libs
install_sbin=${install_dir}/scheduler-server/sbin
install_conf=${install_dir}/scheduler-server/conf
service_monitor=${install_sbin}/monitor
create_dir ${install_libs}
create_dir ${install_conf}
create_dir ${install_sbin}/common
create_dir ${service_monitor}

cp ${project_root_dir}/scheduler-server/target/scheduler-server.jar ${install_libs}
cp ${project_root_dir}/scheduler-server/target/classes/worker.properties  ${install_conf}
cp -r ${common_dir}/* ${install_sbin}/common/
cp ${base_dir}/*daemon.sh ${install_sbin}/
cp ${base_dir}/monitor/* ${service_monitor}



#cd ${install_dir}  && tar -czvf scheduler-server.tar.gz scheduler-server/ && cd ${base_dir}
#cd ${install_dir}  \
#&& rm scheduler-server.tar.gz \
#|| tar -czvf scheduler-server.tar.gz scheduler-server/ \
#&& aws s3 cp scheduler-server.tar.gz s3://cfdp/tmp/scheduler-pre/ \
#&& cd ${base_dir}




#cd ${install_dir}  && rm scheduler-server.tar.gz
#tar -czvf scheduler-server.tar.gz scheduler-server

