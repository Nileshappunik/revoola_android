package com.revoola.moengage.inapp

import android.util.Log
import com.moengage.inapp.listeners.OnClickActionListener
import com.moengage.inapp.model.ClickData
import com.revoola.utils.RLTools


class RLClickActionCallback: OnClickActionListener {

    override fun onClick(clickData: ClickData): Boolean {
       RLTools.RlLogEPrint("TAG"," onClick() $clickData" )
        // return true if the application is handling else false
        return false
    }
}