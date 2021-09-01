package com.netease.componentviewapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.netease.componentview.annotion.ComponentView
import com.netease.componentview.annotion.ComponentViewData
import com.netease.componentview.manager.ComponentViewManager
import com.netease.componentviewapp.rule.TestComponentView
import com.netease.componentviewapp.rule.TestComponentView2
import com.netease.componentviewapp.rule.TestComponentView3


@ComponentView(TestComponentView::class,TestComponentView2::class,TestComponentView3::class)
class MainActivity : AppCompatActivity() {

//    @ViewRuleData(TestViewRule::class)
//    var test = "11111"

    @ComponentViewData(TestComponentView::class)
    var age = Age("test")

    @ComponentViewData(TestComponentView2::class)
    var age2 = Age("test2")


    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<TextView>(R.id.tv_activity_first)?.let {
            it.setOnClickListener {
//                startActivity(Intent(this,SecondActivity::class.java))
                supportFragmentManager.beginTransaction().replace(R.id.container, FirstFragment())
                    .commitAllowingStateLoss()
//                ComponentViewManager.callComponentView(this,TestComponentView2::class.java)
            }
        }
        findViewById<TextView>(R.id.tv)?.let {
            it.setOnClickListener {
                ComponentViewManager.callComponentView(this,TestComponentView3::class.java)
            }
        }
    }

    override fun onResume() {
        Log.d("TestViewRule","beforeOnResume")
        super.onResume()
        Log.d("TestViewRule","onResume")
    }

    open class Age(var name:String){

    }
}