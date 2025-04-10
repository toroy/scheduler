package com.clubfactory.platform.scheduler.core.utils;

import com.clubfactory.platform.scheduler.common.utils.OSUtils;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

import static com.clubfactory.platform.scheduler.common.Constants.*;
import static com.clubfactory.platform.scheduler.core.utils.PropertyUtils.getString;


/**
 * file utils
 */
public class FileUtils {
    public static final Logger logger = LoggerFactory.getLogger(FileUtils.class);

    /**
     * 列出指定文件夹下N天前的文件
     * @param dirPath
     * @param nDays
     * @return
     */
    public static File[] listFiles(String dirPath,int nDays){
        File dir = new File(dirPath);
        if (!dir.exists() || !dir.isDirectory()){
            logger.error("dir {} not exists or is not a directory",dirPath);
            return null;
        }
        long currentTime = System.currentTimeMillis();
        return dir.listFiles(f -> {
            if (f.isDirectory()){
                return false;
            }
            long lastModified = f.lastModified();
            return  currentTime - nDays * 24 * 3_600 * 1_000 > lastModified;
        });
    }

    /**
     * get file suffix
     *
     * @param filename
     * @return file suffix
     */
    public static String suffix(String filename) {
        String fileSuffix = "";
        if (StringUtils.isNotEmpty(filename)) {
            int lastIndex = filename.lastIndexOf(".");
            if (lastIndex > 0) {
                fileSuffix = filename.substring(lastIndex + 1);
            }
        }
        return fileSuffix;
    }



    /**
     * directory of process execution
     * @param taskId
     * @return directory of process execution
     */
    public static String getProcessExecDir(long taskId) {
        // ${process.exec.basepath}/task_instance/${taskId}_${timestamp}
        String processBasePath = getProcessBasePath();
        if (processBasePath.endsWith("/")){
            processBasePath = StringUtils.substringBeforeLast(processBasePath,"/");
        }
        return String.format("%s/task_instance/%s_%s",
                processBasePath,
                taskId,
                System.currentTimeMillis());
    }

    /**
     * 获取任务执行时工作目录的basePath
     * @return
     */
    public static String getProcessBasePath(){
        return StringUtils.isEmpty(getString(PROCESS_EXEC_BASE_PATH)) ?
                System.getProperty("user.home") : getString(PROCESS_EXEC_BASE_PATH);
    }


    /**
     * create directory and user
     * @param execLocalPath
     * @param userName
     * @param logger
     * @throws IOException
     */
    public static void createWorkDirAndUserIfAbsent(String execLocalPath, String userName, Logger logger) throws IOException{
        //if work dir exists, first delete
        File execLocalPathFile = new File(execLocalPath);
        if (execLocalPathFile.exists()){
            org.apache.commons.io.FileUtils.forceDelete(execLocalPathFile);
        }
        //create work dir
        org.apache.commons.io.FileUtils.forceMkdir(execLocalPathFile);
        //if not exists this user,then create
        if (StringUtils.isNotEmpty(userName) && !OSUtils.getUserList().contains(userName)){
            String userGroup = OSUtils.getGroup();
            if (StringUtils.isNotEmpty(userGroup)){
                logger.info("create os user : {}",userName);
                String cmd = String.format("sudo useradd -g %s %s",userGroup,userName);
                logger.info("execute cmd : {}",cmd);
                OSUtils.exeCmd(cmd);
            }
        }

    }




    /**
     * write content to file ,if parent path not exists, it will do one's utmost to mkdir
     *
     * @param content       content
     * @param filePath      target file path
     * @return
     */
    public static boolean writeContent2File(String content, String filePath) {
        boolean flag = true;
        BufferedReader bufferedReader = null;
        BufferedWriter bufferedWriter = null;
        try {
            File distFile = new File(filePath);
            if (!distFile.getParentFile().exists()) {
                distFile.getParentFile().mkdirs();
            }
            bufferedReader = new BufferedReader(new StringReader(content));
            bufferedWriter = new BufferedWriter(new FileWriter(distFile));
            char[] buf = new char[1024];
            int len;
            while ((len = bufferedReader.read(buf)) != -1) {
                bufferedWriter.write(buf, 0, len);
            }
            bufferedWriter.flush();
            bufferedReader.close();
            bufferedWriter.close();
        } catch (IOException e) {
            FileUtils.logger.error(e.getMessage(), e);
            flag = false;
        } finally {
            IOUtils.closeQuietly(bufferedWriter);
            IOUtils.closeQuietly(bufferedReader);
        }
        return flag;
    }

