package com.meitianhui.member.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/**
 * Http请求工具类
 * <p>
 *     GET请求、POST请求操作
 * </p>
 * @author Janda
 *         Created by D.Yang on 2017/11/14 0014.
 */
public class HttpClientUtils {
    /**
     * GET 请求
     * @param url      请求地址
     * @param map      请求参数
     * @param encoding 请求编码
     * @return
     */
    public static String get(String url, Map<String, String> map, String encoding) {
        /* 创建HttpClient 对象 */
        CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
        CloseableHttpResponse closeableHttpResponse = null;

        /* 创建返回数据 */
        String result = null;
        try {
            /* 装填请求参数 */
            List<NameValuePair> nameValuePairList = new ArrayList<>();
            if (map != null) {
                /* 遍历请求参数封装 */
                for (Map.Entry<String, String> enrty : map.entrySet()) {
                    nameValuePairList.add(new BasicNameValuePair(enrty.getKey(), enrty.getValue()));
                }
            }
            /* 转换为键值对 */
            String parameterValue = EntityUtils.toString(new UrlEncodedFormEntity(nameValuePairList, Consts.UTF_8));
            /* 创建Get请求对象 */
            HttpGet httpGet = new HttpGet(url + "?" + parameterValue);
            httpGet.addHeader("X-Requested-With", "XMLHttpRequest");
            /* 执行Get请求操作 */
            closeableHttpResponse = closeableHttpClient.execute(httpGet);
            /* 获取请求响应数据 */
            HttpEntity httpEntity = closeableHttpResponse.getEntity();
            if (httpEntity != null) {
                /* 获取的值转换为键值对 */
                result = EntityUtils.toString(httpEntity, encoding);
            }
            /* 关闭 输入流、释放资源 */
            EntityUtils.consume(httpEntity);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            /* 关闭响应连接 */
            if (closeableHttpResponse != null) {
                try {
                    closeableHttpResponse.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            /* 关闭相应的HttpClient 连接 */
            if (closeableHttpClient != null) {
                try {
                    closeableHttpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }
    /**
     * GET 缺省请求
     * @param url 请求地址
     * @return
     */
    public static String get(String url) {
        return get(url, null, "UTF-8");
    }
    /**
     * GET 请求
     * @param url 请求地址
     * @param map 请求参数
     * @return
     */
    public static String get(String url, Map<String, String> map) {
        return get(url, map, "UTF-8");
    }
    /**
     * POST 请求
     * @param url 请求地址
     * @param map 请求参数
     * @param encoding 请求编码
     * @return
     */
    public static String post(String url,Map<String,String> map,String encoding){
        /* 创建HttpClient 对象 */
        CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
        CloseableHttpResponse closeableHttpResponse = null;
        /* 创建返回数据 */
        String result = null;
        try {
            /* 装填请求参数 */
            List<NameValuePair> nameValuePairList = new ArrayList<>();
            if (map != null) {
                /* 遍历请求参数封装 */
                for (Map.Entry<String, String> enrty : map.entrySet()) {
                    nameValuePairList.add(new BasicNameValuePair(enrty.getKey(), enrty.getValue()));
                }
            }
            /* 创建Post请求对象 */
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader("X-Requested-With", "XMLHttpRequest");
            httpPost.addHeader("s", "/mian_fei/check.html");
            /* 设置参数到请求对象中去 */
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairList,"utf-8"));
            /* 执行Post请求操作 */
            closeableHttpResponse = closeableHttpClient.execute(httpPost);
            /* 获取请求响应数据 */
            HttpEntity httpEntity = closeableHttpResponse.getEntity();
            if (httpEntity != null) {
                /* 获取的值转换为键值对 */
                result = EntityUtils.toString(httpEntity, encoding);
            }
            /* 关闭 输入流、释放资源 */
            EntityUtils.consume(httpEntity);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            /* 关闭响应连接 */
            if (closeableHttpResponse != null) {
                try {
                    closeableHttpResponse.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            /* 关闭相应的HttpClient 连接 */
            if (closeableHttpClient != null) {
                try {
                    closeableHttpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }
    /**
     * POST 缺省请求
     * @param url 请求地址
     * @return
     */
    public static String post(String url) {
        return post(url, null, "UTF-8");
    }
    /**
     * POST 请求
     * @param url 请求地址
     * @param map 请求参数
     * @return
     */
    public static String post(String url, Map<String, String> map) {
        return post(url, map, "UTF-8");
    }

    /**
     * <p>临时调用</p>
     * <p>
     *     调用方式：<br/>
     *     Map<String,Object> map = new HashMap<String,Object>();<br/>
     *     <p>
     *     字符串参数<br/>
     *     map.put("str","str");<br/><br/>
     *
     *     文件参数<br/>
     *     map,put("file",new File("/home/file.jpg"));
     *     </p>
     * </p>
     * @param url 请求地址
     * @param map 请求参数
     * @param encoding 编码
     * @return 返回Json格式结果
     */
    @Deprecated
    public static String postFile(String url,Map<String,Object> map,String encoding){
        /* 创建HttpClient 对象 */
        CloseableHttpClient closeableHttpClient = HttpClients.createDefault();
        CloseableHttpResponse closeableHttpResponse = null;
        /* 创建返回数据 */
        String result = null;
        try {
            /* 装填请求参数 */
            MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
            if (map != null) {
                /* 遍历请求参数封装 */
                for (Map.Entry<String, Object> enrty : map.entrySet()) {
                    /* 判断是否是文件类型 */
                    if(enrty.getValue() instanceof File){
                        /* 文件类型上传 */
                        FileBody binFileBody = new FileBody((File) enrty.getValue());
                        /* 添加文件上传 */
                        multipartEntityBuilder.addPart(enrty.getKey(), binFileBody);
                    }else if(enrty.getValue() instanceof String) {
                        /* 字符串参数 */
                        multipartEntityBuilder.addPart(enrty.getKey(),new StringBody(enrty.getValue().toString(), ContentType.create("text/plain",Consts.UTF_8)));
                    }
                }
            }
            /* 创建Post请求对象 */
            HttpPost httpPost = new HttpPost(url);
            /* 设置参数到请求对象中去 */
            HttpEntity reqEntity = multipartEntityBuilder.build();
            httpPost.setEntity(reqEntity);
            /* 执行Post请求操作 */
            closeableHttpResponse = closeableHttpClient.execute(httpPost);
            /* 获取请求响应数据 */
            HttpEntity httpEntity = closeableHttpResponse.getEntity();
            if (httpEntity != null) {
                /* 获取的值转换为键值对 */
                result = EntityUtils.toString(httpEntity, encoding);
            }
            /* 关闭 输入流、释放资源 */
            EntityUtils.consume(httpEntity);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            /* 关闭实体内容 */
            if (closeableHttpResponse != null) {
                try {
                    closeableHttpResponse.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            /* 关闭相应的HttpClient 连接 */
            if (closeableHttpClient != null) {
                try {
                    closeableHttpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }
}
