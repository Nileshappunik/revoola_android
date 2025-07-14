package com.revoola.moengage.callbacks

import com.moengage.core.listeners.OnLogoutCompleteListener
import com.moengage.core.model.LogoutData
import com.revoola.commonobject.RLTools


class RLLogoutCompleteListener: OnLogoutCompleteListener {
    override fun logoutComplete(data: LogoutData) {
       RLTools.rl_logEPrint("MoengageTag","RLLogoutCompleteListener : logoutComplete() $data")
    }
}