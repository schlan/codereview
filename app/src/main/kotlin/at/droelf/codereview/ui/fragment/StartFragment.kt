package at.droelf.codereview.ui.fragment

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.TabLayout
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import at.droelf.codereview.R
import at.droelf.codereview.dagger.fragment.StartFragmentComponent
import at.droelf.codereview.dagger.fragment.StartFragmentModule
import at.droelf.codereview.model.GithubModel
import at.droelf.codereview.ui.activity.MainActivity
import at.droelf.codereview.ui.adapter.PullRequestViewpagerAdapter
import at.droelf.codereview.utils.CircleTransform
import com.squareup.picasso.Picasso
import rx.Subscription
import javax.inject.Inject

class StartFragment : BaseFragment<StartFragmentComponent>() {

    @Inject lateinit var controller: StartFragmentController
    lateinit var toolbar: Toolbar
    lateinit var tablayout: TabLayout
    lateinit var viewpager: ViewPager
    lateinit var progressbar: ProgressBar
    lateinit var swipeToRefresh: SwipeRefreshLayout

    lateinit var toolbarImage: ImageView
    lateinit var toolbarTitle: TextView
    lateinit var toolbarSubtitle: TextView
    lateinit var toolbarBackContainer: ViewGroup
    lateinit var toolbarPrStatus: TextView

    var subscription: Subscription? = null
    var viewPagerAdapter: PullRequestViewpagerAdapter? = null

    override fun injectComponent(component: StartFragmentComponent) {
        component.inject(this)
    }

    override fun createComponent(mainActivity: MainActivity): StartFragmentComponent? {
        return mainActivity.getOrInit().userComponent().plus(StartFragmentModule(this))
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_pr, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar = view.findViewById(R.id.pr_toolbar) as Toolbar
        tablayout = view.findViewById(R.id.pr_tablayout) as TabLayout
        viewpager = view.findViewById(R.id.pr_viewpager) as ViewPager
        progressbar = view.findViewById(R.id.pr_progressbar) as ProgressBar
        swipeToRefresh = view.findViewById(R.id.pr_swipe_to_refresh) as SwipeRefreshLayout

        toolbarImage = view.findViewById(R.id.pr_toolbar_avatar) as ImageView
        toolbarTitle = view.findViewById(R.id.toolbar_title) as TextView
        toolbarSubtitle = view.findViewById(R.id.toolbar_subtitle) as TextView
        toolbarBackContainer = view.findViewById(R.id.pr_toolbar_back) as ViewGroup
        toolbarPrStatus = view.findViewById(R.id.toolbar_pr_status) as TextView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        val actionbar = (activity as AppCompatActivity).supportActionBar
        actionbar?.setDisplayShowHomeEnabled(false)
        actionbar?.setDisplayShowCustomEnabled(true)
        actionbar?.setDisplayShowTitleEnabled(false)
        toolbarBackContainer.setOnClickListener{ activity.onBackPressed() }
        setHasOptionsMenu(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            android.R.id.home -> {
                activity.onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onStart() {
        super.onStart()
        val owner = arguments.getString("owner")
        val repo = arguments.getString("repo")
        val id = arguments.getLong("id")

        swipeToRefresh.isEnabled = false
        swipeToRefresh.setColorSchemeResources(R.color.colorAccent)

        progressbar.visibility = View.GONE
        swipeToRefresh.post({ swipeToRefresh.isRefreshing = true })
        subscription = controller.prdetails(activity, owner, repo, id).subscribe({ data ->
            val pr = data.first
            val status = data.second.sortedBy { it.updatedAt }.lastOrNull()
            initTabLayout(pr, status)

            Picasso.with(context).load(pr.user.avatarUrl).transform(CircleTransform()).into(toolbarImage)
            toolbarTitle.text = pr.title
            toolbarSubtitle.text = "@${pr.user.login}"

            swipeToRefresh.post({ swipeToRefresh.isRefreshing = false })
        }, {
            Snackbar.make(view!!, "Error: ${it.message}", Snackbar.LENGTH_LONG).show()
            progressbar.visibility = View.GONE
            swipeToRefresh.post({ swipeToRefresh.isRefreshing = false })
        })
    }

    override fun onStop() {
        super.onStop()
        subscription?.unsubscribe()
        subscription = null
        viewPagerAdapter?.unsubscribeRx()
    }

    fun initTabLayout(pr: GithubModel.PullRequestDetail, status: GithubModel.Status?) {
        tablayout.removeAllTabs()
        val commentsTab = tablayout.newTab()
        val filesTab = tablayout.newTab()
        commentsTab.text = "Comments"
        filesTab.text = "Files"


        tablayout.addTab(commentsTab, 0, true)
        tablayout.addTab(filesTab, 1, false)

        tablayout.setOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab) {
                viewpager.currentItem = tab.position
            }
        })

        viewPagerAdapter = PullRequestViewpagerAdapter(fragmentManager, controller, pr, status)
        viewpager.adapter = viewPagerAdapter
        viewpager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tablayout))
    }

}