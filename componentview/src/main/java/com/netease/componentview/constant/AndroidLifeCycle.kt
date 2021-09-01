package com.netease.componentview.constant

enum class AndroidLifeCycle(val value:Int) {
    UNDEFINE(1 shl 0),
    START(1 shl 1),
    CREATE(1 shl 2),
    RESUME(1 shl 3),
    PAUSE(1 shl 4),
    STOP(1 shl 5),
    DESTROY(1 shl 6),
    SAVE_INSTANT_STATE(1 shl 7),
    VIEW_CREATED(1 shl 8)
}