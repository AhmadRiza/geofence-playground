package riza.com.cto.support.base

import retrofit2.Call
import java.lang.Exception

/**
 * Created by riza@deliv.co.id on 2/29/20.
 */
abstract class APIRepository {


    data class APIResponse<T>(
        var success: Boolean = true,
        var code: Int = 0,
        var message: String? = null,
        var data: T? = null
    )


    protected fun <T> callAPI(apiCall: () -> Call<T>): APIResponse<T> {

        val result = APIResponse<T>()

        try {
            val response = apiCall.invoke().execute()
            result.code = response.code()

            when {
                response.isSuccessful -> {
                    result.success = true
                    result.data = response.body()
                    result.message = "Success"
                }
                else -> {
                    result.success = false
                    result.message = response.errorBody()?.string().toString()
                }
            }

        } catch (e: Exception) {
            result.success = false
            result.message = "Internal Server Error"
        }

        return result
    }


}