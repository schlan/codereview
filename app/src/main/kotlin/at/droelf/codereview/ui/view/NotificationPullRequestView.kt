package at.droelf.codereview.ui.view

import android.content.Context
import android.support.v4.app.FragmentManager
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.widget.FrameLayout
import at.droelf.codereview.R
import at.droelf.codereview.ui.adapter.NotificationFragmentAdapter
import at.droelf.codereview.ui.adapter.UnsubscribeRx
import at.droelf.codereview.ui.fragment.NotificationFragmentController
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator

class NotificationPullRequestView(context: Context, val fm: FragmentManager, val controller: NotificationFragmentController): FrameLayout(context), UnsubscribeRx {

    val list: RecyclerView
    val swipeToRefresh: SwipeRefreshLayout

    var listAdapter: NotificationFragmentAdapter? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.view_notification_pull_requests, this, true)
        swipeToRefresh = findViewById(R.id.notification_swipe_to_refresh) as SwipeRefreshLayout
        list = findViewById(R.id.notification_list) as RecyclerView
        init()
    }

    fun init() {
        swipeToRefresh.setColorSchemeResources(R.color.colorAccent)
        swipeToRefresh.setOnRefreshListener {
            controller.observable = null
            controller.listMapCache = hashMapOf()
            listAdapter?.loadData(controller.loadPrs(true))
        }

        list.addItemDecoration(DividerItemDecoration(context, resources.getDimensionPixelOffset(R.dimen.row_notification_pull_request_divider_padding_left)))
        list.layoutManager = LinearLayoutManager(context)
        list.itemAnimator = SlideInUpAnimator()
        listAdapter = NotificationFragmentAdapter(controller, this, fm, swipeToRefresh)
        listAdapter?.loadData(controller.loadPrs(true))
        list.adapter = listAdapter

        if(controller.scrollPos != null){
            list.layoutManager.scrollToPosition(controller.scrollPos!!)
        }
    }

    override fun unsubscribeRx() {
        controller.scrollPos = (list.layoutManager as? LinearLayoutManager)?.findFirstVisibleItemPosition()
        listAdapter?.unsubscribeRx()
    }
}
