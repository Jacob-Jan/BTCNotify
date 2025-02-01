package com.bitcoin.btcnotify

import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.work.WorkManager

class HomeFragment : Fragment() {

    private lateinit var priceText: TextView
    private lateinit var timeText: TextView
    private var isFirstLaunch = true // Ensures worker starts only on first launch
    private var isNetworkAvailable = false // Tracks network status to prevent redundant restarts
    private lateinit var networkMonitor: NetworkMonitor
    private lateinit var syncIcon: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        priceText = view.findViewById(R.id.priceText)
        timeText = view.findViewById(R.id.timeText)
        syncIcon = view.findViewById(R.id.syncIcon)
        syncIcon.setOnClickListener {
            refreshPrice()
        }

        view.findViewById<View>(R.id.settingsButton).setOnClickListener {
            findNavController().navigate(R.id.settingsFragment)
        }

        val sharedPreferences = requireContext().getSharedPreferences("APP_PREFERENCES", Context.MODE_PRIVATE)
        val apiKey = sharedPreferences.getString("API_KEY", "")

        loadLastPriceAndTime()
        observeWorker()

        // Start worker only on first fragment load
        if (isFirstLaunch && !apiKey.isNullOrEmpty()) {
            WorkerUtils.startPriceWorker(requireContext(), apiKey)
            isFirstLaunch = false // Update flag after starting the worker
        }

        // Get reference to the donation button
        val donationButton = view.findViewById<Button>(R.id.donationButton)
        donationButton.setOnClickListener {
            openLightningLink("lnurl:btcnotify@getalby.com")
        }

        return view
    }

    override fun onResume() {
        super.onResume()

        // Initialize and register the NetworkMonitor
        networkMonitor = NetworkMonitor(requireContext()).apply {
            onNetworkAvailable = {
                Log.d("HomeFragment", "Network connected")
                if (!isNetworkAvailable) {
                    isNetworkAvailable = true
                    restartPriceWorker() // Restart the worker only when the network is reconnected
                }
            }
            onNetworkLost = {
                Log.d("HomeFragment", "Network disconnected")
                isNetworkAvailable = false
            }
            register()
        }

        // Reload the price and time whenever the fragment resumes
        loadLastPriceAndTime()
    }

    override fun onPause() {
        super.onPause()

        // Unregister the NetworkMonitor to avoid redundant callbacks
        networkMonitor.unregister()
    }

    private fun loadLastPriceAndTime() {
        val sharedPreferences = requireContext().getSharedPreferences("APP_PREFERENCES", Context.MODE_PRIVATE)
        val lastPrice = sharedPreferences.getInt("LAST_PRICE", 0)
        val lastUpdated = sharedPreferences.getString("LAST_UPDATED", "Never")

        if (lastPrice != 0) {
            priceText.text = "$${lastPrice}"
        } else {
            priceText.text = "--"
        }

        timeText.text = "Last updated: $lastUpdated"
    }

    private fun observeWorker() {
        WorkManager.getInstance(requireContext())
            .getWorkInfosForUniqueWorkLiveData("BitcoinPriceChecker")
            .observe(viewLifecycleOwner) { workInfos ->
                if (!workInfos.isNullOrEmpty()) {
                    loadLastPriceAndTime()
                }
            }
    }

    private fun restartPriceWorker() {
        val sharedPreferences = requireContext().getSharedPreferences("APP_PREFERENCES", Context.MODE_PRIVATE)
        val apiKey = sharedPreferences.getString("API_KEY", "")

        if (!apiKey.isNullOrEmpty()) {
            WorkerUtils.startPriceWorker(requireContext(), apiKey)
        }
    }

    private fun refreshPrice() {
        // Disable the icon and start the rotation animation
        syncIcon.isEnabled = false
        syncIcon.alpha = 0.5f // Dim icon to show activity

        // Rotate animation
        val rotateAnimation = ObjectAnimator.ofFloat(syncIcon, "rotation", 0f, 360f).apply {
            duration = 1000 // Animation duration in milliseconds
        }

        rotateAnimation.start()

        // Restart the worker to fetch the latest data
        val sharedPreferences = requireContext().getSharedPreferences("APP_PREFERENCES", Context.MODE_PRIVATE)
        val apiKey = sharedPreferences.getString("API_KEY", "")

        if (!apiKey.isNullOrEmpty()) {
            WorkerUtils.startPriceWorker(requireContext(), apiKey)

            // Simulate a delay to visually complete the animation and re-enable the icon
            priceText.postDelayed({
                syncIcon.isEnabled = true
                syncIcon.alpha = 1.0f // Restore appearance
                loadLastPriceAndTime() // Reload price and time after worker updates SharedPreferences
            }, 2000)
        } else {
            // Handle the case where API key is missing
            Log.e("HomeFragment", "API Key is missing. Cannot refresh data.")
            syncIcon.isEnabled = true
            syncIcon.alpha = 1.0f
        }
    }

    private fun openLightningLink(link: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
        // Check if there's a Lightning wallet installed to handle LNURL/LN Address
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(intent)
        } else {
            showNoWalletDialog()
        }
    }

    private fun showNoWalletDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("No lightning wallet found")
            .setMessage("You need a lightning wallet which accepts dynamic invoices.")
            .setNegativeButton("Cancel", null)
            .show()
    }
}
