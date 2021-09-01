package com.netease.componentview.manager

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.netease.componentview.utils.ReflectUtils
import com.netease.componentview.annotion.ComponentView
import com.netease.componentview.annotion.ComponentViewData
import com.netease.componentview.common.ComponentViewComparator
import com.netease.componentview.constant.AndroidLifeCycle
import com.netease.componentview.constant.Constant
import com.netease.componentview.constant.ExecuteCondition
import com.netease.componentview.constant.ExecutePeriod
import com.netease.componentview.base.ActivityWrapper
import com.netease.componentview.base.IComponentView
import com.netease.componentview.utils.NameUtils
import com.netease.componentview.utils.TimeUtils
import java.util.*
import kotlin.reflect.full.createInstance

object ComponentViewManager {
    private var ruleMap = mutableMapOf<String,MutableList<IComponentView<*, *>>>()
    private var priorityMap = mutableMapOf<String,Int>()
    private var comparator = ComponentViewComparator()

    private var activityCallBack = object :Application.ActivityLifecycleCallbacks{
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            initViewRule(activity)
            (activity as? FragmentActivity)?.supportFragmentManager?.registerFragmentLifecycleCallbacks(
                fragmentCallBack,true)
            onActivityStateChange(activity,AndroidLifeCycle.CREATE)
        }

        override fun onActivityStarted(activity: Activity) {
            onActivityStateChange(activity,AndroidLifeCycle.START)
        }

        override fun onActivityResumed(activity: Activity) {
            onActivityStateChange(activity,AndroidLifeCycle.RESUME)
        }

        override fun onActivityPaused(activity: Activity) {
            onActivityStateChange(activity,AndroidLifeCycle.PAUSE)

        }

        override fun onActivityStopped(activity: Activity) {
            onActivityStateChange(activity,AndroidLifeCycle.STOP)

        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
            onActivityStateChange(activity,AndroidLifeCycle.SAVE_INSTANT_STATE)

        }

