package com.clubfactory.platform.scheduler.server.core;


import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;

@Slf4j
public class DFSAccessTest {

    private String accessId;
    private String secretKey;
    private String bucketName;
    private String pathPrefix;
    private String hdfsDefaultFs;

    @Before
    public void before(){
        accessId = "s3AccessId";
        secretKey = "s3secretKey";
        // 文件路径:例如访问s3://bucket1/dir1时 ，bucketName = bucket1 pathPrefix = dir1
        bucketName = "bucket1";
        pathPrefix = "dir1";
        // hdfs://nnIp:8020
        hdfsDefaultFs = "hdfs://nnIp:8020";
    }

    @Test
    public void accessS3Test() throws Exception{
        URI uri = URI.create("s3a://" + bucketName);
        Configuration conf = new Configuration();
        conf.set("fs.s3.impl", "org.apache.hadoop.fs.s3a.S3AFileSystem");
        conf.set("fs.s3a.endpoint", "s3.us-west-2.amazonaws.com");
        conf.set("fs.s3a.access.key", accessId);
        conf.set("fs.s3a.secret.key", secretKey);
        conf.set("fs.defaultFS", "s3a://" + bucketName);
        FileSystem fileSystem = null;
        try {
            fileSystem = FileSystem.get(uri,conf,"hadoop");
            FileStatus[] sourceStatus = fileSystem.listStatus(new Path(String.format("s3://%s/%s/",bucketName,pathPrefix)));
            for (FileStatus sta : sourceStatus){
                log.info(sta.getPath().toString());
            }
            log.info("====================");
        } catch (IOException | InterruptedException e) {
            log.error(e.getMessage(),e);
            throw e;
        }
        final FileSystem finalFileSystem = fileSystem;
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (finalFileSystem != null){
                try {
                    finalFileSystem.close();
                } catch (IOException e) {
                    log.error(e.getMessage(),e);
                }
            }
        }));
    }



    @Test
    public void accessNativeOSSTest() throws IOException, InterruptedException {

        URI uri = URI.create("oss://" + bucketName);
        Configuration conf = new Configuration();
        conf.set("fs.oss.impl", "com.aliyun.fs.oss.nat.NativeOssFileSystem");
        conf.set("fs.oss.endpoint", "oss-ap-south-1.aliyuncs.com");
        conf.set("fs.oss.multipart.download.size", "102400");


        conf.set("fs.oss.accessKeyId","");
        conf.set("fs.oss.accessKeySecret","");
        conf.set("mapreduce.job.run-local","true");


        FileSystem fileSystem;
        try {
            fileSystem = FileSystem.get(uri,conf,"hadoop");
            Path path = new Path("/data_platform/");
            FileStatus[] sourceStatus = fileSystem.listStatus(path);
            for (FileStatus sta : sourceStatus){
                log.info(sta.getPath().toString());
            }
        } catch (IOException | InterruptedException e) {
            log.error(e.getMessage(),e);
            throw e;
        }
        final FileSystem finalFileSystem = fileSystem;
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                finalFileSystem.close();
            } catch (IOException e) {
                log.error(e.getMessage(),e);
            }
        }));
    }


    @Test
    public void accessHdfsTest() throws Exception{
        URI uri = URI.create(hdfsDefaultFs);
        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", hdfsDefaultFs);
        FileSystem fileSystem;
        try {
            fileSystem = FileSystem.get(uri,conf,"hadoop");
            FileStatus[] sourceStatus = fileSystem.listStatus(new Path("/user"));
            for (FileStatus sta : sourceStatus){
                log.info(sta.getPath().toString());
            }
            log.info("====================");
        } catch (IOException | InterruptedException e) {
            log.error(e.getMessage(),e);
            throw e;
        }
        final FileSystem finalFileSystem = fileSystem;
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                finalFileSystem.close();
            } catch (IOException e) {
                log.error(e.getMessage(),e);
            }
        }));
    }
}
