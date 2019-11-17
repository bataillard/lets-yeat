package net.hungryboys.letsyeat.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * This class creates and manages API Calls to server, using the Retrofit2 library
 */
public class APICaller {

    //    private static final String BASE_URL = "http://52.183.98.8:3001/";
    private static final String BASE_URL = "localhost:3001/";
    /**
     * Build the Retrofit API caller based on the API Calls defined in {@link APICalls}
     * @return an APICalls Interface that can be used to make calls to server
     */
    public static APICalls getApiCall() {


        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging)
                .readTimeout(40, TimeUnit.SECONDS); // Long timeout for facial verification

        // Create GSON parser and specify date format
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient.build())
                .build();

        return retrofit.create(APICalls.class);
    }

}
