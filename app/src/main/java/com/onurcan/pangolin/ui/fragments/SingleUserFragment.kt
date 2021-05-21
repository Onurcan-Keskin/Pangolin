package com.onurcan.pangolin.ui.fragments

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.setFragmentResultListener
import com.google.android.material.appbar.AppBarLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.onurcan.pangolin.ui.mvp.BaseFragment
import com.onurcan.exovideoreference.utils.showLogError
import com.onurcan.exovideoreference.utils.showLogInfo
import com.onurcan.exovideoreference.utils.showToast
import com.onurcan.pangolin.R
import com.onurcan.pangolin.appuser.AppUser
import com.onurcan.pangolin.databinding.FragmentSingleUserBinding
import com.onurcan.pangolin.helpers.FragmentHelper
import com.onurcan.pangolin.helpers.SwipeDismissActions
import com.onurcan.pangolin.helpers.ViewUtils
import com.onurcan.pangolin.models.FirebaseDBObject
import com.onurcan.pangolin.ui.adapters.SingleUserPagerAdapter
import com.onurcan.pangolin.ui.contracts.ISingleUser
import com.onurcan.pangolin.ui.presenters.SingleUserPresenter
import com.onurcan.pangolin.utils.NumberConvertor
import com.onurcan.pangolin.utils.expandView
import com.squareup.picasso.Picasso
import kotlin.math.abs

