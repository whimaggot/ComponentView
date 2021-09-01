package com.netease.componentviewapp

import android.os.Bundle
import android.view.View
import android.widget.TextView

class ThirdFragment :FirstFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<TextView>(R.id.tv_fragment_first)?.setOnClickListener {
        }
    }
}