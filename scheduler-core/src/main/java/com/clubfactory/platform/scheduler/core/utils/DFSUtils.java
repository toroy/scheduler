package com.clubfactory.platform.scheduler.core.utils;

import com.clubfactory.platform.scheduler.common.Constants;
import com.clubfactory.platform.scheduler.core.enums.ResStorageFSType;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.security.UserGroupInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.PrivilegedExceptionAction;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;


import static com.clubfactory.platform.scheduler.common.Constants.*;
import static com.clubfactory.platform.scheduler.core.utils.PropertyUtils.*;

/**
 * hadoop utils
 * single instance
 */
public class DFSUtils implements Closeable {

    private static final Logger logger = LoggerFactory.getLogger(DFSUtils.class);

    private static String hdfsUser = getString(Constants.DFS_SUPER_USER);
    private static volatile DFSUtils instance = new DFSUtils();
    private static volatile Configuration configuration;
    private static FileSystem fs;


    private DFSUtils(){
        if(StringUtils.isEmpty(hdfsUser)){
            hdfsUser = getString(Constants.DFS_SUPER_USER);
        }
        init();
        initDFSPath();
    }

    public static DFSUtils getInstance(){
        // if kerberos startup , renew DFSUtils
        if (CommonUtils.getKerberosStartupState()){
            return new DFSUtils();
        }
        return instance;
    }

