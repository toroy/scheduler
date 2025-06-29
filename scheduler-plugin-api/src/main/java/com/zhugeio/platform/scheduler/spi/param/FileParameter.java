package com.zhugeio.platform.scheduler.spi.param;

import lombok.Data;

import java.util.List;

@Data
public class FileParameter {
    private String key;
    private List<String> dfsFiles;
    private List<String> localFiles;
}
