package com.onurcan.pangolin.helpers

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
import com.onurcan.pangolin.R

import butterknife.BindView
import butterknife.ButterKnife;
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView


class HeaderView : LinearLayout {
    @BindView (R.id.image)
    lateinit var mImage: CircleImageView

    @BindView(R.id.last_seen)
    lateinit var lastSeen: TextView

    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        ButterKnife.bind(this)
    }

    fun bindTo(image: String?, lastSeen: String?) {
        Picasso.get().load(image).into(mImage)
        this.lastSeen.text = lastSeen
    }
}