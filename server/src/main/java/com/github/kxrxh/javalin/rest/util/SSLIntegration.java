package com.github.kxrxh.javalin.rest.util;

import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Slf4j
public class SSLIntegration {

    private final SSLContext sslContext;

    public SSLIntegration() {
        try {
            this.sslContext = SSLUtils.createSSLContext();
        } catch (Exception e) {
            log.error("Failed to create SSL context: {}", e.getMessage());
            throw new UninitializedSSL("Failed to initialize SSL context");
        }
    }

    // Generic GET request
    public String sendGetRequest(String urlString) throws IOException, URISyntaxException {
        URL url = new URI(urlString).toURL();
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setSSLSocketFactory(sslContext.getSocketFactory());
        connection.setRequestMethod("GET");

        return getResponse(connection);
    }

    // Generic POST request
    public String sendPostRequest(String urlString, String data) throws IOException, URISyntaxException {
        URL url = new URI(urlString).toURL();
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setSSLSocketFactory(sslContext.getSocketFactory());
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = data.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        return getResponse(connection);
    }

    // Generic PUT request
    public String sendPutRequest(String urlString, String data) throws IOException, URISyntaxException {
        URL url = new URI(urlString).toURL();
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setSSLSocketFactory(sslContext.getSocketFactory());
        connection.setRequestMethod("PUT");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = data.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        return getResponse(connection);
    }

    // Generic DELETE request
    public String sendDeleteRequest(String urlString) throws IOException, URISyntaxException {
        URL url = new URI(urlString).toURL();
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setSSLSocketFactory(sslContext.getSocketFactory());
        connection.setRequestMethod("DELETE");

        return getResponse(connection);
    }

    // Handle the response
    private String getResponse(HttpsURLConnection connection) throws IOException {
        int status = connection.getResponseCode();
        InputStream inputStream;

        if (status > 299) {
            inputStream = connection.getErrorStream();
        } else {
            inputStream = connection.getInputStream();
        }

        StringBuilder content;
        try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
            content = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line);
            }
        }

        return content.toString();
    }

    // SOAP request
    public String sendSOAPRequest(String urlString, String soapAction, String soapMessage) throws IOException {
        URL url = new URL(urlString);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setSSLSocketFactory(sslContext.getSocketFactory());
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
        connection.setRequestProperty("SOAPAction", soapAction);
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = soapMessage.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        return getResponse(connection);
    }
}