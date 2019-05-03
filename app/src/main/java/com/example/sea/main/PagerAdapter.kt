package com.example.sea.main

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.example.sea.R
import com.example.sea.hourly.HourlyFragment
import com.example.sea.map.MapFragment
import com.example.sea.now.NowFragment
import com.example.sea.weekly.WeeklyFragment

class PagerAdapter(fm : FragmentManager) : FragmentStatePagerAdapter(fm) {

    private lateinit var context : Context
    constructor(fm : FragmentManager, context : Context) : this(fm) {
        this.context = context
    }

    override fun getCount() = 4

    override fun getItem(p0: Int): Fragment {
        return when(p0) {
            0 -> NowFragment()
            1 -> HourlyFragment()
            2 -> WeeklyFragment()
            else -> {
                MapFragment()
            }
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when(position) {
            0 -> context.getString(R.string.tabs_now)
            1 -> context.getString(R.string.tabs_time)
            2 -> context.getString(R.string.tabs_day)
            else -> {
                context.getString(R.string.tabs_maps)
            }
        }
    }

}