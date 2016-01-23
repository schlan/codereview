package at.droelf.codereview.ui.fragment

import android.os.Bundle
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
import butterknife.Bind
import butterknife.ButterKnife
import okhttp3.HttpUrl
import javax.inject.Inject

class NotificationFragment: BaseFragment<NotificationFragmentComponent>() {

    @Inject lateinit var controller: NotificationFragmentController
    @Bind(R.id.notification_list) lateinit var list: ListView

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
        controller.loadNotifications().subscribe { data ->
            list.adapter = NotificationListAdapter(data)
            list.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
                val notification = (view.tag as GithubModel.Notification)
                val id = HttpUrl.parse(notification.subject.url).pathSegments().last().toLong()
                controller.displayFileFragment(fragmentManager, notification.repository.owner.login, notification.repository.name, id)
            }
        }
    }

    class NotificationListAdapter(val repos: List<GithubModel.Notification>) : BaseAdapter() {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
            val view = convertView ?: LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
            (view.findViewById(android.R.id.text1) as TextView).text = repos[position].subject.title
            view.tag = repos[position]
            return view
        }

        override fun getItem(position: Int): GithubModel.Notification {
            return repos[position]
        }

        override fun getItemId(position: Int): Long {
            return 0L
        }

        override fun getCount(): Int {
            return repos.size
        }
    }


}