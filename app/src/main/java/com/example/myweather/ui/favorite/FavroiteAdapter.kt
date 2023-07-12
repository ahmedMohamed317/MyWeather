package com.example.myweather.ui.favorite

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myweather.R
import com.example.myweather.databinding.FavoriteItemBinding
import com.example.myweather.model.WeatherResponse
import java.util.*

class FavroiteAdapter(var context: Context , var listener : OnDeleteFavoriteInterface) : ListAdapter<WeatherResponse, FavroiteAdapter.ViewHolder>(
    DiffUtils
) {
    class ViewHolder(val binding: FavoriteItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = FavoriteItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val favItem = getItem(position)

        holder.binding.secondaryCountryName.text =  getAddressFromLatLng(favItem.lat,favItem.lon).city
        holder.binding.countryName.text =  getAddressFromLatLng(favItem.lat,favItem.lon).country
        holder.binding.removeBtn.setOnClickListener {
            try {
                showAreYouSureDialogDeletion(favItem)

            }
            catch (e:Exception){
                Log.d("the remove button" , e.message.toString())
            }

        }
        holder.binding.root.setOnClickListener{
            val intent = Intent(context, FavoriteDetailsActivity::class.java)
            intent.putExtra("EXTRA_OBJECT", favItem)
            context.startActivity(intent)
        }



    }
    object DiffUtils : DiffUtil.ItemCallback<WeatherResponse>() {
        override fun areItemsTheSame(oldItem: WeatherResponse, newItem: WeatherResponse): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: WeatherResponse, newItem: WeatherResponse): Boolean {
            return oldItem == newItem
        }

    }

    fun getAddressFromLatLng(latitude: Double, longitude: Double): CountryName {
        val geocoder = Geocoder(context, Locale.getDefault())
        var country: String = ""
        var city: String? = ""
        var city2: String? = ""
        var city3: String? = ""


        try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)

            if (addresses != null) {
                if (addresses.isNotEmpty()) {
                    val address = addresses[0]
                    country = address.countryName

                    city = if(address.adminArea.isBlank()){
                        ""
                    } else{
                        address.adminArea.plus(" , ")

                    }
                    city2 = if(address.locality.isBlank()){
                        ""
                    } else{
                        address.locality.plus("  ")

                    }



                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return CountryName(country,city+" "+city2)

    }

    data class CountryName(var country : String,var city:String)

    private fun showAreYouSureDialogDeletion(favItem: WeatherResponse) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(context.getString(R.string.warning))
        builder.setMessage(context.getString(R.string.warning_content_favorite))
        builder.setPositiveButton(context.getString(R.string.yes)) { dialog, which ->

            listener.onFavoriteDelete(favItem)
            this.notifyDataSetChanged()
        }
        builder.setNegativeButton(context.getString(R.string.no)) { dialog, which ->
           dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }
}