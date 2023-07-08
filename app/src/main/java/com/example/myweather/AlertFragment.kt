package com.example.myweather

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentValues
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.*
import com.example.myweather.databinding.FragmentAlarmBinding
import com.example.myweather.databinding.FragmentFavoriteBinding
import com.example.myweather.databinding.SetAlarmDialogueBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit

class AlertFragment : Fragment() , OnDeleteAlertItem{
    private val REQUEST_OVERLAY_PERMISSION = 123
    private var _binding: FragmentAlarmBinding? = null
    private val binding get() = _binding!!
    var remoteSource: RemoteSource = ApiClient()
    private lateinit var alertViewModel: AlertViewModel
    private lateinit var customAlertDialogBinding: SetAlarmDialogueBinding
    private lateinit var materialAlertDialogBuilder: MaterialAlertDialogBuilder


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlarmBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val alertViewModelFactory = AlertViewModelFactory(
            Repo.getInstance(remoteSource, ConcreteLocalSource.getInstance(requireContext()))
        )
        alertViewModel = ViewModelProvider(
            requireActivity(),
            alertViewModelFactory
        ).get(AlertViewModel::class.java)

        materialAlertDialogBuilder = MaterialAlertDialogBuilder(requireContext(),)
        val alertAdapter = AlertAdapter(requireContext(),this)

        binding.alertRv.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.alertRv.adapter = alertAdapter
        binding.floatingActionButtonAlert.setOnClickListener {
            customAlertDialogBinding =
                SetAlarmDialogueBinding.inflate(LayoutInflater.from(requireContext()), null, false)
            requestOverlayPermission()
        //            startAlertDialog()
        }
        Log.d("alert view " , "before launch")

        viewLifecycleOwner.lifecycleScope.launch {
            alertViewModel.alertWeatherFlow.collectLatest { status ->
                Log.d("insidealertflow", "onView: ${status.javaClass}")
                try{
                when (status) {
                    is AlertApiState.Loading -> {
                        Log.d("insideLoadingAlert", "onView: ${status.javaClass}")

                    }
                    is AlertApiState.Success -> {

                        Log.d(ContentValues.TAG, "status: Success")
                        alertAdapter.submitList(status.alertList)


                    }
                    else -> {
                        Log.d("insideElseAlertFrag", "onView: ${status.javaClass}")

                    }
                }
            }
                catch (e : Exception){
                    Log.d("insidecatchAlertFrag", e.message.toString())

                }
            }
        }


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

