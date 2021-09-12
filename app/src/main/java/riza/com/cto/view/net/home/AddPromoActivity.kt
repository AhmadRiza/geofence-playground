package riza.com.cto.view.net.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_add_promo.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.selector
import org.jetbrains.anko.toast
import riza.com.cto.R
import riza.com.cto.data.db.Area
import riza.com.cto.model.PromoRequest
import riza.com.cto.model.PromoServices
import riza.com.cto.model.PromoType
import riza.com.cto.support.*
import riza.com.cto.view.local.testarea.AreaVH
import riza.com.cto.view.net.selectarea.SelectAreaActivity
import java.util.*

/**
 * Created by riza@deliv.co.id on 5/18/20.
 */

class AddPromoActivity : AppCompatActivity() {

    companion object {
        const val REQ_ADD_AREA = 100
    }

    private val datePicker by lazy { DatePickerFragment() }
    private val mPromo by lazy { PromoRequest() }
    private val mAreas = arrayListOf<Area>()
    private val mServices = listOf(
        PromoServices.DERIDE,
        PromoServices.DECAR,
        PromoServices.DESHOP,
        PromoServices.DEFOOD
    )
    private val mType = listOf(
        PromoType.PERCENT,
        PromoType.PRICE
    )

    private val areaAdapter by lazy {
        object : Adapter<Area, AreaVH>(
            R.layout.item_area,
            AreaVH::class.java,
            Area::class.java,
            mAreas
        ) {
            override fun bindView(holder: AreaVH, data: Area?, position: Int) {
                holder.bind(data = data, onClick = {

                }, onDelete = {
                    mAreas.removeAt(position)
                    notifyDataSetChanged()
                })
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_promo)

        initView()

    }

    private fun initView() {

        rv_area?.adapter = areaAdapter


        Calendar.getInstance().let {

            mPromo.startDate = it.timeInMillis
            btn_date1?.text = printDate(it)

            it.add(Calendar.DATE, 7)
            mPromo.endDate = it.timeInMillis
            btn_date2?.text = printDate(it)

        }


        btn_add_area?.setOnClickListener {
            startActivityForResult(
                intentFor<SelectAreaActivity>(), REQ_ADD_AREA
            )
        }

        btn_date1?.setOnClickListener {

            datePicker.setup(
                Calendar.getInstance().apply { timeInMillis = mPromo.startDate }) { _, y, m, d ->
                val cal = Calendar.getInstance().apply {
                    set(y, m, d)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                }

                mPromo.startDate = cal.timeInMillis
                btn_date1?.text = printDate(cal)
            }
            datePicker.show(supportFragmentManager, "start")
        }

        btn_date2?.setOnClickListener {

            datePicker.setup(Calendar.getInstance().apply {
                timeInMillis = mPromo.endDate
            }) { _, y, m, d ->
                val cal = Calendar.getInstance().apply {
                    set(y, m, d)
                    set(Calendar.HOUR_OF_DAY, 23)
                    set(Calendar.MINUTE, 59)
                    set(Calendar.SECOND, 59)
                }

                mPromo.endDate = cal.timeInMillis
                btn_date2?.text = printDate(cal)
            }
            datePicker.show(supportFragmentManager, "end")

        }


        btn_service?.setOnClickListener {

            selector(
                "Pilih Servis",
                mServices
            ) { d, i ->

                mPromo.service = mServices[i]
                btn_service?.text = mPromo.service
                d.dismiss()
            }

        }

        btn_type?.setOnClickListener {

            selector(
                "Pilih Tipe Promo",
                mType
            ) { d, i ->

                mPromo.type = mType[i]
                btn_type?.text = mPromo.type
                d.dismiss()
            }

        }

        btn_confirm?.setOnClickListener {
            processData()
        }


    }

    private fun processData() {

        if (et_code?.valideteIfEmpty("Kode Promo") == false) return
        mPromo.code = et_code?.text.toString()
        if (et_info?.valideteIfEmpty("Deskripsi Promo") == false) return
        mPromo.description = et_info?.text.toString()
        if (et_total?.valideteIfEmpty("Total Promo") == false) return
        mPromo.value = et_total?.text.toString().toInt()
        if (et_threshold?.valideteIfEmpty("Threshold") == false) return
        mPromo.threshold = et_threshold?.text.toString().toInt()

        if (mPromo.service.isBlank()) {
            toast("Servis Kosong")
            return
        }

        if (mPromo.type.isBlank()) {
            toast("Tipe Kosong")
            return
        }

        if (mAreas.isEmpty()) {
            toast("Tambahkan minimal 1 Area")
            return
        }

        mPromo.areaIds = mAreas.map { it.id }

        setResult(Activity.RESULT_OK, Intent().apply {
            putExtra("data", mPromo)
        })

        finish()

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) return

        when (requestCode) {


            REQ_ADD_AREA -> {

                data?.getParcelableExtra<Area>("data")?.let {

                    debugLog(it)

                    mAreas.forEach { area: Area ->
                        if (it.id == area.id) return@let
                    }

                    mAreas.add(it)
                    areaAdapter.notifyDataSetChanged()
                }

            }

        }

    }
}