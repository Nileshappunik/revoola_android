package com.revoola.moengage.callbacks

import android.util.Log
import com.moengage.core.listeners.OnLogoutCompleteListener
import com.moengage.core.model.LogoutData
import com.revoola.utils.RLTools


class RLLogoutCompleteListener: OnLogoutCompleteListener {
    override fun logoutComplete(data: LogoutData) {
       RLTools.RlLogEPrint("RLLogoutCompleteListener","logoutComplete() $data")
    }
}