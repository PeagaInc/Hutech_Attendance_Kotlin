package com.peagainc.hutech

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.peagainc.hutech.R.layout.activity_home
import com.peagainc.hutech.adapter.ViewPagerAdapter
import com.peagainc.hutech.fragment.AttendanceFragment
import com.peagainc.hutech.fragment.CalendarFragment
import com.peagainc.hutech.fragment.SettingFragment

class HomeActivity : AppCompatActivity() {
    private lateinit var viewPager:ViewPager
    private lateinit var tabLayout: TabLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activity_home)
        initView()
        addEvent()
    }

    private fun initView() {
        viewPager = findViewById(R.id.view_pager)
        tabLayout = findViewById(R.id.tab_layout)

        val attendanceFragment: AttendanceFragment = AttendanceFragment()
        val calendarFragment: CalendarFragment = CalendarFragment()
        val settingFragment: SettingFragment = SettingFragment()
        val viewPagerAdapter: ViewPagerAdapter = ViewPagerAdapter(supportFragmentManager,0)
        viewPagerAdapter.addFragment(attendanceFragment, "")
        viewPagerAdapter.addFragment(calendarFragment, "")
        viewPagerAdapter.addFragment(settingFragment, "")
        viewPager.adapter = viewPagerAdapter
        tabLayout.setupWithViewPager(viewPager)
        tabLayout.getTabAt(0)?.setIcon(R.drawable.tablayout_ic_attendance_selected)
        tabLayout.getTabAt(1)?.setIcon(R.drawable.tablayout_ic_calendar_unselect)
        tabLayout.getTabAt(2)?.setIcon(R.drawable.tablayout_ic_setting_unselect)
        tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    when(tab.position){
                        0 -> {
                            tab.setIcon(R.drawable.tablayout_ic_attendance_selected)
                            tabLayout.getTabAt(1)?.setIcon(R.drawable.tablayout_ic_calendar_unselect)
                            tabLayout.getTabAt(2)?.setIcon(R.drawable.tablayout_ic_setting_unselect)

                        }
                        1 -> {
                            tab.setIcon(R.drawable.tablayout_ic_calendar_selected)
                            tabLayout.getTabAt(0)?.setIcon(R.drawable.tablayout_ic_attendance_unselect)
                            tabLayout.getTabAt(2)?.setIcon(R.drawable.tablayout_ic_setting_unselect)
                        }
                        2 -> {
                            tab.setIcon(R.drawable.tablayout_ic_setting_selected)
                            tabLayout.getTabAt(0)?.setIcon(R.drawable.tablayout_ic_attendance_unselect)
                            tabLayout.getTabAt(1)?.setIcon(R.drawable.tablayout_ic_calendar_unselect)
                        }
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })

    }

    private fun addEvent() {

    }
}