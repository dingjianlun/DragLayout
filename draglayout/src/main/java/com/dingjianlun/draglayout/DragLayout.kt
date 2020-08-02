package com.dingjianlun.draglayout

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewPropertyAnimator
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import androidx.core.view.*

class DragLayout(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(
    context,
    attrs
), NestedScrollingParent3 {

    var onRefreshListener: (isRefresh: Boolean) -> Unit = {}

    var refreshing = false
        set(value) {
            val target = this.target ?: return
            target
                .animate()
                .translationY(if (value) dragDistance else 0f)
                .setDuration(200)
                .setInterpolator(DecelerateInterpolator())
                .setUpdateListener {
                    this.headLayout?.let {
                        it.translationY = -it.height + target.translationY
                    }
                }
                .setAnimatorListener {
                    field = value
                    onRefreshListener.invoke(value)
                    this.headLayout?.onUpdate(if (value) State.Loading() else State.Reset())
                }
                .start()
        }

    private var dragDistance = 0f
    private var damping = 0.6f

    private var target: View? = null
    private var headLayout: HeadLayout? = null

    override fun getNestedScrollAxes(): Int {
        return ViewCompat.SCROLL_AXIS_VERTICAL
    }

    override fun onStartNestedScroll(child: View, target: View, axes: Int, type: Int): Boolean {
        return type == ViewCompat.TYPE_TOUCH
                && !refreshing
                && nestedScrollAxes and ViewCompat.SCROLL_AXIS_VERTICAL != 0
    }

    override fun onNestedScrollAccepted(child: View, target: View, axes: Int, type: Int) {
        if (type != ViewCompat.TYPE_TOUCH) return
        this.target = target
        target.overScrollMode = OVER_SCROLL_NEVER
        this.headLayout = kotlin.run {
            (0 until childCount).forEach {
                val view = getChildAt(it)
                if (view is HeadLayout) {
                    this.dragDistance = view.height.toFloat()
                    return@run view
                }
            }
            null
        }
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        if (type != ViewCompat.TYPE_TOUCH) return

        if (!target.canScrollVertically(-1) && (target.translationY > 0 || dy < 0)) {
            target.translationY -= dy * damping
            consumed[1] = dy

            this.headLayout?.let {
                it.translationY = -it.height + target.translationY
            }

            this.headLayout?.onUpdate(State.Dragging(target.translationY / dragDistance))
        }
    }

    override fun onStopNestedScroll(target: View, type: Int) {
        if (type != ViewCompat.TYPE_TOUCH) return
        this.refreshing = target.translationY > dragDistance
    }

    private fun ViewPropertyAnimator.setAnimatorListener(end: () -> Unit) = setListener(
        object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                end.invoke()
            }

            override fun onAnimationCancel(animation: Animator?) {
                end.invoke()
            }
        }
    )

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int
    ) {
    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int,
        consumed: IntArray
    ) {
    }

    sealed class State {

        data class Dragging(val proportion: Float) : State()
        class Loading : State()
        class Reset : State()
    }

    abstract class HeadLayout(
        context: Context,
        attrs: AttributeSet? = null
    ) : FrameLayout(context, attrs) {

        override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
            super.onSizeChanged(w, h, oldw, oldh)
            translationY = -h.toFloat()
        }

        abstract fun onUpdate(state: State)

    }

    private fun String?.log() = Log.i("RefreshLayout", this.toString())
}
