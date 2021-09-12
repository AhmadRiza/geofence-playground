package riza.com.cto.view.net.home

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_promo.view.*
import riza.com.cto.model.Promo
import riza.com.cto.support.getTimeDif
import java.util.*

/**
 * Created by riza@deliv.co.id on 5/9/20.
 */

class PromoVH(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(data: Promo?, onClick: (promo: Promo) -> Unit) {
        with(itemView) {
            data?.let {
                tv_code?.text = it.code
                tv_area?.text = "${it.areas.size}"
                tv_user?.text = "${it.users.size}"
                tv_until?.text = "${getTimeDif(Date().time, it.endDate)} lagi"

                rootView.setOnClickListener { onClick.invoke(data) }

            }
        }


    }

}