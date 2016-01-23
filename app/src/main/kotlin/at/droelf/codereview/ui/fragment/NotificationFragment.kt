package at.droelf.codereview.ui.fragment

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import at.droelf.codereview.R
import at.droelf.codereview.dagger.fragment.NotificationFragmentComponent
import at.droelf.codereview.dagger.fragment.NotificationFragmentModule
import at.droelf.codereview.ui.activity.MainActivity
import at.droelf.codereview.ui.adapter.NotificationFragmentAdapter
import at.droelf.codereview.ui.adapter.NotificationViewpagerAdapter
import butterknife.Bind
import butterknife.ButterKnife
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator
import javax.inject.Inject

class NotificationFragment: BaseFragment<NotificationFragmentComponent>() {

    @Inject lateinit var controller: NotificationFragmentController
    @Bind(R.id.notification_toolbar) lateinit var toolbar: Toolbar
    @Bind(R.id.notification_viewpager) lateinit var viewpager: ViewPager
    @Bind(R.id.notification_tablayout) lateinit var tablayout: TabLayout

    override fun injectComponent(component: NotificationFragmentComponent) {
        component.inject(this)
    }

    override fun createComponent(mainActivity: MainActivity): NotificationFragmentComponent {
        return mainActivity.controller.userComponent().plus(NotificationFragmentModule(this))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_notifications, container, false)
        ButterKnife.bind(this, view)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
    }

    override fun onStart() {
        super.onStart()

        val prTab = tablayout.newTab()
        prTab.setText("Pull Requests")
        val issuesTab = tablayout.newTab()
        issuesTab.setText("Issues")

        tablayout.addTab(prTab, 0, true)
        tablayout.addTab(issuesTab, 1, false)

        tablayout.setOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabSelected(tab: TabLayout.Tab) {
                viewpager.currentItem = tab.position
            }
        })

        viewpager.adapter = NotificationViewpagerAdapter(fragmentManager, controller)
        viewpager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tablayout))
    }
}