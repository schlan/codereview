package at.droelf.codereview.ui.fragment

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.*
import at.droelf.codereview.R
import at.droelf.codereview.dagger.fragment.NotificationFragmentComponent
import at.droelf.codereview.dagger.fragment.NotificationFragmentModule
import at.droelf.codereview.ui.activity.MainActivity
import at.droelf.codereview.ui.adapter.NotificationViewpagerAdapter
import javax.inject.Inject

class NotificationFragment: BaseFragment<NotificationFragmentComponent>() {

    @Inject lateinit var controller: NotificationFragmentController
    lateinit var toolbar: Toolbar
    lateinit var viewpager: ViewPager
    lateinit var tablayout: TabLayout

    var viewpagerAdapter: NotificationViewpagerAdapter? = null

    override fun injectComponent(component: NotificationFragmentComponent) {
        component.inject(this)
    }

    override fun createComponent(mainActivity: MainActivity): NotificationFragmentComponent? {
        return mainActivity.getOrInit().userComponent().plus(NotificationFragmentModule(this))
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_notifications, container, false)
        setHasOptionsMenu(true)
        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar = view?.findViewById(R.id.notification_toolbar) as Toolbar
        viewpager = view?.findViewById(R.id.notification_viewpager) as ViewPager
        tablayout = view?.findViewById(R.id.notification_tablayout) as TabLayout
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_notifications, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.menu_notificaiton_configure -> {
                controller.displayRepoSettingsFragment(fragmentManager)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onStart() {
        super.onStart()

        if(tablayout.tabCount != 2) {

            val prTab = tablayout.newTab()
            prTab.text = "Pull Requests"
            val issuesTab = tablayout.newTab()
            issuesTab.text = "Issues"

            tablayout.addTab(prTab, 0, true)
            tablayout.addTab(issuesTab, 1, false)

            tablayout.setOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabReselected(tab: TabLayout.Tab?) {
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                }

                override fun onTabSelected(tab: TabLayout.Tab) {
                    viewpager.currentItem = tab.position
                }
            })

            viewpagerAdapter = NotificationViewpagerAdapter(fragmentManager, controller)
            viewpager.adapter = viewpagerAdapter
            viewpager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tablayout))
        }
    }

    override fun onStop() {
        super.onStop()
        viewpagerAdapter?.unsubscribeRx()
        viewpagerAdapter = null
    }

    override fun onDestroy() {
        super.onDestroy()
        //viewpager?.adapter = null
    }
}