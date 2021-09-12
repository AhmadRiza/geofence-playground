package riza.com.cto.view.net.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.view_card_stats.*
import org.jetbrains.anko.clearTop
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.toast
import riza.com.cto.R
import riza.com.cto.model.Promo
import riza.com.cto.model.PromoRequest
import riza.com.cto.support.Adapter2
import riza.com.cto.view.local.testarea.MainActivity
import riza.com.cto.view.net.users.PromoUsersActivity

/**
 * Created by riza@deliv.co.id on 5/8/20.
 */

class HomeActivity : AppCompatActivity() {

    companion object {
        const val REQ_ADD_AREA = 1
    }

    private val vm by lazy { ViewModelProvider(this).get(HomeVM::class.java) }
    private lateinit var promoAdapter: Adapter2<Promo, PromoVH>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        initView()
        initObserver()

        vm.getAllPromo()
        vm.getAllUser()
    }

    private fun initObserver() {

        vm.promos.observe(this, Observer {
            promoAdapter.updateList(it)
        })

        vm.totalArea.observe(this, Observer {
            tv_total_area?.text = it.toString()
        })

        vm.totalUser.observe(this, Observer {
            tv_total_user?.text = it.toString()
        })

        vm.totalPromo.observe(this, Observer {
            tv_total_promo?.text = it.toString()
        })

        vm.loading.observe(this, Observer {
            swipe?.isRefreshing = it
        })

        vm.error.observe(this, Observer {
            toast(it)
        })

    }

    private fun initView() {

        promoAdapter = object : Adapter2<Promo, PromoVH>(
            R.layout.item_promo,
            PromoVH::class.java,
            Promo::class.java,
            emptyList()
        ) {
            override fun bindView(holder: PromoVH, data: Promo?, position: Int) {
                holder.bind(data) {

                    it.users.forEach {
                        it.name += " [targeted]"
                    }

                    startActivity(
                        PromoUsersActivity.getIntent(
                            this@HomeActivity,
                            it.code,
                            vm.getUserList(it.users),
                            it.areas
                        )
                    )

                }
            }

        }

        rv_promo?.adapter = promoAdapter

        fab_test?.setOnClickListener {
            startActivity(
                intentFor<MainActivity>().clearTop()
            )
        }

        fab_add?.setOnClickListener {
            startActivityForResult(
                intentFor<AddPromoActivity>(), REQ_ADD_AREA
            )
        }

        swipe?.setOnRefreshListener {
            vm.getAllPromo()
        }



    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) return

        when (requestCode) {

            REQ_ADD_AREA -> {

                data?.getParcelableExtra<PromoRequest>("data")?.let {
                    vm.addPromo(it)
                }

            }


        }
    }
}