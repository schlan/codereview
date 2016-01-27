package at.droelf.codereview.ui.view

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.HorizontalScrollView

class HScrollView : HorizontalScrollView {

    var enableScrolling = true

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        if(enableScrolling){
            return super.onInterceptTouchEvent(ev)
        }
        return false
    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        if(enableScrolling){
            return super.onTouchEvent(ev)
        }

        return false
    }
}