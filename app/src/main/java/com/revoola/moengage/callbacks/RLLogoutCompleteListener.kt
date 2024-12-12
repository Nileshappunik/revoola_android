package com.revoola.moengage.callbacks

import android.util.Log
import com.moengage.core.listeners.OnLogoutCompleteListener
import com.moengage.core.model.LogoutData



class RLLogoutCompleteListener: OnLogoutCompleteListener {
    override fun logoutComplete(data: LogoutData) {
        Log.e("RLLogoutCompleteListener","logoutComplete() $data")
    }
}