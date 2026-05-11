package com.example.betlink.data;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SupabaseClient {

    private static final String PROJECT_ID = "smfaovkhpppuedbaybty";
    public static final String BASE_URL = "https://" + PROJECT_ID + ".supabase.co/";
    private static final String ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InNtZmFvdmtocHBwdWVkYmF5YnR5Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3Nzc4MDQ1NTgsImV4cCI6MjA5MzM4MDU1OH0.7pf7QqOmwBUc8ddchLsIUR9yt3x7n_tA3T-kZX_cPQ8";

    private static Retrofit retrofit = null;

    /**
     * Returns a service instance whose Authorization header uses the
     * caller-supplied
     * bearer token. Pass null (or a fresh JWT) to always use the anon key.
     */
    public static SupabaseService getService() {
        if (retrofit == null) {
            HttpLoggingInterceptor logger = new HttpLoggingInterceptor();
            logger.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logger)
                    .addInterceptor(chain -> {
                        // Resolve current token lazily. If the user is not logged in yet
                        // (e.g. during login/signup), fall back to the anon key.
                        String token = getEffectiveToken();
                        Request req = chain.request().newBuilder()
                                .header("apikey", ANON_KEY)
                                .header("Authorization", "Bearer " + token)
                                .header("Content-Type", "application/json")
                                .build();
                        return chain.proceed(req);
                    })
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(SupabaseService.class);
    }

    public static String getAnonKey() {
        return ANON_KEY;
    }

    public static String getBaseUrl() {
        return BASE_URL;
    }

    /** @deprecated use getAnonKey() */
    @Deprecated
    public static String getApiKey() {
        return ANON_KEY;
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private static String getEffectiveToken() {
        try {
            String userToken = SessionManager.getInstance().getAccessToken();
            return userToken != null ? userToken : ANON_KEY;
        } catch (IllegalStateException e) {
            // SessionManager not yet initialised (e.g. very first launch)
            return ANON_KEY;
        }
    }
}