    /**
     * init scheduler root path in dfs
     */
    private void initDFSPath(){
        String dfsPath = getString(Constants.DATA_STORAGE_DFS_BASE_PATH);
        Path path = new Path(dfsPath);

        try {
            if (!fs.exists(path)) {
                fs.mkdirs(path);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
    }


    /**
     * init hadoop configuration
     */
    private void init() {
        if (configuration == null) {
            synchronized (DFSUtils.class) {
                if (configuration == null) {
                    try {
                        configuration = new Configuration();

                        String resUploadStartupType = getString(Constants.RESOURCE_STORAGE_DFS_TYPE);
                        ResStorageFSType resStorageFSType = ResStorageFSType.valueOf(resUploadStartupType);

                        switch (resStorageFSType){
                            case S3:
                                configuration.set(FS_DEFAULT_FS,getString(FS_DEFAULT_FS));
                                configuration.set(FS_S3A_ENDPOINT,getString(FS_S3A_ENDPOINT));

                                configuration.set("fs.s3a.connection.maximum",getString("fs.s3a.connection.maximum"));
                                configuration.set("fs.s3a.attempts.maximum",getString("fs.s3a.attempts.maximum"));
                                configuration.set("fs.s3a.connection.establish.timeout",getString("fs.s3a.connection.establish.timeout"));
                                configuration.set("fs.s3a.connection.timeout",getString("fs.s3a.connection.timeout"));
                                configuration.set("fs.s3a.threads.max",getString("fs.s3a.threads.max"));
                                configuration.set("fs.s3a.threads.keepalivetime",getString("fs.s3a.threads.keepalivetime"));

                                if (StringUtils.isNotEmpty(getString(FS_S3A_ACCESS_KEY))) {
                                    configuration.set(FS_S3A_ACCESS_KEY, getString(FS_S3A_ACCESS_KEY));
                                    configuration.set(FS_S3A_SECRET_KEY, getString(FS_S3A_SECRET_KEY));
                                }else {
                                    // 兼容白名单认证方式
                                    configuration.set(FS_S3A_CREDENTIALS_PROVIDER,FS_S3A_DEFAULT_CREDENTIALS_PROVIDER);
                                }
                                fs = FileSystem.get(configuration);
                                break;
                            case OSS:
                                configuration.set("fs.oss.impl", "com.aliyun.fs.oss.nat.NativeOssFileSystem");
                                configuration.set("mapreduce.job.run-local","true");
                                configuration.set(FS_DEFAULT_FS,getString(FS_DEFAULT_FS));
                                configuration.set(FS_OSS_ENDPOINT, getString(FS_OSS_ENDPOINT));
                                configuration.set(FS_OSS_ACCESS_KEY_ID,getString(FS_OSS_ACCESS_KEY_ID));
                                configuration.set(FS_OSS_ACCESS_KEY_SECRET,getString(FS_OSS_ACCESS_KEY_SECRET));

                                if (StringUtils.isNotBlank(getString(FS_OSS_SECURITY_TOKEN))){
                                    configuration.set(FS_OSS_SECURITY_TOKEN,getString(FS_OSS_SECURITY_TOKEN));
                                }
                                if (StringUtils.isNotBlank(FS_OSS_MULTIPART_DOWNLOAD_SIZE)) {
                                    configuration.set(FS_OSS_MULTIPART_DOWNLOAD_SIZE, getString(FS_OSS_MULTIPART_DOWNLOAD_SIZE));
                                }
                                fs = FileSystem.get(configuration);

                                break;
                            case COS:
                                configuration.set("fs.cosn.impl", "org.apache.hadoop.fs.CosFileSystem");
                                configuration.set("fs.AbstractFileSystem.cosn.impl", "org.apache.hadoop.fs.CosN");
                                configuration.set("fs.cosn.tmp.dir", "/tmp/hadoop_cos");
                                configuration.set("fs.cosn.bucket.region", "ap-shanghai");
                                configuration.set("fs.cosn.userinfo.secretId",getString(FS_COS_ACCESS_KEY_ID));
                                configuration.set("fs.cosn.userinfo.secretKey",getString(FS_COS_ACCESS_KEY_SECRET));
                                configuration.set("fs.ofs.user.appid", "1318812101");
//                                configuration.set("fs.cosn.upload.buffer", "mapped_disk");

//                                configuration.set(FS_DEFAULT_FS,getString(FS_DEFAULT_FS));
//                                configuration.set("fs.cosn.credentials.provider","rg.apache.hadoop.fs.auth.SimpleCredentialProvider");
//                                configuration.set("fs.cosn.bucket.endpoint_suffix", "cos.ap-shanghai.myqcloud.com");
//                                configuration.set("fs.cosn.upload.buffer.size", "134217728");
//                                configuration.set("fs.cosn.upload.part.size", "8388608");
//                                configuration.set("fs.cosn.maxRetries", "3");
//                                configuration.set("fs.cosn.retry.interval.seconds", "3");
                                fs = FileSystem.get(URI.create(getString(FS_DEFAULT_FS)),configuration);
                                break;
                            case HDFS:
                                default:
                                    if (getBoolean(Constants.HADOOP_KERBEROS_AUTHENTICATION_ENABLE)){
                                        System.setProperty(Constants.JAVA_SECURITY_KRB5_CONF,
                                                getString(Constants.JAVA_SECURITY_KRB5_CONF_PATH));
                                        configuration.set(Constants.HADOOP_SECURITY_AUTHENTICATION,"kerberos");
                                        UserGroupInformation.setConfiguration(configuration);
                                        UserGroupInformation.loginUserFromKeytab(getString(Constants.LOGIN_USER_KEY_TAB_USERNAME),
                                                getString(Constants.LOGIN_USER_KEY_TAB_PATH));
                                    }

                                    String defaultFS = configuration.get(FS_DEFAULT_FS);
                                    //first get key from core-site.xml hdfs-site.xml ,if null ,then try to get from properties file
                                    // the default is the local file system
                                    if(defaultFS.startsWith("file")){
                                        String defaultFSProp = getString(FS_DEFAULT_FS);
                                        if(StringUtils.isNotBlank(defaultFSProp)){
                                            Map<String, String> fsRelatedProps = getPrefixedProperties("fs.");
                                            configuration.set(FS_DEFAULT_FS,defaultFSProp);
                                            fsRelatedProps.entrySet().stream().forEach(entry -> configuration.set(entry.getKey(), entry.getValue()));
                                        }else{
                                            logger.error("property:{} can not to be empty, please set!",FS_DEFAULT_FS);
                                            throw new RuntimeException("property:{} can not to be empty, please set!");
                                        }
                                    }else{
                                        logger.info("get property:{} -> {}, from core-site.xml hdfs-site.xml ", FS_DEFAULT_FS, defaultFS);
                                    }

                                    if (fs == null) {
                                        if(StringUtils.isNotEmpty(hdfsUser)){
                                            UserGroupInformation ugi = UserGroupInformation.createRemoteUser(hdfsUser);
                                            ugi.doAs(new PrivilegedExceptionAction<Boolean>() {
                                                @Override
                                                public Boolean run() throws Exception {
                                                    fs = FileSystem.get(configuration);
                                                    return true;
                                                }
                                            });
                                        }else{
                                            logger.warn("hdfs.root.user is not set value!");
                                            fs = FileSystem.get(configuration);
                                        }
                                    }
                                    break;
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }

                }
            }
        }
    }

    /**
     * @return Configuration
     */
    public Configuration getConfiguration() {
        return configuration;
    }


    /**
     * cat file on dfs
     *
     * @param dfsFilePath  dfs file path
     * @return byte[]
     */
    public byte[] catFile(String dfsFilePath) throws IOException {

        if(StringUtils.isBlank(dfsFilePath)){
            logger.error("dfs file path:{} is blank",dfsFilePath);
            return null;
        }

        try (FSDataInputStream fsDataInputStream = fs.open(new Path(dfsFilePath))){
            return IOUtils.toByteArray(fsDataInputStream);
        }
    }



    /**
     * cat file on dfs
     *
     * @param dfsFilePath   dfs file path
     * @param skipLineNums  skip line numbers
     * @param limit         read how many lines
     * @return
     */
    public List<String> catFile(String dfsFilePath, int skipLineNums, int limit) throws IOException {

        if (StringUtils.isBlank(dfsFilePath)){
            logger.error("dfs file path:{} is blank",dfsFilePath);
            return null;
        }

        FSDataInputStream in = null;
        BufferedReader br = null;
        try {
            in = fs.open(new Path(dfsFilePath));
            br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            Stream<String> stream = br.lines().skip(skipLineNums).limit(limit);
            List<String> lines = stream.collect(Collectors.toList());
            return lines;
        }finally {
            try {
                if (br != null){
                    br.close();
                }
            }catch (Exception e){
                logger.error(e.getMessage());
            }
            try {
                if (in != null ){
                    in.close();
                }
            }catch (Exception e){
                logger.error(e.getMessage());
            }
        }
    }

    /**
     * make the given file and all non-existent parents into
     * directories. Has the semantics of Unix 'mkdir -p'.
     * Existence of the directory hierarchy is not an error.
     *
     * @param dfsPath path to create
     */
    public boolean mkdir(String dfsPath) throws IOException {
        return fs.mkdirs(new Path(dfsPath));
    }

    /**
     * copy files between FileSystems
     *
     * @param srcPath      source dfs path
     * @param dstPath      destination dfs path
     * @param deleteSource whether to delete the src
     * @param overwrite    whether to overwrite an existing file
     * @return 是否成功
     */
    public boolean copy(String srcPath, String dstPath, boolean deleteSource, boolean overwrite) throws IOException {
        return FileUtil.copy(fs, new Path(srcPath), fs, new Path(dstPath), deleteSource, overwrite, fs.getConf());
    }

    /**
     * the src file is on the local disk.  Add it to FS at
     * the given dst name.

     * @param srcFile       local file
     * @param dstDfsPath   destination dfs path
     * @param deleteSource  whether to delete the src
     * @param overwrite     whether to overwrite an existing file
     */
    public boolean copyLocalToDfs(String srcFile, String dstDfsPath, boolean deleteSource, boolean overwrite) throws IOException {
        Path srcPath = new Path(srcFile);
        Path dstPath= new Path(dstDfsPath);

        fs.copyFromLocalFile(deleteSource, overwrite, srcPath, dstPath);

        return true;
    }

    /**
     * copy dfs file to local
     *
     * @param srcDfsFilePath   source dfs file path
     * @param dstFile           destination file
     * @param deleteSource      delete source
     * @param overwrite         overwrite
     * @return
     * @throws IOException
     */
    public boolean copyDfsToLocal(String srcDfsFilePath, String dstFile, boolean deleteSource, boolean overwrite) throws IOException {
        Path srcPath = new Path(srcDfsFilePath);
        File dstPath = new File(dstFile);

        if (dstPath.exists()) {
            if (dstPath.isFile()) {
                if (overwrite) {
                    dstPath.delete();
                }else{
                    throw new IOException("destination file already exists!");
                }
            } else {
                throw new IOException("destination file must be a file!");
            }
        }

        if(!dstPath.getParentFile().exists()){
            dstPath.getParentFile().mkdirs();
        }

        return FileUtil.copy(fs, srcPath, dstPath, deleteSource, fs.getConf());
    }

    /**
     *
     * delete a file
     *
     * @param dfsFilePath the path to delete.
     * @param recursive if path is a directory and set to
     * true, the directory is deleted else throws an exception. In
     * case of a file the recursive can be set to either true or false.
     * @return  true if delete is successful else false.
     * @throws IOException
     */
    public boolean delete(String dfsFilePath, boolean recursive) throws IOException {
        return fs.delete(new Path(dfsFilePath), recursive);
    }

    /**
     * @param dfsFilePath
     * @param recursive
     */
    public void deleteIfExists(String dfsFilePath,boolean recursive) throws IOException {
        if (StringUtils.isNotBlank(dfsFilePath) && this.exists(dfsFilePath)){
            this.delete(dfsFilePath,recursive);
        }
    }

    /**
     * check if exists
     *
     * @param dfsFilePath source file path
     * @return
     */
    public boolean exists(String dfsFilePath) throws IOException {
        return fs.exists(new Path(dfsFilePath));
    }

    /**
     * Gets a list of files in the directory
     *
     * @param filePath
     * @return {@link FileStatus}
     */
    public FileStatus[] listFileStatus(String filePath)throws Exception{
        Path path = new Path(filePath);
        try {
            return fs.listStatus(new Path(filePath));
        } catch (IOException e) {
            logger.error("Get file list exception", e);
            throw new Exception("Get file list exception", e);
        }
    }

    /**
     * Renames Path src to Path dst.  Can take place on local fs
     * or remote DFS.
     * @param src path to be renamed
     * @param dst new path after rename
     * @throws IOException on failure
     * @return true if rename is successful
     */
    public boolean rename(String src, String dst) throws IOException {
        return fs.rename(new Path(src), new Path(dst));
    }



    /**
     *
     * @return data dfs path
     */
    public static String getDfsDataBasePath() {
        String basePath = getString(DATA_STORAGE_DFS_BASE_PATH);
        if ("/".equals(basePath)) {
            // if basepath is configured to /,  the generated url may be  //default/resources (with extra leading /)
            return "";
        } else {
            return basePath;
        }
    }

    /**
     * dfs resource dir
     *
     * @param tenantCode tenant code
     * @return dfs resource dir
     */
    public static String getDfsResDir(String tenantCode) {
        return String.format("%s/resources", getDfsTenantDir(tenantCode));
    }

    /**
     * dfs user dir
     *
     * @param tenantCode tenant code
     * @return dfs resource dir
     */
    public static String getDfsUserDir(String tenantCode,int userId) {
        return String.format("%s/home/%d", getDfsTenantDir(tenantCode),userId);
    }

    /**
     * dfs udf dir
     *
     * @param tenantCode tenant code
     * @return get udf dir on dfs
     */
    public static String getDfsUdfDir(String tenantCode) {
        return String.format("%s/udfs", getDfsTenantDir(tenantCode));
    }

//    /**
//     * get absolute path and name for file on dfs
//     *
//     * @param tenantCode tenant code
//     * @param filename   file name
//     * @return get absolute path and name for file on dfs
//     */
//    public static String getDfsFilename(String tenantCode, String filename) {
//        return String.format("%s/%s", getDfsResDir(tenantCode), filename);
//    }

    /**
     * get absolute path and name for file on dfs
     *
     * @param dfsFileBasePath
     * @param filename   file name
     * @return get absolute path and name for file on dfs
     */
    public static String getDfsFilePath(String dfsFileBasePath, String filename) {
        return String.format("%s/%s", dfsFileBasePath, filename);
    }

    public static String getDfsFileFullPath(String userResourceDir, String filename){
        String baseScheme = getString(SCHEDULER_SCRIPT_BASE_SCHEME,DEFAULT_SCHEDULER_SCRIPT_BASE_SCHEME);
        if (userResourceDir != null && userResourceDir.startsWith("/")){
            userResourceDir = StringUtils.substringAfter(userResourceDir,"/");
        }
        return String.format("%s/%s/%s", baseScheme,userResourceDir, filename);
    }

    /**
     * get absolute path and name for udf file on dfs
     *
     * @param tenantCode tenant code
     * @param filename   file name
     * @return get absolute path and name for udf file on dfs
     */
    public static String getDfsUdfFilename(String tenantCode, String filename) {
        return String.format("%s/%s", getDfsUdfDir(tenantCode), filename);
    }

    /**
     * @return file directory of tenants on dfs
     */
    public static String getDfsTenantDir(String tenantCode) {
        return String.format("%s/%s", getDfsDataBasePath(), tenantCode);
    }


    @Override
    public void close() throws IOException {
        if (fs != null) {
            try {
                fs.close();
            } catch (IOException e) {
                logger.error("Close DFSUtils instance failed", e);
                throw new IOException("Close DFSUtils instance failed", e);
            }
        }
    }



}
