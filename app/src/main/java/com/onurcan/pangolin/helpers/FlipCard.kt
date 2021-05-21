
package com.onurcan.pangolin.helpers

import android.animation.AnimatorSet
import android.view.View

object FlipCard {

    fun flipFrontAnimator(
        frontAnim: AnimatorSet,
        frontView: View,
        backAnim: AnimatorSet,
        backView: View
    ) {
        frontAnim.setTarget(frontView)
        backAnim.setTarget(backAnim)
        frontAnim.start()
        backAnim.start()
        frontView.visibility = View.GONE
        backView.visibility = View.VISIBLE
    }

    fun flipBackAnimator(
        frontAnim: AnimatorSet,
        frontView: View,
        backAnim: AnimatorSet,
        backView: View
    ) {
        frontAnim.setTarget(backView)
        backAnim.setTarget(frontView)
        frontAnim.start()
        backAnim.start()
        frontView.visibility = View.VISIBLE
        backView.visibility = View.GONE
    }
}