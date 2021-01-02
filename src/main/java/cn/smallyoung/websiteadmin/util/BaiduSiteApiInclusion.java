package cn.smallyoung.websiteadmin.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpStatus;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 百度API收录
 * @author smallyoung
 * @data 2021/1/2
 */
@Component
public class BaiduSiteApiInclusion {


    @Value("${baidu.siteUrl}")
    private String baiduSiteUrl;

    public String inclusion(String url) {
        return this.inclusion(Collections.singletonList(url));
    }

    public String inclusion(List<String> urls) {
        URLConnection urlConnection;
        StringBuilder result = new StringBuilder();
        PrintWriter postPrintWriter;
        BufferedReader bufferedReader;
        int statusCode;
        try {
            urlConnection = new URL(baiduSiteUrl).openConnection();
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            HttpURLConnection httpUrlConnection = (HttpURLConnection) urlConnection;
            postPrintWriter = new PrintWriter(httpUrlConnection.getOutputStream());
            //构造请求参数
            String param = urls.stream().filter(StrUtil::isNotBlank).map(String::trim)
                    .collect(Collectors.joining("\n"));
            //发送参数
            postPrintWriter.print(param);
            //刷新输出流缓冲
            postPrintWriter.flush();
            statusCode = httpUrlConnection.getResponseCode();
            switch (statusCode) {
                case HttpStatus.HTTP_OK:
                    //通过BufferedReader输入流来读取Url的响应
                    bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        result.append(line);
                    }
                    String resultT = result.toString();
                    JSONObject jsonObject = JSONObject.parseObject(resultT);
                    Object t = jsonObject.get("not_same_site");
                    Object successInt = jsonObject.get("success");
                    if(successInt.equals(0)){
                        result.delete(0, result.length());
                        result.append("提交失败,");

                    }else if (successInt.equals(1)){
                        result.delete(0, result.length());
                        result.append("提交成功");
                    }
                    if (t != null) {
                        result.append("接口调用地址与提交的网址不匹配");
                    }
                    break;
                case HttpStatus.HTTP_BAD_REQUEST:
                    result.append("站点未在站长平台验证");
                    break;
                case HttpStatus.HTTP_UNAUTHORIZED:
                    result.append("接口调用地址 错误");
                    break;
                case HttpStatus.HTTP_NOT_FOUND:
                    result.append("接口地址填写错误");
                    break;
                case HttpStatus.HTTP_INTERNAL_ERROR:
                    result.append("服务器偶然异常，通常重试就会成功");
                    break;
                default:
                    result.append("未知错误");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toString();
    }
}
