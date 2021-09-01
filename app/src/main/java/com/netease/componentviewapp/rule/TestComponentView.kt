package com.netease.componentviewapp.rule

import android.util.Log
import android.widget.TextView
import com.netease.componentview.constant.AndroidLifeCycle
import com.netease.componentview.constant.ExecuteCondition
import com.netease.componentview.constant.ExecutePeriod
import com.netease.componentview.base.ActivityWrapper
import com.netease.componentview.base.IComponentView
import com.netease.componentviewapp.MainActivity
import com.netease.componentviewapp.R

class TestComponentView : IComponentView<TextView, MainActivity.Age>() {


    override fun execute(context: ActivityWrapper, androidLifeCycle: AndroidLifeCycle, target: TextView?, data: MainActivity.Age?) {
        Log.d("TestViewRule","execute")
        target?.let {
            target.setText("TestComponentView")
            target.setOnClickListener {
                finish(context,true)
            }
        }
    }

    override fun period(): ExecutePeriod {
        return ExecutePeriod.NONE
    }

    override fun periodGap(): Int {
        return 0
    }

    override fun condition(): ExecuteCondition {
        return ExecuteCondition.ExecuteConditionSP("TestComponentView")
    }

    override fun viewId(): Int {
        return R.id.tv
    }

    override fun priority(): Int {
        return 1000
    }

    override fun release() {
        TODO("Not yet implemented")
    }

}