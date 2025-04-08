package com.clubfactory.platform.scheduler.core.utils;

import com.clubfactory.platform.scheduler.common.Constants;
import com.clubfactory.platform.scheduler.core.enums.ResStorageFSType;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static com.clubfactory.platform.scheduler.common.Constants.*;
import static com.clubfactory.platform.scheduler.core.utils.PropertyUtils.*;


/**
 * common utils
 */
public class CommonUtils {

  private static final Logger logger = LoggerFactory.getLogger(CommonUtils.class);

  /**
   * @return get the path of system environment variables
   */
  public static String getSystemEnvPath() {
    String envPath = getString(SCHEDULER_ENV_PATH);
    if (StringUtils.isEmpty(envPath)) {
      envPath = System.getProperty("user.home") + File.separator + ".bash_profile";
    }

    return envPath;
  }


  /**
   * 判断是否开启多租户模式
   * @return
   */
  public static boolean isMultiTenantModeStartup(){
    Boolean state = getBoolean(Constants.PROCESS_EXEC_MULTI_TENANT_MODE_STARTUP_STATE);
    return state == null ? false : state;
  }

  /**
   * if upload resource is HDFS and kerberos startup is true , else false
   * @return
   */
  public static boolean getKerberosStartupState(){
    String resUploadStartupType = getString(Constants.RESOURCE_STORAGE_DFS_TYPE,"HDFS");
    ResStorageFSType resUploadType = ResStorageFSType.valueOf(resUploadStartupType);
    Boolean kerberosStartupState = getBoolean(Constants.HADOOP_KERBEROS_AUTHENTICATION_ENABLE,false);
    return resUploadType == ResStorageFSType.HDFS && kerberosStartupState;
  }

  /**
   * load kerberos configuration
   * @throws Exception
   */
  public static void loadKerberosConf()throws Exception{
    if (CommonUtils.getKerberosStartupState())  {
      System.setProperty(JAVA_SECURITY_KRB5_CONF, getString(JAVA_SECURITY_KRB5_CONF_PATH));
      Configuration configuration = new Configuration();
      configuration.set(HADOOP_SECURITY_AUTHENTICATION, KERBEROS);
      UserGroupInformation.setConfiguration(configuration);
      UserGroupInformation.loginUserFromKeytab(getString(LOGIN_USER_KEY_TAB_USERNAME),
              getString(Constants.LOGIN_USER_KEY_TAB_PATH));
    }
  }
}
