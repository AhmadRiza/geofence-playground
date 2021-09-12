package riza.com.cto.view.local.testarea

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_area.view.*
import riza.com.cto.data.db.Area

/**
 * Created by riza@deliv.co.id on 2/29/20.
 */

class AreaVH(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(
        data: Area?,
        onClick: (data: Area) -> Unit,
        onDelete: (data: Area) -> Unit
    ) {

        data?.let {
            itemView.tv_name?.text = it.name
            itemView.btn_delete?.setOnClickListener { onDelete.invoke(data) }
            itemView.rootView?.setOnClickListener { onClick.invoke(data) }
        }

    }


}