package riza.com.cto.data.db

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

/**
 * Created by riza@deliv.co.id on 2/29/20.
 */

@Parcelize
@Entity
data class Area(
    @PrimaryKey(autoGenerate = true) val id: Long,
    var name: String,
    val points: String
): Parcelable