package at.droelf.codereview.ui.view

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import at.droelf.codereview.R
import at.droelf.codereview.ui.adapter.NotificationFragmentAdapter
import at.droelf.codereview.ui.adapter.UnsubscribeRx
import at.droelf.codereview.ui.fragment.NotificationFragmentController
import butterknife.Bind
import butterknife.ButterKnife
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator

class NotificationPullRequestView(context: Context, val fm: FragmentManager, val controller: NotificationFragmentController): FrameLayout(context), UnsubscribeRx {

    @Bind(R.id.notification_list) lateinit var list: RecyclerView
    @Bind(R.id.notification_swipe_to_refresh) lateinit var swipeToRefresh: SwipeRefreshLayout

    var listAdapter: NotificationFragmentAdapter? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.view_notification_pull_requests, this, true)
        swipeToRefresh = findViewById(R.id.notification_swipe_to_refresh) as SwipeRefreshLayout
        list = findViewById(R.id.notification_list) as RecyclerView
        //ButterKnife.bind(this)
        init()
    }

    fun init() {
        swipeToRefresh.isEnabled = false
        swipeToRefresh.setColorSchemeResources(R.color.colorAccent)
        swipeToRefresh.setOnRefreshListener {
            swipeToRefresh.isRefreshing = false
        }

        listAdapter = NotificationFragmentAdapter(controller.loadPrs(), controller, this, fm, swipeToRefresh)

        list.addItemDecoration(DividerItemDecoration(context, resources.getDimensionPixelOffset(R.dimen.row_notification_pull_request_divider_padding_left)))
        list.layoutManager = LinearLayoutManager(context)
        list.itemAnimator = SlideInUpAnimator()
        list.adapter = listAdapter
    }

    override fun unsubscribeRx() {
        listAdapter?.unsubscribeRx()
    }
}
