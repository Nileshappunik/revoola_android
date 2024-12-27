package com.revoola.moengage.inapp

import com.moengage.inapp.listeners.SelfHandledAvailableListener
import com.moengage.inapp.model.SelfHandledCampaignData
import com.revoola.commonobject.RLTools

class RLSelfHandledCallback: SelfHandledAvailableListener {

    override fun onSelfHandledAvailable(data: SelfHandledCampaignData?) {
       RLTools.RlLogEPrint("TAG"," onSelfHandledAvailable() $data" )
    }
}