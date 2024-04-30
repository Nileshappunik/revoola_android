package com.example.myfirstapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment

import com.example.myfirstapp.R
import com.example.myfirstapp.api.ApiClientRet
import com.example.myfirstapp.databinding.NoInternetConnectionBinding
import com.example.myfirstapp.utils.PrefManager


class FragNoInternet : DialogFragment() {
    val TAG: String = FragNoInternet::class.java.simpleName
    lateinit var noInternetConnectionBinding: NoInternetConnectionBinding
    lateinit var apiClientRetrofit: ApiClientRet
    var callbackResult: CallbackResult? = null
    private var request_code = 205

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        noInternetConnectionBinding = DataBindingUtil.inflate(inflater, R.layout.no_internet_connection, container, false) as NoInternetConnectionBinding
        noInternetConnectionBinding.layoutNotConnect.setOnClickListener(View.OnClickListener { })
        PrefManager.setSomeStringValue(activity, PrefManager.current_fragment,"FragNoInternet" )
        noInternetConnectionBinding.tvRetry.setOnClickListener(View.OnClickListener {
            apiClientRetrofit = ApiClientRet(activity)
            if (apiClientRetrofit.isConnected) {
                sendDataResult()
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

    fun setOnCallbackResult(callbackResult: CallbackResult?) {
        this.callbackResult = callbackResult
    }

    fun setRequestCode(request_code: Int) {
        this.request_code = request_code
    }

    private fun sendDataResult() {
        if (callbackResult != null) {
            dialog!!.cancel()
            callbackResult!!.sendResult(request_code, true)
        }
    }

}
