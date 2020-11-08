package com.house.criminalintent

import androidx.fragment.app.Fragment
import com.house.criminalintent.fragments.CrimeListFragment

class CrimeListActivity: SingleFragmentActivity() {

    override fun createFragment(): Fragment = CrimeListFragment()

}