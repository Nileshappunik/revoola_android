package com.revoola.fragment.guest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.revoola.R
import com.revoola.databinding.RlGuestQuestionOneDialogBinding
import com.revoola.databinding.RlGuestWelcomeDialogBinding
import com.revoola.fragment.start.challenges.adapter.RLCalenderListAdapter


class RLQuestionOneDialog : DialogFragment() {
    private val TAG: String = RLQuestionOneDialog::class.java.simpleName
    private lateinit var fragBinding: RlGuestQuestionOneDialogBinding

    private var selectionQuestion:String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        fragBinding = RlGuestQuestionOneDialogBinding.inflate(inflater, container, false)
        setupUI()
        return fragBinding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    private fun setupUI() {
        fragBinding.ivClose.setOnClickListener {
            dismiss()
        }
        fragBinding.ivBack.setOnClickListener {
            dismiss()
            val welcomeDialog = RLWelcomeDialog()
            welcomeDialog.show(parentFragmentManager, "RLWelcomeDialog")
        }
        fragBinding.tvSkip.setOnClickListener {
            dismiss()
        }

        val questionList= listOf(
            RLQuestionModel(R.drawable.ic_one_dot,"I don't feel as healthy as I would like to"),
            RLQuestionModel(R.drawable.ic_two_dot,"I feel quite healthy but want to do more"),
            RLQuestionModel(R.drawable.ic_three_dot,"I feel super healthy"))

        val linearLayoutMain = LinearLayoutManager(activity)
        fragBinding.radioGroupOptions.layoutManager = linearLayoutMain
        val adapter = RLQuestionListAdapter(requireContext(),questionList) { cardData ->
            // Handle date selection
            println("Selected date: $cardData")
            selectionQuestion=cardData.Description
        }
        fragBinding.radioGroupOptions.adapter=adapter
        fragBinding.tvContinue.setOnClickListener {
            if (selectionQuestion.isNotEmpty()){
                dismiss()
                //test
                val thankyouDialog = RLThankYouDialog()
                thankyouDialog.show(parentFragmentManager, "RLThankYouDialog")
            }
        }
    }

}
