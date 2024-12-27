package com.revoola.moengage.callbacks

import android.content.Context
import com.moengage.core.listeners.AppBackgroundListener
import com.moengage.core.model.AppBackgroundData
import com.revoola.commonobject.RLTools


class RLApplicationBackgroundListener: AppBackgroundListener {
    override fun onAppBackground(context: Context, data: AppBackgroundData) {
       RLTools.RlLogEPrint("RLApplicationBackgroundListener","onAppBackground() $data")
    }
}