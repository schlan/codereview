package at.droelf.codereview.ui.fragment

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.support.v7.widget.Toolbar
import at.droelf.codereview.R
import at.droelf.codereview.dagger.fragment.RepositoryFragmentComponent
import at.droelf.codereview.dagger.fragment.RepositoryFragmentModule
import at.droelf.codereview.model.GithubModel
import at.droelf.codereview.ui.activity.MainActivity
import javax.inject.Inject

class RepositoryFragment: BaseFragment<RepositoryFragmentComponent>() {

    @Inject lateinit var controller: RepositoryFragmentController
    lateinit var listView: ListView
    lateinit var toolbar: Toolbar

    override fun injectComponent(component: RepositoryFragmentComponent) {
        component.inject(this)
    }

    override fun createComponent(mainActivity: MainActivity): RepositoryFragmentComponent? {
        return mainActivity.getOrInit().userComponent().plus(RepositoryFragmentModule(this))
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_repository, container, false)
        listView = view?.findViewById(R.id.repository_fragment_list) as ListView
        toolbar = view?.findViewById(R.id.repository_toolbar) as Toolbar
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
    }

    override fun onStart() {
        super.onStart()
        controller.loadRepositories().subscribe {
            listView.adapter = RepoListAdapter(it)
            listView.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
                val repo = (view.tag as GithubModel.Repository)
                Toast.makeText(context, repo.fullName, Toast.LENGTH_LONG).show()
            }
        }
    }

    class RepoListAdapter(val repos: List<GithubModel.Repository>) : BaseAdapter() {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
            val view = convertView ?: LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
            (view.findViewById(android.R.id.text1) as TextView).text = repos[position].fullName
            view.tag = repos[position]
            return view
        }

        override fun getItem(position: Int): GithubModel.Repository {
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