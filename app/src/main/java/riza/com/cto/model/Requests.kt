package riza.com.cto.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import riza.com.cto.core.Point

/**
 * Created by riza@deliv.co.id on 5/18/20.
 */


@Parcelize
data class PromoRequest(
    var code: String = "",
    var startDate: Long = 0L,
    var endDate: Long = 0L,
    var type: String = PromoType.PERCENT,
    var value: Int = 0,
    var service: String = "",
    var description: String = "",
    var areaIds: List<Long> = emptyList(),
    var threshold: Int = 0
) : Parcelable

data class AddAreaRequest(
    val name: String,
    val points: List<Point>
)