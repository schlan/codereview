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
import at.droelf.codereview.ui.viewholder.PullRequestFileViewHolder
import rx.Observable
import rx.Subscription

class PullRequestFilesAdapter(val commentsObserver: Observable<List<Pair<GithubModel.PullRequestFile, Int>>>,
                              val controller: StartFragmentController,
                              val fm: FragmentManager,
                              val swipeToRefresh: SwipeRefreshLayout,
                              val pr: GithubModel.PullRequestDetail): RecyclerView.Adapter<PullRequestFileViewHolder>(), UnsubscribeRx{

    var files: MutableList<Pair<GithubModel.PullRequestFile, Int>> = arrayListOf()
    var subscription : Subscription?

    init {
        setHasStableIds(true)
        swipeToRefresh.post({ swipeToRefresh.isRefreshing = true })
        subscription = commentsObserver
                .subscribe({
                    files.addAll(it)
                    notifyDataSetChanged()
                },{
                    Snackbar.make(swipeToRefresh, "Error: ${it.message}", Snackbar.LENGTH_LONG).show()
                    swipeToRefresh.post({ swipeToRefresh.isRefreshing = false })
                },{
                    swipeToRefresh.post({ swipeToRefresh.isRefreshing = false })
                })
    }

    override fun getItemCount(): Int {
        return files.size
    }

    override fun onBindViewHolder(holder: PullRequestFileViewHolder, position: Int) {
        holder.bind(PullRequestFileViewHolderData(files[position], controller, fm, pr))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PullRequestFileViewHolder? {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_pr_file, parent, false)
        return PullRequestFileViewHolder(view)
    }

    override fun getItemId(position: Int): Long {
        return (files[position].first.filename.hashCode() + position).toLong()
    }

    override fun unsubscribeRx() {
        subscription?.unsubscribe()
        subscription = null
    }

    data class PullRequestFileViewHolderData(
            val file: Pair<GithubModel.PullRequestFile, Int>,
            val controller: StartFragmentController,
            val fm: FragmentManager,
            val pr: GithubModel.PullRequestDetail)
}