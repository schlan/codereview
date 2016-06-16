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
import at.droelf.codereview.ui.viewholder.PullRequestCommentInitViewHolder
import at.droelf.codereview.ui.viewholder.PullRequestCommentViewHolder
import rx.Observable
import rx.Subscription


class PullRequestCommentsAdapter(
        val commentsObserver: Observable<List<GithubModel.Comment>>,
        val controller: StartFragmentController,
        val swipeToRefresh: SwipeRefreshLayout,
        data: StartFragmentController.PullRequestDetails) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), UnsubscribeRx {

    var items: List<RecyclerItem> = listOf()
    var subscription: Subscription?

    val HEADER = 1
    val SPACE = 2
    val COMMENT = 3

    init {
        swipeToRefresh.isRefreshing = true
        items = listOf(RecyclerItem(HEADER, data))
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
            newItems += listOf(RecyclerItem(SPACE, Any()))
        }
        newItems += newComments.map{ RecyclerItem(COMMENT, it) }
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
            COMMENT -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.row_pr_comment, parent, false)
                PullRequestCommentViewHolder(view)
            }
            HEADER -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.row_pr_comment_init, parent, false)
                PullRequestCommentInitViewHolder(view)
            }
            SPACE -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.row_empty, parent, false)
                object: RecyclerView.ViewHolder(view){}
            }
            else -> throw RuntimeException("unknown type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(items[position].type){
            HEADER -> {
                val p = (holder as PullRequestCommentInitViewHolder)
                @Suppress("UNCHECKED_CAST")
                val data = items[position].data as StartFragmentController.PullRequestDetails
                p.bind(data)
            }
            COMMENT -> {
                val p = (holder as PullRequestCommentViewHolder)
                p.bind(items[position].data as GithubModel.Comment, controller)
            }
        }
    }

    override fun unsubscribeRx() {
        subscription?.unsubscribe()
        subscription = null
    }


    data class RecyclerItem(val type: Int, val data: Any)
}