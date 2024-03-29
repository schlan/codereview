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
import at.droelf.codereview.ui.viewholder.PullRequestFileViewHolder
import rx.Observable
import rx.Subscription

class PullRequestFilesAdapter(val commentsObserver: Observable<List<Triple<GithubModel.PullRequestFile, Int, List<GithubModel.ReviewComment>>>>,
                              val controller: StartFragmentController,
                              val fm: FragmentManager,
                              val swipeRefreshLayout: SwipeRefreshLayout,
                              val pr: GithubModel.PullRequestDetail): RecyclerView.Adapter<PullRequestFileViewHolder>(), UnsubscribeRx{

    var files: MutableList<Triple<GithubModel.PullRequestFile, Int, List<GithubModel.ReviewComment>>> = arrayListOf()
    var subscription : Subscription?

    init {
        setHasStableIds(true)
        swipeRefreshLayout.post({ swipeRefreshLayout.isRefreshing = true })
        subscription = commentsObserver
                .subscribe({
                    files.clear()
                    files.addAll(it)
                    files.sortBy { it.first.filename }
                    files.sortByDescending { it.second }
                    notifyDataSetChanged()
                },{
                    Snackbar.make(swipeRefreshLayout, "Error: ${it.message}", Snackbar.LENGTH_LONG).show()
                    swipeRefreshLayout.post({ swipeRefreshLayout.isRefreshing = false })
                },{
                    swipeRefreshLayout.post({ swipeRefreshLayout.isRefreshing = false })
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
            val file: Triple<GithubModel.PullRequestFile, Int, List<GithubModel.ReviewComment>>,
            val controller: StartFragmentController,
            val fm: FragmentManager,
            val pr: GithubModel.PullRequestDetail)
}