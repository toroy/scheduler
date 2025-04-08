## ClubFactory Scheduler Engine

### 服务部署
- 准备工作
    - jdk8及以上
    - maven 3.5及以上
    - git
- 安装步骤
    1. 下载源码，进入到deploy目录
    2. 执行build.sh脚本进行源码编译
        - 可通过--env选项指定需要选择的SpringBoot profile,不指定的话默认选中dev
            - 例如： ./build.sh --env prod 
    3. 执行install.sh进行服务部署：
        - 可通过--install_dir选项指定服务安装目录，不指定的话默认安装在项目根目录下的dist目录
             - 例如: ./install.sh --install_dir /opt/
    4. 进入安装目录的sbin目录下
        - 通过logger-daemon.sh <start|stop|status|restart>脚本启动日志传输服务
        - 通过master-daemon.sh <start|stop|status|restart>脚本启动master服务
        - 通过worker-daemon.sh <start|stop|status|restart>脚本启动worker服务
        - **注意：** 这些启动脚本均支持传递main方法的命令行参数，直接在启动命令后面传递即可
            - 例如：./master-daemon.sh start/restart prod 9998
            - 例如：./worker-daemon.sh start/restart prod 9997
            - 例如：./logger-daemon.sh start/restart prod 9996
            
            
---
### Linux系统配置      
- 文件描述符（句柄）限制
- 查看：ulimit -n 
    - 65535

- 修改：
    - sudo vim /etc/security/limits.conf，到最后两行
        ```bash
        * soft nofile 655350
        * hard nofile 655350  
        ```
    - 或者使用sed
        - sudo sed -i 's/\*\ soft\ nofile\ 65535/\*\ soft\ nofile\ 655350/g' /etc/security/limits.conf
        - sudo sed -i 's/\*\ hard\ nofile\ 65535/\*\ hard\ nofile\ 655350/g' /etc/security/limits.conf
        - ./exec_all.sh "echo '* soft nofile 655350' | sudo tee -a /etc/security/limits.conf"
        - ./exec_all.sh "echo '* hard nofile 655350' | sudo tee -a /etc/security/limits.conf"
        
        - ./exec_all.sh "sudo sed -i 's/\*\ soft\ nofile\ 65535/\*\ soft\ nofile\ 655350/g' /etc/security/limits.conf"
        - ./exec_all.sh "sudo sed -i 's/\*\ hard\ nofile\ 65535/\*\ hard\ nofile\ 655350/g' /etc/security/limits.conf"

