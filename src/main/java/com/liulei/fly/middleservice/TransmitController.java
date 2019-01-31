package com.liulei.fly.middleservice;

import com.telek.business.jwtUtil.JwtUtil;
import com.telek.business.message.Param;
import com.telek.business.message.Token;
import com.telek.business.protoUtil.SerializeUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author liu_l
 * @email: liu_lei_programmer@163.com
 * @time 2019/1/21 17:00
 * @Description: 描述:
 */
@Controller
public class TransmitController {

    private static final Log log = LogFactory.getLog(TransmitController.class);

    private static final String charssetName = "ISO-8859-1";

    @ResponseBody
    @RequestMapping(path = "/transmit")
    public String transmit(HttpServletRequest request, @RequestBody Map<String, Object> map) throws IOException, NoSuchFieldException, IllegalAccessException {
        long s0 = System.currentTimeMillis();
        String ip = map.get("ip").toString();
        System.out.println("获取到请求；请求转发服务地址为" + ip);
        int port = Integer.valueOf(map.get("port").toString());
        MyHttpClient myHttpClient = new MyHttpClient(ip, port);
        myHttpClient.setCookies(getCookie(map));
        Map<String,String> paras = new HashMap<String,String>();
        String param = getParam(map);
        paras.put("param", param);
        String requestURI = request.getRequestURI();
        String url = requestURI.replace("/transmit.", "").replace("_","/" );
        long s1 = System.currentTimeMillis();
        String result = myHttpClient.doPost(url,paras);
        return "中间服务器耗时：" + (s1 - s0) + "||服务器返回数据为：" + result;
    }

    private static String getCookie(Map<String, Object> map){
        Token token = new Token();
        Date date=new Date();
        token.setCookie(map.get("cookie").toString());
        token.setCompCode(map.get("compCode").toString());
        token.setLoginCompCode(map.get("loginCompCode").toString());
        token.setUserName(map.get("userName").toString());
        map.remove("cookie");
        map.remove("userName");
        map.remove("loginCompCode");
        map.remove("compCode");
        String tokenString=JwtUtil.createToken(token);
        return tokenString;
    }

    private static String getParam(Map<String, Object> map) throws UnsupportedEncodingException, IllegalAccessException, NoSuchFieldException {
        SerializeUtil.initProtoParam(false);
        Param param = new Param();
        Class clazz = Param.class;
        Map<String, Object> paramMap = new HashMap<>();
        for(String key : map.keySet()){
            Field declaredField = null;
            try {
                declaredField = clazz.getDeclaredField(key);
                declaredField.setAccessible(true);
                declaredField.set(param,map.get(key));
            } catch (NoSuchFieldException e) {
                paramMap.put(key, map.get(key));
            }
        }
        if(paramMap.size() > 0){
            //没有该字段，则放入map中
            Field declaredField = clazz.getDeclaredField("map");
            declaredField.setAccessible(true);
            declaredField.set(param, paramMap);
        }
        byte[] bytes = SerializeUtil.serialize(param);
        String bytes2String = new String(bytes, charssetName);
        return bytes2String;
    }
}
