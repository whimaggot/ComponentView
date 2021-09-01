package com.netease.componentview.base

import android.content.Context
import android.view.View
import com.netease.componentview.constant.AndroidLifeCycle
import com.netease.componentview.constant.Constant
import com.netease.componentview.constant.ExecuteCondition
import com.netease.componentview.constant.ExecutePeriod
import com.netease.componentview.manager.ComponentViewManager
import com.netease.componentview.utils.NameUtils

abstract class IComponentView<T : View,D:Any> {
    companion object{
        const val  DEFAULT_PRIORITY = -10086
    }

    abstract fun execute(activityWrapper: ActivityWrapper, androidLifeCycle: AndroidLifeCycle, target:T?, data:D?)
    abstract fun period(): ExecutePeriod
    abstract fun periodGap():Int
    abstract fun condition(): ExecuteCondition
    open fun time(): Int{
        return AndroidLifeCycle.RESUME.value
    }
    abstract fun viewId():Int

    open fun finish(activityWrapper: ActivityWrapper, forever: Boolean){
        if(forever){
            when(condition().type){
                ExecuteCondition.TYPE_SHARE_PREFERENCE->{
                    activityWrapper.activity?.getSharedPreferences(Constant.VIEW_RULE_SHARE_PREFERENCE_NAME, Context.MODE_PRIVATE)?.edit()?.putString(
                        NameUtils.getRuleSPKeyName(this as IComponentView<out View, Any>), Constant.VIEW_RULE_OK)?.apply()
                }
            }
        }
        ComponentViewManager.onViewFinish(activityWrapper,this)
    }
    fun restart(activityWrapper: ActivityWrapper){
        when(condition().type){
            ExecuteCondition.TYPE_SHARE_PREFERENCE->{
                activityWrapper.activity?.getSharedPreferences(Constant.VIEW_RULE_SHARE_PREFERENCE_NAME, Context.MODE_PRIVATE)?.edit()?.putString(
                    NameUtils.getRuleSPKeyName(this as IComponentView<out View, Any>),null)?.apply()
            }
        }
    }
    open fun priority():Int{
        return DEFAULT_PRIORITY
    }

    abstract fun release()

    fun updateExecuteTime(context: Context){
        context.getSharedPreferences(Constant.VIEW_RULE_SHARE_PREFERENCE_NAME,Context.MODE_PRIVATE).edit().putLong(
            Constant.KEY_SHARE_PREFERENCE_RULE_TIME+this.javaClass.name,System.currentTimeMillis()).apply()
    }
}