package com.revoola.moengage.inapp

import android.util.Log
import com.moengage.inapp.listeners.SelfHandledAvailableListener
import com.moengage.inapp.model.SelfHandledCampaignData
import com.revoola.utils.RLTools

class RLSelfHandledCallback: SelfHandledAvailableListener {

    override fun onSelfHandledAvailable(data: SelfHandledCampaignData?) {
       RLTools.RlLogEPrint("TAG"," onSelfHandledAvailable() $data" )
    }
}