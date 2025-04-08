import os
import sys
import json
import psycopg2
import pymysql
from pyhive import hive


# 将传进来的参数转换成dataX需要的参数
def transform_params():
    with open(os.path.abspath(os.path.dirname(__file__)) + '/data_collection.json') as r:
        data = json.loads(r.read())
    db = data['sourceDb']
    dw = data['targetDb']

    format = data['storageFormat']
    columnDto = data['columnDto']
    split_pk = columnDto['splitPk']
    dw_columns = ','.join(columnDto['sourceColumns'])
    db_columns = ','.join(columnDto['targetColumns'])

    db_name = db['dbName']
    db_type = db['dsType']
    db_host = db['dsHost']
    db_username = db['dsUser']
    db_password = db['dsPassword']
    db_table = data['sourceTable']

    dw_name = dw['dbName']
    dw_table = data['targetTable']

    task_date = data['taskTime'][:10]

    return {'task_date': task_date,
            'db_type': db_type,
            'db_host': db_host,
            'db_name': db_name,
            'db_table': db_table,
            'db_username': db_username,
            'db_password': db_password,
            'concurrency': 20,
            'dw_columns': dw_columns,
            'assign_columns': db_columns,
            'format': format,
            'split_pk': split_pk,
            'dw_name': dw_name,
            'dw_table': dw_table
            }


# 打印并执行命令
# --mysql_conn_id='{conn_id}'
def mysql_to_s3(**kwargs):
    sync_command = "python /home/ubuntu/data-sync/mysql2S3.py -db='{db_name}' -tb='{db_table}' --biz_date='{task_date}' --concurrency={concurrency} --db_type='{db_type}' --format='{format}' "
    command = sync_command.format(**kwargs)
    if kwargs['split_pk'] not in ['id', '']:
        command = command + " --split_pk='{}' ".format(kwargs['split_pk'])
    if kwargs['dw_name'] != '':
        command = command + " --target_db='{}' ".format(kwargs['dw_name'])
    if kwargs['dw_table'] != '':
        command = command + " --target_tb='{}' ".format(kwargs['dw_table'])
    print(command)
    # os.system(command)


if __name__ == '__main__':
    # print(sys.argv[1])
    params_json = transform_params()
    mysql_to_s3(**params_json)