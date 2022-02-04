package com.example.sharedataassignment.view.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.sharedataassignment.adapter.FragmentAdapter
import com.example.sharedataassignment.R
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var  fragmentAdapter: FragmentAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        setTabListener()
    }

    private fun setTabListener() {
        tabLayout.addOnTabSelectedListener(object:TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewpager.currentItem = tabLayout.selectedTabPosition
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                viewpager.currentItem = tabLayout.selectedTabPosition
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                viewpager.currentItem = tabLayout.selectedTabPosition
            }


        })
        viewpager.registerOnPageChangeCallback(object :ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                tabLayout.selectTab(tabLayout.getTabAt(position))
            }

        })
    }

    private fun init(){
        tabLayout.addTab(tabLayout.newTab().setText("Contacts") )
        tabLayout.addTab(tabLayout.newTab().setText("Music"))
        val fragmentManger=supportFragmentManager
        fragmentAdapter= FragmentAdapter(fragmentManger,lifecycle)
        viewpager.adapter=fragmentAdapter
    }
}