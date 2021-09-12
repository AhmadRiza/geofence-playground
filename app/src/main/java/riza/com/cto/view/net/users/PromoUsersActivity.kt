package riza.com.cto.view.net.users

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_users_look.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.clearTop
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.toast
import riza.com.cto.R
import riza.com.cto.model.AreaPromo
import riza.com.cto.model.UserIds
import riza.com.cto.support.Adapter2
import riza.com.cto.support.CSVWriterHelper
import riza.com.cto.view.net.userlocation.UserLocationsActivity

/**
 * Created by riza@deliv.co.id on 5/27/20.
 */

class PromoUsersActivity : AppCompatActivity() {

    companion object {

        fun getIntent(
            context: Context?,
            promoCode: String,
            users: List<UserIds>,
            areaPromo: List<AreaPromo>
        ) =
            context?.run {
                intentFor<PromoUsersActivity>().apply {
                    putExtra("code", promoCode)
                    putParcelableArrayListExtra("users", ArrayList(users))
                    putParcelableArrayListExtra("areas", ArrayList(areaPromo))
                }.clearTop()
            }


    }

    private val userAdapter by lazy {

        object : Adapter2<UserIds, UserVH>(
            R.layout.item_user,
            UserVH::class.java,
            UserIds::class.java,
            emptyList()
        ) {
            override fun bindView(holder: UserVH, data: UserIds?, position: Int) {
                holder.bind(data) {

                    startActivity(
                        UserLocationsActivity.getIntent(
                            this@PromoUsersActivity,
                            areas,
                            it.id
                        )
                    )

                }
            }

        }

    }

    private lateinit var areas: List<AreaPromo>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users_look)
        initViews()
        initExtras()
    }

    @SuppressLint("SetTextI18n")
    private fun initExtras() {

        intent.getStringExtra("code")?.let {
            tv_title?.text = "Promo #$it"
        }

        intent.getParcelableArrayListExtra<UserIds>("users")?.let {
            userAdapter.updateList(it)
        }

        areas = intent.getParcelableArrayListExtra("areas") ?: emptyList()

    }

    private fun initViews() {

        btn_back?.setOnClickListener { onBackPressed() }

        rv_user?.adapter = userAdapter

        btn_save?.setOnClickListener {
            saveResult()
        }

    }

    private fun saveResult() {

        GlobalScope.launch {

            val csvHelper = CSVWriterHelper(this@PromoUsersActivity)

            csvHelper.writeArea(areas)

            csvHelper.writeUserResult(userAdapter.mData ?: emptyList())

            runOnUiThread {
                toast("Data disimpan")
            }

        }


    }

}