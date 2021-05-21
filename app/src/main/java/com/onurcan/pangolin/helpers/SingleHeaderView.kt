package com.onurcan.pangolin.helpers

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.onurcan.pangolin.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class SingleHeaderView : LinearLayout {
    @BindView(R.id.s_image)
    lateinit var mImage: CircleImageView

    @BindView(R.id.s_last_seen)
    lateinit var lastSeen: TextView

    @BindView(R.id.s_call)
    lateinit var call: ImageButton

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

    // TODO Bind Phone Number for call
    fun bindTo(image: String?, lastSeen: String?) {
        Picasso.get().load(image).into(mImage)
        this.lastSeen.text = lastSeen
    }
}