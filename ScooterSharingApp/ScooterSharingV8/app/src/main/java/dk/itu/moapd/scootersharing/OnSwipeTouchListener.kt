package dk.itu.moapd.scootersharing

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs

open class OnSwipeTouchListener constructor(context : Context?) : View.OnTouchListener {

    private val gestureDetector : GestureDetector

    init {
        gestureDetector = GestureDetector(context, GestureListener())
    }

    override fun onTouch(view: View?, event: MotionEvent?): Boolean {
        return gestureDetector.onTouchEvent(event)
    }

    open fun onSwipeRight() {}

    open fun onSwipeLeft() {}

    inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
        private val distanceThreshold = 100
        private val velocityThreshold = 100

        override fun onDown(e: MotionEvent?): Boolean {
            return true
        }

        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent?,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            val distanceX = (e2?.x ?: 0f) - (e1?.x ?: 0f)
            val distanceY = (e2?.y ?: 0f) - (e1?.y ?: 0f)

            if(abs(distanceX) > abs(distanceY) && abs(distanceX) > distanceThreshold && abs(velocityX) > velocityThreshold) {
                if (distanceX > 0)
                    onSwipeRight()
                else
                    onSwipeLeft()
                return true
            }
            return false
        }
    }

}