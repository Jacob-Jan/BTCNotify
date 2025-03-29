package com.example.bitcoinnotifier

import android.animation.ValueAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.animation.doOnEnd
import androidx.recyclerview.widget.RecyclerView
import com.bitcoin.btcnotify.R

data class FaqItem(val question: String, val answer: String, var isExpanded: Boolean = false)

class FaqAdapter(private val items: List<FaqItem>) : RecyclerView.Adapter<FaqAdapter.FaqViewHolder>() {

    inner class FaqViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val questionText: TextView = view.findViewById(R.id.faqQuestion)
        val answerText: TextView = view.findViewById(R.id.faqAnswer)
        val chevron: ImageView = view.findViewById(R.id.chevronIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FaqViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_faq, parent, false)
        return FaqViewHolder(view)
    }

    override fun onBindViewHolder(holder: FaqViewHolder, position: Int) {
        val item = items[position]
        holder.questionText.text = item.question
        holder.answerText.text = item.answer

        // Set initial visibility (needed on recycle)
        holder.answerText.visibility = if (item.isExpanded) View.VISIBLE else View.GONE
        holder.chevron.rotation = if (item.isExpanded) 180f else 0f

        holder.itemView.setOnClickListener {
            item.isExpanded = !item.isExpanded

            // Animate chevron
            val targetRotation = if (item.isExpanded) 180f else 0f
            holder.chevron.animate().rotation(targetRotation).setDuration(200).start()

            // Animate expansion/collapse
            if (item.isExpanded) {
                expand(holder.answerText)
            } else {
                collapse(holder.answerText)
            }
        }
    }

    private fun expand(view: View) {
        // Prevent jump on initial state
        view.visibility = View.INVISIBLE

        // Measure full expanded height
        view.measure(
            View.MeasureSpec.makeMeasureSpec((view.parent as View).width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.UNSPECIFIED
        )
        val targetHeight = view.measuredHeight

        // Collapse to height 0 before expanding
        view.layoutParams.height = 0
        view.visibility = View.VISIBLE

        val animator = ValueAnimator.ofInt(0, targetHeight)
        animator.addUpdateListener {
            view.layoutParams.height = it.animatedValue as Int
            view.requestLayout()
        }

        // After animation, reset height to WRAP_CONTENT for proper layout
        animator.doOnEnd {
            view.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            view.requestLayout()
        }

        animator.duration = 200
        animator.start()
    }

    private fun collapse(view: View) {
        val initialHeight = view.measuredHeight

        val animator = ValueAnimator.ofInt(initialHeight, 0)
        animator.addUpdateListener {
            view.layoutParams.height = it.animatedValue as Int
            view.requestLayout()
        }

        animator.duration = 200
        animator.start()

        animator.doOnEnd {
            view.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = items.size
}
