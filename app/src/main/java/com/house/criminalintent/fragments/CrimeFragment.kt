package com.house.criminalintent.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.house.criminalintent.R
import com.house.criminalintent.models.Crime
import com.house.criminalintent.models.CrimeLab
import java.util.*

class CrimeFragment: Fragment() {

    companion object {
        private const val ARG_CRIME_ID = "crime_id"
        private const val DIALOG_DATE = "DialogDate"
        private const val REQUEST_DATE = 0
        private const val REQUEST_CONTACT = 1

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
    private lateinit var suspectButton: Button
    private lateinit var reportButton: Button

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

        reportButton = view.findViewById(R.id.crime_report)
        reportButton.setOnClickListener { run {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, getCrimeReport())
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject))
            val chooserIntent = Intent.createChooser(intent, getString(R.string.send_report))
            startActivity(chooserIntent)
        } }

        val pickContact = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
        suspectButton = view.findViewById(R.id.crime_suspect)
        suspectButton.setOnClickListener { run {
            startActivityForResult(pickContact, REQUEST_CONTACT)
        } }
        if (crime.suspect != null)
            suspectButton.text = crime.suspect

        val packageManager = activity!!.packageManager
        if (packageManager.resolveActivity(pickContact, PackageManager.MATCH_DEFAULT_ONLY) == null) {
            suspectButton.isEnabled = false
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK)
            return
        if (requestCode == REQUEST_DATE) {
            val date = data!!.getSerializableExtra(DatePickerFragment.EXTRA_DATE) as Date
            crime.date = date
            updateDate()
        } else if (requestCode == REQUEST_CONTACT && data != null) {
            val contactUri = data.data!!
            val queryFields = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)
            val cursor = activity!!.contentResolver.query(contactUri, queryFields, null, null, null)
            cursor.use { cursor ->
                if (cursor!!.count == 0) {
                    return
                }
                cursor.moveToFirst()
                val suspect = cursor.getString(0)
                crime.suspect = suspect
                suspectButton.text = suspect
            }
        }
    }

    private fun updateDate() {
        dateButton.text = crime.date.toString()
    }

    private fun getCrimeReport(): String {
        val solvedString = if (crime.solved) {
            getString(R.string.crime_report_solved)
        } else {
            getString(R.string.crime_report_unsolved)
        }
        val dateFormat = "EEE, MMM dd"
        val dateString = DateFormat.format(dateFormat, crime.date).toString()

        val suspect = if (crime.suspect == null) {
            getString(R.string.crime_report_no_suspect)
        } else {
            getString(R.string.crime_report_suspect, crime.suspect)
        }

        return getString(R.string.crime_report, crime.title, dateString, solvedString, suspect)
    }

}