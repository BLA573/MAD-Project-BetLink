package com.example.betlink.data;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SupabaseClient {

    private static final String PROJECT_ID = "woygsvpfyfqotpvhjhmf";
    private static final String BASE_URL   = "https://" + PROJECT_ID + ".supabase.co/";
    private static final String ANON_KEY   =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9." +
        "eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6IndveWdzdnBmeWZxb3RwdmhqaG1mIiwi" +
        "cm9sZSI6ImFub24iLCJpYXQiOjE3Nzc2NDU2MDAsImV4cCI6MjA5MzIyMTYwMH0." +
        "ghmQI-2mxCHGEXRMCTv0QGG8X12RAZWAj-gStJa-2BA";

    private static Retrofit retrofit = null;

    /**
     * Returns a service instance whose Authorization header uses the caller-supplied
     * bearer token.  Pass null (or a fresh JWT) to always use the anon key.
     */
    public static SupabaseService getService() {
        if (retrofit == null) {
            HttpLoggingInterceptor logger = new HttpLoggingInterceptor();
            logger.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logger)
                .addInterceptor(chain -> {
                    // Resolve current token lazily.  If the user is not logged in yet
                    // (e.g. during login/signup), fall back to the anon key.
                    String token = getEffectiveToken();
                    Request req = chain.request().newBuilder()
                        .header("apikey",        ANON_KEY)
                        .header("Authorization", "Bearer " + token)
                        .header("Content-Type",  "application/json")
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
