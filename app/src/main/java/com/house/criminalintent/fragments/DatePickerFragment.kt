package com.house.criminalintent.fragments

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.DatePicker
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.house.criminalintent.R
import java.util.*

class DatePickerFragment: DialogFragment() {

    companion object {
        const val EXTRA_DATE = "java.com.house.criminalintent.date"

        private const val ARG_DATE = "date"

        fun newInstance(date: Date): DatePickerFragment {
            val args = Bundle()
            args.putSerializable(ARG_DATE, date)

            val fragment = DatePickerFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var datePicker: DatePicker

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val date = arguments!!.getSerializable(ARG_DATE) as Date
        val calendar = Calendar.getInstance()
        calendar.time = date
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val view = LayoutInflater.from(activity as Context).inflate(R.layout.dialog_date, null)

        datePicker = view.findViewById(R.id.dialog_date_picker)
        datePicker.init(year, month, day, null)

        return AlertDialog.Builder(activity as Context)
            .setView(view)
            .setTitle(R.string.date_picker_title)
            .setPositiveButton(android.R.string.ok) { _, _ -> run {
                val year = datePicker.year
                val month = datePicker.month
                val day = datePicker.dayOfMonth
                val date = GregorianCalendar(year, month, day).time
                sendResult(Activity.RESULT_OK, date)
            }}
            .create()
    }

    private fun sendResult(resultCode: Int, date: Date) {
        if (targetFragment == null)
            return
        val intent = Intent()
        intent.putExtra(EXTRA_DATE, date)
        val fragment = targetFragment as Fragment
        fragment.onActivityResult(targetRequestCode, resultCode, intent)
    }

}