package com.revoola.api;

import android.content.Context;
import android.net.NetworkInfo;
import android.util.Log;

import com.revoola.utils.RLConstants;

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

public class RLApiClientRet {
   private static Retrofit retrofit = null;
   public final RLNetworkService networkService;

   public static final String HEADER_CACHE_CONTROL = "Cache-Control";
   public static final String HEADER_PRAGMA = "Pragma";
   private Context mContext;

   public RLApiClientRet(final Context context) {

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

      networkService = retrofitBuilder.baseUrl(RLConstants.BASE_URL).build().create(RLNetworkService.class);
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

            if (RLApiClientRet.this.RLisConnected()) {
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

            if (!RLApiClientRet.this.RLisConnected()) {
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

   public boolean RLisConnected() {
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
