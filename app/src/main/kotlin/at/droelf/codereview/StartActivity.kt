package at.droelf.codereview

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
import at.droelf.codereview.model.GithubModel
import at.droelf.codereview.network.GithubService
import at.droelf.codereview.network.RetrofitHelper

class StartActivity : AppCompatActivity(), RetrofitHelper {

    var list: ListView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        list = findViewById(R.id.listview) as? ListView

        GithubService.githubClient().pullRequestFiles(Constants.owner, Constants.repo, Constants.pullRequest).enqueue({ repos ->
            list?.adapter = Adapter(repos.body())
            list?.onItemClickListener = AdapterView.OnItemClickListener { adapter, view, pos, id ->
                val file = (adapter.adapter as Adapter).getItem(pos)
                val intent = Intent(this@StartActivity, MainActivity::class.java)
                intent.putExtra("url", file?.contentsUrl)
                intent.putExtra("patch", file?.patch)
                intent.putExtra("fname", file?.filename)
                startActivity(intent)
            }

        },{ error ->

        })
    }

    class Adapter(val list: Array<GithubModel.PullRequestFile>) : BaseAdapter(){

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