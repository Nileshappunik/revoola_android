package com.revoola.watch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.revoola.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.revoola.databinding.RlWatchConformationDialogBinding


class RLWatchWakeUpDialog(private val onConfirmed: ((String) -> Unit)? = null) : DialogFragment() {
    private val TAG: String = RLWatchWakeUpDialog::class.java.simpleName
    private lateinit var fragBinding: RlWatchConformationDialogBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        fragBinding = RlWatchConformationDialogBinding.inflate(inflater, container, false)
        setupUI()
        return fragBinding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    private fun setupUI() {
        fragBinding.btnOk.setOnClickListener {
            val context = context ?: return@setOnClickListener
            dismiss() // Dismiss the dialog first
            // Invoke callback when OK is clicked
            onConfirmed?.invoke("Watch wake-up confirmed!")

            CoroutineScope(Dispatchers.IO).launch {
                context?.let {
                    if (RLWatchManager().isWatchConnected(it)) {
                        RLWatchManager().openWatchApp(it)
                    }
                }
            }
        }
    }

}
