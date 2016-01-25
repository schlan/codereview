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
import rx.Subscription
import java.util.concurrent.TimeUnit

class NotificationFragmentAdapter(
        pullRequestsObservable: Observable<List<GithubModel.PullRequest>>,
        val controller: NotificationFragmentController,
        parent: View,
        val fragmentManager: FragmentManager,
        val swipeRefreshLayout: SwipeRefreshLayout) : RecyclerView.Adapter<ViewHolderBinder<*>>(), UnsubscribeRx {

    val lock = Any()

    var holderWrapperList: List<HolderWrapper> = listOf()

    val myPullRequests: MutableList<HolderWrapper> = arrayListOf()
    val pullRequests: MutableList<HolderWrapper> = arrayListOf()

    val subHeaderMine = HolderWrapper(0, 0L, "Mine")
    val subHeaderOther = HolderWrapper(0, 1L, "All Pull Requests")

    var subscription: Subscription?

    init {
        setHasStableIds(true)
        swipeRefreshLayout.post({ swipeRefreshLayout.isRefreshing = true })
        subscription = pullRequestsObservable
                .subscribe({ d ->
                    updateList(d)
                }, { error ->
                    swipeRefreshLayout.post({ swipeRefreshLayout.isRefreshing = false })
                    Snackbar.make(parent, "Error: ${error.message}", Snackbar.LENGTH_LONG).show()
                }, {
                    swipeRefreshLayout.post({ swipeRefreshLayout.isRefreshing = false })
                })
    }

    fun updateList(prs: List<GithubModel.PullRequest>) {
        prs.forEach { pr ->
            val emptyMy = myPullRequests.isEmpty()
            val emptyOther = pullRequests.isEmpty()

            if (pr.user.login == Constants.login) {
                myPullRequests.add(HolderWrapper(1, pr.id + pr.number + pr.body.hashCode(), pr))
                myPullRequests.sortByDescending { (it.data as GithubModel.PullRequest).createdAt }
            } else {
                pullRequests.add(HolderWrapper(1, pr.id + pr.number + pr.body.hashCode(), pr))
                pullRequests.sortByDescending { (it.data as GithubModel.PullRequest).createdAt }
            }

            val tmpList: MutableList<HolderWrapper> = arrayListOf()
            if (myPullRequests.size > 0) {
                tmpList.add(subHeaderMine)
                tmpList.addAll(myPullRequests)
            }
            if (pullRequests.size > 0) {
                tmpList.add(subHeaderOther)
                tmpList.addAll(pullRequests)
            }

            holderWrapperList = tmpList
            if (emptyMy || emptyOther) {
                notifyDataSetChanged()
            } else {
                val index = holderWrapperList.indexOfFirst { it.type == 1 && it.data.equals(pr) }
                notifyItemInserted(index)
            }
        }
    }

    override fun getItemId(position: Int): Long {
        return holderWrapperList[position].id
    }

    override fun getItemCount(): Int {
        return holderWrapperList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderBinder<*> {
        return when (viewType) {
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
        if (holder is NotificationFragmentViewHolderHeader) {
            holder.bind(holderWrapperList[position].data as String)

        } else if (holder is NotificationFragmentViewHolder) {
            val pr = holderWrapperList[position].data as GithubModel.PullRequest
            holder.bind(NotificationFragmentViewHolder.NotificationFragmentViewHolderData(pr, fragmentManager, controller))

        }
    }

    override fun getItemViewType(position: Int): Int {
        return holderWrapperList[position].type
    }

    override fun unsubscribeRx() {
        subscription?.unsubscribe()
        subscription = null
    }

    data class HolderWrapper(val type: Int, val id: Long, val data: Any)
}