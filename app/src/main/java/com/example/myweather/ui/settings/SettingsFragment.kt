package com.example.myweather.ui.settings

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import com.example.myweather.MainActivity
import com.example.myweather.R
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
    private lateinit var  speedRadioGroup : RadioGroup


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
        speedRadioGroup = binding.measurmentRadioGroup


        langRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            var radioButton = root.findViewById<RadioButton>(checkedId)
            if (radioButton == null) {
                langRadioGroup.check(R.id.english_radio_btn)
                radioButton = root.findViewById(R.id.english_radio_btn)

            }
            Log.d("radio button lang", checkedId.toString()+" "+radioButton.toString())

            val language = when (radioButton.text.toString().toLowerCase(Locale.getDefault())) {
                "german" ,"الألمانية","allemand","deutsch"-> "de"
                "arabic","العربية","arabe","arabisch" -> "ar"
                "english","الإنجليزية","anglais","englisch" -> "en"
                "french","الفرنسية","français","französisch" -> "fr"
//                "الألمانية" -> "de"
//                "العربية" -> "ar"
//                "الإنجليزية" -> "en"
//                "الفرنسية" -> "fr"
                else -> ""
            }
            saveData("language", language)
            saveID("languageId",checkedId)
            changeLanguageLocaleTo(requireContext(),language)
            Log.d("lang",language)
        }
        unitsRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            var radioButton = root.findViewById<RadioButton>(checkedId)
            if (radioButton == null) {
                unitsRadioGroup.check(R.id.metric_radio_btn)
                radioButton = root.findViewById(R.id.metric_radio_btn)

            }
            Log.d("radio button", checkedId.toString()+" "+radioButton.toString())

            val units = radioButton.text.toString().toLowerCase(Locale.getDefault())
            saveData("units", units)
            saveID("unitsId",checkedId)
            Log.d("units",units)
        }

        animationRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            var radioButton = root.findViewById<RadioButton>(checkedId)
            if (radioButton == null) {
                animationRadioGroup.check(R.id.animated_radio_btn)
                radioButton = root.findViewById(R.id.animated_radio_btn)

            }
            val animation = radioButton.text.toString()
            saveData("animation", animation)
            saveID("animationId",checkedId)
            Log.d("animation",animation)
            val sharedPreference = requireContext().getSharedPreferences("FirstTimeChoice", Context.MODE_PRIVATE)!!
            val editor: SharedPreferences.Editor = sharedPreference.edit()
            if (checkedId==R.id.non_animated_radio_btn)
            {
                editor.putBoolean("isFirstTime",false )
                editor.putBoolean("isChoiceGps",false )
                editor.apply()

            }
            else if(checkedId==R.id.animated_radio_btn){

                editor.putBoolean("isFirstTime",false )
                editor.putBoolean("isChoiceGps",true )
                editor.apply()

            }

        }
        speedRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            var radioButton = root.findViewById<RadioButton>(checkedId)
            if (radioButton == null) {
                speedRadioGroup.check(R.id.meterPerSecond_radio_btn)
                radioButton = root.findViewById(R.id.meterPerSecond_radio_btn)

            }
            val measurement = radioButton.text.toString()
            saveData("measurement", measurement)
            saveID("measurementId",checkedId)
            Log.d("measurement",measurement)

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
        val selectedRadioButtonMeasure = sharedPreferences.getInt("measurementId", -1)
        if (selectedRadioButtonLang != -1) {
            speedRadioGroup.check(selectedRadioButtonMeasure)
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
        if (speedRadioGroup.checkedRadioButtonId == -1) {
            speedRadioGroup.check(R.id.meterPerSecond_radio_btn)
        }
        binding.button.setOnClickListener {

            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
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