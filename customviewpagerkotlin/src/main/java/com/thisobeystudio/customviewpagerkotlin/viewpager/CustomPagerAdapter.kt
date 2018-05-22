package com.thisobeystudio.customviewpagerkotlin.viewpager

/*
 * Created by Endika Aguilera on 20/5/18.
 * Copyright: (c) 2018 Endika Aguilera
 * Contact: thisobeystudio@gmail.com
 */

import android.support.v4.app.FragmentManager

/**
 * A [CustomFragmentStatePagerAdapter] that returns a fragment corresponding
 * to one of the pages.
 */

abstract class CustomPagerAdapter
protected constructor(fm: FragmentManager) : CustomFragmentStatePagerAdapter(fm)