    /**
     * Writes a String to a file creating the file if it does not exist.
     *
     * NOTE: As from v1.3, the parent directories of the file will be created
     * if they do not exist.
     *
     * @param file  the file to write
     * @param data  the content to write to the file
     * @param encoding  the encoding to use, {@code null} means platform default
     * @throws IOException in case of an I/O error
     * @throws UnsupportedEncodingException if the encoding is not supported by the VM
     * @since 2.4
     */
    public static void writeStringToFile(File file, String data, Charset encoding) throws IOException {
        writeStringToFile(file, data, encoding, false);
    }

    /**
     * Writes a String to a file creating the file if it does not exist.
     *
     * NOTE: As from v1.3, the parent directories of the file will be created
     * if they do not exist.
     *
     * @param file  the file to write
     * @param data  the content to write to the file
     * @param encoding  the encoding to use, {@code null} means platform default
     * @throws IOException in case of an I/O error
     * @throws UnsupportedEncodingException if the encoding is not supported by the VM
     */
    public static void writeStringToFile(File file, String data, String encoding) throws IOException {
        writeStringToFile(file, data, encoding, false);
    }

    /**
     * Writes a String to a file creating the file if it does not exist.
     *
     * @param file  the file to write
     * @param data  the content to write to the file
     * @param encoding  the encoding to use, {@code null} means platform default
     * @param append if {@code true}, then the String will be added to the
     * end of the file rather than overwriting
     * @throws IOException in case of an I/O error
     * @since 2.3
     */
    public static void writeStringToFile(File file, String data, Charset encoding, boolean append) throws IOException {
        OutputStream out = null;
        try {
            out = openOutputStream(file, append);
            IOUtils.write(data, out, encoding);
            out.close(); // don't swallow close Exception if copy completes normally
        } finally {
            IOUtils.closeQuietly(out);
        }
    }

    /**
     * Writes a String to a file creating the file if it does not exist.
     *
     * @param file  the file to write
     * @param data  the content to write to the file
     * @param encoding  the encoding to use, {@code null} means platform default
     * @param append if {@code true}, then the String will be added to the
     * end of the file rather than overwriting
     * @throws IOException in case of an I/O error
     * @throws UnsupportedCharsetException
     *             thrown instead of {@link UnsupportedEncodingException} in version 2.2 if the encoding is not
     *             supported by the VM
     * @since 2.1
     */
    public static void writeStringToFile(File file, String data, String encoding, boolean append) throws IOException {
        writeStringToFile(file, data, Charsets.toCharset(encoding), append);
    }

    /**
     * Writes a String to a file creating the file if it does not exist using the default encoding for the VM.
     *
     * @param file  the file to write
     * @param data  the content to write to the file
     * @throws IOException in case of an I/O error
     */
    public static void writeStringToFile(File file, String data) throws IOException {
        writeStringToFile(file, data, Charset.defaultCharset(), false);
    }

    /**
     * Writes a String to a file creating the file if it does not exist using the default encoding for the VM.
     *
     * @param file  the file to write
     * @param data  the content to write to the file
     * @param append if {@code true}, then the String will be added to the
     * end of the file rather than overwriting
     * @throws IOException in case of an I/O error
     * @since 2.1
     */
    public static void writeStringToFile(File file, String data, boolean append) throws IOException {
        writeStringToFile(file, data, Charset.defaultCharset(), append);
    }

    /**
     * Opens a {@link FileOutputStream} for the specified file, checking and
     * creating the parent directory if it does not exist.
     * <p>
     * At the end of the method either the stream will be successfully opened,
     * or an exception will have been thrown.
     * <p>
     * The parent directory will be created if it does not exist.
     * The file will be created if it does not exist.
     * An exception is thrown if the file object exists but is a directory.
     * An exception is thrown if the file exists but cannot be written to.
     * An exception is thrown if the parent directory cannot be created.
     *
     * @param file  the file to open for output, must not be {@code null}
     * @return a new {@link FileOutputStream} for the specified file
     * @throws IOException if the file object is a directory
     * @throws IOException if the file cannot be written to
     * @throws IOException if a parent directory needs creating but that fails
     * @since 1.3
     */
    public static FileOutputStream openOutputStream(File file) throws IOException {
        return openOutputStream(file, false);
    }

