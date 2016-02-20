package at.droelf.codereview.ui.adapter

import android.support.design.widget.Snackbar
import android.support.v4.app.FragmentManager
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import at.droelf.codereview.R
import at.droelf.codereview.model.GithubModel
import at.droelf.codereview.model.ResponseHolder
import at.droelf.codereview.ui.fragment.NotificationFragmentController
import at.droelf.codereview.ui.viewholder.NotificationFragmentViewHolder
import at.droelf.codereview.ui.viewholder.NotificationFragmentViewHolderHeader
import at.droelf.codereview.ui.viewholder.ViewHolderBinder
import rx.Observable
import rx.Subscription

class NotificationFragmentAdapter(
        pullRequestsObservable: Observable<ResponseHolder<List<GithubModel.PullRequest>>>,
        val controller: NotificationFragmentController,
        parent: View,
        val fragmentManager: FragmentManager,
        val swipeRefreshLayout: SwipeRefreshLayout) : RecyclerView.Adapter<ViewHolderBinder<*>>(), UnsubscribeRx {

    var holderWrapperList: List<HolderWrapper> = listOf()

    val myPullRequests: MutableList<HolderWrapper> = arrayListOf()
    val pullRequests: MutableList<HolderWrapper> = arrayListOf()

    val subHeaderMine = HolderWrapper(0, 0L, "Mine", null, null)
    val subHeaderOther = HolderWrapper(0, 1L, "All Pull Requests", null, null)

    var subscription: Subscription?

    val lock = Any()

    init {
        setHasStableIds(true)
        swipeRefreshLayout.post({ swipeRefreshLayout.isRefreshing = true })
        subscription = pullRequestsObservable
                .subscribe({ d ->
                    synchronized(lock){
                        updateList(d)
                    }
                }, { error ->
                    error.printStackTrace()
                    swipeRefreshLayout.post({ swipeRefreshLayout.isRefreshing = false })
                    Snackbar.make(parent, "Error: ${error.message}", Snackbar.LENGTH_LONG).show()
                }, {
                    swipeRefreshLayout.post({ swipeRefreshLayout.isRefreshing = false })
                })
    }

    fun updateList(prs: ResponseHolder<List<GithubModel.PullRequest>>) {
        prs.data.forEach { pr ->
            val emptyMy = myPullRequests.isEmpty()
            val emptyOther = pullRequests.isEmpty()

            var replaced = if(pr.user.id == controller.user.id) {
                updateSectionList(pr, myPullRequests, prs.upToDate(), prs.source)
            } else {
                updateSectionList(pr, pullRequests, prs.upToDate(), prs.source)
            }

           refreshList(pr, emptyMy, emptyOther, replaced)
        }
    }

    fun updateSectionList(pr: GithubModel.PullRequest, list: MutableList<HolderWrapper>, upToDate: Boolean, source: ResponseHolder.Source): Boolean {
        var replaced = false
        if(upToDate && list.filter { (it.data as GithubModel.PullRequest).id == pr.id }.isNotEmpty()){
            replaced = true
            list.remove(list.filter { (it.data as GithubModel.PullRequest).id == pr.id }.first())
        }
        list.add(HolderWrapper(1, pr.id + pr.number + (pr.body?.hashCode() ?: 123123), pr, source, upToDate))
        list.sortByDescending { (it.data as GithubModel.PullRequest).createdAt }

        return replaced
    }

    fun refreshList(pr: GithubModel.PullRequest, myPullRequestsEmpty: Boolean, pullRequestEmpty: Boolean, replaced: Boolean){
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
        if (myPullRequestsEmpty != myPullRequests.isEmpty() || pullRequestEmpty != pullRequests.isEmpty()) {
            notifyDataSetChanged()
        } else {
            val index = holderWrapperList.indexOfFirst { it.type == 1 && it.data.equals(pr) }

            if(replaced){
                notifyItemChanged(index)
            } else {
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
        when(holder){
            is NotificationFragmentViewHolderHeader -> {
                holder.bind(holderWrapperList[position].data as String)
            }
            is NotificationFragmentViewHolder -> {
                val wrapper = holderWrapperList[position]
                val pr = wrapper.data as GithubModel.PullRequest
                holder.bind(NotificationFragmentViewHolder.NotificationFragmentViewHolderData(pr, fragmentManager, controller, wrapper.source!!, wrapper.updateToDate!!))
            }
        }
    }

    override fun onViewDetachedFromWindow(holder: ViewHolderBinder<*>) {
        super.onViewDetachedFromWindow(holder)
        if(holder is NotificationFragmentViewHolder){
            holder.pause()
        }
    }


    override fun getItemViewType(position: Int): Int {
        return holderWrapperList[position].type
    }

    override fun unsubscribeRx() {
        subscription?.unsubscribe()
        subscription = null
    }

    data class HolderWrapper(val type: Int, val id: Long, val data: Any, var source: ResponseHolder.Source?, var updateToDate: Boolean?)
}