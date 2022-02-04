package com.example.sharedataassignment.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.sharedataassignment.view.fragment.ContactsFragment
import com.example.sharedataassignment.view.fragment.MusicFragment

class FragmentAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
     return 2
    }

    override fun createFragment(position: Int): Fragment {
        when(position){
            (0)->{
                return ContactsFragment()

            }
            (1)->{
                return MusicFragment()
            }
        }
        return ContactsFragment()
    }
}