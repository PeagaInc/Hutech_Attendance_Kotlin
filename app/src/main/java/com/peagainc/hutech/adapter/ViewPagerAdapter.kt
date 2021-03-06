package com.peagainc.hutech.adapter

import android.service.quicksettings.Tile
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class ViewPagerAdapter(fm: FragmentManager, behavior: Int) : FragmentPagerAdapter(fm, behavior) {
    private var fragments: MutableList<Fragment> = mutableListOf()
    private var fragmentTitle: MutableList<String> = mutableListOf()

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return fragmentTitle[position]
    }

    fun addFragment(fragment: Fragment, title:String){
        fragments.add(fragment)
        fragmentTitle.add(title)
    }
}