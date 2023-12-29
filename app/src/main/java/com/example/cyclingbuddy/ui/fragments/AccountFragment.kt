package com.example.cyclingbuddy.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.cyclingbuddy.R
import com.example.cyclingbuddy.databinding.FragmentSettingsBinding
import com.example.cyclingbuddy.util.Constants.KEY_EMAIL
import com.example.cyclingbuddy.util.Constants.KEY_GOAL
import com.example.cyclingbuddy.util.Constants.KEY_NAME
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AccountFragment : Fragment(R.layout.fragment_settings) {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private var _binding: FragmentSettingsBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadFieldsFromSharedPref()
        binding.applyChangesBtn.setOnClickListener {
            val success = applyChangesToSharedPref()
            if(success) {
                Snackbar.make(view, "Saved changes", Snackbar.LENGTH_LONG).show()
            } else {
                Snackbar.make(view, "Please fill out all the fields", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun loadFieldsFromSharedPref() {
        val name = sharedPreferences.getString(KEY_NAME, "")
        val email = sharedPreferences.getString(KEY_EMAIL, "")
        val goal = sharedPreferences.getInt(KEY_GOAL, 0)
        binding.nameTiet.setText(name)
        binding.emailTiet.setText(email)
        binding.goalTiet.setText(goal.toString())
    }

    private fun applyChangesToSharedPref(): Boolean {
        val nameText = binding.nameTiet.text.toString()
        val weightText = binding.emailTiet.text.toString()
        val goalText = binding.goalTiet.text.toString()
        if(nameText.isEmpty() || weightText.isEmpty()) {
            return false
        }
        sharedPreferences.edit()
            .putString(KEY_NAME, nameText)
            .putInt(KEY_EMAIL, weightText.toInt())
            .putInt(KEY_GOAL, goalText.toInt())
            .apply()
        val toolbarText = "Let's go $nameText"
        requireActivity().findViewById<com.google.android.material.textview.MaterialTextView>(R.id.toolbarTitle_tv).text = toolbarText
        return true
    }
}