package com.hzf;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZhiFeng Hu on 2016/5/6.
 */
public class HttpClientTest {
    public static void main(String[] args) {
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("http://localhost:8080/tcm/search/frontresultlist");
        List<NameValuePair> params = new ArrayList<>();
        JSONObject obj = new JSONObject();
        obj.put("keyword", "麻黄");
        obj.put("pageno", 1);
        obj.put("pagesize", 5);
        obj.put("subori", "null");
        obj.put("type", "med");


        httpPost.setHeader("Content-Type","application/json");
        httpPost.setHeader("Accept","application/json");
        try {
            httpPost.setEntity(new StringEntity(obj.toString()));
            System.out.println("executing request " + httpPost);
            HttpResponse response = httpClient.execute(httpPost);

            HttpEntity entity = response.getEntity();
            if (entity != null) {
                System.out.println("--------------------------------------");
                System.out.println("Response content: " + EntityUtils.toString(entity, "UTF-8"));
                System.out.println("--------------------------------------");
            }

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
