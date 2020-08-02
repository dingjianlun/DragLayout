package com.dingjianlun.draglayout.demo

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import com.dingjianlun.draglayout.DragLayout
import kotlinx.android.synthetic.main.refresh_layout_head_view.view.*

class HeadLayout(
    context: Context,
    attrs: AttributeSet? = null
) : DragLayout.HeadLayout(context, attrs) {

    init {
        View.inflate(context, R.layout.refresh_layout_head_view, this)
    }

    override fun onUpdate(state: DragLayout.State) {
        when (state) {
            is DragLayout.State.Dragging -> {
                iv_loading.rotation = 360 * state.proportion
            }
            is DragLayout.State.Loading -> {
                iv_loading.startAnimation(rotationAnim)
            }
            is DragLayout.State.Reset -> {
                iv_loading.clearAnimation()
            }
        }

    }

    private val rotationAnim = RotateAnimation(
        0f, 360f,
        Animation.RELATIVE_TO_SELF, 0.5f,
        Animation.RELATIVE_TO_SELF, 0.5f
    ).apply {
        duration = 200
        repeatMode = Animation.RESTART
        repeatCount = Animation.INFINITE
        interpolator = LinearInterpolator()
    }

    private fun String?.log() = Log.i("RefreshLayout", this.toString())

}