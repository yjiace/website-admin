package cn.smallyoung.websiteadmin.util;

import com.upyun.RestManager;
import com.upyun.UpException;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * 又拍云接口调用公共接口，
 * https://github.com/upyun/java-sdk
 *
 * @author smallyoung
 * @date 2020/8/17
 */
@Component
public class UPYunUtil {


    private static String bucketName;


    private static String userName;


    private static String password;

    private volatile static RestManager manager;

    @Value("${upyun.bucketName}")
    public void setBucketName(String bucketName) {
        UPYunUtil.bucketName = bucketName;
    }

    @Value("${upyun.userName}")
    public void setUserName(String userName) {
        UPYunUtil.userName = userName;
    }

    @Value("${upyun.password}")
    public void setPassword(String password) {
        UPYunUtil.password = password;
    }

    public static RestManager getManager() {
        if (manager == null) {
            synchronized (UPYunUtil.class){
                if (manager == null) {
                    manager = new RestManager(bucketName, userName, password);
                    //手动设置超时时间：默认为30秒
                    manager.setTimeout(60);
                    //选择最优的接入点,ED_AUTO:根据网络条件自动选择接入点;ED_TELECOM:电信接入点;ED_CNC:联通网通接入点;ED_CTT:移动铁通接入点
                    manager.setApiDomain(RestManager.ED_AUTO);
                }
            }
        }
        return manager;
    }

    /**
     * 创建目录
     *
     * @param path 目录路径，以/结尾
     */
    public static Response mkDir(String path) throws IOException, UpException {
        return getManager().mkDir(path);
    }

    /**
     * 删除目录
     *
     * @param path 目录路径
     * @return 结果为 true 删除目录成功;若待删除的目录 path 下还存在任何文件或子目录，将返回『不允许删除』的错误
     */
    public static Response rmDir(String path) throws IOException, UpException {
        return getManager().rmDir(path);
    }

    /**
     * 获取目录文件列表
     *
     * @param path   目录路径
     * @param params 可选参数
     */
    public static Response readDirIter(String path, Map<String, String> params) throws IOException, UpException {
        return getManager().readDirIter(path, params);
    }

    /**
     * 上传文件
     *
     * @param filePath 保存到又拍云存储的文件路径，以/开始
     * @param data     byte[]类型的文件数据
     * @param params   上传额外可选参数
     */
    public static Response writeFile(String filePath, byte[] data, Map<String, String> params) throws IOException, UpException {
        return getManager().writeFile(filePath, data, params);
    }

    /**
     * 上传文件
     *
     * @param filePath 保存到又拍云存储的文件路径，以/开始
     * @param file     file文件类型的文件数据
     * @param params   上传额外可选参数
     */
    public static Response writeFile(String filePath, File file, Map<String, String> params) throws IOException, UpException {
        return getManager().writeFile(filePath, file, params);
    }

    /**
     * 上传文件
     *
     * @param filePath    保存到又拍云存储的文件路径，以/开始
     * @param inputStream inputStream数据流类型的文件数据
     * @param params      上传额外可选参数
     */
    public static Response writeFile(String filePath, InputStream inputStream, Map<String, String> params) throws IOException, UpException {
        return getManager().writeFile(filePath, inputStream, params);
    }

    /**
     * 删除文件
     * @param filePath 文件在又拍云的路径
     * @param params 可选参数 可为 null
     */
    public static Response deleteFile(String filePath,Map<String, String> params) throws IOException, UpException {
        return getManager().deleteFile(filePath, params);
    }

    /**
     * 获取文件信息
     * @param filePath 又拍云中文件的路径
     */
    public static Response getFileInfo(String filePath) throws IOException, UpException {
        return getManager().getFileInfo(filePath);
    }
}
