package at.droelf.codereview.ui.fragment

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import at.droelf.codereview.R
import at.droelf.codereview.dagger.fragment.NotificationFragmentComponent
import at.droelf.codereview.dagger.fragment.NotificationFragmentModule
import at.droelf.codereview.model.GithubModel
import at.droelf.codereview.ui.activity.MainActivity
import at.droelf.codereview.ui.adapter.NotificationFragmentAdapter
import butterknife.Bind
import butterknife.ButterKnife
import jp.wasabeef.recyclerview.animators.SlideInRightAnimator
import okhttp3.HttpUrl
import rx.Observable
import javax.inject.Inject

class NotificationFragment: BaseFragment<NotificationFragmentComponent>() {

    @Inject lateinit var controller: NotificationFragmentController
    @Bind(R.id.notification_list) lateinit var list: RecyclerView

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

    override fun onStart() {
        super.onStart()
        list.layoutManager = LinearLayoutManager(activity)
        list.itemAnimator = SlideInRightAnimator()
        list.adapter = NotificationFragmentAdapter(controller.loadPrs(), controller, view, fragmentManager)
    }
}