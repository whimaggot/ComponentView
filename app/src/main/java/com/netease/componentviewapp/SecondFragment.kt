package com.netease.componentviewapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.netease.componentview.annotion.ComponentView
import com.netease.componentviewapp.rule.SecondFragmentComponentView

@ComponentView(SecondFragmentComponentView::class)
class SecondFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_second,container,false)
    }
}