package com.netease.componentview.utils

import java.util.*

object TimeUtils {
    fun isSameDay(time1: Long, time2: Long): Boolean {
        return isSameDay(Date(time1), Date(time2))
    }

    fun isSameDay(date1: Date?, date2: Date?): Boolean {
        val cal1 = Calendar.getInstance()
        val cal2 = Calendar.getInstance()
        cal1.time = date1
        cal2.time = date2
        return cal1[Calendar.YEAR] == cal2[Calendar.YEAR] &&
                cal1[Calendar.DAY_OF_YEAR] == cal2[Calendar.DAY_OF_YEAR]
    }

    fun isSameMonth(time1: Long, time2: Long): Boolean {
        return isSameMonth(Date(time1), Date(time2))
    }

    fun isSameMonth(date1: Date?, date2: Date?): Boolean {
        val cal1 = Calendar.getInstance()
        val cal2 = Calendar.getInstance()
        cal1.time = date1
        cal2.time = date2
        return cal1[Calendar.YEAR] == cal2[Calendar.YEAR] &&
                cal1[Calendar.MONTH] == cal2[Calendar.MONTH]
    }


    fun isSameWeek(time1: Long, time2: Long): Boolean {
        return isSameWeek(Date(time1), Date(time2))
    }

    /**
     * 判断两个日期是否在同一周
     *
     * @param date1
     * @param date2
     * @return
     */
    fun isSameWeek(date1: Date?, date2: Date?): Boolean {
        val cal1 = Calendar.getInstance()
        val cal2 = Calendar.getInstance()
        cal1.time = date1
        cal2.time = date2
        val subYear = cal1[Calendar.YEAR] - cal2[Calendar.YEAR]
        if (0 == subYear) {
            if (cal1[Calendar.WEEK_OF_YEAR] == cal2[Calendar.WEEK_OF_YEAR]) return true
        } else if (1 == subYear && 11 == cal2[Calendar.MONTH]) {
            // 如果12月的最后一周横跨来年第一周的话则最后一周即算做来年的第一周
            if (cal1[Calendar.WEEK_OF_YEAR] == cal2[Calendar.WEEK_OF_YEAR]) return true
        } else if (-1 == subYear && 11 == cal1[Calendar.MONTH]) {
            if (cal1[Calendar.WEEK_OF_YEAR] == cal2[Calendar.WEEK_OF_YEAR]) return true
        }
        return false
    }
}