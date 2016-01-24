package at.droelf.codereview.ui.adapter

import android.support.design.widget.Snackbar
import android.support.v4.app.FragmentManager
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import at.droelf.codereview.Constants
import at.droelf.codereview.R
import at.droelf.codereview.model.GithubModel
import at.droelf.codereview.ui.fragment.NotificationFragmentController
import rx.Observable

class NotificationFragmentAdapter(
        pullRequestsObservable: Observable<List<GithubModel.PullRequest>>,
        val controller: NotificationFragmentController,
        parent: View,
        val fragmentManager: FragmentManager,
        val swipeRefreshLayout: SwipeRefreshLayout) : RecyclerView.Adapter<ViewHolderBinder<*>>() {

    var holderWrapperList: List<HolderWrapper> = arrayListOf()
    var myPullRequests: MutableList<GithubModel.PullRequest> = arrayListOf()
    var pullRequests: MutableList<GithubModel.PullRequest> = arrayListOf()

    val subHeaderMine = HolderWrapper(0, "Mine")
    val subHeaderOther = HolderWrapper(0, "All Pull Requests")

    init {
        swipeRefreshLayout.post({ swipeRefreshLayout.isRefreshing = true })
        pullRequestsObservable.subscribe({
            updateList(it)
        }, { error ->
            Snackbar.make(parent, "Error: ${error.message}", Snackbar.LENGTH_LONG).show()
        }, {
            swipeRefreshLayout.post({ swipeRefreshLayout.isRefreshing = false })
        })
    }

    fun updateList(prs: List<GithubModel.PullRequest>) {
        prs.forEach { pr ->

            val emptyMy = myPullRequests.isEmpty()
            val emptyOther = pullRequests.isEmpty()

            if(pr.user.login == Constants.login) {
                myPullRequests.add(pr)
                myPullRequests.sortByDescending { it.createdAt }
            } else {
                pullRequests.add(pr)
                pullRequests.sortByDescending { it.createdAt }
            }

            val tmpList: MutableList<HolderWrapper> = arrayListOf()

            if(myPullRequests.size > 0){
                tmpList.add(subHeaderMine)
                tmpList.addAll(myPullRequests.map{ HolderWrapper(1, it) })
            }

            if(pullRequests.size > 0){
                tmpList.add(subHeaderOther)
                tmpList.addAll(pullRequests.map{ HolderWrapper(1, it) })
            }

            holderWrapperList = tmpList
            if (emptyMy) notifyItemInserted(tmpList.indexOf(subHeaderMine))
            if (emptyOther) notifyItemInserted(tmpList.indexOf(subHeaderOther))
            val index = tmpList.indexOfFirst { it.type == 1 && it.data == pr }
            notifyItemInserted(index)

        }
    }

    override fun getItemCount(): Int {
        val myPulls = if(myPullRequests.isNotEmpty()) myPullRequests.size + 1 else 0
        val otherPulls = if(pullRequests.isNotEmpty()) pullRequests.size + 1 else 0
        return myPulls + otherPulls
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderBinder<*> {
        return when(viewType) {
            0 -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.row_notification_fragment_subheader, parent, false)
                NotificationFragmentViewHolderHeader(view)
            }
            1 -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.row_notification_fragment, parent, false)
                NotificationFragmentViewHolder(view)
            }
            else -> throw IllegalArgumentException("Unknown pos")
        }
    }

    override fun onBindViewHolder(holder: ViewHolderBinder<*>, position: Int) {
        if(holder is NotificationFragmentViewHolderHeader){
            holder.bind(holderWrapperList[position].data as String)

        } else if (holder is NotificationFragmentViewHolder){
            val pr = holderWrapperList[position].data as GithubModel.PullRequest
            holder.bind(NotificationFragmentViewHolder.NotificationFragmentViewHolderData(pr, fragmentManager, controller))

        }
    }

    override fun getItemViewType(position: Int): Int {
        return holderWrapperList[position].type
    }


    data class HolderWrapper(val type: Int, val data: Any)
}