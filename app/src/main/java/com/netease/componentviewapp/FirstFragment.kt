package com.netease.componentviewapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.netease.componentview.annotion.ComponentView
import com.netease.componentview.annotion.ComponentViewData
import com.netease.componentview.manager.ComponentViewManager
import com.netease.componentviewapp.rule.FirstFragmentComponentView


@ComponentView(FirstFragmentComponentView::class)
open class FirstFragment: Fragment() {


    @ComponentViewData(FirstFragmentComponentView::class)
    public open var data = "222"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_first,container,false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<TextView>(R.id.tv_fragment_first)?.setOnClickListener {
            ComponentViewManager.callComponentView(this,FirstFragmentComponentView::class.java)
            var secondFragment = ThirdFragment()
            activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.tttttt,secondFragment)?.commitAllowingStateLoss()

        }
    }

    override fun onDestroy() {
        Log.d("Viewfragment","onDestroy")
        super.onDestroy()
        Log.d("Viewfragment","onDestroy1")
    }

}