package com.house.criminalintent.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.house.criminalintent.CrimeActivity
import com.house.criminalintent.CrimePagerActivity
import com.house.criminalintent.R
import com.house.criminalintent.models.Crime
import com.house.criminalintent.models.CrimeLab

class CrimeListFragment: Fragment() {

    companion object {
        private const val SAVED_SUBTITLE_VISIBLE = "subtitle"
    }

    private lateinit var crimeRecyclerView: RecyclerView
    private var adapter: CrimeAdapter? = null
    private var isSubtitleVisible: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crime_list, container, false)
        crimeRecyclerView = view.findViewById(R.id.crime_recycler_view)
        crimeRecyclerView.layoutManager = LinearLayoutManager(activity)

        if (savedInstanceState != null) {
            isSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE)
        }

        updateUI()

        return view
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_crime_list, menu)

        val subtitleItem = menu.findItem(R.id.show_subtitle)
        if (isSubtitleVisible) {
            subtitleItem.setTitle(R.string.hide_subtitle)
        } else {
            subtitleItem.setTitle(R.string.show_subtitle)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.new_crime -> {
                val crime = Crime()
                CrimeLab.get(activity as Context).addCrime(crime)
                val intent = CrimePagerActivity.newIntent(activity as Context, crime.id)
                startActivity(intent)
                true
            }
            R.id.show_subtitle -> {
                isSubtitleVisible = !isSubtitleVisible
                activity!!.invalidateOptionsMenu()
                updateSubtitle()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun updateSubtitle() {
        val crimeLab = CrimeLab.get(activity as Context)
        val crimeCount = crimeLab.crimes.size
        var subtitle: String? = resources.getQuantityString(R.plurals.subtitle_plural, crimeCount, crimeCount)
        if (!isSubtitleVisible)
            subtitle = null
        val activity = activity as AppCompatActivity
        activity.supportActionBar!!.subtitle = subtitle
    }

    private fun updateUI() {
        val crimeLab = CrimeLab.get(activity as Context)
        val crimes = crimeLab.crimes
        if (adapter == null) {
            adapter = CrimeAdapter(crimes)
            crimeRecyclerView.adapter = adapter
        } else {
            adapter!!.crimes = crimes
            adapter!!.notifyDataSetChanged()
        }
        updateSubtitle()
    }

    private inner class CrimeHolder(inflater: LayoutInflater, parent: ViewGroup):
        RecyclerView.ViewHolder(inflater.inflate(R.layout.list_item_crime, parent, false)),
        View.OnClickListener {

        private lateinit var crime: Crime
        private var titleTextView: TextView
        private var dateTextView: TextView
        private var solvedImageView: ImageView

        init {
            itemView.setOnClickListener(this)
            titleTextView = itemView.findViewById(R.id.crime_title)
            dateTextView = itemView.findViewById(R.id.crime_date)
            solvedImageView = itemView.findViewById(R.id.crime_solved)
        }

        fun bind(crime: Crime) {
            this.crime = crime
            titleTextView.text = crime.title
            dateTextView.text = DateFormat.format("EEE, MMM dd, yyyy", crime.date)
            solvedImageView.visibility = if (crime.solved) View.VISIBLE else View.GONE
        }

        override fun onClick(v: View?) {
            val intent = CrimePagerActivity.newIntent(activity as Context, crime.id)
            startActivity(intent)
        }

    }

    private inner class CrimeAdapter(var crimes: MutableList<Crime>): RecyclerView.Adapter<CrimeHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeHolder {
            val layoutInflater = LayoutInflater.from(activity)
            return CrimeHolder(layoutInflater, parent)
        }

        override fun getItemCount(): Int = crimes.size

        override fun onBindViewHolder(holder: CrimeHolder, position: Int) {
            val crime = crimes.get(position)
            holder.bind(crime)
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, isSubtitleVisible)
    }

}