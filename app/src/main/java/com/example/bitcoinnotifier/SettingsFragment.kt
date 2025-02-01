package com.bitcoin.btcnotify

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView

class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        val sharedPreferences = requireContext().getSharedPreferences("APP_PREFERENCES", Context.MODE_PRIVATE)

        val apiKeyInput = view.findViewById<EditText>(R.id.apiKeyInput)
        apiKeyInput.setText(sharedPreferences.getString("API_KEY", ""))
        apiKeyInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val apiKey = s.toString()
                if (apiKey.isNotEmpty()) {
                    sharedPreferences.edit().putString("API_KEY", apiKey).apply()
                } else {
                    sharedPreferences.edit().putString("API_KEY", null).apply()
                }
                // Start the price worker with the new API key
                WorkerUtils.startPriceWorker(requireContext())

            }
        })

        this.addMultipleListeners(view, sharedPreferences)

        val switchNotificationAction = view.findViewById<Switch>(R.id.switchNotificationAction)

        // Load the saved state and set the switch position
        val isTradingViewEnabled = sharedPreferences.getBoolean("NotificationActionTradingView", false)
        switchNotificationAction.isChecked = isTradingViewEnabled

        // Save the state when the switch is toggled
        switchNotificationAction.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("NotificationActionTradingView", isChecked).apply()
        }

        return view
    }

    private fun addMultipleListeners(view: View, sharedPreferences: SharedPreferences) {
        val multipleInput = view.findViewById<EditText>(R.id.multipleInput)
        val button1000 = view.findViewById<Button>(R.id.button1000)
        val button10000 = view.findViewById<Button>(R.id.button10000)
        val button100000 = view.findViewById<Button>(R.id.button100000)
        val multipleDescription = view.findViewById<TextView>(R.id.multipleDescription)

        // Load saved value and set in EditText
        val savedValue = sharedPreferences.getInt("MULTIPLE", 1000)
        multipleInput.setText(savedValue.toString())

        // Update label text dynamically
        multipleDescription.text = "Notify at every crossing of a multiple of $savedValue"

        // Save value when user edits the input field
        multipleInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val inputValue = s.toString().toIntOrNull() ?: 1000 // Default to 100 if input is invalid
                sharedPreferences.edit().putInt("MULTIPLE", inputValue).apply()
                multipleDescription.text = "Notify at every crossing of a multiple of $inputValue"
            }
        })

        // Set value and save when a button is clicked
        val buttonClickListener = View.OnClickListener { button ->
            val value = (button as Button).text.toString().toInt()
            multipleInput.setText(value.toString())
            sharedPreferences.edit().putInt("MULTIPLE", value).apply()
            multipleDescription.text = "Notify at every crossing of a multiple of $value"
        }

        button1000.setOnClickListener(buttonClickListener)
        button10000.setOnClickListener(buttonClickListener)
        button100000.setOnClickListener(buttonClickListener)
    }
}
