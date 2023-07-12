package com.example.myweather.ui.search

import android.content.ContentValues
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.myweather.apistates.AutoCompleteApiState
import com.example.myweather.database.ConcreteLocalSource
import com.example.myweather.database.Repo
import com.example.myweather.databinding.FragmentSearchBinding
import com.example.myweather.model.City
import com.example.myweather.network.ApiClient
import com.example.myweather.network.RemoteSource
import com.example.myweather.ui.home.HourlyAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    var remoteSource : RemoteSource = ApiClient()
    lateinit var searchViewModel : SearchViewModel
    lateinit var adapter: SearchAdapter
    val mutableList = mutableListOf<City>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val searchViewModelFactory = SearchViewModelFactory(
            Repo.getInstance(remoteSource, ConcreteLocalSource.getInstance(requireContext()))
        )

        searchViewModel = ViewModelProvider(requireActivity(), searchViewModelFactory).get(SearchViewModel::class.java)
        adapter = SearchAdapter(requireContext())
        binding.searchRecyclerView.adapter = adapter

    return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s?.toString()?.trim()
                if (query != null) {
//                    viewLifecycleOwner.lifecycleScope.launch {
//                        val city = ApiClient().getAutoComplete(query)
//                        city.body()?.let { Log.d("city obj", it.get(0).country) }
//                    }
                    viewLifecycleOwner.lifecycleScope.launch {
                        searchViewModel.getAutoComplete(query)
                        searchViewModel.cityFlow.collectLatest { status ->
                            Log.d("insideflow", "onView: ${status.javaClass}")
                            when (status) {
                                is AutoCompleteApiState.Loading -> {
                                    Log.d("insideLoading", "onView: ${status.javaClass}")

                                }
                                is AutoCompleteApiState.Success -> {

                                    Log.d(ContentValues.TAG, "status: Success")
                                    status.city?.let {

                                    Log.d("listOfCities" , it.toString())
                                    adapter.submitList(it)

                                }
                            }
                                else -> {
                                    Log.d("insideElse", "onView: ${status.javaClass}")

                                }
                            }
                        }


                    }
                }
            }

            override fun afterTextChanged(editable: Editable?) {
            }
        }
        binding.searchView.addTextChangedListener(textWatcher)
    }


}