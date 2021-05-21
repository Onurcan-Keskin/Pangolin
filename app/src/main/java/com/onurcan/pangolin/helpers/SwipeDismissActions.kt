package com.onurcan.pangolin.helpers

import android.app.Dialog
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.FragmentActivity
import com.google.android.material.behavior.SwipeDismissBehavior
import com.google.android.material.card.MaterialCardView
import com.onurcan.exovideoreference.utils.showLogInfo

object SwipeDismissActions {

    fun dialogDismiss(view:View, dialog: Dialog){
        val swipe = SwipeDismissBehavior<MaterialCardView>()
        swipe.setSwipeDirection(SwipeDismissBehavior.SWIPE_DIRECTION_START_TO_END)

        swipe.listener = object : SwipeDismissBehavior.OnDismissListener{
            override fun onDismiss(view: View?) {
                dialog.dismiss()
            }

            override fun onDragStateChanged(state: Int) {
                showLogInfo(this.javaClass.simpleName,"Drag State $state ")
            }
        }
        val params = view.layoutParams as CoordinatorLayout.LayoutParams
        params.behavior = swipe
    }


    /**
     * @param view dismissible item
     * @param context parent activity
     */
    fun fragmentDismiss(view: View,context:FragmentActivity){
        val swipe = SwipeDismissBehavior<MaterialCardView>()
        swipe.setSwipeDirection(SwipeDismissBehavior.SWIPE_DIRECTION_START_TO_END)

        swipe.listener = object : SwipeDismissBehavior.OnDismissListener{
            override fun onDismiss(view: View?) {
                FragmentHelper.popBackStack(context)
            }

            override fun onDragStateChanged(state: Int) {
                showLogInfo(this.javaClass.simpleName,"Drag State $state ")
            }
        }
        val params = view.layoutParams as CoordinatorLayout.LayoutParams
        params.behavior = swipe
    }
}