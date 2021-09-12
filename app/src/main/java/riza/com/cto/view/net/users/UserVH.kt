package riza.com.cto.view.net.users

import android.annotation.SuppressLint
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_user.view.*
import riza.com.cto.model.UserIds

/**
 * Created by riza@deliv.co.id on 5/27/20.
 */


class UserVH(itemView: View) : RecyclerView.ViewHolder(itemView) {


    @SuppressLint("SetTextI18n")
    fun bind(data: UserIds?, onClick: (data: UserIds) -> Unit) {

        with(itemView) {

            data?.let {

                tv_name?.text = "${adapterPosition + 1}. ${it.name}"

                rootView.setOnClickListener {
                    onClick.invoke(data)
                }

            }


        }


    }


}