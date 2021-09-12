package riza.com.cto.data.api

import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import riza.com.cto.model.*
import riza.com.cto.support.base.BaseResponse

interface MainAPI {

    @GET("promo/all")
    fun getAllPromoAsync(): Deferred<Response<BaseResponse<List<Promo>>>>

    @POST("promo/add")
    fun addPromoAsync(@Body body: PromoRequest): Deferred<Response<BaseResponse<Any>>>

    @GET("area/all")
    fun getAllAreaAsync(): Deferred<Response<BaseResponse<List<AreaPromo>>>>

    @POST("area/add")
    fun addAreaAsync(@Body body: AddAreaRequest): Deferred<Response<BaseResponse<Any>>>

    @GET("user/{id}")
    fun getUserAsync(@Path("id") id: Long): Deferred<Response<BaseResponse<User>>>


    @GET("user/all")
    fun getAllUserAsync(): Deferred<Response<BaseResponse<List<User>>>>

}
