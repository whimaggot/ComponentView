package com.netease.componentviewapp.rule

import android.util.Log
import android.widget.TextView
import com.netease.componentview.constant.AndroidLifeCycle
import com.netease.componentview.constant.ExecuteCondition
import com.netease.componentview.constant.ExecutePeriod
import com.netease.componentview.base.ActivityWrapper
import com.netease.componentview.base.IComponentView
import com.netease.componentviewapp.R


class FirstFragmentComponentView : IComponentView<TextView, String>() {
    var count = 0;


    override fun period(): ExecutePeriod {
        return ExecutePeriod.NONE
    }

    override fun periodGap(): Int {
        return 0
    }

    override fun condition(): ExecuteCondition {
        return ExecuteCondition.ExecuteConditionNone()
    }
    override fun viewId(): Int {
        return R.id.tv_fragment_first
    }

    override fun execute(context: ActivityWrapper, androidLifeCycle: AndroidLifeCycle, target: TextView?, data:String?) {
        target?.let {
            target.setText(data+"FirstFragmentComponentView")
            count++
        }
        context.let {
            finish(context,true)
        }
    }

    override fun release() {
        Log.d("Viewfragment","release")
    }
}