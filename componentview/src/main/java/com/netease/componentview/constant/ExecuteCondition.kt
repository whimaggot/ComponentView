package com.netease.componentview.constant

class ExecuteCondition(type:Int,key:String){
    var key: String = key
        private set

    var type: Int = type
        private set

    companion object{
        var TYPE_NONE = 0;
        var TYPE_SHARE_PREFERENCE = 1;

        fun ExecuteConditionNone(): ExecuteCondition {
            return ExecuteCondition(TYPE_NONE,"")
        }
        fun ExecuteConditionSP(key:String): ExecuteCondition {
            return ExecuteCondition(TYPE_SHARE_PREFERENCE,key)
        }
    }
}