import os
import sys
import json
import psycopg2
import pymysql
from pyhive import hive


# 连接数据库和数仓
class DbConn(object):

    def __init__(self, db_type, host, user, password, database):
        if db_type == 'mysql':
            self.conn = pymysql.connect(host=host, user=user, password=password, database=database)
        elif db_type in ['redshift', 'postgresql']:
            self.conn = psycopg2.connect(host=host, user=user, password=password, database=database)
        elif db_type == 'hive':
            self.conn = hive.Connection(host=host, username=user, port=10000, database=database)
        self.cur = self.conn.cursor()

    def __enter__(self):
        return self.cur

    def __exit__(self, exc_type, exc_value, exc_trace):
        self.conn.commit()
        self.cur.close()
        self.conn.close()


# 将传进来的参数转换成dataX需要的参数
def transform_params():
    with open(os.path.abspath(os.path.dirname(__file__)) + '/data_reflow.json') as r:
        data = json.loads(r.read())
    dw = data['sourceDb']
    db = data['targetDb']
    columnDto = data['columnDto']
    is_sql = columnDto['isSql']
    custom_sql = columnDto['sql']
    dw_columns = ','.join(columnDto['sourceColumns'])
    db_columns = ','.join(columnDto['targetColumns'])
    reflow_type = columnDto['incrementType']  # ALL|ADD
    sync_condition = columnDto['incrementColumn']
    partition_condition = columnDto['where'] if columnDto['where'] != '' else '1=1'

    dw_name = dw['dbName']
    dw_type = dw['dsType']
    dw_host = dw['dsHost']
    dw_username = dw['dsUser']
    dw_password = dw['dsPassword']
    dw_table = data['sourceTable']
    bucket = 'cf-data-sync'

    db_name = db['dbName']
    db_type = db['dsType']
    db_host = db['dsHost']
    db_username = db['dsUser']
    db_password = db['dsPassword']
    db_table = data['targetTable']

    task_date = data['taskTime'][:10]
    pre_operator = 'truncate table {dw_table}'.format(
        dw_table=dw_table) if reflow_type == 'ALL' else "delete from {dw_table} where {c1} ".format(dw_table=dw_table,
                                                                                                    c1=sync_condition)
    return {'bucket': bucket,
            'dw_type': dw_type,
            'dw_host': dw_host,
            'dw_name': dw_name,
            'dw_table': dw_table,
            'dw_username': dw_username,
            'dw_password': dw_password,
            'task_date': task_date,
            'db_type': db_type,
            'db_host': db_host,
            'db_name': db_name,
            'db_table': db_table,
            'db_username': db_username,
            'db_password': db_password,
            'concurrency': 20,
            'is_sql': is_sql,
            'custom_sql': custom_sql,
            'dw_columns': dw_columns,
            'assign_columns': db_columns,
            'pre_operator': pre_operator,
            'partition_condition': partition_condition,
            'sync_condition': sync_condition
            }


def create_sql(**kwargs):
    if kwargs['dw_type'] == 'redshift':
        if kwargs['is_sql']:
            sql = '''
                  UNLOAD ('{custom_sql} ')
                  TO 's3://cf-data-sync/export/{dw_name}/{dw_table}/{task_date}/'
                  CREDENTIALS 'aws_iam_role=arn:aws:iam::660338696248:role/redshift'
                  delimiter '\\001'
                  ALLOWOVERWRITE
                  MAXFILESIZE  10 MB
                  ESCAPE;
                  '''.format(**kwargs)
        else:
            sql = '''
                  UNLOAD ('SELECT {dw_columns} FROM {dw_name}.{dw_table}  ')
                  TO 's3://cf-data-sync/export/{dw_name}/{dw_table}/{task_date}/'
                  CREDENTIALS 'aws_iam_role=arn:aws:iam::660338696248:role/redshift'
                  delimiter '\\001'
                  ALLOWOVERWRITE
                  MAXFILESIZE  10 MB
                  ESCAPE;
                  '''.format(**kwargs)
    else:
        if kwargs['is_sql']:
            sql = '''
                  set hive.execution.engine=mr;
                  set hive.merge.mapredfiles=false;
                  set mapred.reduce.tasks=100;
                  INSERT overwrite  directory 's3://cf-data-sync/export/{dw_name}/{dw_table}/{task_date}/'
                  {custom_sql}
                  distribute by rand(100);
                  '''.format(**kwargs)
        else:
            sql = '''
                    set hive.execution.engine=mr;
                    set hive.merge.mapredfiles=false;
                    set mapred.reduce.tasks=100;
                    INSERT overwrite  directory 's3://cf-data-sync/export/{dw_name}/{dw_table}/{task_date}/'
                    SELECT {dw_columns} from {dw_name}.{dw_table}
                    where {partition_condition} and {sync_condition}
                    distribute by rand(100);
                    '''.format(**kwargs)
    return sql


# 写入s3
def load_to_s3(**kwargs):
    print(kwargs['exe_sql'])
    # if kwargs['dw_type'] == 'hive':
    #     with DbConn('hive', 'host', 'user', 'password', 'database') as cur:
    #         cur.execute(kwargs['exe_sql'])
    # elif kwargs['dw_type'] == 'redshift':
    #     with DbConn('redshift', 'host', 'user', 'password', 'database') as cur:
    #         cur.execute(kwargs['exe_sql'])


# 打印并执行命令
# --mysql_conn_id='{conn_id}'
def s3_to_mysql(**kwargs):
    sync_command = "python /home/ubuntu/data-sync/s32mysql_concurrent.py  --bucket_name='{bucket}' --s3_key='export/{dw_name}/{dw_table}/{task_date}/'   --mysql_db='{db_name}' --mysql_table='{db_table}' --assign_columns='{assign_columns}' --concurrency={concurrency}  --mysql_preoperator '{pre_operator}' "
    command = sync_command.format(**kwargs)
    print(command)
    # os.system(command)


if __name__ == '__main__':
    # print(sys.argv[1])
    params_json = transform_params()
    exe_sql = create_sql(**params_json)
    load_to_s3(exe_sql=exe_sql, **params_json)
    s3_to_mysql(**params_json)
