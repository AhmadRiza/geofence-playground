package id.co.deliv.kios.repository.api

import okhttp3.Interceptor
import okhttp3.Interceptor.Chain
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.BODY

/**
 * Created by riza@deliv.co.id on 10/16/19.
 */

object InterceptorFactory {

    fun loggingInterceptor() = HttpLoggingInterceptor().apply {
        level = BODY
    }

    fun jsonTypeInterceptor() = object : Interceptor {
        override fun intercept(chain: Chain): Response {
            val builder = chain.request().newBuilder()
            builder.header("Content-Type", "application/json")
            builder.header("Accept", "application/json")
            return chain.proceed(builder.build())
        }

    }

    fun authInterceptor(token: String) = object : Interceptor {
        override fun intercept(chain: Chain): Response {
            val builder = chain.request().newBuilder()
            builder.header("Authorization", token)
            return chain.proceed(builder.build())
        }

    }


}