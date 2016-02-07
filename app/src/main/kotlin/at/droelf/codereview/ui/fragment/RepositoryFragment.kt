package at.droelf.codereview.ui.fragment

import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import at.droelf.codereview.R
import at.droelf.codereview.dagger.fragment.RepositoryFragmentComponent
import at.droelf.codereview.dagger.fragment.RepositoryFragmentModule
import at.droelf.codereview.ui.activity.MainActivity
import at.droelf.codereview.ui.adapter.RepositoryFragmentAdapter
import at.droelf.codereview.ui.view.DividerItemDecoration
import javax.inject.Inject

class RepositoryFragment: BaseFragment<RepositoryFragmentComponent>() {

    @Inject lateinit var controller: RepositoryFragmentController
    lateinit var list: RecyclerView
    lateinit var toolbar: Toolbar
    lateinit var swipeToRefresh: SwipeRefreshLayout

    override fun injectComponent(component: RepositoryFragmentComponent) {
        component.inject(this)
    }

    override fun createComponent(mainActivity: MainActivity): RepositoryFragmentComponent? {
        return mainActivity.getOrInit().userComponent().plus(RepositoryFragmentModule(this))
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_repository, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        list = view?.findViewById(R.id.repository_fragment_list) as RecyclerView
        toolbar = view?.findViewById(R.id.repository_toolbar) as Toolbar
        swipeToRefresh = view?.findViewById(R.id.repository_fragment_swipe_to_refresh) as SwipeRefreshLayout
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
    }

    override fun onStart() {
        super.onStart()
        swipeToRefresh.isEnabled = false
        swipeToRefresh.setColorSchemeResources(R.color.colorAccent)
        list.layoutManager = LinearLayoutManager(activity)
        list.addItemDecoration(DividerItemDecoration(context, resources.getDimensionPixelOffset(R.dimen.row_notification_pull_request_divider_padding_left)))
        list.adapter = RepositoryFragmentAdapter(controller.loadRepositories(), controller, swipeToRefresh)
    }

    override fun onDestroy() {
        super.onDestroy()
        controller.realm.close()
    }
}