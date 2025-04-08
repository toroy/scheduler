import groovy.transform.Field

// 接收报警的用户的企业微信ID(即工号)列表，用英文逗号分隔
@Field
List<Integer> Infoee = [835]

// 开发语言
@Field
String Language = 'java'

// App 名称
@Field
String AppName = 'cfdp-scheduler'

// 运行jar包的相对路径，可以不填，如果不填的化默认通过 ${MainModule}/target/${MainModule}*.jar 查找
@Field
String Jar = 'scheduler-server/target/scheduler-server.jar'

// 开发机器, 地址类型：['aws:loc:u:ip:port',]
@Field
List<String> Devs = [
    's3:jiayun-search-code'
]

// 线上
@Field
List<String> Tests = [
    's3:jiayun-search-code'
]

// 线上
@Field
List<String> Pres = [
    's3:jiayun-search-code'
]

// 线上
@Field
List<String> Prods = [
    's3:jiayun-search-code'
]



// 逻辑入口。永远不要修改之后的内容。
PlatCI(this)
