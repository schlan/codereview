package at.droelf.codereview.ui.view

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.support.design.widget.Snackbar
import android.support.v4.app.FragmentManager
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import at.droelf.codereview.R
import at.droelf.codereview.model.GithubModel
import at.droelf.codereview.ui.adapter.NotificationFragmentAdapter
import at.droelf.codereview.ui.adapter.PullRequestCommentsAdapter
import at.droelf.codereview.ui.adapter.PullRequestFilesAdapter
import at.droelf.codereview.ui.adapter.UnsubscribeRx
import at.droelf.codereview.ui.fragment.NotificationFragmentController
import at.droelf.codereview.ui.fragment.StartFragmentController
import butterknife.Bind
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator
import rx.Observable
import rx.Subscription

class PullRequestFileView(context: Context, val pr: GithubModel.PullRequestDetail, val fm: FragmentManager, val controller: StartFragmentController): FrameLayout(context), UnsubscribeRx {

    val list: RecyclerView
    val swipeToRefresh: SwipeRefreshLayout
    var listAdapter: PullRequestFilesAdapter? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.view_pr_files, this, true)
        swipeToRefresh = findViewById(R.id.pr_files_swipe_to_refresh) as SwipeRefreshLayout
        list = findViewById(R.id.pr_files_list) as RecyclerView
        init()
    }

    fun init() {
        swipeToRefresh.isEnabled = false
        swipeToRefresh.setColorSchemeResources(R.color.colorAccent)
        swipeToRefresh.setOnRefreshListener {
            swipeToRefresh.isRefreshing = false
        }

        list.addItemDecoration(DividerItemDecoration(context, resources.getDimensionPixelOffset(R.dimen.row_notification_pull_request_divider_padding_left)))
        list.layoutManager = LinearLayoutManager(context)
        list.itemAnimator = SlideInUpAnimator()

        val owner = pr.base.repo.owner.login
        val repo = pr.base.repo.name
        val number = pr.number
        val comments = controller.comments(owner, repo, number)

        listAdapter = PullRequestFilesAdapter(controller.prfiles(owner, repo, number), controller, fm, swipeToRefresh, pr)
        list.adapter = listAdapter
    }

    override fun unsubscribeRx() {
        listAdapter?.unsubscribeRx()
        listAdapter = null
    }


//    class Adapter(prfiles: Observable<List<Pair<GithubModel.PullRequestFile, Int>>>, swipeToRefresh: SwipeRefreshLayout) : BaseAdapter(), UnsubscribeRx {
//
//        var files: MutableList<Pair<GithubModel.PullRequestFile, Int>> = arrayListOf()
//        var subscription: Subscription?
//
//        init {
//            swipeToRefresh.post({ swipeToRefresh.isRefreshing = true })
//            subscription = prfiles.subscribe ({ data ->
//                files.addAll(data)
//                notifyDataSetChanged()
//            },{
//                Snackbar.make(swipeToRefresh, "Error: ${it.message}", Snackbar.LENGTH_LONG).show()
//                swipeToRefresh.post({ swipeToRefresh.isRefreshing = false })
//            },{
//                swipeToRefresh.post({ swipeToRefresh.isRefreshing = false })
//            })
//        }
//
//        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
//            val view = convertView ?: LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
//            (view.findViewById(android.R.id.text1) as TextView).text = "${files[position].first.filename} ${files[position].second}"
//            view.background = ColorDrawable(ContextCompat.getColor(parent.context, R.color.bg_white))
//            view.tag = files[position].first
//            return view
//        }
//
//        override fun getItem(position: Int): Any? {
//            return files[position]
//        }
//
//        override fun getItemId(position: Int): Long {
//            return 0
//        }
//
//        override fun getCount(): Int {
//            return files.size
//        }
//
//        override fun unsubscribeRx() {
//            subscription?.unsubscribe()
//        }
//    }
}