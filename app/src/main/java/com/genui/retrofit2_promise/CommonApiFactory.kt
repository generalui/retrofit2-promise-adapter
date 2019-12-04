package com.genui.retrofit2_promise

import com.genui.retrofit2_promise_adapter.PromiseCallAdapterFactory
import com.shopify.promises.Promise
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.io.IOException
import java.util.concurrent.TimeUnit

data class User(val name:String)

interface CommonApiService {
    @GET("/user")
    fun getUser(): Promise<User, Exception>
}

class CommonApiFactory {
    fun makeCommonApiService(): CommonApiService {
        return Retrofit.Builder()
            .baseUrl("https://example.com")
            .client(makeOkHttpClient(authToken = ""))
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(PromiseCallAdapterFactory())
            .build().create(CommonApiService::class.java)
    }

    private fun makeOkHttpClient(authToken: String): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(makeLoggingInterceptor())
            .addInterceptor(makeIOExceptionInterceptor())
            .addInterceptor(makeAuthorizationInterceptor(authToken))
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(90, TimeUnit.SECONDS)
            .build()
    }

    private fun makeLoggingInterceptor(): HttpLoggingInterceptor {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        return logging
    }

    private fun makeAuthorizationInterceptor(authToken: String): Interceptor {
        return Interceptor {
            var request = it.request()
            val headers =
                request.headers().newBuilder().add("Authorization", "Bearer $authToken").build()
            request = request.newBuilder().headers(headers).build()
            it.proceed(request)
        }
    }

    /**
     * Catch errors thrown from the MS Graph server
     * Throws an IOException, which can be caught by Retrofit.
     */
    private fun makeIOExceptionInterceptor(): Interceptor {
        return Interceptor { chain ->
            val request = chain.request()
            val response = chain.proceed(request)
            var message = "There was an unexpected error"
            if (response.code() == 400 || response.code() == 500) {
                response.body()?.let { body ->
                    message = body.string()
                }
                throw IOException(message)
            }
            response
        }
    }

}