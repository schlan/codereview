package at.droelf.codereview.ui.fragment

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import at.droelf.codereview.R
import at.droelf.codereview.dagger.fragment.StartFragmentComponent
import at.droelf.codereview.dagger.fragment.StartFragmentModule
import at.droelf.codereview.model.GithubModel
import at.droelf.codereview.ui.activity.MainActivity
import at.droelf.codereview.ui.adapter.PullRequestViewpagerAdapter
import butterknife.Bind
import butterknife.ButterKnife
import rx.Subscription
import javax.inject.Inject

class StartFragment : BaseFragment<StartFragmentComponent>() {

    @Inject lateinit var controller: StartFragmentController
    @Bind(R.id.pr_toolbar) lateinit var toolbar: Toolbar
    @Bind(R.id.pr_tablayout) lateinit var tablayout: TabLayout
    @Bind(R.id.pr_viewpager) lateinit var viewpager: ViewPager
    @Bind(R.id.pr_progressbar) lateinit var progressbar: ProgressBar
    @Bind(R.id.pr_swipe_to_refresh) lateinit var swipeToRefresh: SwipeRefreshLayout

    var subscription: Subscription? = null
    var viewPagerAdater: PullRequestViewpagerAdapter? = null

    override fun injectComponent(component: StartFragmentComponent) {
        component.inject(this)
    }

    override fun createComponent(mainActivity: MainActivity): StartFragmentComponent {
        return mainActivity.controller.userComponent().plus(StartFragmentModule(this))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_pr, container, false)
        ButterKnife.bind(this, view)
        toolbar = view.findViewById(R.id.pr_toolbar) as Toolbar
        tablayout = view.findViewById(R.id.pr_tablayout) as TabLayout
        viewpager = view.findViewById(R.id.pr_viewpager) as ViewPager
        progressbar = view.findViewById(R.id.pr_progressbar) as ProgressBar
        swipeToRefresh = view.findViewById(R.id.pr_swipe_to_refresh) as SwipeRefreshLayout
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar.setDisplayHomeAsUpEnabled(true)
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
        subscription = controller.prdetails(activity, owner, repo, id).subscribe({ pr ->
            initTabLayout(pr)
            swipeToRefresh.post({ swipeToRefresh.isRefreshing = false })
        }, {
            Snackbar.make(view, "Error: ${it.message}", Snackbar.LENGTH_LONG).show()
            progressbar.visibility = View.GONE
            swipeToRefresh.post({ swipeToRefresh.isRefreshing = false })
        })
    }

    override fun onStop() {
        super.onStop()
        subscription?.unsubscribe()
        subscription = null
        viewPagerAdater?.unsubscribeRx()
    }

    fun initTabLayout(pr: GithubModel.PullRequestDetail) {
        tablayout.removeAllTabs()
        val commentsTab = tablayout.newTab()
        commentsTab.setText("Comments")
        val filesTab = tablayout.newTab()
        filesTab.setText("Files")

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

        viewPagerAdater = PullRequestViewpagerAdapter(fragmentManager, controller, pr)
        viewpager.adapter = viewPagerAdater
        viewpager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tablayout))
    }

}