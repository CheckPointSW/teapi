package com.checkpoint.tp_api.upload;

import com.checkpoint.tp_api.logger.TeLogger;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.Map;

import static org.apache.commons.io.FileUtils.copyInputStreamToFile;

/**
 * Created by edanha on 3/28/2017.
 */
public class TeHttpClient {
    private Map<String, Object> argMap;
    private HttpHost proxy;
    private HttpClientBuilder httpClientBuilder;
    private HttpClient httpClient;
    private CookieStore cookieStore;
    private static Logger logger;
    private static final String DOMAIN = "te.checkpoint.com";
    private static final String API_PATH = "/tecloud/api/v1/file/";
    private static final String TE_API_URL = "https://" + DOMAIN + API_PATH;
    private static final String UPLOAD_URL = TE_API_URL + "upload";
    private static final String QUERY_URL = TE_API_URL + "query";
    private static final String DOWNLOAD_PATH = API_PATH + "download";

    public TeHttpClient(Map<String, Object> argMap) throws Exception{
        this.argMap = argMap;
        logger = TeLogger.init(TeHttpClient.class, argMap);
        if ((boolean) argMap.get("withProxy")) {
            proxy = new HttpHost((String) argMap.get("host"), Integer.parseInt((String) argMap.get("port")));
        }
    }

    public InputStream queryRequest(String jsonAsString) throws Exception {
        HttpPost post = new HttpPost(QUERY_URL);
        post.setHeader("Authorization", (String) argMap.get("K"));
        post.setEntity(new StringEntity(jsonAsString));
        HttpResponse response = httpBuildAndGetResponse(post);
        if (cookieStore == null) {
            cookieStore = getCookieStore(response);
        }
        return response.getEntity().getContent();
    }

    public InputStream uploadRequest(String jsonAsString, File file) throws Exception {
        HttpPost post = new HttpPost(UPLOAD_URL);

        FileBody fileBody = new FileBody(file);
        StringBody stringBody = new StringBody(jsonAsString, ContentType.APPLICATION_JSON);

        post.setHeader("Authorization", (String) argMap.get("K"));
        post.setEntity(MultipartEntityBuilder.create()
                .addPart("request", stringBody)
                .addPart("file", fileBody)
                .build());

        HttpResponse response = httpBuildAndGetResponse(post);
        return response.getEntity().getContent();
    }

    public void downloadFile(String id, String whereToSave) throws Exception {
        HttpGet getter = new HttpGet(new URIBuilder()
                .setScheme("https")
                .setHost(DOMAIN)
                .setPath(DOWNLOAD_PATH)
                .setParameter("id", id)
                .build());
        getter.setHeader("Authorization", (String) argMap.get("K"));
        HttpResponse response = httpBuildAndGetResponse(getter);
        logger.debug("Download response Code : " + response.getStatusLine().getStatusCode());
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            String fileName = response.getFirstHeader("Content-Disposition").getValue();
            fileName = fileName.substring(fileName.indexOf("filename") + "filename".length() + 1).replaceAll("\"", "");
            copyInputStreamToFile(response.getEntity().getContent(), new File(whereToSave + fileName));
            return;
        }
        logger.error("Error with downloading cleaned file");
    }

    private CookieStore getCookieStore(HttpResponse response) {
        CookieStore cookieStore = new BasicCookieStore();
        String te_cookieString = response.getFirstHeader("Set-Cookie").getValue();
        te_cookieString = te_cookieString.substring(
                te_cookieString.indexOf("te_cookie=") + "te_cookie=".length()
                , te_cookieString.indexOf(";"));
        BasicClientCookie cookie = new BasicClientCookie("te_cookie", te_cookieString);
        cookie.setDomain(DOMAIN);
        cookie.setPath("/");
        cookieStore.addCookie(cookie);
        return cookieStore;
    }

    private HttpResponse httpBuildAndGetResponse(HttpRequestBase request) throws Exception{
        if (httpClientBuilder == null) { //First Request
            if ((boolean) argMap.get("withProxy"))
                httpClientBuilder = HttpClientBuilder.create().setProxy(proxy);
            else
                httpClientBuilder = HttpClientBuilder.create();
            return httpClientBuilder.build().execute(request);
        }
        if (httpClient == null) { //Second Request
            httpClient = httpClientBuilder.setDefaultCookieStore(cookieStore).build();
        }
        return httpClient.execute(request);
    }

}
