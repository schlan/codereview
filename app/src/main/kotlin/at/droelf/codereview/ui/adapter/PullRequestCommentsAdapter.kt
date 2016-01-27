package at.droelf.codereview.ui.adapter

import android.support.design.widget.Snackbar
import android.support.v4.app.FragmentManager
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import at.droelf.codereview.R
import at.droelf.codereview.model.GithubModel
import at.droelf.codereview.ui.fragment.StartFragmentController
import at.droelf.codereview.ui.view.PullRequestCommentView
import at.droelf.codereview.ui.viewholder.PullRequestCommentViewHolder
import rx.Observable
import rx.Subscription


class PullRequestCommentsAdapter(
        val commentsObserver: Observable<List<GithubModel.Comment>>,
        val controller: StartFragmentController,
        pullRequestCommentView: PullRequestCommentView,
        fm: FragmentManager,
        val swipeToRefresh: SwipeRefreshLayout,
        pr: GithubModel.PullRequestDetail) : RecyclerView.Adapter<PullRequestCommentViewHolder>(), UnsubscribeRx {

    var comments: MutableList<GithubModel.Comment> = arrayListOf()
    var subscription: Subscription?

    init {
        swipeToRefresh.isRefreshing = true
        comments.add(GithubModel.Comment(-1L, "", "", "", pr.user, pr.bodyHtml))
        notifyItemInserted(0)

        subscription = commentsObserver.subscribe({ comments ->
            update(comments)
        }, {
            Snackbar.make(swipeToRefresh, "Error: ${it.message}", Snackbar.LENGTH_LONG).show()
            swipeToRefresh.isRefreshing = false
        }, {
            swipeToRefresh.isRefreshing = false
        })
    }

    fun update(comments: List<GithubModel.Comment>){
        this.comments.addAll(comments)
        notifyItemRangeInserted(1, comments.size)
    }

    override fun getItemCount(): Int {
        return comments.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PullRequestCommentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_pr_comment, parent, false)
        return PullRequestCommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: PullRequestCommentViewHolder, position: Int) {
        holder.bind(comments[position], controller)
    }

    override fun unsubscribeRx() {
        subscription?.unsubscribe()
        subscription = null
    }

}