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

public class HttpPutRequest extends AsyncTask<String, String, String> {
    private static final String REQUEST_METHOD  = "PUT";
    private static final int READ_TIMEOUT       = 15000;
    private static final int CONNECTION_TIMEOUT = 15000;

    @Override
    protected String doInBackground(String... params) {
        String stringURL = params[0];
        String data = params[1];
        String resource = params[2];
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
            connection.setRequestProperty("Content-Type", resource);
            //this starts connection to server
            connection.getOutputStream().write(postData);

            int rc = connection.getResponseCode();
            result.append(rc);
            result.append("\n");

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
            }
        } catch (ProtocolException e) {
            e.printStackTrace();
            result = null;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            result = null;
        } catch (IOException e) {
            e.printStackTrace();
            result = null;
        }

        if (result != null) {
            return result.toString();
        }
        else{
            return "Exception";
        }
    }

    @Override
    protected void onPostExecute(String result){
        super.onPostExecute(result);
    }
}
