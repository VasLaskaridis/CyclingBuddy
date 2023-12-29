package com.example.cyclingbuddy.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.cyclingbuddy.R
import com.example.cyclingbuddy.databinding.FragmentSetupBinding
import com.example.cyclingbuddy.util.Constants.KEY_FIRST_TIME_TOGGLE
import com.example.cyclingbuddy.util.Constants.KEY_NAME
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SetupFragment : Fragment(R.layout.fragment_setup) {

    @Inject
    lateinit var sharedPref: SharedPreferences

    var isFirstAppOpen = true

    private var _binding: FragmentSetupBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSetupBinding.inflate(inflater, container, false)
        val view = binding.root

        isFirstAppOpen=sharedPref.getBoolean(KEY_FIRST_TIME_TOGGLE, true)
        if(!isFirstAppOpen) {
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.setupFragment, true)
                .build()
            findNavController().navigate(R.id.action_setupFragment_to_goalsFragment, savedInstanceState, navOptions)
        }
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.continueTv.setOnClickListener {
            val success = writePersonalDataToSharedPref()
            if(success) {
                findNavController().navigate(R.id.action_setupFragment_to_goalsFragment)
            } else {
                Snackbar.make(requireView(), "Please enter your username", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun writePersonalDataToSharedPref(): Boolean {
        val name = binding.nameTiet.text.toString()
        if(name.isEmpty() ){
            return false
        }
        sharedPref.edit()
            .putString(KEY_NAME, name)
            .apply()
        val toolbarText = "Let's go, $name!"
        requireActivity().findViewById<com.google.android.material.textview.MaterialTextView>(R.id.toolbarTitle_tv).text = toolbarText
        return true
    }
}