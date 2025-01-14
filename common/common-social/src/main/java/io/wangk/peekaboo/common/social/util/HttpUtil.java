package io.wangk.peekaboo.common.social.util;

import io.wangk.peekaboo.common.core.exception.MyRuntimeException;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * HTTP工具类，用于发送GET和POST请求。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Slf4j
public class HttpUtil {
    /**
     * 连接超时时间，单位毫秒。
     */
    private static final int CONNECT_TIMEOUT = 5000;
    /**
     * 读取超时时间，单位毫秒。
     */
    private static final int READ_TIMEOUT = 5000;
    /**
     * 最大重定向次数。
     */
    private static final int MAX_REDIRECTS = 5;

    /**
     * 发送GET请求，处理重定向。
     *
     * @param url 请求的URL。
     * @return 重定向后的内容。
     * @throws MyRuntimeException 如果请求失败或发生异常。
     */
    public static String getResponseWithRedirect(String url) throws MyRuntimeException {
        validateUrl(url);
        return getResponseWithRedirect(url, 0);
    }

    /**
     * 发送简单的GET请求，不处理重定向。
     *
     * @param url 请求的URL。
     * @return 响应内容。
     */
    public static String get(String url) throws MyRuntimeException {
        validateUrl(url);
        HttpURLConnection connection = openConnection(url, "GET");
        int responseCode = sendRequestAndGetResponseCode(connection);

        if (responseCode == HttpURLConnection.HTTP_OK) {
            return readResponse(connection);
        } else {
            throw new MyRuntimeException("Failed : HTTP error code : " + responseCode);
        }
    }

    /**
     * 发送POST请求。
     *
     * @param api      请求的API URL。
     * @param jsonBody 请求体内容，JSON格式。
     * @return 响应内容。
     */
    public static String post(String api, String jsonBody) throws MyRuntimeException {
        validateUrl(api);
        validateJsonBody(jsonBody);

        HttpURLConnection connection = null;
        try {
            connection = openConnection(api, "POST");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);
            try (DataOutputStream os = new DataOutputStream(connection.getOutputStream())) {
                byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            int responseCode = sendRequestAndGetResponseCode(connection);
            if (responseCode == HttpURLConnection.HTTP_OK) {
                return readResponse(connection);
            } else {
                log.error("Failed to post request to {}: HTTP error code {}", api, responseCode);
                throw new MyRuntimeException("Failed : HTTP error code : " + responseCode);
            }
        } catch (IOException e) {
            log.error("IO Exception occurred while posting request to {}: {}", api, e.getMessage());
            throw new MyRuntimeException("IO Exception occurred while posting request", e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private static String getResponseWithRedirect(String url, int redirectCount) throws MyRuntimeException {
        if (redirectCount >= MAX_REDIRECTS) {
            throw new MyRuntimeException("Exceeded maximum number of redirects");
        }
        HttpURLConnection connection = openConnection(url, "GET");
        int responseCode = sendRequestAndGetResponseCode(connection);
        switch (responseCode) {
            case HttpURLConnection.HTTP_OK:
                return readResponse(connection);
            case HttpURLConnection.HTTP_MOVED_TEMP:
            case HttpURLConnection.HTTP_SEE_OTHER:
                String redirectUrl = connection.getHeaderField("Location");
                if (redirectUrl == null) {
                    log.error("Received redirect response but Location header is missing for URL: {}", url);
                    throw new MyRuntimeException("Received redirect response but Location header is missing");
                }
                log.debug("Redirect URL: {}", redirectUrl);
                // 递归调用处理重定向
                return getResponseWithRedirect(redirectUrl, redirectCount + 1);
            default:
                throw new MyRuntimeException("Failed: HTTP error code : " + responseCode);
        }
    }

    private static HttpURLConnection openConnection(String urlString, String requestMethod) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(requestMethod);
            connection.setConnectTimeout(CONNECT_TIMEOUT);
            connection.setReadTimeout(READ_TIMEOUT);
            return connection;
        } catch (IOException e) {
            throw new MyRuntimeException("Error opening connection to URL: " + urlString, e);
        }
    }

    private static int sendRequestAndGetResponseCode(HttpURLConnection connection) {
        try {
            return connection.getResponseCode();
        } catch (IOException e) {
            throw new MyRuntimeException("Error getting response code", e);
        }
    }

    private static String readResponse(HttpURLConnection connection) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            String body = response.toString();
            log.debug("Response Body: {}", body);
            return body;
        } catch (IOException e) {
            throw new MyRuntimeException("Error reading response", e);
        } finally {
            connection.disconnect();
        }
    }

    private static void validateUrl(String url) {
        if (url == null || url.isEmpty()) {
            throw new MyRuntimeException("Invalid URL: " + url);
        }
    }

    private static void validateJsonBody(String jsonBody) {
        if (jsonBody == null || jsonBody.isEmpty()) {
            throw new MyRuntimeException("Invalid JSON body");
        }
    }

    private HttpUtil() {
        //避免SonarQube的警告
    }
}
