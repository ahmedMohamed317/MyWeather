package com.example.myweather.ui.alert

import com.example.myweather.model.AlertPojo

interface OnDeleteAlertItem {

    fun deleteAlertItem(alertPojo: AlertPojo)
}