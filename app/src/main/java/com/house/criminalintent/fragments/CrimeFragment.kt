package com.house.criminalintent.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.house.criminalintent.CrimeActivity
import com.house.criminalintent.R
import com.house.criminalintent.models.Crime
import com.house.criminalintent.models.CrimeLab
import java.util.*

class CrimeFragment: Fragment() {

    companion object {
        private const val ARG_CRIME_ID = "crime_id"
        private const val DIALOG_DATE = "DialogDate"
        private const val REQUEST_DATE = 0

        fun newInstance(crimeId: UUID): CrimeFragment {
            val args = Bundle()
            args.putSerializable(ARG_CRIME_ID, crimeId)

            val fragment = CrimeFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var crime: Crime
    private lateinit var titleField: EditText
    private lateinit var dateButton: Button
    private lateinit var solvedCheckBox: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val crimeId = arguments!!.getSerializable(ARG_CRIME_ID) as UUID
        crime = CrimeLab.get(activity as Context).getCrime(crimeId)!!
    }

    override fun onPause() {
        super.onPause()

        CrimeLab.get(activity as Context).updateCrime(crime)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crime, container, false)
        titleField = view.findViewById(R.id.crime_title)
        titleField.setText(crime.title)
        titleField.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                crime.title = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {

            }
        } )

        dateButton = view.findViewById(R.id.crime_date)
        updateDate()
        dateButton.setOnClickListener {
            val manager = fragmentManager
            val dialog = DatePickerFragment.newInstance(crime.date)
            dialog.setTargetFragment(CrimeFragment@this, REQUEST_DATE)
            dialog.show(manager!!, DIALOG_DATE)
        }

        solvedCheckBox = view.findViewById(R.id.crime_solved)
        solvedCheckBox.isChecked = crime.solved
        solvedCheckBox.setOnCheckedChangeListener { _, isChecked -> crime.solved = isChecked }
        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK)
            return
        if (requestCode == REQUEST_DATE) {
            val date = data!!.getSerializableExtra(DatePickerFragment.EXTRA_DATE) as Date
            crime.date = date
            updateDate()
        }
    }

    private fun updateDate() {
        dateButton.text = crime.date.toString()
    }

}