package at.droelf.codereview

import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.SpannableString
import android.view.View
import at.droelf.codereview.network.RetrofitHelper
import at.droelf.codereview.patch.Patch
import kotlinx.android.synthetic.activity_main.*
import syntaxhighlight.ParseResult

class MainActivity : AppCompatActivity(), RetrofitHelper {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadCode()
        println()
    }


    fun loadCode() {
        object : AsyncTask<Void, Void, List<SpannableString>>() {
            override fun doInBackground(vararg params: Void?): List<SpannableString> {
                return benchmark {
                    PrettyfyHighlighter.highlight(Constants.rawFile, "java")
                }
            }

            override fun onPostExecute(result: List<SpannableString>) {
                progressbar.visibility = View.GONE

                val patch = Patch.parse(Constants.patch)

                if(patch != null) {
                    recyclerView.layoutManager = LinearLayoutManager(applicationContext)
                    recyclerView.adapter = PatchAdapter(patch, result)
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