package com.revoola.moengage.inapp

import android.util.Log
import com.moengage.inapp.listeners.SelfHandledAvailableListener
import com.moengage.inapp.model.SelfHandledCampaignData

class RLSelfHandledCallback: SelfHandledAvailableListener {

    override fun onSelfHandledAvailable(data: SelfHandledCampaignData?) {
        Log.e("TAG"," onSelfHandledAvailable() $data" )
    }
}