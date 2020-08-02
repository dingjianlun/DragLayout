package com.dingjianlun.draglayout.demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.dingjianlun.recyclerview.SimpleAdapter
import com.dingjianlun.recyclerview.linearLayout
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.list_item_1.view.*

class MainActivity : AppCompatActivity() {

    private val adapter = SimpleAdapter<String>()
            .setItem(R.layout.list_item_1) { item ->
                tv_text.text = item
                setOnClickListener {
                    Toast.makeText(context, item, Toast.LENGTH_SHORT).show()
                }
            }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView.linearLayout().adapter = adapter


        val list: List<String> = (0..100).map { "item：$it" }
        adapter.setData(list)


        refreshLayout.onRefreshListener = {
            cb_refreshing.isChecked = it
            if (it) {
                refreshLayout.postDelayed({
                    refreshLayout.refreshing = false
                }, 2000)
            }
        }

        cb_refreshing.setOnCheckedChangeListener { buttonView, isChecked ->
            refreshLayout.refreshing = isChecked
        }

    }
}