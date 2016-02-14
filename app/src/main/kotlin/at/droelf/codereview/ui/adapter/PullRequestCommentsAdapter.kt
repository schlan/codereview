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
        pr: GithubModel.PullRequestDetail) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), UnsubscribeRx {

    var items: List<RecyclerItem> = listOf()
    var subscription: Subscription?

    init {
        swipeToRefresh.isRefreshing = true
        items = listOf(RecyclerItem(1, GithubModel.Comment(-1L, "", "", "", pr.user, pr.bodyHtml)))
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

    fun update(newComments: List<GithubModel.Comment>){
        var newItems = listOf(this.items.first())
        if(newComments.isNotEmpty()){
            newItems += listOf(RecyclerItem(3, Any()))
        }
        newItems += newComments.map{ RecyclerItem(2, it) }
        items = newItems
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return items.size
    }


    override fun getItemViewType(position: Int): Int {
        return items[position].type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            1,2 -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.row_pr_comment, parent, false)
                PullRequestCommentViewHolder(view)
            }
            3 -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.row_empty, parent, false)
                object: RecyclerView.ViewHolder(view){}
            }
            else -> throw RuntimeException("unknown type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(items[position].type){
            1,2 -> {
                val p = (holder as PullRequestCommentViewHolder)
                p.bind(items[position].data as GithubModel.Comment, controller)
                p.showAvatar(position != 0)
            }
        }
    }

    override fun unsubscribeRx() {
        subscription?.unsubscribe()
        subscription = null
    }


    data class RecyclerItem(val type: Int, val data: Any)
}