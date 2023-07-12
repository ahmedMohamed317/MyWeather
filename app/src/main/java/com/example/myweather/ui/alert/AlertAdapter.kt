package com.example.myweather.ui.alert

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.work.WorkManager
import com.example.myweather.R
import com.example.myweather.databinding.AlertItemBinding
import com.example.myweather.ui.settings.getFormattedDateWithoutYear
import com.example.myweather.ui.settings.getFormattedTime
import com.example.myweather.model.AlertPojo
import java.util.*

class AlertAdapter(var context: Context,var listener: OnDeleteAlertItem) : ListAdapter<AlertPojo, AlertAdapter.ViewHolder>(
    DiffUtils
) {
    class ViewHolder(val binding: AlertItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = AlertItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)    }


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val alertItem = getItem(position)
        holder.binding.endDate.text = getFormattedDateWithoutYear(alertItem.start/1000)
        holder.binding.startDate.text = getFormattedDateWithoutYear(alertItem.end/1000)
        holder.binding.endHour.text = getFormattedTime(alertItem.start/1000)
        holder.binding.startHour.text = getFormattedTime(alertItem.end/1000)
        Log.d("timestamp start" , alertItem.start.toString())
        Log.d("timestamp end" , alertItem.end.toString())

        holder.binding.CancelBtn.setOnClickListener {
            try {
             showAreYouSureDialogDeletion(alertItem)
            }
            catch (e:Exception){
                Log.d("the cancel button" , e.message.toString())
            }

        }



    }
    object DiffUtils : DiffUtil.ItemCallback<AlertPojo>() {
        override fun areItemsTheSame(oldItem: AlertPojo, newItem: AlertPojo): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: AlertPojo, newItem: AlertPojo): Boolean {
            return oldItem == newItem
        }

    }

    private fun showAreYouSureDialogDeletion(alertPojo: AlertPojo) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(context.getString(R.string.warning))
        builder.setMessage(context.getString(R.string.warning_content_alert))
        builder.setPositiveButton(context.getString(R.string.yes)) { dialog, which ->
            WorkManager.getInstance(context).cancelAllWorkByTag(alertPojo.id)
            listener.deleteAlertItem(alertPojo)
        }
        builder.setNegativeButton(context.getString(R.string.no)) { dialog, which ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }


}