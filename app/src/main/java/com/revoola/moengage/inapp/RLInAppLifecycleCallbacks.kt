package com.revoola.moengage.inapp

import android.util.Log
import com.moengage.inapp.listeners.InAppLifeCycleListener
import com.moengage.inapp.model.InAppData
import com.revoola.utils.RLTools

class RLInAppLifecycleCallbacks: InAppLifeCycleListener {

    override fun onDismiss(inAppData: InAppData) {
       RLTools.RlLogEPrint("TAG"," onDismiss() Data: $inAppData"  )
    }

    override fun onShown(inAppData: InAppData) {
       RLTools.RlLogEPrint("TAG"," onShown() Data: $inAppData" )
    }
}