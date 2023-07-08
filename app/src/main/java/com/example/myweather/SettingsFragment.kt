package com.example.myweather

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import com.example.myweather.databinding.FragmentHomeBinding
import com.example.myweather.databinding.FragmentSettingsBinding
import java.util.*


class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var langRadioGroup: RadioGroup
    private lateinit var unitsRadioGroup: RadioGroup
    private lateinit var animationRadioGroup: RadioGroup


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        langRadioGroup = binding.langRg
        unitsRadioGroup = binding.UnitsRadioGroup
        animationRadioGroup = binding.animationRadioGroup

        langRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            val radioButton = root.findViewById<RadioButton>(checkedId)
            val language = when (radioButton.text.toString().toLowerCase(Locale.getDefault())) {
                "german" -> "de"
                "arabic" -> "ar"
                "english" -> "en"
                "french" -> "fr"
                else -> ""
            }
            saveData("language", language)
            saveID("languageId",checkedId)
            changeLanguageLocaleTo(requireContext(),language)
            Log.d("lang",language)
        }
        unitsRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            val radioButton = root.findViewById<RadioButton>(checkedId)
            val units = radioButton.text.toString().toLowerCase(Locale.getDefault())
            saveData("units", units)
            saveID("unitsId",checkedId)
            Log.d("units",units)
        }

        animationRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            val radioButton = root.findViewById<RadioButton>(checkedId)
            val animation = radioButton.text.toString()
            saveData("animation", animation)
            saveID("animationId",checkedId)
            Log.d("animation",animation)

        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val selectedRadioButtonLang = sharedPreferences.getInt("languageId", -1)
        if (selectedRadioButtonLang != -1) {
            langRadioGroup.check(selectedRadioButtonLang)
        }
        val selectedRadioButtonUnit = sharedPreferences.getInt("unitsId", -1)
        if (selectedRadioButtonUnit != -1) {
            unitsRadioGroup.check(selectedRadioButtonUnit)
        }
        val selectedRadioButtonAnime = sharedPreferences.getInt("animationId", -1)
        if (selectedRadioButtonLang != -1) {
            animationRadioGroup.check(selectedRadioButtonAnime)
        }
        if (langRadioGroup.checkedRadioButtonId == -1) {
            langRadioGroup.check(R.id.english_radio_btn)
        }

        if (unitsRadioGroup.checkedRadioButtonId == -1) {
            unitsRadioGroup.check(R.id.metric_radio_btn)
        }

        if (animationRadioGroup.checkedRadioButtonId == -1) {
            animationRadioGroup.check(R.id.non_animated_radio_btn)
        }
    }

    fun saveData( key: String, value: String) {

        editor.putString(key, value)
        editor.apply()
    }
    fun saveID( key: String, value: Int) {

        editor.putInt(key, value)
        editor.apply()
    }


}