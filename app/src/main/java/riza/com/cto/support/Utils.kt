package riza.com.cto.support

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.text.Html
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.jetbrains.anko.toast
import riza.com.cto.BuildConfig
import java.util.*


/**
 * Created by riza@deliv.co.id on 2/28/20.
 */


fun debugLog(message: Any?) {
    if (BuildConfig.DEBUG) Log.e("debugLog", message.toString())
}

fun Context.getCompatColor(resource: Int) = ContextCompat.getColor(this, resource)

fun <T> gsontoList(string: String): List<T> {
    val listType = object : TypeToken<List<T>>() {}.type
    return Gson().fromJson(string, listType)
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun Exception.printDebugLog() {
    debugLog(message)
    if (BuildConfig.DEBUG) printStackTrace()
}

fun getTimeDif(time1: Long, time2: Long): String {

    var difference = time2 - time1

    val secondsInMilli = 1000
    val minutesInMilli = secondsInMilli * 60
    val hoursInMilli = minutesInMilli * 60
    val daysInMilli = hoursInMilli * 24

    val elapsedDays: Long = difference / daysInMilli
    difference %= daysInMilli

    val elapsedHours: Long = difference / hoursInMilli
    difference %= hoursInMilli

    if (elapsedDays > 0) {
        return "$elapsedDays Hari"
    } else {
        return "$elapsedHours Jam"
    }

}

val month =
    arrayOf("Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Agt", "Sep", "Okt", "Nov", "Des")

fun printDate(calendar: Calendar): String {
    return "${calendar.get(Calendar.DATE)} ${month[calendar.get(Calendar.MONTH)]} ${
        calendar.get(
            Calendar.YEAR
        )
    }"
}

fun printCurrentTime(calendar: Calendar): String {
    return "${calendar.get(Calendar.DATE)}-${calendar.get(Calendar.MONTH)}-${calendar.get(Calendar.HOUR)}:${
        calendar.get(
            Calendar.MINUTE
        )
    }"
}

fun EditText.valideteIfEmpty(fieldName: String): Boolean {
    if (text.isNullOrBlank()) {
        context.toast("$fieldName tidak boleh kosong")
        showKeyboad()
        return false
    }
    return true
}


fun EditText.showKeyboad() {
    requestFocus()
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}


fun EditText.hideKeyboard() {
    clearFocus()
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}


fun TextView.loadHTML(html: String) {
    text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT)
    } else {
        Html.fromHtml(html)
    }
    movementMethod = LinkMovementMethod.getInstance()
}

fun Activity.openFolder(uri: Uri) {
    try {
        val intent = Intent(Intent.ACTION_VIEW)
//        val mydir = Uri.parse("file://$location")
        intent.setDataAndType(uri, "application/*") // or use */*
        startActivity(intent)
    } catch (e: Exception) {
        toast(e.message.toString())
    }
}