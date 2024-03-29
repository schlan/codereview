package at.droelf.codereview.ui.view

import android.content.Context
import android.support.v4.app.FragmentManager
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.widget.FrameLayout
import at.droelf.codereview.R
import at.droelf.codereview.model.GithubModel
import at.droelf.codereview.ui.adapter.PullRequestFilesAdapter
import at.droelf.codereview.ui.adapter.UnsubscribeRx
import at.droelf.codereview.ui.fragment.StartFragmentController
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator

class PullRequestFileView(context: Context, val pr: GithubModel.PullRequestDetail, val fm: FragmentManager, val controller: StartFragmentController): FrameLayout(context), UnsubscribeRx {

    val list: RecyclerView
    val swipeToRefresh: SwipeRefreshLayout
    var listAdapter: PullRequestFilesAdapter? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.view_pr_files, this, true)
        swipeToRefresh = findViewById(R.id.pr_files_swipe_to_refresh) as SwipeRefreshLayout
        list = findViewById(R.id.pr_files_list) as RecyclerView
        init()
    }

    fun init() {
        swipeToRefresh.isEnabled = false
        swipeToRefresh.setColorSchemeResources(R.color.colorAccent)
        swipeToRefresh.setOnRefreshListener {
            swipeToRefresh.isRefreshing = false
        }

        list.addItemDecoration(DividerItemDecoration(context, 0))
        list.layoutManager = LinearLayoutManager(context)
        list.itemAnimator = SlideInUpAnimator()

        val owner = pr.base.repo.owner.login
        val repo = pr.base.repo.name
        val number = pr.number

        listAdapter = PullRequestFilesAdapter(controller.prfiles(owner, repo, number), controller, fm, swipeToRefresh, pr)
        list.adapter = listAdapter

        if(controller.scrollPos != null){
            list.layoutManager.scrollToPosition(controller.scrollPos!!)
        }
    }

    override fun unsubscribeRx() {
        controller.scrollPos = (list.layoutManager as? LinearLayoutManager)?.findFirstVisibleItemPosition()
        listAdapter?.unsubscribeRx()
        listAdapter = null
    }

}