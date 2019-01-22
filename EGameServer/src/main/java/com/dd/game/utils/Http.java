package com.dd.game.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Http {
    private static final Logger logger = LoggerFactory.getLogger(Http.class);

    public static String getRequest(String url, Params p, Options options) {
        if (options == null) {
            options = Options.build();
        }
        String sendUrl = buildUrl(url, p);
        URL serverUrl = null;
        try {
            serverUrl = new URL(sendUrl);
            HttpURLConnection conn = (HttpURLConnection) serverUrl.openConnection();
            conn.setConnectTimeout(options.connectTimeout);
            conn.setReadTimeout(options.readTimeout);
            conn.setRequestMethod("GET");
            conn.setDoOutput(true);
            options.setPropertiesInConnect(conn);
            conn.connect();
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK || conn.getResponseCode() == HttpURLConnection.HTTP_CREATED) {
                String res = toString(conn.getInputStream(), options.encoding);
                return res;
            } else {
                String res = toString(conn.getInputStream(), options.encoding);
                logger.debug("http response not OK:[" + sendUrl + "]" + res);
                return "";
            }
        } catch (Exception e) {
            logger.error("http exception:[" + sendUrl + "]", e);
            return "";
        }
    }

    public static String getRequestV2(String url, Params p, Options options) {
        if (options == null) {
            options = Options.build();
        }
        String sendUrl = buildUrl(url, p);
        CloseableHttpClient client = HttpClients.createDefault();

        HttpGet get = new HttpGet(sendUrl);
        get.setConfig(options.getRequestConfig());
        Header[] headers = options.getHeader();
        if (headers != null) {
            get.setHeaders(headers);
        }
        BufferedReader reader = null;
        try {
            HttpResponse response = client.execute(get);
            reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuffer buffer = new StringBuffer();
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                buffer.append(line);
            }
            return buffer.toString();
        } catch (Exception e) {
            logger.error("getRequestV2 error", e);
            return "";
        } finally {
            try {
                client.close();
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {

            }
        }
    }

    public static String postRequestV2(String url, Params p, Options options) {
        if (options == null) {
            options = Options.build();
        }
        String sendUrl = buildUrl(url, p);
        CloseableHttpClient client = HttpClients.createDefault();

        HttpPost post = new HttpPost(sendUrl);
        if (p != null) {
            HttpEntity entity = p.getUrlEncodeEntry();
            if (entity != null) {
                post.setEntity(entity);
            }
        }
        post.setConfig(options.getRequestConfig());
        Header[] headers = options.getHeader();
        if (headers != null) {
            post.setHeaders(headers);
        }
        BufferedReader reader = null;
        try {
            HttpResponse response = client.execute(post);
            reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuffer buffer = new StringBuffer();
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                buffer.append(line);
            }
            return buffer.toString();
        } catch (Exception e) {
            logger.error("postRequestV2 error", e);
            return "";
        } finally {
            try {
                client.close();
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {

            }
        }
    }

    public static String postRequest(String url, Params p, Options options) {
        if (options == null) {
            options = Options.build();
        }
        String sendUrl = buildUrl(url, p);
        URL serverUrl = null;
        try {
            serverUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) serverUrl.openConnection();
            conn.setConnectTimeout(options.connectTimeout);
            conn.setReadTimeout(options.readTimeout);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            options.setPropertiesInConnect(conn);
            conn.connect();
            if (p != null && !p.isEmpty()) {
                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                os.writeBytes(p.httpBuildQuery());
                os.flush();
                os.close();
            }
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK || conn.getResponseCode() == HttpURLConnection.HTTP_CREATED) {
                String res = toString(conn.getInputStream(), options.encoding);
                return res;
            } else {
                String res = toString(conn.getInputStream(), options.encoding);
                logger.debug("http response not OK:[" + sendUrl + "]" + res);
                return "";
            }
        } catch (Exception e) {
            logger.error("http exception:[" + sendUrl + "]", e);
            return "";
        }
    }

    public static String rest_put(String url, String data, Options options) {
        if (options == null) {
            options = Options.build();
        }
        URL serverUrl = null;
        try {
            serverUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) serverUrl.openConnection();
            conn.setConnectTimeout(options.connectTimeout);
            conn.setReadTimeout(options.readTimeout);
            conn.setRequestMethod("PUT");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            options.setPropertiesInConnect(conn);
            conn.connect();
            if (data != null) {
                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                os.write(data.getBytes("UTF-8"));
                os.flush();
                os.close();
            }
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK || conn.getResponseCode() == HttpURLConnection.HTTP_CREATED) {
                String res = toString(conn.getInputStream(), options.encoding);
                return res;
            } else {
                String res = toString(conn.getInputStream(), options.encoding);
                logger.debug("http response not OK:[" + url + "]" + res);
                return "";
            }
        } catch (Exception e) {
            logger.error("http exception:[" + url + "]", e);
            return "";
        }
    }

    public static String rest_delete(String url, String data, Options options) {
        if (options == null) {
            options = Options.build();
        }
        URL serverUrl = null;
        try {
            serverUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) serverUrl.openConnection();
            conn.setConnectTimeout(options.connectTimeout);
            conn.setReadTimeout(options.readTimeout);
            conn.setRequestMethod("DELETE");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            options.setPropertiesInConnect(conn);
            conn.connect();
            if (data != null) {
                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                os.writeBytes(data);
                os.flush();
                os.close();
            }
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK || conn.getResponseCode() == HttpURLConnection.HTTP_CREATED) {
                String res = toString(conn.getInputStream(), options.encoding);
                return res;
            } else {
                String res = toString(conn.getInputStream(), options.encoding);
                logger.debug("http response not OK:[" + url + "]" + res);
                return "";
            }
        } catch (Exception e) {
            logger.error("http exception:[" + url + "]", e);
            return "";
        }
    }

    private static String toString(InputStream is, String encoding) throws IOException {
        InputStreamReader in = new InputStreamReader(is, encoding);
        StringWriter sw = new StringWriter();
        char[] b = new char[1024 * 4];
        int n = 0;
        while (-1 != (n = in.read(b))) {
            sw.write(b, 0, n);
        }
        in.close();
        sw.close();
        return sw.toString();
    }

    public static String urlEncode(String str) {
        try {
            return URLEncoder.encode(str, "UTF-8");
        } catch (Exception e) {
            return str;
        }
    }

    public static String buildUrl(String url, Params p) {
        if (p == null || p.isEmpty()) {
            return url;
        }
        if (url.contains("?")) {
            return url + "&" + p.httpBuildQuery();
        } else {
            return url + "?" + p.httpBuildQuery();
        }
    }

    public static class Options {
        protected int connectTimeout = 3000;
        protected int readTimeout = 10000;
        protected String encoding = "UTF-8";
        private Map<String, String> properties;
        private List<Header> headers;

        private Options() {
            properties = new HashMap<String, String>();
            headers = new ArrayList<Header>();
        }

        public static Options build() {
            return new Options();
        }

        public Options setConnectTimeout(int connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }

        public Options setEncoding(String encoding) {
            this.encoding = encoding;
            return this;
        }

        public Options setReadTimeout(int readTimeout) {
            this.readTimeout = readTimeout;
            return this;
        }

        public Options setContentType(String contentType) {
            Header contentTypeHeader = new BasicHeader(HttpHeaders.CONTENT_TYPE, contentType);
            headers.add(contentTypeHeader);
            return this;
        }

        public Options setAuthorization(String authorization) {
            Header authorizationHeader = new BasicHeader(HttpHeaders.AUTHORIZATION, authorization);
            headers.add(authorizationHeader);
            return this;
        }

        public Options addProperty(String key, String val) {
            this.properties.put(key, val);
            return this;
        }

        protected void setPropertiesInConnect(HttpURLConnection conn) {
            for (Map.Entry<String, String> e : this.properties.entrySet()) {
                conn.setRequestProperty(e.getKey(), e.getValue());
            }
        }

        protected RequestConfig getRequestConfig() {
            RequestConfig config = RequestConfig.custom().setConnectTimeout(connectTimeout).setSocketTimeout(readTimeout).build();
            return config;
        }

        protected Header[] getHeader() {
            return headers.isEmpty() ? null : headers.toArray(new Header[headers.size()]);
        }
    }

    public static class Params {

        Map<String, String> p = null;

        private Params() {
            p = new HashMap<String, String>();
        }

        public static Params build() {
            return new Params();
        }

        public Params addParam(String key, String val) {
            if (StringUtils.isBlank(key)) {
                return this;
            }
            if (StringUtils.isBlank(val)) {
                val = "";
            }
            this.p.put(key, val);
            return this;
        }

        public String httpBuildQuery() {
            List<String> querys = new ArrayList<>();
            for (Map.Entry<String, String> e : p.entrySet()) {
                querys.add(e.getKey() + "=" + Http.urlEncode(e.getValue()));
            }
            return StringUtils.join(querys, "&");
        }

        protected boolean isEmpty() {
            return this.p.isEmpty();
        }

        protected HttpEntity getUrlEncodeEntry() {
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            for (Map.Entry<String, String> e : p.entrySet()) {
                nameValuePairs.add(new BasicNameValuePair(e.getKey(), e.getValue()));
            }
            try {
                return new UrlEncodedFormEntity(nameValuePairs);
            } catch (UnsupportedEncodingException e) {
                logger.error("getUrlEncodeEntry error", e);
                return null;
            }
        }
    }
}
