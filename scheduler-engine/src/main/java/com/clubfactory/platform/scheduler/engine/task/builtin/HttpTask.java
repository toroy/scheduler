package com.clubfactory.platform.scheduler.engine.task.builtin;

import com.alibaba.fastjson.JSONObject;
import com.clubfactory.platform.scheduler.core.enums.HttpParametersType;
import com.clubfactory.platform.scheduler.core.utils.Bytes;
import com.clubfactory.platform.scheduler.common.utils.DateUtils;
import com.clubfactory.platform.scheduler.core.utils.JSONUtils;
import com.clubfactory.platform.scheduler.core.utils.LoggerUtils;
import com.clubfactory.platform.scheduler.core.vo.TaskVO;
import com.clubfactory.platform.scheduler.engine.task.builtin.param.HttpParameters;
import com.clubfactory.platform.scheduler.engine.task.builtin.param.HttpProperty;
import com.clubfactory.platform.scheduler.spi.param.IParameters;
import com.clubfactory.platform.scheduler.spi.plugin.AbstractTask;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.Charsets;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author xiejiajun
 */
public class HttpTask extends AbstractTask {
    /**
     * Convert mill seconds to second unit
     */
    protected static final int MAX_CONNECTION_MILLISECONDS = 60000;
    protected static final String APPLICATION_JSON = "application/json";
    protected String output;

    public HttpTask(TaskVO taskInfo, Logger logger, StateTracker stateTracker) {
        super(taskInfo, logger, stateTracker);
    }

    @Override
    protected Class<? extends IParameters> getParameterType() {
        return HttpParameters.class;
    }

    @Override
    protected String fileExt() {
        return null;
    }

    @Override
    public void handle() throws Exception {
        // TaskLogInfo-%s
        String threadLoggerInfoName = String.format(LoggerUtils.TASK_LOGGER_THREAD_NAME + "-%s", this.taskName);
        Thread.currentThread().setName(threadLoggerInfoName);

        long startTime = System.currentTimeMillis();
        String statusCode;
        String body;
        StringBuilder responseHeader = new StringBuilder();
        HttpParameters httpParameters = this.getParameter();
        try(CloseableHttpClient client = createHttpClient()) {
            try(CloseableHttpResponse response = sendRequest(client)) {
                statusCode = String.valueOf(getStatusCode(response));
                body = getResponseBody(response);
                for (Header header:response.getAllHeaders()){
                    responseHeader.append("\n\t\t");
                    responseHeader.append(header.getName());
                    responseHeader.append(" = ");
                    responseHeader.append(header.getValue());
                }
                exitStatusCode = validResponse(body, statusCode);
                long costTime = System.currentTimeMillis() - startTime;
                logger.info("startTime: {}\n, httpUrl: {}\n, httpMethod: {}\n, costTime : {} Millisecond\n, " +
                                "statusCode : {}\n, body : {}\n, errMsg : {}\n,header:{}",
                        DateUtils.format2Readable(startTime), httpParameters.getUrl(),httpParameters.getHttpMethod(),
                        costTime, statusCode, body, output,responseHeader.toString());
            }catch (Exception e) {
                appendMessage(e.toString());
                exitStatusCode = -1;
                logger.error("httpUrl[" + httpParameters.getUrl() + "] connection failed："+output, e);
            }
        } catch (Exception e) {
            appendMessage(e.toString());
            exitStatusCode = -1;
            logger.error("httpUrl[" + httpParameters.getUrl() + "] connection failed："+output, e);
        }
    }

    @Override
    protected void parseTaskConfigs(){
        Map<String,String> allParam = JSONUtils.toMap(this.taskInfo.getParams());
        HttpParameters httpParameters = this.getParameter();
        if (allParam != null && allParam.get("httpParams") != null){
            // 变量拍平
            allParam.remove("httpParams");
            for (HttpProperty httpProperty: httpParameters.getHttpParams()){
                allParam.put(httpProperty.getProp(),httpProperty.getValue());
            }
        }
        if (MapUtils.isNotEmpty(allParam)){
            this.taskParams.putAll(allParam);
        }
    }

    protected CloseableHttpResponse sendRequest(CloseableHttpClient client) throws IOException {
        RequestBuilder builder = createRequestBuilder();
        List<HttpProperty> httpPropertyList = new ArrayList<>();
        HttpParameters httpParameters = this.getParameter();
        if(httpParameters.getHttpParams() != null && httpParameters.getHttpParams().size() > 0){
            for (HttpProperty httpProperty: httpParameters.getHttpParams()) {
                String params = this.convertVariable(JSONObject.toJSONString(httpProperty));
                logger.info("http request params：{}",params);
                httpPropertyList.add(JSONObject.parseObject(params,HttpProperty.class));
            }
        }
        addRequestParams(builder,httpPropertyList);
        HttpUriRequest request = builder.setUri(httpParameters.getUrl()).build();
        setHeaders(request,httpPropertyList);
        return client.execute(request);
    }

