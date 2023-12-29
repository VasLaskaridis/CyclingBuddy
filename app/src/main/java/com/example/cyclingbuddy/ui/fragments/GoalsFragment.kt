package com.example.cyclingbuddy.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.cyclingbuddy.R
import com.example.cyclingbuddy.databinding.FragmentGoalsBinding
import com.example.cyclingbuddy.util.Constants.KEY_EMAIL
import com.example.cyclingbuddy.util.Constants.KEY_FIRST_TIME_TOGGLE
import com.example.cyclingbuddy.util.Constants.KEY_GOAL
import com.example.cyclingbuddy.util.Constants.KEY_NAME
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class GoalsFragment : Fragment() {

    @Inject
    lateinit var sharedPref: SharedPreferences

    var isFirstAppOpen = true

    private var _binding: FragmentGoalsBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGoalsBinding.inflate(inflater, container, false)
        val view = binding.root

        isFirstAppOpen=sharedPref.getBoolean(KEY_FIRST_TIME_TOGGLE, true)
        val name = sharedPref.getString(KEY_NAME, "")
        binding.welcomeTv.setText("Hello ${name}. Please fill the following the fields to complete your account.")
        if(!isFirstAppOpen) {
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.goalsFragment, true)
                .build()
            findNavController().navigate(R.id.action_goalsFragment_to_rideFragment, savedInstanceState, navOptions)
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
            writePersonalDataToSharedPref()
            findNavController().navigate(R.id.action_goalsFragment_to_rideFragment)
        }
    }

    private fun writePersonalDataToSharedPref(){
        var email=""
        var goal="0"
        if(binding.emailTiet.text!!.isNotEmpty()){
            email = binding.emailTiet.text.toString()
        }
        if(binding.goalTiet.text!!.isNotEmpty()) {
            goal = binding.goalTiet.text.toString()
        }
        sharedPref.edit()
            .putString(KEY_EMAIL, email)
            .putInt(KEY_GOAL, goal.toInt())
            .putBoolean(KEY_FIRST_TIME_TOGGLE, false)
            .apply()
    }

}