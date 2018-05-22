package com.thisobeystudio.customviewpagerkotlin.indicator

/*
 * Created by Endika Aguilera on 20/5/18.
 * Copyright: (c) 2018 Endika Aguilera
 * Contact: thisobeystudio@gmail.com
 */

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

import com.thisobeystudio.customviewpagerkotlin.R
import com.thisobeystudio.customviewpagerkotlin.util.layoutInflater

class IndicatorsRecyclerViewAdapter internal constructor(
        private val mContext: Context?,
        private var mCount: Int,
        private var mSelection: Int) :
        RecyclerView.Adapter<IndicatorsRecyclerViewAdapter.IndicatorViewHolder>() {

    private var mIndicatorCallbacks: IndicatorCallbacks? = null

    inner class IndicatorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val indicatorsImage: ImageView = itemView.findViewById(R.id.indicators_image)
    }

    override fun getItemCount(): Int {
        return if (this.mCount < 0) 0 else this.mCount
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): IndicatorViewHolder {
        val rowView = mContext!!.layoutInflater.inflate(R.layout.indicator_item, parent, false)
        return IndicatorViewHolder(rowView)
    }

    override fun onBindViewHolder(viewHolder: IndicatorViewHolder, position: Int) {

        if (mContext == null) return

        viewHolder.indicatorsImage.isSelected = position == mSelection

        viewHolder.indicatorsImage.setOnClickListener {
            viewHolder.indicatorsImage.isSelected = true
            notifyItemChanged(mSelection)

            if (mIndicatorCallbacks != null)
                mIndicatorCallbacks!!.onIndicatorClick(position)
        }
    }

    internal fun updateSelection(newSelection: Int) {
        notifyItemChanged(mSelection)
        mSelection = newSelection
        notifyItemChanged(mSelection)
    }

    interface IndicatorCallbacks {
        fun onIndicatorClick(position: Int)
    }

    // sets indicator click callback
    internal fun setIndicatorCallbacks(callbacks: IndicatorCallbacks) {
        this.mIndicatorCallbacks = callbacks
    }

    internal fun swapData(newCount: Int) {
        mCount = if (newCount < 0) 0 else newCount
        notifyDataSetChanged()
    }
}
