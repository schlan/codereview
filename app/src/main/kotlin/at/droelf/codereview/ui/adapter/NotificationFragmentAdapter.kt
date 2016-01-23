package at.droelf.codereview.ui.adapter

import android.support.design.widget.Snackbar
import android.support.v4.app.FragmentManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import at.droelf.codereview.R
import at.droelf.codereview.model.GithubModel
import at.droelf.codereview.ui.fragment.NotificationFragmentController
import rx.Observable

class NotificationFragmentAdapter(
        pullRequestsObservable: Observable<List<GithubModel.PullRequest>>,
        val controller: NotificationFragmentController,
        parent: View,
        val fragmentManager: FragmentManager) : RecyclerView.Adapter<NotificationFragmentViewHolder>() {

    var pullRequests: MutableList<GithubModel.PullRequest> = arrayListOf()

    init {
        pullRequestsObservable.subscribe({ updateList(it) }, { error ->
            Snackbar.make(parent, "Error: ${error.message}", Snackbar.LENGTH_LONG).show()
        })
    }

    fun updateList(prs: List<GithubModel.PullRequest>) {
        prs.forEach { pr ->
            pullRequests.add(pr)
            pullRequests.sortByDescending { it.updatedAt }
            val index = pullRequests.indexOf(pr)
            notifyItemInserted(index)
        }
    }

    override fun getItemCount(): Int {
        return pullRequests.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationFragmentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_notification_fragment, parent, false)
        return NotificationFragmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationFragmentViewHolder, position: Int) {
        holder.bind(pullRequests[position], fragmentManager, controller)
    }
}