     @SuppressLint("PrivateResource")
     private fun startAlertDialog() {
        customAlertDialogBinding.radioAlarm.isChecked = true
        val alertDialog = materialAlertDialogBuilder.setView(customAlertDialogBinding.root)
            .setBackground(
                ResourcesCompat.getDrawable(
                    resources, R.drawable.dialogue_background, requireActivity().theme
                )
            ).setCancelable(false).show()
        val calendar = Calendar.getInstance()
        val currentTime = calendar.timeInMillis
        customAlertDialogBinding.textViewStartDate.setDate(currentTime)
        customAlertDialogBinding.textViewStartTime.setTime(currentTime)
        val timeAfterOneHour = calendar.get(Calendar.HOUR_OF_DAY)
        calendar.set(Calendar.HOUR_OF_DAY, timeAfterOneHour + 1)
        customAlertDialogBinding.textViewEndDate.setDate(calendar.timeInMillis)
        customAlertDialogBinding.textViewEndTime.setTime(calendar.timeInMillis)

        var startTime = Calendar.getInstance().timeInMillis
        val endCal = Calendar.getInstance()
        endCal.add(Calendar.DAY_OF_MONTH, 1)
        var endTime = endCal.timeInMillis

         customAlertDialogBinding.cardViewChooseStart.setOnClickListener {
             setAlarm(startTime) { currentTime ->
                 startTime = currentTime
                 customAlertDialogBinding.textViewStartDate.setDate(currentTime)
                 customAlertDialogBinding.textViewStartTime.setTime(currentTime)
             }
         }
         customAlertDialogBinding.cardViewChooseEnd.setOnClickListener {
             setAlarm(endTime) { currentTime ->
                 endTime = currentTime
                 customAlertDialogBinding.textViewEndDate.setDate(currentTime)
                 customAlertDialogBinding.textViewEndTime.setTime(currentTime)
             }
         }
         customAlertDialogBinding.buttonCancel.setOnClickListener {
             alertDialog.dismiss()
         }

        customAlertDialogBinding.buttonSave.setOnClickListener {

            val id: String?
            val alertPojo: AlertPojo

            if (customAlertDialogBinding.radioAlarm.isChecked) {
                alertPojo = AlertPojo(start = startTime, end = endTime, kind = AlertKind.ALARM)
                alertViewModel.insert(alertPojo)
                id = alertPojo.id
            } else  {
                alertPojo =
                    AlertPojo(start = startTime, end = endTime, kind = AlertKind.NOTIFICATION)
                alertViewModel.insert(alertPojo)
                id = alertPojo.id
            }


            doWorker(startTime, endTime, id)
            with(Intent(requireContext(), AlertMapActivity::class.java)) {

                putExtra("ID", id)
                startActivity(this)
            }
            alertDialog.dismiss()
        }


    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(requireContext())) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${requireContext().packageName}")
            )
            startActivityForResult(intent, REQUEST_OVERLAY_PERMISSION)
        }
        else if(Settings.canDrawOverlays(requireContext())){
            startAlertDialog()
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_OVERLAY_PERMISSION) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(requireContext())) {
                startAlertDialog()

            } else {
                AlertDialog.Builder(requireActivity()).setTitle("Warning")
                    .setCancelable(false).setMessage(
                        "Sorry , We can't proceed your request as you Draw Over Apps Permission is not granted")
                    .setPositiveButton(android.R.string.ok) { _, _ -> }.show()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
     private fun doWorker(startTime: Long, endTime: Long, id: String) {

         val currentTime = Calendar.getInstance().timeInMillis
         val constraint =
             Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()

        val inputData = Data.Builder()

         inputData.putString("ID", id)

        val myWorkRequest: WorkRequest = if ((endTime - startTime) < 24 * 60 * 60 * 1000) {
            OneTimeWorkRequestBuilder<AlertWorker>().addTag(id).setInitialDelay(
                startTime - currentTime, TimeUnit.MILLISECONDS
            ).setInputData(
                 inputData.build()
            ).setConstraints(constraint).build()

        } else {

            WorkManager.getInstance(requireContext()).enqueue(
                OneTimeWorkRequestBuilder<AlertWorker>().addTag(id).setInitialDelay(
                    startTime - currentTime, TimeUnit.MILLISECONDS
                ).setInputData(
                     inputData.build()
                ).setConstraints(constraint).build()
            )

            PeriodicWorkRequest.Builder(
                AlertWorker::class.java, 24L, TimeUnit.HOURS, 1L, TimeUnit.HOURS
            ).addTag(id).setInputData(
                inputData = inputData.build()
            ).setConstraints(constraint).build()
        }
        WorkManager.getInstance(requireContext()).enqueue(myWorkRequest)
    }
    private fun setAlarm(minTime: Long, callback: (Long) -> Unit) {
        val calendar = Calendar.getInstance()
        calendar.apply {
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            R.style.Theme_MyWeather,
            { _, year, month, day ->
                calendar.apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DAY_OF_MONTH, day)
                }

                val timePickerDialog = TimePickerDialog(
                    requireContext(),
                    R.style.Theme_MyWeather,
                    { _, hour, minute ->
                        calendar.apply {
                            set(Calendar.HOUR_OF_DAY, hour)
                            set(Calendar.MINUTE, minute)
                        }
                        callback(calendar.timeInMillis)
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    false
                )

                timePickerDialog.show()
                timePickerDialog.setCancelable(false)
                timePickerDialog.getButton(TimePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)
                timePickerDialog.getButton(TimePickerDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.datePicker.minDate = minTime
        datePickerDialog.show()
        datePickerDialog.setCancelable(false)
        datePickerDialog.getButton(TimePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)
        datePickerDialog.getButton(TimePickerDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
    }

    override fun deleteAlertItem(alertPojo: AlertPojo) {
        alertViewModel.delete(alertPojo)
        WorkManager.getInstance(requireContext()).cancelAllWorkByTag(alertPojo.id)
    }


}