package com.revoola.moengage.inapp

import com.moengage.inapp.listeners.InAppLifeCycleListener
import com.moengage.inapp.model.InAppData
import com.revoola.commonobject.RLTools

class RLInAppLifecycleCallbacks: InAppLifeCycleListener {

    override fun onDismiss(inAppData: InAppData) {
       RLTools.rl_logEPrint("MoengageTag"," onDismiss() Data: $inAppData"  )
    }

    override fun onShown(inAppData: InAppData) {
       RLTools.rl_logEPrint("MoengageTag"," onShown() Data: $inAppData" )
    }
}