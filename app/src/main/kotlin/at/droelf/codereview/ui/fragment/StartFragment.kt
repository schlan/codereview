package at.droelf.codereview.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import at.droelf.codereview.Constants
import at.droelf.codereview.R
import at.droelf.codereview.dagger.fragment.StartFragmentComponent
import at.droelf.codereview.dagger.fragment.StartFragmentModule
import at.droelf.codereview.model.GithubModel
import at.droelf.codereview.ui.activity.MainActivity
import butterknife.Bind
import butterknife.ButterKnife
import javax.inject.Inject

class StartFragment : BaseFragment<StartFragmentComponent>() {

    @Inject lateinit var controller: StartFragmentController
    @Bind(R.id.listview) lateinit var listView: ListView

    private fun loadFiles(context: Context) {
        controller.loadData(Constants.owner, Constants.repo, Constants.pullRequest).subscribe ({ repos ->
            listView.adapter = Adapter(repos)
            listView.onItemClickListener = AdapterView.OnItemClickListener { adapter, view, pos, id ->
                val file = (adapter.adapter as Adapter).getItem(pos)
                controller.showFile(fragmentManager, file?.contentsUrl, file?.patch, file?.filename)
//                val intent = Intent(context, PatchFragment::class.java)
//                intent.putExtra("url", file?.contentsUrl)
//                intent.putExtra("patch", file?.patch)
//                intent.putExtra("fname", file?.filename)
//                startActivity(intent)
            }
        }, { error ->
            error.printStackTrace()
        })
    }

    override fun onStart() {
        super.onStart()
        loadFiles(activity)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_start, container, false)
        ButterKnife.bind(this, view)
        listView = view.findViewById(R.id.listview) as ListView
        return view
    }

    override fun injectComponent(component: StartFragmentComponent) {
        component.inject(this)
    }

    override fun createComponent(mainActivity: MainActivity): StartFragmentComponent {
        return mainActivity.controller.userComponent().plus(StartFragmentModule(this))
    }

    class Adapter(val list: List<GithubModel.PullRequestFile>) : BaseAdapter() {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
            (view.findViewById(android.R.id.text1) as TextView).text = list.get(position).filename
            return view
        }

        override fun getItem(position: Int): GithubModel.PullRequestFile? {
            return list.get(position)
        }

        override fun getItemId(position: Int): Long {
            return 0
        }

        override fun getCount(): Int {
            return list.size
        }
    }

}