import os
import boto3
import pymysql
import optparse
import logging


log = logging.getLogger(__name__)
logging.basicConfig(format='%(asctime)s - %(levelname)s - %(message)s', level=logging.INFO)


s3 = boto3.resource('s3')
client = s3.Bucket('cfdp')

def s3_file_exists(s3_file):
    bucket, obj_key = bucket_prefix(s3_file)
    for obj in client.objects.filter(Prefix=obj_key):
        if obj.key == obj_key:
            return True
    return False

def bucket_prefix(s3_file):
    path_list = s3_file.split('/')
    path_list = list(filter(None, path_list))
    bucket = path_list[1]
    obj_key = os.path.join(*path_list[2:])
    return bucket, obj_key

def select(mysql_host = 'localhost', mysql_user = 'root', mysql_pwd = '', mysql_db='', sql= ''):
    global cursor
    try:
        conn = pymysql.connect(mysql_host,user = mysql_user, passwd = mysql_pwd,db = mysql_db)
    except Exception as e:
        log.error('Mysql服务器连接失败', e)
        return

    try:
        cursor=conn.cursor()
        cursor.execute(sql)

        log.info('select data Successful')
        # 返回值是一个元组List
        return cursor.fetchall()
    except Exception as e:
        log.error("数据查询失败", e)
    finally:
        cursor.close()
        conn.close()

if __name__ == "__main__":
    parse=optparse.OptionParser(usage='"usage:%prog [options] arg1,arg2"',version="%prog 1.0")
    parse.add_option('-m','--mysql_host',dest='mysql_host',type=str,metavar='mysql_host',default='localhost',help='Enter Mysql Host!!')
    parse.add_option('-u','--mysql_user',dest='mysql_user',type=str,metavar='mysql_user',default='root',help='Enter Mysql User!!')
    parse.add_option('-p','--mysql_pwd',dest='mysql_pwd',type=str,metavar='mysql_pwd',default='root',help='Enter Mysql Password!!')
    parse.add_option('-d','--mysql_db',dest='mysql_db',type=str,metavar='mysql_db',default='',help='Enter Mysql DB!!')
    options,args=parse.parse_args()


    select_sql = """
    select script_base_path, file_name, version from sc_script
    """

    mysql_host = options.mysql_host
    mysql_user = options.mysql_user
    mysql_pwd = options.mysql_pwd
    mysql_db = options.mysql_db
    res = select(mysql_host, mysql_user, mysql_pwd, mysql_db, sql = select_sql)

    s3_prefix = "s3://cfdp"

    for r in res:
        s3_file = "{}/{}/{}_{}".format(s3_prefix,*r)
        print(s3_file)
        if not s3_file_exists(s3_file):
            print("========= script {} 不存在".format(s3_file))

