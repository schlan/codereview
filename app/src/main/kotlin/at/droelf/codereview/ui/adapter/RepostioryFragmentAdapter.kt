package at.droelf.codereview.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import at.droelf.codereview.R
import at.droelf.codereview.model.GithubModel
import at.droelf.codereview.ui.viewholder.RepositoryFragmentViewHolder
import rx.Observable

class RepostioryFragmentAdapter(repos: Observable<List<GithubModel.Repository>>): RecyclerView.Adapter<RepositoryFragmentViewHolder>() {

    private var repoList: MutableList<GithubModel.Repository> = mutableListOf()

    init {
        repos.subscribe({
            repoList.addAll(it)
            notifyDataSetChanged()
        }, {
            it.printStackTrace()
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
        viewHolder?.bind(repoList[position])
    }
}