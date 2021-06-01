package com.one.russell.f_app.presentation.activities

import android.animation.LayoutTransition
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.util.TypedValue
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.Consumer
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.paging.PagedList
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import com.vk.api.sdk.VK
import com.vk.api.sdk.auth.VKAccessToken
import com.vk.api.sdk.auth.VKAuthCallback
import kotlinx.android.synthetic.main.activity_main.*
import android.animation.ObjectAnimator
import androidx.appcompat.app.AlertDialog
import com.one.russell.f_app.presentation.misc.BottomSheet
import com.one.russell.f_app.presentation.misc.CircleTextSpan
import com.one.russell.f_app.presentation.misc.GridSpacingItemDecoration
import com.one.russell.f_app.R
import com.one.russell.f_app.data.Community
import com.one.russell.f_app.presentation.adapters.CommunityListAdapter
import com.one.russell.f_app.presentation.viewmodels.MainViewModel
import com.vk.api.sdk.auth.VKScope

class MainActivity : AppCompatActivity() {

    private var bottomSheet = BottomSheet()
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        toolbar.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        checkAuth()
        initListeners()
        initObservers()
    }

    private fun checkAuth() {
        val accessToken = viewModel.getAccessToken(this)
        if (accessToken == null || !accessToken.isValid) {
            VK.login(this, listOf(VKScope.GROUPS))
        } else {
            setupPagedList()
        }
    }

    private fun initListeners() {
        btn_leave.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle(R.string.leave_dialog_title)
                .setMessage(R.string.leave_dialog_message)
                .setPositiveButton(R.string.leave_dialog_yes) { _, _ -> viewModel.leaveSelectedCommunities() }
                .setNegativeButton(R.string.leave_dialog_cancel) { dialog, _ -> dialog.dismiss() }
                .setCancelable(true)
                .show()
        }

        list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (recyclerView.computeVerticalScrollOffset() > 0) {
                    hideHintAndShowToolbar()
                    recyclerView.removeOnScrollListener(this)
                }
            }
        })
    }

    private fun initObservers() {
        viewModel.getSelectedItems().observe(this) { list ->
            if (list.size > 0) {
                setLeaveButtonText(list.size)
                hideHintAndShowToolbar()
            }
        }

        viewModel.getleaveButtonVisibilityLiveData().observe(this) { isVisible ->
            showLeaveButton(isVisible)
        }
    }

    private fun setupPagedList() {
        val adapter = CommunityListAdapter(
            object :
                CommunityListAdapter.OnItemClickListener {
                override fun onItemClicked(item: Community) {
                    viewModel.selectItem(item)
                }

                override fun onItemLongClicked(item: Community) {
                    showItemInBottomSheet(item)
                    hideHintAndShowToolbar()
                }
            },
            CommunityListAdapter.CommunityDiffUtilCallback()
        )

        viewModel.getCommunityList().observe(this,
            Observer<PagedList<Community>> { communities ->
                adapter.submitList(communities)
            })

        list.adapter = adapter
        list.addItemDecoration(
            GridSpacingItemDecoration(
                3,
                resources.getDimension(R.dimen.grid_items_offset).toInt()
            )
        )
    }

    private fun showItemInBottomSheet(item: Community) {
        bottomSheet.show(supportFragmentManager, "bottom_sheet_tag")
        viewModel.loadCommunityDetails(item,
            Consumer { detailedCommunity -> bottomSheet.setData(detailedCommunity) })
    }

    private fun hideHintAndShowToolbar() {
        toolbar.visibility = View.VISIBLE

        TransitionManager.beginDelayedTransition(content)

        val params = hint_container.layoutParams
        params.height = 0
        hint_container.layoutParams = params
    }

    private fun setLeaveButtonText(count: Int) {
        val btnString = resources.getString(R.string.btn_leave_text, count)
        val spannable = SpannableString(btnString)

        val circleColor = resources.getColor(R.color.textColorPrimaryInverse)
        val counterColor = resources.getColor(R.color.blueButtonBackground)
        val circlePadding = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            8f,
            resources.displayMetrics
        ).toInt()

        spannable.setSpan(
            CircleTextSpan(
                circleColor,
                counterColor,
                circlePadding
            ),
            spannable.length - 1,
            spannable.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        btn_leave.text = spannable
    }

    private fun showLeaveButton(show: Boolean) {
        val containerHeight = resources.getDimension(R.dimen.leave_button_container_height)

        val translationAnimation = ObjectAnimator.ofFloat(
            leave_button_container,
            "translationY",
            if (show) containerHeight else 0f,
            if (show) 0f else containerHeight
        )

        translationAnimation.duration = 200
        translationAnimation.start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (!VK.onActivityResult(requestCode, resultCode, data, object : VKAuthCallback {
                override fun onLogin(token: VKAccessToken) {
                    viewModel.saveAccessToken(this@MainActivity, token)
                    setupPagedList()
                }

                override fun onLoginFailed(errorCode: Int) {
                }
            })
        ) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}
