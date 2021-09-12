package riza.com.cto.support

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.lang.reflect.InvocationTargetException

abstract class Adapter2<Type, ViewHolder : RecyclerView.ViewHolder>(
    private var mLayout: Int,
    private var mViewHolderClass: Class<ViewHolder>,
    private var mModelClass: Class<Type>,
    var mData: List<Type>?)
    : RecyclerView.Adapter<ViewHolder>() {

    fun updateList(list: List<Type>){
        mData = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(mLayout, parent, false) as ViewGroup

        try {
            val constructor = mViewHolderClass.getConstructor(View::class.java)
            return constructor.newInstance(view)
        } catch (e: NoSuchMethodException) {
            throw RuntimeException(e)
        } catch (e: IllegalAccessException) {
            throw RuntimeException(e)
        } catch (e: InstantiationException) {
            throw RuntimeException(e)
        } catch (e: InvocationTargetException) {
            throw RuntimeException(e)
        }

    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = getItem(position)
        bindView(holder, model, position)
    }

    protected abstract fun bindView(holder: ViewHolder, data: Type?, position: Int)

    private fun getItem(position: Int): Type? {
        return mData?.get(position)
    }

    override fun getItemCount(): Int {
        return mData?.size?: 0
    }
}

