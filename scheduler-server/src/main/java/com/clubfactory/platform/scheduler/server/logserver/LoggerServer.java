package com.clubfactory.platform.scheduler.server.logserver;


import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.clubfactory.platform.scheduler.common.Constants;
import com.clubfactory.platform.scheduler.core.utils.DBConnTestUtil;
import com.clubfactory.platform.scheduler.core.utils.FileUtils;
import com.clubfactory.platform.scheduler.common.utils.OSUtils;
import com.clubfactory.platform.scheduler.dal.enums.DbType;
import com.clubfactory.platform.scheduler.logger.grpc.*;
import com.clubfactory.platform.scheduler.logger.vo.DBConnVO;
import com.clubfactory.platform.scheduler.logger.vo.LogVO;
import com.google.protobuf.ByteString;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *  logger server
 */
public class LoggerServer {

    private static  final Logger logger = LoggerFactory.getLogger(LoggerServer.class);

    /**
     *  server
     */
    private Server server;

    private ExecutorService executors;


    public void start() throws IOException {
	    /* The port on which the server should run */
        int port = Constants.RPC_PORT;
        executors = Executors.newSingleThreadExecutor();
        server = ServerBuilder.forPort(port)
                .addService(new LogViewServiceGrpcImpl(executors))
                .build()
                .start();
        logger.info("server started, listening on port : {}" , port);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                logger.info("shutting down gRPC server since JVM is shutting down");
                LoggerServer.this.stop();
                logger.info("server shut down");
            }
        });
    }

    private void stop() {
        if (server != null) {
            server.shutdown();
        }
        if (executors != null) {
            executors.shutdownNow();
        }
    }

    /**
     * await termination on the main thread since the grpc library uses daemon threads.
     */
    private void awaitTermination() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }


    /**
     * main launches the server from the command line.
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        final LoggerServer server = new LoggerServer();
        server.start();
        server.awaitTermination();
    }


    static class LogViewServiceGrpcImpl extends LogViewServiceGrpc.LogViewServiceImplBase {

        private ExecutorService executors;

        private String defaultTaskLogDir;

        private LogViewServiceGrpcImpl(ExecutorService executors){
            this.executors = executors;
            this.defaultTaskLogDir = System.getProperty("LOG_PATH");
            if (StringUtils.isNotBlank(defaultTaskLogDir)){
                this.defaultTaskLogDir = new File(this.defaultTaskLogDir).getAbsolutePath();
            }else {
                this.defaultTaskLogDir = new File("./logs").getAbsolutePath();
            }
            this.defaultTaskLogDir = this.defaultTaskLogDir +  "/task_instance";

        }

        @Override
        public void expireLogNdaysAgo(ExpireLogReq request,
                                      StreamObserver<RetStrInfo> responseObserver) {

            executors.execute(() -> {
                String dirPath = request.getDir();
                if (StringUtils.isBlank(dirPath)) {
                    dirPath = this.defaultTaskLogDir;
                }

                File[] files = FileUtils.listFiles(dirPath,request.getNDays());
                if (files == null || files.length <= 0) {
                    return;
                }
                for (File f : files) {
                    if (f.exists() && f.isFile()) {
                        logger.info("delete expire file {}", f.getName());
                        f.delete();
                    }
                }
            });


            RetStrInfo retInfoBuild = RetStrInfo.newBuilder()
                    .setMsg("")
                    .build();
            responseObserver.onNext(retInfoBuild);
            responseObserver.onCompleted();
        }


        @Override
        public void testDBConn(DBConnReq request,
                               StreamObserver<RetStrInfo> responseObserver) {
            logger.info("test conn {} on {}",request.getJdbcUrl(), OSUtils.getHost());
            boolean connSuccess = false;
            String errMsg = "";
            try {
                String jdbcUrl = request.getJdbcUrl();
                String jdbcUser = request.getJdbcUser();
                String jdbcPassword = request.getJdbcPassword();
                DbType dbType = DbType.valueOf(request.getJdbcType());
                int timeout = request.getTimeout();
                connSuccess = DBConnTestUtil.isConnSuccess(jdbcUrl, jdbcUser, jdbcPassword, dbType, timeout);
            }catch (Exception e){
                errMsg = e.getMessage();
            }
            DBConnVO dbConnVO = DBConnVO.builder()
                    .status(connSuccess)
                    .errMsg(errMsg)
                    .build();
            RetStrInfo retInfoBuild = RetStrInfo.newBuilder()
                    .setMsg(
                            JSONObject.toJSONString(dbConnVO, SerializerFeature.PrettyFormat)
                    )
                    .build();
            responseObserver.onNext(retInfoBuild);
            responseObserver.onCompleted();
        }

        @Override
        public void getLogOnLastNRows(LastNRowsReq request,
                                      StreamObserver<RetStrInfo> responseObserver) {

            logger.info("log parameter path : {} ,pull last {} rows",
                    request.getPath(),
                    request.getRows());
            LogVO logVO = readLastNRows(request.getPath(), StandardCharsets.UTF_8,request.getRows());
            RetStrInfo retInfoBuild = RetStrInfo.newBuilder()
                    .setMsg(
                            JSONObject.toJSONString(logVO, SerializerFeature.PrettyFormat)
                    )
                    .build();
            responseObserver.onNext(retInfoBuild);
            responseObserver.onCompleted();

        }

        @Override
        public void rollViewLog(LogParameter request, StreamObserver<RetStrInfo> responseObserver) {

            logger.info("log parameter path : {} ,skip line : {}, limit : {}",
                    request.getPath(),
                    request.getSkipLineNum(),
                    request.getLimit());
            List<String> list = readFile(request.getPath(), request.getSkipLineNum(), request.getLimit());
            StringBuilder sb = new StringBuilder();
            int offset = 0;

            if (list != null) {
                offset = request.getSkipLineNum();
                for (String line : list) {
                    offset += 1;
                    sb.append(line).append("\r\n");
                }
            }else {
                sb.append("no such log file: ");
                sb.append(request.getPath());
            }
            LogVO logVo =  LogVO.builder()
                    .offset(offset)
                    .content(sb.toString())
                    .build();
            RetStrInfo retInfoBuild = RetStrInfo.newBuilder().setMsg(
                    JSONObject.toJSONString(logVo, SerializerFeature.PrettyFormat)
            ).build();
            responseObserver.onNext(retInfoBuild);
            responseObserver.onCompleted();
        }

        @Override
        public void viewLog(PathParameter request, StreamObserver<RetStrInfo> responseObserver) {
            logger.info("task path is : {} " , request.getPath());
            RetStrInfo retInfoBuild = RetStrInfo.newBuilder().setMsg(readFile(request.getPath())).build();
            responseObserver.onNext(retInfoBuild);
            responseObserver.onCompleted();
        }

        @Override
        public void getLogBytes(PathParameter request, StreamObserver<RetByteInfo> responseObserver) {
            try {
                ByteString bytes = ByteString.copyFrom(getFileBytes(request.getPath()));
                RetByteInfo.Builder builder = RetByteInfo.newBuilder();
                builder.setData(bytes);
                responseObserver.onNext(builder.build());
                responseObserver.onCompleted();
            }catch (Exception e){
                logger.error("get log bytes failed",e);
            }
        }
    }

    /**
     *  get files bytes
     * @param path
     * @return
     * @throws Exception
     */
    private static byte[] getFileBytes(String path){
        InputStream in = null;
        ByteArrayOutputStream bos = null;
        try {
            in = new FileInputStream(path);
            bos  = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int len = 0;
            while ((len = in.read(buf)) != -1) {
                bos.write(buf, 0, len);
            }
            return bos.toByteArray();
        }catch (IOException e){
            logger.error("get file bytes error",e);
        }finally {
            if (bos != null){
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (in != null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return new byte[0];
    }

    /**
     *  read file content
     * @param path
     * @param skipLine
     * @param limit
     * @return
     */
    private static List<String> readFile(String path,int skipLine,int limit){
        try (Stream<String> stream = Files.lines(Paths.get(path))) {
            return stream.skip(skipLine).limit(limit).collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("read file failed",e);
        }
        return null;
    }

    /**
     * read  file content
     * @param path
     * @return
     * @throws Exception
     */
    private static String readFile(String path){
        BufferedReader br = null;
        String line = null;
        StringBuilder sb = new StringBuilder();
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8));
            while ((line = br.readLine()) != null){
                sb.append(line + "\r\n");
            }

            return sb.toString();
        }catch (IOException e){
            logger.error("read file failed",e);
        }finally {
            try {
                if (br != null){
                    br.close();
                }
            } catch (IOException e) {
                logger.error(e.getMessage(),e);
            }
        }
        return null;
    }


    /**
     * 读取文件最后N行
     * 相当于Linux系统中的tail命令 读取大小限制是2GB
     * @param filename 文件名
     * @param charset  文件编码格式,传null默认使用defaultCharset
     * @param rows     读取行数
     * @throws IOException
     */
    private static LogVO readLastNRows(final String filename, Charset charset, final int rows) {
        charset = charset == null ? Charset.defaultCharset() : charset;
        LogVO logVO = LogVO.builder()
                .offset(0)
                .build();
        String lineSeparator = "\n";
        int fileTotalLines = getFileTotalLines(filename);
        if (fileTotalLines < 0){
            logVO.setContent("日志文件不存在或为空");
            return logVO;
        }
        try (RandomAccessFile rf = new RandomAccessFile(filename, "r")) {
            byte[] sepBuffer = new byte[lineSeparator.getBytes().length];
            // 在获取到指定行数和读完文档之前,从文档末尾向前移动指针,遍历文档每一个字节查找换行符来统计行数
            for (long pointer = rf.length(), currRows = 0; pointer >= 0 && currRows < rows;) {
                // 移动指针
                rf.seek(pointer--);
                // 读取数据
                int readLength = rf.read(sepBuffer);
                if (readLength != -1 && new String(sepBuffer, 0, readLength).equals(lineSeparator)) {
                    currRows ++;
                }
                //扫描完依然没有找到足够的行数,将指针归0
                if (pointer == -1) {
                    rf.seek(0);
                }
            }
            byte[] buffer = new byte[(int) (rf.length() - rf.getFilePointer())];
            rf.readFully(buffer);
            logVO.setContent(new String(buffer, charset));
            logVO.setOffset(fileTotalLines);
        } catch (IOException e) {
            logger.error("read file failed :{}",e.getMessage());
            logVO.setContent(String.format("read file failed : %s", e.getMessage()));
        }
        return logVO;
    }


    /**
     * 获取文件总行数
     * @param filePath
     * @return
     */
    private static int getFileTotalLines(String filePath) {
        try (LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(filePath))){
            lineNumberReader.skip(Long.MAX_VALUE);
            int lineNumber = lineNumberReader.getLineNumber();
            return lineNumber + 1;
        } catch (IOException e) {
            return -1;
        }
    }

}