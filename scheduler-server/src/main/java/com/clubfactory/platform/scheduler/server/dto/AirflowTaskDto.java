package com.clubfactory.platform.scheduler.server.dto;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author xiejiajun
 */
@Data
@Deprecated
public class AirflowTaskDto implements Serializable {


    public static final String AIRFLOW_TASK_METADATA = "--airflow_task_metadata=";
    public static final String AIRFLOW_TASK_METADATA_PATTEN = AIRFLOW_TASK_METADATA+".*\\|.*\\|[^,\"\\s+]*";

    private static final long serialVersionUID = 5544232800235310113L;

    private String dagId;

    private String taskId;

    private String executeDateSuffix;

    private String executeTime;

    private boolean isOnlyOnGaia;

    private boolean isValid;

    public AirflowTaskDto(String airflowTaskParam){
        if (StringUtils.isBlank(airflowTaskParam)){
            this.isValid = false;
            return;
        }
        String []array = airflowTaskParam.split("\\|");
        if (array.length < 3){
            this.isValid = false;
            return;
        }
        this.dagId = array[0];
        this.taskId = array[1];
        this.executeDateSuffix = array[2];
        this.isOnlyOnGaia = false;
        if (array.length > 3) {
            if (StringUtils.isNotBlank(array[3]) && array[3].trim().equalsIgnoreCase("TRUE")) {
                this.isOnlyOnGaia = true;
            }
        }
        this.isValid = true;
    }


    public boolean checkParameters(){
        return isValid && StringUtils.isNotBlank(dagId)
                && StringUtils.isNotBlank(taskId)
                && StringUtils.isNotBlank(executeDateSuffix);
    }

    public static String replaceAirflowMetaToEmpty(String jobParam){
        Pattern pattern = Pattern.compile(AIRFLOW_TASK_METADATA_PATTEN);
        Matcher matcher = pattern.matcher(jobParam);
        if (matcher.find()){
            return jobParam.replace(matcher.group(),"");
        }
        return jobParam;
    }

}
