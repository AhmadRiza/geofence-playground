package riza.com.cto.support.base

import kotlinx.coroutines.Deferred
import org.json.JSONObject
import retrofit2.Response
import riza.com.cto.support.printDebugLog

/**
 * Created by riza@deliv.co.id on 4/13/20.
 */

abstract class BaseRepository() {

    protected suspend fun <T> callAPI(apiCall: suspend () -> Deferred<Response<BaseResponse<T>>>): BaseResponse<T> {

        val result = BaseResponse<T>()

        try {
            val response = apiCall.invoke().await()

            result.code = response.code()

            when {
                response.isSuccessful -> {
                    result.success = true
                    result.data = response.body()?.data
                    result.message = "Success"
                }
                else -> {
                    result.success = false
                    val error = JSONObject(response.errorBody()?.string().toString())
                    result.message = error.getString("message")
                }
            }

        } catch (e: Exception) {
            e.printDebugLog()
            result.success = false
            result.message = "Koneksi Gagal"
        }

        return result

    }

    protected suspend fun <T> executeQuery(query: suspend () -> T?): T? {
        return try {
            query.invoke()
        } catch (e: Exception) {
            e.printDebugLog()
            null
        }

    }

    protected suspend fun <T> executeUpdate(query: suspend () -> T?): Boolean {
        return try {
            query.invoke()
            true
        } catch (e: Exception) {
            e.printDebugLog()
            false
        }
    }

}