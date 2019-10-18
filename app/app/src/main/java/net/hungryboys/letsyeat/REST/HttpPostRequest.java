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

public class HttpPostRequest extends AsyncTask<String, Void, Response> {

    private static final String REQUEST_METHOD  = "POST";
    private static final int READ_TIMEOUT       = 15000;
    private static final int CONNECTION_TIMEOUT = 15000;

    public interface OnPostResponseHandler {
        void onPostResponse(Response result);
    }

    private HttpPostRequest.OnPostResponseHandler handler;

    public HttpPostRequest(HttpPostRequest.OnPostResponseHandler handler) {
        this.handler = handler;
    }

    @Override
    protected Response doInBackground(String ...params) {
        String stringURL = params[0];
        String data = params[1];
        Response response;

        StringBuilder result = new StringBuilder();

        HttpURLConnection connection;
        try {
            //create URL object
            URL url = new URL(stringURL);

            //create connection
            connection = (HttpURLConnection) url.openConnection();

            //set methods and timeout
            connection.setRequestMethod(REQUEST_METHOD);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            connection.setDoOutput(true); //set as output for POST request


            //set parameters to send and write them
            @SuppressWarnings("CharsetObjectCanBeUsedWithAPI_19") byte[] postData = data.getBytes("UTF-8");
            connection.setRequestProperty("Content-Length", String.valueOf(postData.length));
            connection.setRequestProperty("Content-Type", HttpGetRequest.JSON);
            //this starts connection to server
            connection.getOutputStream().write(postData);

            int rc = connection.getResponseCode();

            //read response
            if(rc == HttpURLConnection.HTTP_OK) {
                //read input data
                InputStream in = new BufferedInputStream(connection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                in.close();

                response = new Response(result.toString(), connection.getContentType(),rc);
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


    @Override
    protected void onPostExecute(Response result){
        super.onPostExecute(result);
    }
}