    protected String getResponseBody(CloseableHttpResponse httpResponse) throws ParseException, IOException {
        if (httpResponse == null) {
            return null;
        }
        HttpEntity entity = httpResponse.getEntity();
        if (entity == null) {
            return null;
        }
        String webPage = EntityUtils.toString(entity, Bytes.UTF8_ENCODING);
        return webPage;
    }

    protected int getStatusCode(CloseableHttpResponse httpResponse) {
        int status = httpResponse.getStatusLine().getStatusCode();
        return status;
    }

    protected int validResponse(String body, String statusCode){
        int exitStatusCode = 0;
        HttpParameters httpParameters = this.getParameter();
        switch (httpParameters.getHttpCheckCondition()) {
            case BODY_CONTAINS:
                if (StringUtils.isEmpty(body) || !body.contains(httpParameters.getCondition())) {
                    appendMessage(httpParameters.getUrl() + " doesn contain "
                            + httpParameters.getCondition());
                    exitStatusCode = -1;
                }
                break;
            case BODY_NOT_CONTAINS:
                if (StringUtils.isEmpty(body) || body.contains(httpParameters.getCondition())) {
                    appendMessage(httpParameters.getUrl() + " contains "
                            + httpParameters.getCondition());
                    exitStatusCode = -1;
                }
                break;
            case STATUS_CODE_CUSTOM:
                if (!statusCode.equals(httpParameters.getCondition())) {
                    appendMessage(httpParameters.getUrl() + " statuscode: " + statusCode + ", Must be: " + httpParameters.getCondition());
                    exitStatusCode = -1;
                }
                break;
            default:
                if (!"200".equals(statusCode)) {
                    appendMessage(httpParameters.getUrl() + " statuscode: " + statusCode + ", Must be: 200");
                    exitStatusCode = -1;
                }
                break;
        }
        return exitStatusCode;
    }

    public String getOutput() {
        return output;
    }

    protected void appendMessage(String message) {
        if (output == null) {
            output = "";
        }
        if (message != null && !message.trim().isEmpty()) {
            output += message;
        }
    }

    protected void addRequestParams(RequestBuilder builder,List<HttpProperty> httpPropertyList) {
        if(httpPropertyList != null && httpPropertyList.size() > 0){
            JSONObject jsonParam = new JSONObject();
            for (HttpProperty property: httpPropertyList){
                if(property.getHttpParametersType() != null){
                    if (property.getHttpParametersType().equals(HttpParametersType.PARAMETER)){
                        builder.addParameter(property.getProp(), property.getValue());
                    }else if(property.getHttpParametersType().equals(HttpParametersType.BODY)){
                        jsonParam.put(property.getProp(), property.getValue());
                    }
                }
            }
            if (jsonParam.size() > 0) {
                StringEntity postingString = new StringEntity(jsonParam.toString(), Charsets.UTF_8);
                postingString.setContentEncoding(Bytes.UTF8_ENCODING);
                postingString.setContentType(APPLICATION_JSON);
                builder.setEntity(postingString);
            }
        }
    }

    protected void setHeaders(HttpUriRequest request,List<HttpProperty> httpPropertyList) {
        if(httpPropertyList != null && httpPropertyList.size() > 0){
            for (HttpProperty property: httpPropertyList){
                if(property.getHttpParametersType() != null) {
                    if (property.getHttpParametersType().equals(HttpParametersType.HEADERS)) {
                        request.addHeader(property.getProp(), property.getValue());
                    }
                }
            }
        }
    }

    protected CloseableHttpClient createHttpClient() {
        final RequestConfig requestConfig = requestConfig();
        HttpClientBuilder httpClientBuilder;
        httpClientBuilder = HttpClients.custom().setDefaultRequestConfig(requestConfig);
        return httpClientBuilder.build();
    }

    private RequestConfig requestConfig() {
        return RequestConfig.custom().setSocketTimeout(MAX_CONNECTION_MILLISECONDS).setConnectTimeout(MAX_CONNECTION_MILLISECONDS).build();
    }

    protected RequestBuilder createRequestBuilder() {
        HttpParameters httpParameters = this.getParameter();
        switch (httpParameters.getHttpMethod()){
            case GET:
                return RequestBuilder.get();
            case PUT:
                return RequestBuilder.put();
            case HEAD:
                return RequestBuilder.head();
            case POST:
                return RequestBuilder.post();
            case DELETE:
                return RequestBuilder.delete();
            default:
                return null;
        }
    }

    @Override
    protected String buildCommand() throws Exception {
        return null;
    }

    @Override
    protected List<String> buildCommandList() throws Exception {
        return null;
    }
}
