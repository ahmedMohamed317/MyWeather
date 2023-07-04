package com.example.myweather

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myweather.databinding.FragmentFavoriteBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class FavoriteFragment : Fragment(),OnDeleteFavoriteInterface {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!
    var remoteSource : RemoteSource = ApiClient()
    private lateinit var favoriteViewModel: FavoriteViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val favoriteViewModelFactory = FavoriteViewModelFactory(
        Repo.getInstance(remoteSource, ConcreteLocalSource.getInstance(requireContext())))
        favoriteViewModel = ViewModelProvider(requireActivity(), favoriteViewModelFactory).get(FavoriteViewModel::class.java)
        val favoriteAdapter = FavroiteAdapter(requireContext(),this)
        binding.favRv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.favRv.adapter= favoriteAdapter
        binding.floatingActionButton2.setOnClickListener {
            val intent = Intent(requireContext(), FavoriteMapActivity::class.java)
            startActivity(intent)
        }


        viewLifecycleOwner.lifecycleScope.launch {
            favoriteViewModel.favWeatherFlow.collectLatest { status ->
                Log.d("insidefavflow", "onView: ${status.javaClass}")
                when (status) {
                    is FavApiState.Loading -> {
                        Log.d("insideLoading", "onView: ${status.javaClass}")

                    }
                    is FavApiState.Success -> {

                        Log.d(ContentValues.TAG, "status: Success")
                        favoriteAdapter.submitList(status.favWeatherList)


                    }
                    else -> {
                        Log.d("insideElseFavoriteFrag", "onView: ${status.javaClass}")

                    }
                }
            }
        }



        return root

    }

    override fun onFavoriteDelete(weatherResponse: WeatherResponse) {

        lifecycleScope.launch(Dispatchers.IO){
            Log.d("the remove button" , "in favfrag")

            favoriteViewModel.delete(weatherResponse)
        }
    }


}