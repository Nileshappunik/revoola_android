package com.revoola.moengage.inapp

import android.util.Log
import com.moengage.inapp.listeners.InAppLifeCycleListener
import com.moengage.inapp.model.InAppData

class RLInAppLifecycleCallbacks: InAppLifeCycleListener {

    override fun onDismiss(inAppData: InAppData) {
        Log.e("TAG"," onDismiss() Data: $inAppData"  )
    }

    override fun onShown(inAppData: InAppData) {
        Log.e("TAG"," onShown() Data: $inAppData" )
    }
}