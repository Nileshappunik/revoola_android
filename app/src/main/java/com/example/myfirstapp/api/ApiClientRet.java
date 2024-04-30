package com.example.myfirstapp.api;

import android.content.Context;
import android.net.NetworkInfo;
import android.util.Log;

import com.example.myfirstapp.api.NetworkService;
import com.example.myfirstapp.utils.Constants;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClientRet {
   private static Retrofit retrofit = null;
   public final NetworkService networkService;

   public static final String HEADER_CACHE_CONTROL = "Cache-Control";
   public static final String HEADER_PRAGMA = "Pragma";
   private Context mContext;

   public ApiClientRet(final Context context) {

      this.mContext = context;

      File httpCacheDirectory = new File(context.getCacheDir(), "cache_file");

      Cache cache = new Cache(httpCacheDirectory, 40 * 1024 * 1024);
      OkHttpClient okHttpClient = new OkHttpClient.Builder()
              .readTimeout(1, TimeUnit.MINUTES)
              .connectTimeout(1, TimeUnit.MINUTES)
              .writeTimeout(1, TimeUnit.MINUTES)
              .addInterceptor(provideOfflineCacheInterceptor())
              .addNetworkInterceptor(provideCacheInterceptor())
              .cache(provideCache())
              .build();

      final Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
              .addConverterFactory(GsonConverterFactory.create())
              // Important to set a client on the build to prevent .build() from making new ones
              .client(okHttpClient);
      networkService = retrofitBuilder.baseUrl(Constants.BASE_URL).build().create(NetworkService.class);
   }

   private Cache provideCache() {
      Cache cache = null;

      try {
         cache = new Cache(new File(mContext.getCacheDir(), "http-cache"),
                 10 * 1024 * 1024); // 10 MB
      } catch (Exception e) {
         Log.e("ApiClientRet", "Could not create Cache!");
      }

      return cache;
   }

   private Interceptor provideCacheInterceptor() {
      return new Interceptor() {
         @Override
         public Response intercept(Chain chain) throws IOException {
            Response response = chain.proceed(chain.request());

            CacheControl cacheControl;

            if (ApiClientRet.this.isConnected()) {
               cacheControl = new CacheControl.Builder()
                       .maxAge(0, TimeUnit.SECONDS)
                       .build();
            } else {
               cacheControl = new CacheControl.Builder()
                       .maxStale(7, TimeUnit.DAYS)
                       .build();
            }

            return response.newBuilder()
                    .removeHeader(HEADER_PRAGMA)
                    .removeHeader(HEADER_CACHE_CONTROL)
                    .header(HEADER_CACHE_CONTROL, cacheControl.toString())
                    .build();

         }
      };
   }

   private Interceptor provideOfflineCacheInterceptor() {
      return new Interceptor() {
         @Override
         public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();

            if (!ApiClientRet.this.isConnected()) {
               CacheControl cacheControl = new CacheControl.Builder()
                       .maxStale(7, TimeUnit.DAYS)
                       .build();

               request = request.newBuilder()
                       .removeHeader(HEADER_PRAGMA)
                       .removeHeader(HEADER_CACHE_CONTROL)
                       .cacheControl(cacheControl)
                       .build();
            }
            return chain.proceed(request);
         }
      };
   }

   public boolean isConnected() {
      try {
         android.net.ConnectivityManager e = (android.net.ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
         NetworkInfo activeNetwork = e.getActiveNetworkInfo();
         return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
      } catch (Exception e) {
         Log.w("ApiClientRet", e.toString());
      }
      return false;
   }
}
