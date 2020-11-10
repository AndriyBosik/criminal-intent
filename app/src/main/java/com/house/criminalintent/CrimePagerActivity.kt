package com.house.criminalintent

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.house.criminalintent.fragments.CrimeFragment
import com.house.criminalintent.models.Crime
import com.house.criminalintent.models.CrimeLab
import java.util.*

class CrimePagerActivity: AppCompatActivity(), CrimeFragment.Callbacks {

    companion object {
        private const val EXTRA_CRIME_ID = "java.com.house.criminalintent.crime_id"

        fun newIntent(packageContext: Context, crimeId: UUID): Intent {
            val intent = Intent(packageContext, CrimePagerActivity::class.java)
            intent.putExtra(EXTRA_CRIME_ID, crimeId)
            return intent
        }
    }

    private lateinit var viewPager: ViewPager
    private lateinit var crimes: MutableList<Crime>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crime_pager)

        val crimeId = intent.getSerializableExtra(EXTRA_CRIME_ID) as UUID

        viewPager = findViewById(R.id.crime_view_pager)
        crimes = CrimeLab.get(this).crimes
        viewPager.adapter = object: FragmentStatePagerAdapter(supportFragmentManager) {
            override fun getItem(position: Int): Fragment = CrimeFragment.newInstance(crimes.get(position).id)

            override fun getCount(): Int = crimes.size
        }

        for (i in 0 until crimes.size) {
            if (crimes.get(i).id == crimeId) {
                viewPager.currentItem = i
                break
            }
        }
    }

    override fun onCrimeUpdated(crime: Crime) {

    }

}