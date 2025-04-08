import pymysql
import optparse
import logging
from aes_utils import *
# from cftool.aes_encryptor import *
# aes_utils = aes_encryptor()
aes_utils = aes_utils()


log = logging.getLogger(__name__)
logging.basicConfig(format='%(asctime)s - %(levelname)s - %(message)s', level=logging.INFO)


def update_or_delete(mysql_host = 'localhost', mysql_user = 'root', mysql_pwd = '', mysql_db='', sql= ''):
    try:
        conn = pymysql.connect(mysql_host,user = mysql_user, passwd = mysql_pwd,db = mysql_db)
    except Exception as e:
        log.error('Mysql服务器连接失败', e)
        return

    try:
        cursor=conn.cursor()
        cursor.execute(sql)
        log.info('update / delete data Successful')
        conn.commit()
    except Exception as e:
        log.error("数据更新/删除失败", e)
        conn.rollback()
    finally:
        cursor.close()
        conn.close()


def select(mysql_host = 'localhost', mysql_user = 'root', mysql_pwd = '', mysql_db='', sql= ''):
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
    select id,encrypt_pwd,pwd_key from sc_collect_db
    """

    mysql_host = options.mysql_host
    mysql_user = options.mysql_user
    mysql_pwd = options.mysql_pwd
    mysql_db = options.mysql_db
    res = select(mysql_host, mysql_user, mysql_pwd, mysql_db, sql = select_sql)
    for r in res:
        encrpt_pwd = r[1]
        pwd_key = r[2]
        if encrpt_pwd == None:
            print("跳过Null值")
        else:
            py_decrypt_pwd = aes_utils.decrypt(encrpt_pwd,pwd_key)
            upgrade_sql = """
            update sc_collect_db set py_decrypt_pwd = "{}" where id = {}
            """.format(py_decrypt_pwd, r[0])
            print(upgrade_sql)
            update_or_delete(mysql_host, mysql_user, mysql_pwd, mysql_db, upgrade_sql)