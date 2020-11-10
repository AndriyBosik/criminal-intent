package com.house.criminalintent.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.house.criminalintent.R
import com.house.criminalintent.models.Crime
import com.house.criminalintent.models.CrimeLab
import com.house.criminalintent.utils.PictureUtils
import java.io.File
import java.util.*

class CrimeFragment: Fragment() {

    companion object {
        private const val ARG_CRIME_ID = "crime_id"
        private const val DIALOG_DATE = "DialogDate"
        private const val REQUEST_DATE = 0
        private const val REQUEST_CONTACT = 1
        private const val REQUEST_PHOTO = 2

        fun newInstance(crimeId: UUID): CrimeFragment {
            val args = Bundle()
            args.putSerializable(ARG_CRIME_ID, crimeId)

            val fragment = CrimeFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var crime: Crime
    private lateinit var photoFile: File
    private lateinit var titleField: EditText
    private lateinit var dateButton: Button
    private lateinit var solvedCheckBox: CheckBox
    private lateinit var suspectButton: Button
    private lateinit var reportButton: Button
    private lateinit var photoButton: ImageButton
    private lateinit var photoView: ImageView
    private var callbacks: Callbacks? = null

    interface Callbacks {
        fun onCrimeUpdated(crime: Crime)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val crimeId = arguments!!.getSerializable(ARG_CRIME_ID) as UUID
        crime = CrimeLab.get(activity as Context).getCrime(crimeId)!!
        photoFile = CrimeLab.get(activity as Context).getPhotoFile(crime)
    }

    override fun onPause() {
        super.onPause()

        CrimeLab.get(activity as Context).updateCrime(crime)
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
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
                updateCrime()
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
        solvedCheckBox.setOnCheckedChangeListener { _, isChecked -> {
            crime.solved = isChecked
            updateCrime()
        } }

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

        photoButton = view.findViewById(R.id.crime_camera)
        val captureImage = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val canTakePhoto = photoFile != null && captureImage.resolveActivity(packageManager) != null
        photoButton.isEnabled = canTakePhoto
        photoButton.setOnClickListener{ run {
            val uri = FileProvider.getUriForFile(activity as Context, "com.house.criminalintent.fileprovider", photoFile)
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri)
            val cameraActivities = activity!!.packageManager.queryIntentActivities(captureImage, PackageManager.MATCH_DEFAULT_ONLY)

            for (activity in cameraActivities) {
                this.activity!!.grantUriPermission(activity.activityInfo.packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            }
            startActivityForResult(captureImage, REQUEST_PHOTO)
        } }

        photoView = view.findViewById(R.id.crime_photo)
        updatePhotoView()

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK)
            return
        if (requestCode == REQUEST_DATE) {
            val date = data!!.getSerializableExtra(DatePickerFragment.EXTRA_DATE) as Date
            crime.date = date
            updateCrime()
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
                updateCrime()
                suspectButton.text = suspect
            }
        } else if (requestCode == REQUEST_PHOTO) {
            val uri = FileProvider.getUriForFile(activity as Context, "com.house.criminalintent.fileprovider", photoFile)
            activity!!.revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            updateCrime()
            updatePhotoView()
        }
    }

    private fun updateCrime() {
        CrimeLab.get(activity as Context).updateCrime(crime)
        callbacks!!.onCrimeUpdated(crime)
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

    private fun updatePhotoView() {
        if (photoFile == null || !photoFile.exists()) {
            photoView.setImageDrawable(null)
        } else {
            val bitmap = PictureUtils.getScaledBitmap(photoFile.path, activity!!)
            photoView.setImageBitmap(bitmap)
        }
    }

}