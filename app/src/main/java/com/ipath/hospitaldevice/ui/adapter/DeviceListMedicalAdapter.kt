package com.ipath.hospitaldevice.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.ipath.hospitaldevice.R

class DeviceListMedicalAdapter(
    private val mContext: Context,
    private var mSearchDeviceList: Array<String>?
) :
    RecyclerView.Adapter<DeviceListMedicalAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(mContext).inflate(
            R.layout.item_device_search, parent,
            false
        )
        return ViewHolder(view)
    }

    @SuppressLint("MissingPermission")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvDeviceSearch.text = mSearchDeviceList!![holder.adapterPosition].toString()
        holder.llDeviceSearch.tag =  holder.adapterPosition
        holder.llDeviceSearch.setOnClickListener { v ->
            val position = v.tag as Int
            mOnItemClickListener!!.onItemClick(position)
        }
    }

    override fun getItemCount(): Int {
        return mSearchDeviceList!!.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val llDeviceSearch: CardView
         val tvDeviceSearch: TextView

        init {
            llDeviceSearch = itemView.findViewById(R.id.ll_device_search)
            tvDeviceSearch = itemView.findViewById(R.id.tv_device_search)
        }
    }

    fun setOnItemClick(onItemClickListener: OnItemClickListener?) {
        mOnItemClickListener = onItemClickListener
    }

    private var mOnItemClickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
    fun setData(scans: Array<String>) {
        mSearchDeviceList = scans
        notifyDataSetChanged()
    }
}