        override fun onActivityDestroyed(activity: Activity) {
            releaseViewRule(activity)
//            (activity as? FragmentActivity)?.supportFragmentManager?.unregisterFragmentLifecycleCallbacks(fragmentCallBack)
            onActivityStateChange(activity,AndroidLifeCycle.DESTROY)
        }

    }

    private fun initViewRule(any: Any) {
        var aList = ruleMap.get(any.javaClass.name)
        if(aList==null){
            aList = mutableListOf()
            ruleMap.put(any.javaClass.name,aList)
            var activityVR = any.javaClass.getAnnotation(ComponentView::class.java)
            var priority = IComponentView.DEFAULT_PRIORITY
            var context:Context? = if(any is Activity) any else if( any is Fragment) any.activity else null
            context?.run {
                activityVR?.let {
                    activityVR.view.let {
                        for (rule in it){
                            var ruleInstant = rule.createInstance();
                            aList.add(ruleInstant)
                            var ret = checkViewRule(ruleInstant,context,AndroidLifeCycle.UNDEFINE,true,false,false)
                            if(priority<ruleInstant.priority() && ret){
                                priority = ruleInstant.priority()
                            }
                        }
                    }
                }
            }
            Collections.sort(aList, comparator)
            priorityMap.put(any.javaClass.name,priority)
        }
    }

    private fun releaseViewRule(any: Any){
        var aList = ruleMap.get(any.javaClass.name)
        aList?.let {
            for (view in it){
                view.let {
                    view.release()
                }
            }
            aList.clear()
        }
        ruleMap.remove(any.javaClass.name)
    }

    private fun getViewRuleData(obj:Any, componentView: IComponentView<*, *>):Any?{
        val declaredFields = obj.javaClass.declaredFields
        for (field in declaredFields) {
            field.setAccessible(true)
            if (field.isAnnotationPresent(ComponentViewData::class.java)) {
                val annotation = field.getAnnotation(ComponentViewData::class.java)
                annotation?.let {
                    if(TextUtils.equals(componentView.javaClass.name,annotation.value.java.name)){
                        return ReflectUtils.getData(field.name,obj)
                    }
                }
            }
        }
        return null
    }

    private var fragmentCallBack = object : FragmentManager.FragmentLifecycleCallbacks() {
        override fun onFragmentCreated(fm: FragmentManager, f: Fragment, savedInstanceState: Bundle?) {
            super.onFragmentCreated(fm, f, savedInstanceState)
            initViewRule(f)
            onFragmentStateChange(f,AndroidLifeCycle.CREATE)
        }

        override fun onFragmentPaused(fm: FragmentManager, f: Fragment) {
            super.onFragmentPaused(fm, f)
            onFragmentStateChange(f,AndroidLifeCycle.PAUSE)
        }

        override fun onFragmentStopped(fm: FragmentManager, f: Fragment) {
            super.onFragmentStopped(fm, f)
            onFragmentStateChange(f,AndroidLifeCycle.STOP)
        }

        override fun onFragmentStarted(fm: FragmentManager, f: Fragment) {
            super.onFragmentStarted(fm, f)
            onFragmentStateChange(f,AndroidLifeCycle.START)
        }

        override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
            super.onFragmentResumed(fm, f)
            onFragmentStateChange(f,AndroidLifeCycle.RESUME)
        }

        override fun onFragmentViewCreated(
            fm: FragmentManager,
            f: Fragment,
            v: View,
            savedInstanceState: Bundle?
        ) {
            super.onFragmentViewCreated(fm, f, v, savedInstanceState)
            onFragmentStateChange(f,AndroidLifeCycle.VIEW_CREATED)
        }

        override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
            super.onFragmentDestroyed(fm, f)
            releaseViewRule(f)
            onFragmentStateChange(f,AndroidLifeCycle.DESTROY)
            f.activity?.let {
                if(it.isFinishing){
                    fm.unregisterFragmentLifecycleCallbacks(this)
                }
            }
        }

        override fun onFragmentSaveInstanceState(fm: FragmentManager, f: Fragment, outState: Bundle) {
            super.onFragmentSaveInstanceState(fm, f, outState)
            onFragmentStateChange(f,AndroidLifeCycle.SAVE_INSTANT_STATE)
        }
    }

    fun onViewFinish(activityWrapper: ActivityWrapper, view: IComponentView<*, *>){
        if(activityWrapper.activity==null){
            return
        }
        var activity:Activity = activityWrapper.activity!!
        var fragment:Fragment? = activityWrapper.fragment
        var context = (fragment?:activity as Any)



        var list = getRule(context)
        var nowPriority = view.priority()
        var nextPriority = getRecentEqualPriority(nowPriority,list)
        if(nowPriority==nextPriority){
            return
        }
        nextPriority?.let {
            priorityMap.put(context.javaClass.name,nextPriority)
        }

        getActiveRule(context,true)?.let {
            for (viewRule in it) {
                var ret = checkViewRule(viewRule,activity,AndroidLifeCycle.UNDEFINE,true,false,false) && (viewRule.time() and AndroidLifeCycle.DESTROY.value !=viewRule.time())
                if(ret){
                    var data = getViewRuleData(context,viewRule)
                    var view:View? = null
                    if(context is Activity) {
                        view = context.findViewById(viewRule.viewId())
                    }else if (context is Fragment){
                        view = context.view?.findViewById(viewRule.viewId())
                    }
                    (viewRule as IComponentView<View, Any>).execute(activityWrapper,
                        AndroidLifeCycle.UNDEFINE,view,data)
                    viewRule.updateExecuteTime(activity)
                }
            }
        }
    }


    fun callComponentView(activity: Activity, name:Class<*>?){
        name?.run {
            var list = ruleMap.get(activity.javaClass.name)
            list?.let {
                for (viewRule in it) {
                    if( viewRule.javaClass== name){
                        var ret = checkViewRule(viewRule,activity,AndroidLifeCycle.UNDEFINE,true,false,false)
                        if(ret){
                            var data = getViewRuleData(activity,viewRule)
                            (viewRule as IComponentView<View, Any>).execute(
                                getActivityWrapper(activity),
                                AndroidLifeCycle.UNDEFINE,activity.findViewById(viewRule.viewId()),data)
                            viewRule.updateExecuteTime(activity)
                        }
                    }
                }
            }
        }

    }

    private fun getActivityWrapper(any: Any): ActivityWrapper {
        if(any is Activity){
            return ActivityWrapper(any,null)
        }else if(any is Fragment){
            return ActivityWrapper(any.activity,any)
        }
        return ActivityWrapper(null,null)
    }



    fun callComponentView(fragment: Fragment, name:Class<*>?){
        name?.let {
            var list = ruleMap.get(fragment.javaClass.name)
            list?.let {
                for (viewRule in it) {
                    var ret = checkViewRule(viewRule,fragment.activity as Context,AndroidLifeCycle.UNDEFINE,true,false,false)
                    if(ret){
                        var data = getViewRuleData(fragment,viewRule)
                        (viewRule as IComponentView<View, Any>).execute(
                            getActivityWrapper(fragment),
                            AndroidLifeCycle.UNDEFINE,fragment.view?.findViewById(viewRule.viewId()),data)
                        fragment.activity?.let {
                            viewRule.updateExecuteTime(it)
                        }
                    }
                }
            }
        }

    }


    fun register(application: Application?){
        application?.let {
            application.registerActivityLifecycleCallbacks(activityCallBack)
        }
    }
    fun unregister(application: Application?){
        application?.let {
            application.unregisterActivityLifecycleCallbacks(activityCallBack)
        }
    }
    private fun getActiveRule(any: Any,step:Boolean): MutableList<IComponentView<*, *>>? {
        var priority = priorityMap.get(any.javaClass.name)
        var list = ruleMap.get(any.javaClass.name)
        if(list==null || list.size==0){
            return list
        }
        if(priority== IComponentView.DEFAULT_PRIORITY){
            return list
        }
        var ret = mutableListOf<IComponentView<*, *>>()
        for (view in list){
            view?.let {
                if(view.priority()==priority){
                    ret.add(view)
                }
                if(!step and (view.priority()==IComponentView.DEFAULT_PRIORITY)){
                    ret.add(view)
                }
            }
        }
        return ret
    }

    private fun getRule(any: Any): MutableList<IComponentView<*, *>>?{
        return ruleMap.get(any.javaClass.name)
    }

    private fun getRecentEqualPriority(now:Int?,list:MutableList<IComponentView<*, *>>?):Int?{
        if(now==null){
            return now
        }
        if(list==null){
            return now
        }
        for(view in list){
            view?.let {
                if(view.priority()<now){
                    return view.priority()
                }
            }
        }
        return now
    }


    private fun onFragmentStateChange(fragment: Fragment,androidTime: AndroidLifeCycle) {
        getActiveRule(fragment,false)?.let {
            for (viewRule in it) {
                var ret = checkViewRule(viewRule,fragment.activity as Context,androidTime,false,false,false)
                if(ret){
                    var data = getViewRuleData(fragment,viewRule)
                    (viewRule as IComponentView<View, Any>).execute(getActivityWrapper(fragment),androidTime,fragment.view?.findViewById(viewRule.viewId()),data)
                    fragment.activity?.let {
                        viewRule.updateExecuteTime(it)
                    }
                }
            }
        }
    }

    private fun onActivityStateChange(activity: Activity, androidTime: AndroidLifeCycle){
        getActiveRule(activity,false)?.let {
            for (viewRule in it) {
                var ret = checkViewRule(viewRule,activity,androidTime,false,false,false)
                if(ret){
                    var data = getViewRuleData(activity,viewRule)
                    (viewRule as IComponentView<View, Any>).execute(getActivityWrapper(activity),androidTime,activity.findViewById(viewRule.viewId()),data)
                    viewRule.updateExecuteTime(activity)
                }
            }
        }

    }
    private fun checkTime(componentView: IComponentView<*, *>, context: Context, androidTime: AndroidLifeCycle):Boolean{
        var ret = componentView.time() and androidTime.value  == androidTime.value
        return ret;
    }

    private fun checkCondition(componentView: IComponentView<*, *>, context: Context):Boolean {
        var ret = true
        when(componentView.condition().type){
            ExecuteCondition.TYPE_NONE->{

            }
            ExecuteCondition.TYPE_SHARE_PREFERENCE->{
                var c = context?.getSharedPreferences(Constant.VIEW_RULE_SHARE_PREFERENCE_NAME,Context.MODE_PRIVATE)?.getString(
                    NameUtils.getRuleSPKeyName(componentView as IComponentView<out View, Any>),null)
                if(!TextUtils.isEmpty(c)){
                    ret = false
                }
            }
        }
        return ret
    }

    private fun checkPeriod(componentView: IComponentView<*, *>, context: Context):Boolean{
        var ret = true
        var lastTime = context.getSharedPreferences(Constant.VIEW_RULE_SHARE_PREFERENCE_NAME,Context.MODE_PRIVATE).getLong(
            Constant.KEY_SHARE_PREFERENCE_RULE_TIME+componentView.javaClass.name,0)
        var timeGap = System.currentTimeMillis()-lastTime
        when(componentView.period()){
            ExecutePeriod.NONE->{

            }
            ExecutePeriod.ONCE->{
                if(lastTime>0){
                    ret = false
                }
            }
            ExecutePeriod.SECOND->{
                if(timeGap/1000<componentView.periodGap()){
                    ret = false
                }
            }
            ExecutePeriod.MINUTE->{
                if(timeGap/(1000*60)<componentView.periodGap()){
                    ret = false
                }
            }
            ExecutePeriod.HOUR->{
                if(timeGap/(1000*60*60)<componentView.periodGap()){
                    ret = false
                }
            }
            ExecutePeriod.DAY->{
                if(TimeUtils.isSameDay(System.currentTimeMillis(),lastTime)){
                    ret = false
                }
            }
            ExecutePeriod.WEEK->{
                if(TimeUtils.isSameWeek(System.currentTimeMillis(),lastTime)){
                    ret = false
                }
            }
            ExecutePeriod.MONTH->{
                if(TimeUtils.isSameMonth(System.currentTimeMillis(),lastTime)){
                    ret = false
                }
            }
        }
        return ret
    }

    private fun checkViewRule(componentView: IComponentView<*, *>, context: Context, androidTime: AndroidLifeCycle, ignoreTime:Boolean, ignoreCondition:Boolean, ignorePeriod:Boolean):Boolean{
        var ret = if (ignoreTime) true else checkTime(componentView,context,androidTime)

        //checkCondition
        if(ret){
            ret = if(ignoreCondition) true else checkCondition(componentView,context)
        }

        //checkPeriod
        if(ret){
            ret = if(ignorePeriod) true else checkPeriod(componentView,context)
        }
        return ret
    }
}