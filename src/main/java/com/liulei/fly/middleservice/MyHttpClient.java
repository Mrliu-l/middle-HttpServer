package com.liulei.fly.middleservice;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

/**
 * @author liu_l
 * @email: liu_lei_programmer@163.com
 * @time 2019/1/21 16:43
 * @Description: 描述:
 */
public class MyHttpClient {
    private HttpClient httpClient;
    private String cookies;

    public MyHttpClient(String ip, int port) {
        httpClient = new HttpClient();
        httpClient.getHostConfiguration().setHost(ip, port, "http");
        httpClient.getParams().setCookiePolicy(
                CookiePolicy.BROWSER_COMPATIBILITY);
    }

    public String getCookies(){
        return cookies;
    }

    public void setCookies(String cookies){
        this.cookies = cookies;
    }

    public String doGet(String url) throws IOException{
        return doGet(url,null);
    }

    public String doGet(String url, String paras) throws IOException {
        GetMethod method = new GetMethod(url);
        if (StringUtils.isNotBlank(paras)) {
            method.setQueryString(URIUtil.encodeQuery(paras));
        }
        return sendRequest(method);
    }

    public String doPost(String url, Map<String,String> paras) throws IOException{
        int index=0;
        NameValuePair[] data = new NameValuePair[paras.size()];
        for(Map.Entry<String,String> entry:paras.entrySet()){
            data[index++] = new NameValuePair(entry.getKey(),entry.getValue());
        }
        return doPost(url,data);
    }

    private String doPost(String url, NameValuePair[] data) throws IOException{
        PostMethod method = new PostMethod(url);
        method.setRequestHeader("loginCompcode","41311016120100001");
        method.setRequestHeader("compcode","41311016120100001");
        method.setRequestHeader("userName", "41311016120100001");
        method.setRequestBody(data);
        method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,"utf-8");
        return sendRequest(method);
    }

    private String sendRequest(HttpMethod method) throws IOException{
        StringBuffer response = new StringBuffer();
        try {
            method.setRequestHeader("token", cookies);
            httpClient.executeMethod(method);

            if (method.getStatusCode() == HttpStatus.SC_OK) {
                response.append(getResponseBody(method));
            }else if(method.getStatusCode() == HttpStatus.SC_MOVED_TEMPORARILY){
                //302 重定向
                response.append(getResponseHeader(method));
            }else {
                System.out.println("response statuscode : "
                        + method.getStatusCode());
                throw new IllegalStateException("response statuscode : "
                        + method.getStatusCode());
            }
            fetchCookies();
        }finally {
            method.releaseConnection();
        }
        return response.toString();
    }

    private void fetchCookies(){
        Cookie[] values = httpClient.getState().getCookies();
        StringBuffer cookieBuffer = new StringBuffer();
        for (Cookie c : values)
        {
            cookieBuffer.append(c.toString()).append(";");
        }
        cookies = cookieBuffer.toString();
    }

    private String getResponseBody(HttpMethod method) throws IOException, IOException {
        StringBuffer response = new StringBuffer();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(
                    method.getResponseBodyAsStream(), "UTF-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line).append(
                        System.getProperty("line.separator"));
            }
        }finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
        }
        return response.toString();
    }

    private String getResponseHeader(HttpMethod method){
        StringBuffer response = new StringBuffer();
        Header[] headers = method.getResponseHeaders();
        for(Header header:headers){
            String line = header.getName()+" : "+header.getValue();
            response.append(line);
            response.append(System.getProperty("line.separator"));
        }
        return response.toString();
    }
}
