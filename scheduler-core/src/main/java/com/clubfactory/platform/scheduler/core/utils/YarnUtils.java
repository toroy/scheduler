package com.clubfactory.platform.scheduler.core.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.clubfactory.platform.scheduler.common.Constants;
import com.clubfactory.platform.scheduler.core.exception.MultiActiveStepException;
import com.clubfactory.platform.scheduler.core.model.AppInfo;
import com.clubfactory.platform.scheduler.dal.enums.TaskStatusEnum;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.client.cli.RMAdminCLI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static com.clubfactory.platform.scheduler.common.Constants.*;
import static com.clubfactory.platform.scheduler.common.Constants.COLON;
import static com.clubfactory.platform.scheduler.core.utils.PropertyUtils.*;

/**
 * @author xiejiajun
 */
public class YarnUtils {
    private static final Logger logger = LoggerFactory.getLogger(YarnUtils.class);

    private static String superUser = getString(Constants.YARN_SUPER_USER);
    private static volatile YarnUtils instance = new YarnUtils();
    private static volatile Configuration configuration;


    private YarnUtils(){
        if(StringUtils.isEmpty(superUser)){
            superUser = YARN_DEFAULT_SUPER_USER;
        }
        init();
    }

    private void init() {

        if (configuration == null) {
            synchronized (YarnUtils.class) {
                if (configuration == null) {
                    try {
                        configuration = new Configuration();

                        String[] rmHaIds = getArray(YARN_RM_HA_HOSTS);
                        String  activeRMAppStateApi = getYarnAppStateRestApiTemplate(rmHaIds);
                        if (ArrayUtils.isNotEmpty(rmHaIds)) {
                            logger.info("activeRMAppStateApi is {} , rmHaHosts is {}",activeRMAppStateApi,rmHaIds);
                            activeRMAppStateApi = getActiveRMAppStateRestApi(activeRMAppStateApi, rmHaIds);
                            logger.info("Yarn Application State Http Address : {}", activeRMAppStateApi);
                        }
                        configuration.set(Constants.YARN_RM_APP_STATE_ADDRESS, activeRMAppStateApi);
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }

                }
            }
        }
    }

    public static YarnUtils getInstance(){
        // if kerberos startup , renew YarnUtils
        if (CommonUtils.getKerberosStartupState()){
            return new YarnUtils();
        }
        return instance;
    }

    /**
     * 组装RM state REST API URL模版
     * @return
     */
    private String getYarnAppStateRestApiTemplate(String[] rmHaHosts){
        if (ArrayUtils.isEmpty(rmHaHosts)){
            return null;
        }
        String rm1 = rmHaHosts[0];
        String restPort = StringUtils.isNotBlank(getString(YARN_RM_HTTP_PORT)) ? getString(YARN_RM_HTTP_PORT) : "8088";
        return String.format("http://%s:%s/ws/v1/cluster/apps/",rm1,restPort) + "%s";
    }

    /**
     * get the state of an application
     *
     * @param applicationId
     * @return the return may be null or there may be other parse exceptions
     * @throws JSONException
     * @throws IOException
     */
    public TaskStatusEnum getApplicationStatus(String applicationId) throws JSONException {
        return getApplicationStatus(applicationId,null);
    }


    /**
     * get application url
     *
     * @param applicationId
     * @return
     */
    public String getApplicationUrl(String applicationId,String activeRMTemplate) {
        if (StringUtils.isBlank(activeRMTemplate)) {
            String appStateAddress = configuration.get(YARN_RM_APP_STATE_ADDRESS);
            if (StringUtils.isNotBlank(appStateAddress)) {
                return String.format(appStateAddress, applicationId);
            }
            return null;
        }
        return String.format(activeRMTemplate, applicationId);
    }

    /**
     * getAppAddress
     *
     * @param appAddress
     * @param rmHaIds
     * @return
     */
    public static String getActiveRMAppStateRestApi(String appAddress, String[] rmHaIds) {

        //get active ResourceManager
        String activeRM = YarnHAAdminUtils.getActiveRMName(rmHaIds);
        if (activeRM == null){
            return null;
        }

        // http://rm1:8088/ws/v1/cluster/apps/%s
        String[] split1 = appAddress.split(DOUBLE_SLASH);

        if (split1.length != 2) {
            return null;
        }

        String start = split1[0] + DOUBLE_SLASH;
        String[] split2 = split1[1].split(COLON);

        if (split2.length != 2) {
            return null;
        }

        String end = COLON + split2[1];

        return start + activeRM + end;
    }


    /**
     * yarn ha admin utils
     */
    private static final class YarnHAAdminUtils extends RMAdminCLI {

        /**
         * 获取当前活动的RM
         * @param rmIdArr
         * @return
         */
        public static String getActiveRMName(String[] rmIdArr) {
            int activeResourceManagerPort = getInt(YARN_RM_HTTP_PORT, 8088);
            String yarnUrl = "http://%s:" + activeResourceManagerPort + "/ws/v1/cluster/info";
            logger.info("resourcemanager hosts is {}, size is {}",String.join(",",rmIdArr),rmIdArr.length);
            for (String rmHost : rmIdArr){
                try {
                    String url = String.format(yarnUrl, rmHost);
                    String state = getRMState(url);
                    logger.info("trace resourcemanager {} state {}",url,state);
                    if (YARN_RM_STATE_ACTIVE.equalsIgnoreCase(state)){
                        return rmHost;
                    }
                }catch (Exception e){
                    logger.info("{} is standby or shutdown",rmHost);
                }
            }
            return null;
        }


