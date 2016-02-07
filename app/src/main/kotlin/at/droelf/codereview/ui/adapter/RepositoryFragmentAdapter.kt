package at.droelf.codereview.ui.adapter

import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import at.droelf.codereview.R
import at.droelf.codereview.model.Model
import at.droelf.codereview.ui.fragment.RepositoryFragmentController
import at.droelf.codereview.ui.viewholder.RepositoryFragmentViewHolder
import rx.Observable

class RepositoryFragmentAdapter(
        repos: Observable<List<Model.GithubSubscription>>,
        val controller: RepositoryFragmentController,
        swipeToRefresh: SwipeRefreshLayout
): RecyclerView.Adapter<RepositoryFragmentViewHolder>() {

    private var repoList: MutableList<Model.GithubSubscription> = mutableListOf()

    init {
        swipeToRefresh.post({ swipeToRefresh.isRefreshing = true })
        repos.subscribe({
            repoList.addAll(it)
            notifyDataSetChanged()
        }, {
            swipeToRefresh.post({ swipeToRefresh.isRefreshing = false })
            it.printStackTrace()
        }, {
            swipeToRefresh.post({ swipeToRefresh.isRefreshing = false })
        })
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup?, position: Int): RepositoryFragmentViewHolder? {
        val viewgroup = viewGroup ?: return null
        return RepositoryFragmentViewHolder(LayoutInflater.from(viewgroup.context).inflate(R.layout.row_repository_fragment, viewgroup, false))
    }

    override fun getItemCount(): Int {
        return repoList.size
    }

    override fun onBindViewHolder(viewHolder: RepositoryFragmentViewHolder?, position: Int) {
        viewHolder?.bind(repoList[position], controller)
    }
}