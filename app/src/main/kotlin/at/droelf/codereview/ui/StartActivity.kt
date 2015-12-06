package at.droelf.codereview.ui

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import at.droelf.codereview.Constants
import at.droelf.codereview.Global
import at.droelf.codereview.R
import at.droelf.codereview.dagger.activity.ActivityScope
import at.droelf.codereview.dagger.activity.StartActivityComponent
import at.droelf.codereview.dagger.activity.StartActivityModule
import at.droelf.codereview.model.GithubModel
import at.droelf.codereview.model.Model
import at.droelf.codereview.network.GithubService
import at.droelf.codereview.ui.PatchActivity
import at.droelf.codereview.utils.RetrofitHelper
import javax.inject.Inject

class StartActivity : BaseActivity<StartActivityComponent>(), RetrofitHelper {

    @Inject
    lateinit var controller: StartActivityController

    var list: ListView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        Global.get(this).createUserComponent(Model.GithubAuth("e7cf96ea81ebca1445411b49ebea514f25592641"))
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_start)
        list = findViewById(R.id.listview) as? ListView

        controller.loadData(Constants.owner, Constants.repo, Constants.pullRequest)
                .subscribe ({ repos ->
                    list?.adapter = Adapter(repos)
                    list?.onItemClickListener = AdapterView.OnItemClickListener { adapter, view, pos, id ->
                        val file = (adapter.adapter as Adapter).getItem(pos)
                        val intent = Intent(this@StartActivity, PatchActivity::class.java)
                        intent.putExtra("url", file?.contentsUrl)
                        intent.putExtra("patch", file?.patch)
                        intent.putExtra("fname", file?.filename)
                        startActivity(intent)
                    }
                }, { error ->
                    error.printStackTrace()
                })
    }

    override fun injectComponent(component: StartActivityComponent) {
        component.inject(this)
    }

    override fun createComponent(): StartActivityComponent {
        return Global.get(this).userComponent().plus(StartActivityModule(this))
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