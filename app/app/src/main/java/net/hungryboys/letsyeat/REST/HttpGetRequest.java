package net.hungryboys.letsyeat.REST;

import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Map;

public class HttpGetRequest extends AsyncTask<String, Void, HttpGetRequest.Response> {

    private static final String REQUEST_METHOD = "GET";
    private static final int READ_TIMEOUT = 15000;
    private static final int CONNECTION_TIMEOUT = 15000;

    private static final String JSON = "application/json";

    public static final class Response {
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
            return responseCode == HttpURLConnection.HTTP_OK && contentType.equals(JSON);
        }

        public String getContent() {
            return content;
        }
    }

    public interface OnGetResponseHandler {
        void onGetResponse(Response result);
    }

    private OnGetResponseHandler handler;

    public HttpGetRequest(OnGetResponseHandler handler) {
        this.handler = handler;
    }

    @Override
    protected Response doInBackground(String ...params) {
        String stringURL = params[0];
        Response response;

        try {
            //create URL object
            URL url = new URL(stringURL);

            //create connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            //set methods and timeout
            connection.setRequestMethod(REQUEST_METHOD);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            int rc = connection.getResponseCode();

            //read response
            if(rc == HttpURLConnection.HTTP_OK && connection.getContentType().equals(JSON)) {
                StringBuilder result = new StringBuilder();

                //read input data, this starts connection to server
                InputStream in = new BufferedInputStream(connection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                in.close();

                response = new Response(result.toString(), connection.getContentType(), rc);
            } else {
                response = new Response(rc);
            }


        } catch (ProtocolException e) {
            e.printStackTrace();
            response = new Response(Response.CONNECTION_ERROR);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            response = new Response(Response.CONNECTION_ERROR);
        } catch (IOException e) {
            e.printStackTrace();
            response = new Response(Response.CONNECTION_ERROR);
        }

        return response;
    }



    protected void onPostExecute(Response result){
        handler.onGetResponse(result);
    }
}
