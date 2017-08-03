package com.yosua.takehome.model.remote

import android.content.Context
import com.google.gson.GsonBuilder
import com.yosua.takehome.util.LogUtil
import com.yosua.takehome.util.NetworkUtil
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Created by Yosua_Setiawan on 28/07/2017.
 */

class ApiServiceBuilder {
    private val TAG: String = "ApiServiceBuilder"
    private val CACHE_CONTROL: String = "Cache-Control"
    private val HTTP_CACHE: String = "http-cache"
    private val PRAGMA: String = "Pragma"
    private val CACHE_SIZE: Int = 10 * 1024 * 2014

    private fun provideInterceptor(): Interceptor {
        return Interceptor { chain ->
            val originalRequest = chain.request()
            val request = originalRequest.newBuilder().build()
            chain.proceed(request)
        }
    }

    private fun provideInterceptorWithHttpLogging(): HttpLoggingInterceptor {
        val httpLoggingInterceptor = HttpLoggingInterceptor(
                HttpLoggingInterceptor.Logger { message -> LogUtil.debugLog(TAG, message) })
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return httpLoggingInterceptor
    }

    fun provideApiServiceWithCache(context: Context, apiUrl: String): ApiService {
        return provideRetrofitWithCache(context, apiUrl).create(ApiService::class.java)
    }

    private fun provideCache(context: Context): Cache {
        var cache: Cache? = null
        try {
            cache = Cache(File(context.applicationContext.cacheDir, HTTP_CACHE),
                    CACHE_SIZE.toLong())
        } catch (e: Exception) {
            LogUtil.errorLog(TAG, e.message)
        }
        return cache!!
    }

    private fun provideRetrofitWithCache(context: Context, apiUrl: String): Retrofit {

        val builder = OkHttpClient.Builder()
                .addInterceptor(provideInterceptor())
                .addInterceptor(provideInterceptorWithHttpLogging())
                .addInterceptor(provideInterceptorWithOfflineCache(context))
                .addNetworkInterceptor(provideInterceptorWithCache())
                .cache(provideCache(context))
                .readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)

        val gson = GsonBuilder().setLenient().create()
        return Retrofit.Builder()
                .baseUrl(apiUrl)
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
    }

    private fun provideInterceptorWithOfflineCache(context: Context): Interceptor {
        return Interceptor { chain ->
            var request = chain.request()

            val cacheControl = CacheControl.Builder()
                    .maxStale(10, TimeUnit.SECONDS)
                    .build()
            when {
                !NetworkUtil.hasNetwork(context) ->
                    request = request.newBuilder()
                            .removeHeader(PRAGMA)
                            .cacheControl(cacheControl)
                            .build()
            }
            chain.proceed(request)
        }
    }

    private fun provideInterceptorWithCache(): Interceptor {
        return Interceptor { chain ->
            val response = chain.proceed(chain.request())

            val cacheControl = CacheControl.Builder()
                    .maxAge(10, TimeUnit.SECONDS)
                    .build()

            response.newBuilder()
                    .removeHeader(PRAGMA)
                    .header(CACHE_CONTROL, cacheControl.toString())
                    .build()
        }
    }
}