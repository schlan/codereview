package at.droelf.codereview

import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.SpannableString
import android.view.View
import at.droelf.codereview.network.GithubService
import at.droelf.codereview.network.RetrofitHelper
import at.droelf.codereview.patch.Patch
import kotlinx.android.synthetic.activity_main.*
import kotlin.text.Regex

class MainActivity : AppCompatActivity(), RetrofitHelper {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loadCode(
                getIntent().extras.getString("url"),
                getIntent().extras.getString("patch"),
                getIntent().extras.getString("fname")
        )
    }


    fun loadCode(contentUrl: String, p: String, filename: String) {
        object : AsyncTask<Void, Void, Pair<List<SpannableString>, Patch.Patch>>() {
            override fun doInBackground(vararg params: Void?): Pair<List<SpannableString>, Patch.Patch> {

                val fileType = filename.split(Regex("\\.")).last()
                val patch = Patch.parse(p)

                val file = GithubService.githubClient().file(contentUrl, "application/vnd.github.VERSION.raw+json")
                val rawFile = file.execute()

                return benchmark {
                    Pair(PrettyfyHighlighter.highlight(rawFile.body().string(), fileType), patch!!)
                }
            }

            override fun onPostExecute(result: Pair<List<SpannableString>, Patch.Patch>) {
                progressbar.visibility = View.GONE

                val patch = Patch.parse(Constants.patch)

                if(patch != null) {
                    recyclerView.layoutManager = LinearLayoutManager(applicationContext)
                    recyclerView.adapter = PatchAdapter(PatchAdapterControllerImpl(result.second, result.first))
                }
            }
        }.execute()
    }

    fun <T> benchmark(call: () -> T): T {
        val long = System.currentTimeMillis()
        val result: T = call()
        println("-------- Benchmark Call: ${System.currentTimeMillis() - long}")
        return result
    }
}