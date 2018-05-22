package com.thisobeystudio.customviewpagerkotlin.models

/*
 * Created by Endika Aguilera on 20/5/18.
 * Copyright: (c) 2018 Endika Aguilera
 * Contact: thisobeystudio@gmail.com
 */

import android.os.Bundle
import android.support.v4.app.Fragment

abstract class CustomFragment : Fragment() {

    companion object {
        /**
         * The fragment argument representing the [CustomIndexHelper] for the fragment.
         */
        @JvmStatic
        val ARG_CUSTOM_INDEX_HELPER = "custom_index_helper"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setCustomIndexHelper(arguments)
    }

    abstract fun setHelperPageData(data: Any)

    private lateinit var customIndexHelper: CustomIndexHelper

    protected val pageIndex: Int get() = customIndexHelper.pagerPosition

    protected val dataIndex: Int get() = customIndexHelper.dataPosition

    protected val isHelperFirst: Boolean get() = customIndexHelper.isHelperFirst

    protected val isRealFirst: Boolean get() = customIndexHelper.isRealFirst

    protected val isHelperLast: Boolean get() = customIndexHelper.isHelperLast

    protected val isRealLast: Boolean get() = customIndexHelper.isRealLast

    private fun setCustomIndexHelper(args: Bundle?) {
        if (args?.containsKey(ARG_CUSTOM_INDEX_HELPER) == true)
            customIndexHelper = args.getParcelable(ARG_CUSTOM_INDEX_HELPER)
    }
}
