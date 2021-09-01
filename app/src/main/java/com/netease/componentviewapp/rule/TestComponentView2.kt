package com.netease.componentviewapp.rule

import android.widget.TextView
import com.netease.componentview.constant.AndroidLifeCycle
import com.netease.componentview.constant.ExecuteCondition
import com.netease.componentview.constant.ExecutePeriod
import com.netease.componentview.base.ActivityWrapper
import com.netease.componentview.base.IComponentView
import com.netease.componentviewapp.MainActivity
import com.netease.componentviewapp.R

open class TestComponentView2 : IComponentView<TextView, MainActivity.Age>() {
    var hasDo = false

    override fun execute(
        context: ActivityWrapper,
        androidLifeCycle: AndroidLifeCycle,
        target: TextView?,
        data: MainActivity.Age?
    ) {
        if(hasDo){
            finish(context,false)
        }
        target?.let {
            it.setText("TestComponentView2")
        }
        hasDo = true
    }

    override fun period(): ExecutePeriod {
        return ExecutePeriod.MINUTE
    }

    override fun periodGap(): Int {
        return 3
    }

    override fun condition(): ExecuteCondition {
        return ExecuteCondition.ExecuteConditionNone()
    }

    override fun viewId(): Int {
        return R.id.tv
    }

    override fun priority(): Int {
        return 100
    }

    override fun release() {
        TODO("Not yet implemented")
    }
}