class SingleUserFragment : BaseFragment(R.layout.fragment_single_user),
    AppBarLayout.OnOffsetChangedListener, ISingleUser.ViewSingleUser {

    private lateinit var binding: FragmentSingleUserBinding
    private lateinit var context: FragmentActivity

    private var isHideToolbarView = false

    private var passedUserID: String? = ""
    val isEven = { i: Int -> i % 2 == 0 }

    private val presenter: SingleUserPresenter by lazy { SingleUserPresenter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = requireActivity()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSingleUserBinding.bind(view)

        presenter.onViewsCreated()

        setFragmentResultListener("requestKey") { _, bundle ->
            passedUserID = bundle.getString("bundle")
            presenter.onDataPassed(passedUserID!!)
        }

        this.view?.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(p0: View?, p1: Int, p2: KeyEvent?): Boolean {
                if (p1 == KeyEvent.KEYCODE_BACK) {
                    FragmentHelper.popBackStack(context)
                    return true
                }
                return false
            }
        })

        binding.singleUserBannerInc.singleUserSendFriendBtn.setOnClickListener {
            presenter.friendMap(AppUser.getUserId(), passedUserID!!)
            binding.singleUserBannerInc.singleUserSendFriendBtn.expandView()
            binding.singleUserBannerInc.singleUserRevokeBtn.expandView()
        }

        FirebaseDBObject.getFriends(passedUserID!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.hasChildren()) {
                        /* Request Accepted */
                        requestAccepted()
                    } else {
                        /* Not Friends Yet */
                        notFriendsYet()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    showLogError(this.javaClass.simpleName, error.message)
                }
            })

        /* Request Sent */
        FirebaseDBObject.getReceivedFriendRequests(passedUserID!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.hasChildren()) {
                        requestSent()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    showLogError(this.javaClass.simpleName, error.message)
                }
            })
    }

    fun notFriendsYet() {
        binding.singleUserBannerInc.singleUserSendFriendBtn.visibility = View.VISIBLE
        binding.singleUserBannerInc.llFollowingSince.visibility = View.GONE
        binding.singleUserBannerInc.singleUserRevokeBtn.visibility = View.GONE
        /* Send Friend Request Notification */
        binding.singleUserBannerInc.singleUserSendFriendBtn.setOnClickListener {
            presenter.friendMap(AppUser.getUserId(), passedUserID!!)
            binding.singleUserBannerInc.singleUserSendFriendBtn.visibility = View.GONE
            binding.singleUserBannerInc.llFollowingSince.visibility = View.GONE
            binding.singleUserBannerInc.singleUserRevokeBtn.visibility = View.VISIBLE
        }
    }

    fun requestSent() {
        binding.singleUserBannerInc.singleUserSendFriendBtn.visibility = View.GONE
        binding.singleUserBannerInc.llFollowingSince.visibility = View.GONE
        binding.singleUserBannerInc.singleUserRevokeBtn.visibility = View.VISIBLE
        /* Revoke Friend Request */
        binding.singleUserBannerInc.singleUserRevokeBtn.setOnClickListener {
            presenter.revokeMap(AppUser.getUserId(),passedUserID!!)
        }
    }

    fun requestAccepted() {
        binding.singleUserBannerInc.singleUserSendFriendBtn.visibility = View.GONE
        binding.singleUserBannerInc.llFollowingSince.visibility = View.VISIBLE
        binding.singleUserBannerInc.singleUserRevokeBtn.visibility = View.GONE
    }

    override fun initUI() {
        binding.toolbarInc.backArrow.setOnClickListener {
            FragmentHelper.popBackStack(context)
        }
        binding.appBarLayout.post {
            val height = ViewUtils.getScreenWidth(context) * 1 / 2
            presenter.setupOffset(height)
        }

        binding.singleUserBannerInc.singleUserImage.layoutParams.height =
            ViewUtils.getScreenWidth(context)

        binding.appBarLayout.addOnOffsetChangedListener(this)

        //SwipeDismissActions.fragmentDismiss(binding.fragSingleMaterialRoot,context)

        val adapterL = SingleUserPagerAdapter(context.supportFragmentManager)
        adapterL.addFragment(MediaFragment(), context.getString(R.string.media))
        adapterL.addFragment(DocsFragment(), context.getString(R.string.docs))
        adapterL.addFragment(LinksFragment(), context.getString(R.string.links))

        binding.singleUserPager.adapter = adapterL
        binding.singleUserBar.setupWithViewPager2(binding.singleUserPager)
    }

    override fun populateViews(string: String) {
        FirebaseDBObject.getUserInfo(string).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val image = snapshot.child("photoUrl").value.toString()
                val name = snapshot.child("name").value.toString()

                Picasso.get().load(image)
                    .into(binding.singleUserBannerInc.singleUserImage)
                binding.fragSuCToolbar.title = name

                showToast(context, name)

                binding.toolbarInc.toolbarHeaderView.header.bindTo(image, name)
            }

            override fun onCancelled(error: DatabaseError) {
                showLogError(this.javaClass.simpleName, error.toString())
            }
        })

        FirebaseDBObject.getFriends(passedUserID!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.hasChildren()) {
                        for (postSnaps in snapshot.children) {
                            binding.singleUserBannerInc.singleUserFriendsNum.text = getString(
                                R.string.total_friends,
                                NumberConvertor.prettyCount(snapshot.childrenCount)
                            )
                        }
                    } else
                        binding.singleUserBannerInc.singleUserFriendsNum.text = getString(
                            R.string.total_friends,
                            "0"
                        )
                }

                override fun onCancelled(error: DatabaseError) {
                    showLogError(this.javaClass.simpleName, error.message)
                }
            })
    }

    override fun setOffset(offsetPX: Int) {
        val params = binding.appBarLayout.layoutParams as CoordinatorLayout.LayoutParams
        val behavior = params.behavior as AppBarLayout.Behavior
        behavior.onNestedPreScroll(
            binding.root,
            binding.appBarLayout,
            binding.singleUserBannerInc.singleUserImage,
            0,
            offsetPX,
            intArrayOf(0, 0)
        )
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        val maxScroll = binding.appBarLayout.totalScrollRange
        val perc = abs(verticalOffset).toFloat() / maxScroll.toFloat()
        showLogInfo(this.javaClass.simpleName, perc.toString())
        if (perc >= .9f && isHideToolbarView) {
            binding.toolbarInc.root.visibility = View.VISIBLE
            binding.toolbar.background=ContextCompat.getDrawable(context,R.color.darkPurple_Navigation)
            isHideToolbarView = !isHideToolbarView
        } else if (perc < .9f && !isHideToolbarView) {
            binding.toolbarInc.root.visibility = View.GONE
            binding.toolbar.background=ContextCompat.getDrawable(context,R.color.transparent)
            isHideToolbarView = !isHideToolbarView
        }
    }
}