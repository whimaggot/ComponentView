package com.netease.componentview.common

import com.netease.componentview.base.IComponentView

class ComponentViewComparator :Comparator<IComponentView<*, *>>{
    override fun compare(o1: IComponentView<*, *>?, o2: IComponentView<*, *>?): Int {
        if(o1==null){
            return -1
        }
        if(o2==null){
            return 1
        }
        var gap = o1.priority()-o2.priority()
        if(gap==0){
            return 0
        }
        return if (gap>0) -1 else 0
    }
}