    /**
     * Opens a {@link FileOutputStream} for the specified file, checking and
     * creating the parent directory if it does not exist.
     * <p>
     * At the end of the method either the stream will be successfully opened,
     * or an exception will have been thrown.
     * <p>
     * The parent directory will be created if it does not exist.
     * The file will be created if it does not exist.
     * An exception is thrown if the file object exists but is a directory.
     * An exception is thrown if the file exists but cannot be written to.
     * An exception is thrown if the parent directory cannot be created.
     *
     * @param file  the file to open for output, must not be {@code null}
     * @param append if {@code true}, then bytes will be added to the
     * end of the file rather than overwriting
     * @return a new {@link FileOutputStream} for the specified file
     * @throws IOException if the file object is a directory
     * @throws IOException if the file cannot be written to
     * @throws IOException if a parent directory needs creating but that fails
     * @since 2.1
     */
    public static FileOutputStream openOutputStream(File file, boolean append) throws IOException {
        if (file.exists()) {
            if (file.isDirectory()) {
                throw new IOException("File '" + file + "' exists but is a directory");
            }
            if (!file.canWrite()) {
                throw new IOException("File '" + file + "' cannot be written to");
            }
        } else {
            File parent = file.getParentFile();
            if (parent != null) {
                if (!parent.mkdirs() && !parent.isDirectory()) {
                    throw new IOException("Directory '" + parent + "' could not be created");
                }
            }
        }
        return new FileOutputStream(file, append);
    }


    /**
     * deletes a directory recursively
     * @param dir
     */

    public static void deleteDir(String dir) throws IOException {
        org.apache.commons.io.FileUtils.deleteDirectory(new File(dir));
    }

    /**
     * Deletes a file. If file is a directory, delete it and all sub-directories.
     * <p>
     * The difference between File.delete() and this method are:
     * <ul>
     * <li>A directory to be deleted does not have to be empty.</li>
     * <li>You get exceptions when a file or directory cannot be deleted.
     *      (java.io.File methods returns a boolean)</li>
     * </ul>
     *
     * @param filename
     * @throws IOException in case deletion is unsuccessful
     */
    public static void deleteFile(String filename) throws IOException {
        org.apache.commons.io.FileUtils.forceDelete(new File(filename));
    }


    /**
     * 若文件存在，强制进行递归删除
     * @param filename
     * @throws IOException
     */
    public static void deleteIfExists(String filename) throws IOException {
        File file = new File(filename);
        if (file.exists()) {
            org.apache.commons.io.FileUtils.forceDelete(file);
        }
    }

    /**
     * Gets all the parent subdirectories of the parentDir directory
     * @param parentDir
     * @return
     */
    public static File[] getAllDir(String parentDir){
        if(parentDir == null || "".equals(parentDir)) {
            throw new RuntimeException("parentDir can not be empty");
        }
        File file = new File(parentDir);
        if(!file.exists() || !file.isDirectory()) {
            throw new RuntimeException("parentDir not exist, or is not a directory:"+parentDir);
        }
        return file.listFiles(pathname -> {
            if (pathname.isDirectory()) {
                return true;
            }
            else {
                return false;
            }
        });
    }

    /**
     * Get Content
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static String readFile2Str(InputStream inputStream) throws IOException{
        String all_content;
        try {
            InputStream ins = inputStream;
            ByteArrayOutputStream outputstream = new ByteArrayOutputStream();
            byte[] str_b = new byte[1024];
            int i = -1;
            while ((i=ins.read(str_b)) > 0) {
                outputstream.write(str_b,0,i);
            }
            all_content = outputstream.toString();
            return all_content;
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            throw new RuntimeException(e);
        }
    }


    /**
     * 获取文件大小(bytes)
     * @param filePath
     * @return
     */
    public static long getFileSize(String filePath) {
        try {
            File f = new File(filePath);
            if (f.exists() && f.isFile()){
                return f.length();
            }else{
                return -1;
            }
        } catch (Exception e) {
            return -1;
        }
    }

}
