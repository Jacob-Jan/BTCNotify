package com.example.bitcoinnotifier

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bitcoin.btcnotify.R

class InfoFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FaqAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_info, container, false)
        recyclerView = view.findViewById(R.id.faqRecyclerView)

        val faqList = listOf(
            // 1. App Purpose
            FaqItem(
                "What is the purpose of this app?",
                "This app alerts you when Bitcoin crosses a price threshold you define. It helps you stay informed without needing to constantly check charts or price feeds. No distractions, no altcoins — just Bitcoin notifications that matter."
            ),

            // 2. Why Bitcoin
            FaqItem(
                "Why only Bitcoin and not altcoins?",
                "Bitcoin is decentralized, battle-tested, and truly scarce. Altcoins often introduce centralization, dilution, or complexity without solving real problems. Bitcoin focuses on doing one thing extremely well — being sound money."
            ),
            FaqItem(
                "Isn't Bitcoin old tech? Aren't altcoins more advanced?",
                "Bitcoin prioritizes security and decentralization over flashy features. Many 'advanced' altcoins sacrifice trustlessness. Bitcoin evolves slowly and carefully — and that’s a feature, not a bug."
            ),
            FaqItem(
                "What if a new coin replaces Bitcoin?",
                "Bitcoin has the strongest brand, the largest network effect, the highest security, and the most decentralized governance. Like gold or TCP/IP, Bitcoin is here to stay — it doesn’t need to be replaced."
            ),

            // 3. Trust & Design Philosophy
            FaqItem(
                "Isn't Bitcoin controlled by whales?",
                "Unlike fiat systems or altcoins, Bitcoin is transparent. Anyone can see the distribution. Large holders have no power to change the rules. In fiat, the real whales are invisible."
            ),
            FaqItem(
                "Why does Bitcoin use Proof of Work instead of Proof of Stake?",
                "Proof of Work turns energy into security. It anchors Bitcoin in the physical world and ensures decentralization through real-world cost. Proof of Stake rewards the rich and leads to centralized control."
            ),
            FaqItem(
                "Can governments ban Bitcoin?",
                "They can try, but Bitcoin is decentralized and resistant to censorship. Anyone with an internet connection can access it. Like the internet, it routes around obstacles. Nations that embrace it will benefit."
            ),
            FaqItem(
                "Why not just use stablecoins or CBDCs?",
                "Stablecoins are backed by banks and trust. CBDCs are surveillance tools. Bitcoin is neutral, permissionless, and outside the control of any government or corporation. It’s freedom money."
            ),

            // 4. Real-World Impact
            FaqItem(
                "How does Bitcoin fight corruption?",
                "Bitcoin removes the power to print money from corrupt governments and central banks. It enforces monetary rules with code, not political will, and gives individuals sovereignty over their wealth."
            ),
            FaqItem(
                "Can Bitcoin help end wars?",
                "Wars are often funded through unchecked money printing. Bitcoin introduces financial discipline by making money scarce and transparent. When you can’t inflate endlessly, you can’t fund endless war."
            ),
            FaqItem(
                "What about poverty?",
                "Inflation erodes the value of savings and hits the poor hardest. Bitcoin empowers people to store value in a currency that cannot be debased, giving them long-term financial stability and independence."
            ),

            // 5. FUD Busting
            FaqItem(
                "Isn't Bitcoin bad for the environment?",
                "Bitcoin mining uses energy to secure the network, but it increasingly uses renewable, stranded, or wasted energy. It’s also incentivizing innovations in clean energy and grid balancing that benefit everyone."
            ),
            FaqItem(
                "Doesn't Bitcoin encourage crime?",
                "Bitcoin is pseudonymous and every transaction is publicly recorded. It’s far less private than cash. Criminals use every form of money — but Bitcoin is the most transparent."
            ),
            FaqItem(
                "Isn't Bitcoin too volatile to be useful?",
                "Volatility is part of early adoption. Over time, as more people hold and use Bitcoin, volatility will decrease — just like the internet went from chaos to essential infrastructure."
            ),
            FaqItem(
                "Is Bitcoin only useful for speculation?",
                "Speculation is the on-ramp. In many countries, Bitcoin is used daily for remittances, savings, and surviving inflation. Over time, it becomes less about profit — and more about freedom."
            ),

            // 6. Technical Clarity
            FaqItem(
                "Can Bitcoin scale to support global payments?",
                "Yes — but not all on the base layer. Bitcoin scales in layers, like the internet. The Lightning Network handles instant, low-fee transactions while the base layer ensures secure, final settlement."
            ),
            FaqItem(
                "Isn't Bitcoin too slow or expensive?",
                "The base layer is for final settlement — like gold bars, not credit cards. For fast and cheap payments, Bitcoin has the Lightning Network: instant, scalable, and decentralized."
            ),
            FaqItem(
                "What happens when all 21 million are mined?",
                "Bitcoin will continue through transaction fees. Scarcity gives it value, and value ensures demand. The system is designed to operate long after the last coin is mined — sustainably and predictably."
            )
        )

        adapter = FaqAdapter(faqList)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        return view
    }
}