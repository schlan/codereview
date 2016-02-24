package at.droelf.codereview.ui.view

import android.content.Context
import android.support.v4.app.FragmentManager
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import at.droelf.codereview.R
import at.droelf.codereview.model.GithubModel
import at.droelf.codereview.ui.adapter.PullRequestCommentsAdapter
import at.droelf.codereview.ui.adapter.UnsubscribeRx
import at.droelf.codereview.ui.fragment.StartFragmentController
import com.jakewharton.rxbinding.widget.RxAutoCompleteTextView
import com.jakewharton.rxbinding.widget.RxTextView
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator

class PullRequestCommentView(
        context: Context,
        val pr: GithubModel.PullRequestDetail,
        val fm: FragmentManager,
        val controller: StartFragmentController,
        val status: GithubModel.Status?): FrameLayout(context), UnsubscribeRx {

    val list: RecyclerView
    val swipeToRefresh: SwipeRefreshLayout

    var listAdapter: PullRequestCommentsAdapter? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.view_pr_comments, this, true)
        swipeToRefresh = findViewById(R.id.pr_comments_swipe_to_refresh) as SwipeRefreshLayout
        list = findViewById(R.id.pr_comments_list) as RecyclerView
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
        val comments = controller.comments(owner, repo, number)
        listAdapter = PullRequestCommentsAdapter(comments, controller, swipeToRefresh, pr, status)
        list.adapter = listAdapter
    }

    override fun unsubscribeRx() {
        listAdapter?.unsubscribeRx()
        listAdapter = null
    }

}