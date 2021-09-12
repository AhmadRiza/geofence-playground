package riza.com.cto.data.api

import com.google.gson.Gson
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import id.co.deliv.kios.repository.api.InterceptorFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object APIServiceFactory {

    private val okHttp = OkHttpClient
        .Builder()
        .addInterceptor(InterceptorFactory.loggingInterceptor())
        .build()


    private val builder = Retrofit.Builder()
        .addConverterFactory(
            GsonConverterFactory.create(
                Gson()
            )
        )
        .addCallAdapterFactory(CoroutineCallAdapterFactory())


    private fun <S> createService(
        serviceClass: Class<S>
    ): S {
        builder.client(okHttp)
        return builder.build().create(serviceClass)
    }

    fun <S> createMain(serviceClass: Class<S>): S {
        builder.baseUrl(getBaseURL())
        return createService(serviceClass)
    }

    private fun getBaseURL() = "http://localhost:8080/"

}