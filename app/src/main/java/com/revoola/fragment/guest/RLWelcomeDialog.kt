package com.revoola.fragment.guest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.revoola.R
import com.revoola.databinding.RlGuestWelcomeDialogBinding


class RLWelcomeDialog() : DialogFragment() {
    private val TAG: String = RLWelcomeDialog::class.java.simpleName
    private lateinit var fragBinding: RlGuestWelcomeDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        fragBinding = RlGuestWelcomeDialogBinding.inflate(inflater, container, false)
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
        fragBinding.btnSkip.setOnClickListener {
            dismiss()
        }
        fragBinding.btnGetStarted.setOnClickListener {
            dismiss()
            val questionDialog = RLQuestionOneDialog()
            questionDialog.show(parentFragmentManager, "QuestionOneDialog")
        }
    }

}
