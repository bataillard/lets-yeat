package net.hungryboys.letsyeat.REST;

import java.net.HttpURLConnection;

public class Response {
    private final String content;
    private final String contentType;
    private final int responseCode;

    public static final int CONNECTION_ERROR = -1;

    public Response(String content, String contentType, int responseCode) {
        this.content = content;
        this.contentType = contentType;
        this.responseCode = responseCode;
    }

    public Response(int responseCode) {
        this(null, null, responseCode);
    }

    public boolean isValid() {
        return responseCode == HttpURLConnection.HTTP_OK && contentType.equals(HttpGetRequest.JSON);
    }

    public String getContent() {
        return content;
    }
}