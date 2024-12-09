package com.revoola.fragment.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment

import com.revoola.R
import com.revoola.api.RLApiClientRet
import com.revoola.databinding.RlNoInternetConnectionBinding


class RLFragNoInternet : DialogFragment() {
    val TAG: String = RLFragNoInternet::class.java.simpleName
    lateinit var noInternetConnectionBinding: RlNoInternetConnectionBinding
    lateinit var RLApiClientRetrofit: RLApiClientRet
    var callbackResult: CallbackResult? = null
    private var request_code = 205

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        noInternetConnectionBinding = DataBindingUtil.inflate(inflater, R.layout.rl_no_internet_connection, container, false) as RlNoInternetConnectionBinding
        noInternetConnectionBinding.layoutNotConnect.setOnClickListener(View.OnClickListener { })
        com.revoola.utils.RLPrefManager.RLsetSomeStringValue(activity, com.revoola.utils.RLPrefManager.current_fragment,"RLFragNoInternet" )
        noInternetConnectionBinding.tvRetry.setOnClickListener(View.OnClickListener {
            RLApiClientRetrofit =
                RLApiClientRet(activity)
            if (RLApiClientRetrofit.RLisConnected()) {
                RLsendDataResult()
            }
        })
        return noInternetConnectionBinding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.setCancelable(false)
            dialog.window!!.setLayout(width, height)
        }
    }

    interface CallbackResult {
        fun sendResult(requestCode: Int, obj: Any?)
    }

    fun RLsetOnCallbackResult(callbackResult: CallbackResult?) {
        this.callbackResult = callbackResult
    }

    fun RLsetRequestCode(request_code: Int) {
        this.request_code = request_code
    }

    private fun RLsendDataResult() {
        if (callbackResult != null) {
            dialog!!.cancel()
            callbackResult!!.sendResult(request_code, true)
        }
    }

}
