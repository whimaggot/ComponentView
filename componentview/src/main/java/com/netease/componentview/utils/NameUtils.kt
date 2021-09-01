package com.netease.componentview.utils

import android.view.View
import com.netease.componentview.base.IComponentView

object NameUtils {


    fun getRuleSPKeyName(componentView: IComponentView<out View, Any>):String{
        return componentView.javaClass.name+"_"+componentView.condition().key
    }
}