        /**
         * get ResourceManager state
         *
         * @param url
         * @return
         */
        public static String getRMState(String url) {

            String retStr = HttpUtils.get(url);

            if (StringUtils.isEmpty(retStr)) {
                return null;
            }
            //to json
            JSONObject jsonObject = JSON.parseObject(retStr);

            //get ResourceManager state
            String state = jsonObject.getJSONObject("clusterInfo").getString("haState");
            return state;
        }

    }


    /**
     * 获取其他Yarn集群上active的RM对应的app state查询api
     * @param stateApiUrl : http://<hostname>:<port>/ws/v1/cluster/apps/%s
     * @param remoteRMIds
     */
    public String getRemoteActiveRMAppStateApiTemplate(String stateApiUrl,String[] remoteRMIds){
        String remoteActiveRMTemplate = getActiveRMAppStateRestApi(stateApiUrl, remoteRMIds);
        logger.info("remoteActiveRMTemplate : {}", remoteActiveRMTemplate);
        return remoteActiveRMTemplate;
    }

    /**
     * 通过指定的RM ids获取app状态
     * @param remoteRMIds
     * @param restPort
     * @param applicationId
     * @return
     */
    public TaskStatusEnum getRemoteApplicationStatus(String[] remoteRMIds,String restPort,String applicationId) {
        if (ArrayUtils.isEmpty(remoteRMIds)){
            return null;
        }
        String rm1 = remoteRMIds[0];
        restPort = StringUtils.isNotBlank(restPort) ? restPort : "8088";
        String stateApiUrl = String.format("http://%s:%s/ws/v1/cluster/apps/",rm1,restPort) + "%s";
        String remoteAppStateApiTemplate = getRemoteActiveRMAppStateApiTemplate(stateApiUrl,remoteRMIds);
        if (remoteAppStateApiTemplate == null){
            return null;
        }
        return getApplicationStatus(applicationId,remoteAppStateApiTemplate);
    }


    /**
     * 根据EMR临时集群的master DNS获取appId等信息
     * @param masterDNS
     */
    public AppInfo getTempClusterAppInfo(String masterDNS) throws MultiActiveStepException {
        String appApi = String.format("http://%s:8088/ws/v1/cluster/apps?states=accepted,running",masterDNS);
        String responseContent = HttpUtils.get(appApi);
        if (StringUtils.isBlank(responseContent)){
            return null;
        }
        JSONObject appsJson = JSONObject.parseObject(responseContent);
        if (appsJson == null){
            return null;
        }
        JSONObject apps = appsJson.getJSONObject("apps");
        if (apps == null){
            return null;
        }

        JSONArray appList = apps.getJSONArray("app");
        if (appList == null || appList.size() == 0){
            return null;
        }

        if (appList.size() > 1){
            throw new MultiActiveStepException("临时集群检测到多个同时运行的App");
        }
        // ACCEPTED RUNNING
        JSONObject app = appList.getJSONObject(0);
        String appId = app.get("id").toString();
        String appState = app.get("state").toString();
        return AppInfo.builder().appId(appId).appState(appState).build();

    }



    /**
     * 获取Yarn集群上App运行状态
     * @param applicationId
     * @param remoteActiveRMTemplate
     * @return
     * @throws JSONException
     */
     private TaskStatusEnum getApplicationStatus(String applicationId,String remoteActiveRMTemplate){
        if (StringUtils.isBlank(applicationId)) {
            logger.info("applicationId is null");
            return null;
        }

        String applicationUrl = getApplicationUrl(applicationId,remoteActiveRMTemplate);
        if (StringUtils.isBlank(applicationUrl)){
            logger.info("applicationUrl is null");
            return null;
        }

        String responseContent = HttpUtils.get(applicationUrl);
        JSONObject jsonObject = JSONObject.parseObject(responseContent);
         if (jsonObject == null){
             logger.info("application is not exists");
             return null;
         }
        // TODO..这里注意，CDH使用的Yarn和原生Yarn的Response结构不一样（各个版本间也可能有差异）,需要针对性更改该处响应值解析语句
        JSONObject app = jsonObject.getJSONObject("app");
        if (app == null){
            logger.info("application is not exists");
            return null;
        }
        String result = app.getString("state");
        String finalStatus = app.getString("finalStatus");
        if (result != null) {
            switch (result.toUpperCase()) {
                case SUCCEEDED:
                    return TaskStatusEnum.SUCCESS;
                case FAILED:
                    return TaskStatusEnum.FAILED;
                case KILLED:
                    return TaskStatusEnum.KILLED;
                case FINISHED:
                    if (FAILED.equalsIgnoreCase(finalStatus)) {
                        return TaskStatusEnum.FAILED;
                    } else {
                        return TaskStatusEnum.SUCCESS;
                    }
                case NEW:
                case NEW_SAVING:
                case SUBMITTED:
                case ACCEPTED:
                case RUNNING:
                default:
                    return TaskStatusEnum.RUNNING;
            }
        }
        return TaskStatusEnum.SUCCESS;
    }


}
