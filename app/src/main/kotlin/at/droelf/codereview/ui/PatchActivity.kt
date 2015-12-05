package at.droelf.codereview.ui

import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.SpannableString
import android.view.*
import at.droelf.codereview.*
import at.droelf.codereview.dagger.activity.PatchActivityComponent
import at.droelf.codereview.dagger.activity.PatchActivityModule
import at.droelf.codereview.model.GithubModel
import at.droelf.codereview.network.GithubService
import at.droelf.codereview.utils.RetrofitHelper
import at.droelf.codereview.patch.Patch
import at.droelf.codereview.utils.RxHelper
import kotlinx.android.synthetic.activity_main.*
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject
import kotlin.text.Regex

class PatchActivity : BaseActivity<PatchActivityComponent>() {

    @Inject
    lateinit var controller: PatchActivityController

    var maxWidth: Int = -1
    var minWidth: Int = -1
    var hscrollEnabled: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loadCode(
                intent.extras.getString("url"),
                intent.extras.getString("patch"),
                intent.extras.getString("fname")
        )
    }

    override fun injectComponent(component: PatchActivityComponent) {
        component.inject(this)
    }

    override fun createComponent(): PatchActivityComponent {
        return Global.get(this).userComponent().plus(PatchActivityModule(this))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_code, menu);
        return true;
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_code_hscroll -> {
                hscroll(!hscrollEnabled)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun loadCode(contentUrl: String, p: String, filename: String) {
        controller.data(contentUrl, p, filename).subscribe({ result ->
            progressbar.visibility = View.GONE

            val maxLengthLine = result.fileContent.maxBy { it.length }
            maxWidth = maxLengthLine!!.lengthInPixel() + applicationContext.resources.getDimensionPixelOffset(R.dimen.code_text_margin_left)
            minWidth = main.width

            hscroll(hscrollEnabled)

            recyclerView.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
            recyclerView.adapter = PatchAdapter(PatchAdapterControllerImpl(result))
        }, { error ->
            println(error)
        })
    }

    fun hscroll(active: Boolean) {
        if(active && maxWidth != -1){
            recyclerViewBounds.layoutParams.width = maxWidth
            horizontalScrollView.enableScrolling = true
            hscrollEnabled = true
        } else {
            recyclerViewBounds.layoutParams.width = minWidth
            horizontalScrollView.enableScrolling = false
            hscrollEnabled = false
        }
        recyclerView.requestLayout()
    }

    fun SpannableString.lengthInPixel(): Int {
        val p = Paint();
        p.setTypeface(Typeface.MONOSPACE)
        p.textSize = applicationContext.resources.getDimension(R.dimen.code_text_size)
        return p.measureText(this, 0, length).toInt()
    }
}