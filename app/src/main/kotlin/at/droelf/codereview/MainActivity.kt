package at.droelf.codereview

import android.graphics.Paint
import android.graphics.Typeface
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
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
                intent.extras.getString("url"),
                intent.extras.getString("patch"),
                intent.extras.getString("fname")
        )
    }

    fun loadCode(contentUrl: String, p: String, filename: String) {
        object : AsyncTask<Void, Void, Pair<List<SpannableString>, Patch.Patch>>() {
            override fun doInBackground(vararg params: Void?): Pair<List<SpannableString>, Patch.Patch> {

                val patch = Patch.parse(p)
                val rawFile = GithubService.githubClient().file(contentUrl, "application/vnd.github.VERSION.raw+json").execute()

                return benchmark {
                    val fileType = filename.split(Regex("\\.")).last()
                    Pair(PrettyfyHighlighter.highlight(rawFile.body().string(), fileType), patch!!)
                }
            }

            override fun onPostExecute(result: Pair<List<SpannableString>, Patch.Patch>) {
                progressbar.visibility = View.GONE

                val patch = Patch.parse(Constants.patch)
                val maxLengthLine = result.first.maxBy { it.length }
                recyclerViewBounds.layoutParams.width = maxLengthLine!!.lengthInPixel() + applicationContext.resources.getDimensionPixelOffset(R.dimen.code_text_margin_left)

                if(patch != null) {

                    recyclerView.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
                    recyclerView.adapter = PatchAdapter(PatchAdapterControllerImpl(result.second, result.first))
                }
            }
        }.execute()
    }

    fun SpannableString.lengthInPixel(): Int {
        val p = Paint();
        p.setTypeface(Typeface.MONOSPACE)
        p.textSize = applicationContext.resources.getDimension(R.dimen.code_text_size)
        return p.measureText(this, 0, length).toInt()
    }

    fun <T> benchmark(call: () -> T): T {
        val long = System.currentTimeMillis()
        val result: T = call()
        println("-------- Benchmark Call: ${System.currentTimeMillis() - long}")
        return result
    }
}