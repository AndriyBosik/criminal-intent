package com.house.criminalintent

import android.view.View
import androidx.fragment.app.Fragment
import com.house.criminalintent.fragments.CrimeFragment
import com.house.criminalintent.fragments.CrimeListFragment
import com.house.criminalintent.models.Crime

class CrimeListActivity: SingleFragmentActivity(), CrimeListFragment.Callbacks, CrimeFragment.Callbacks {

    override fun getLayoutResId() = R.layout.activity_masterdetail

    override fun createFragment(): Fragment = CrimeListFragment()

    override fun onCrimeSelected(crime: Crime) {
        if (findViewById<View>(R.id.detail_fragment_container) == null) {
            val intent = CrimePagerActivity.newIntent(this, crime.id)
            startActivity(intent)
        } else {
            val newDetail = CrimeFragment.newInstance(crime.id) as Fragment
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.detail_fragment_container, newDetail)
                .commit()
        }
    }

    override fun onCrimeUpdated(crime: Crime) {
        val listFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as CrimeListFragment
        listFragment.updateUI()
    }

}