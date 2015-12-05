package at.droelf.codereview

import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.SpannableString
import android.view.*
import at.droelf.codereview.dagger.activity.MainActivityModule
import at.droelf.codereview.model.GithubModel
import at.droelf.codereview.network.GithubService
import at.droelf.codereview.network.RetrofitHelper
import at.droelf.codereview.patch.Patch
import kotlinx.android.synthetic.activity_main.*
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject
import kotlin.text.Regex

class MainActivity : AppCompatActivity(), RetrofitHelper {

    @Inject
    lateinit var githubService: GithubService

    var maxWidth: Int = -1
    var minWidth: Int = -1
    var hscrollEnabled: Boolean = true
    var cacheFragment: CacheFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupDagger()
        setContentView(R.layout.activity_main)
        cacheFragment = cacheFragment()
        loadCode(
                intent.extras.getString("url"),
                intent.extras.getString("patch"),
                intent.extras.getString("fname")
        )
    }

    fun setupDagger(){
        Global.get(this).userComponent().plus(MainActivityModule(this)).inject(this)
    }

    fun cacheFragment(): CacheFragment {
        val tag = "cachefragment"
        val frag = supportFragmentManager.findFragmentByTag(tag) as? CacheFragment ?: initFragment(tag)
        frag.githubService = githubService
        return frag
    }

    fun initFragment(tag: String): CacheFragment{
        val fragment = CacheFragment()
        supportFragmentManager.beginTransaction().add(fragment, tag).commit()
        return fragment
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
        cacheFragment?.data(contentUrl, p, filename)?.subscribe({ result ->
                progressbar.visibility = View.GONE

                val maxLengthLine = result.fileContent.maxBy { it.length }
                maxWidth = maxLengthLine!!.lengthInPixel() + applicationContext.resources.getDimensionPixelOffset(R.dimen.code_text_margin_left)
                minWidth = main.width

                hscroll(hscrollEnabled)

                recyclerView.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
                recyclerView.adapter = PatchAdapter(PatchAdapterControllerImpl(result))
            },{ error ->
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


    class CacheFragment: Fragment() {

        lateinit var githubService: GithubService

        var observable: Observable<GithubDataSet>? = null

        override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            retainInstance = true
            return null
        }

        fun data(contentUrl: String, p: String, filename: String): Observable<GithubDataSet>? {
            if(observable == null) {
                val patchO = Patch.parse(p)
                val contentO = githubService.fileRx(contentUrl, "application/vnd.github.VERSION.raw+json").flatMap {
                    PrettyfyHighlighter.highlight(it.string(), filename.split(Regex("\\.")).last())
                }

                val commentsO = githubService.commentsRx(Constants.owner, Constants.repo, Constants.pullRequest)
                val commentsReviewO = githubService.reviewCommentsRx(Constants.owner, Constants.repo, Constants.pullRequest)


                observable = Observable.combineLatest(patchO, contentO, commentsO, commentsReviewO,  { patch, fileContent, comments, reviewComments ->
                    GithubDataSet(patch, fileContent, comments.toList(), reviewComments.toList())
                })
                        .cache()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.newThread())
            }
            return observable
        }
    }

    data class GithubDataSet(val patch: Patch.Patch, val fileContent: List<SpannableString>, val comments: List<GithubModel.Comment>, val reviewComments: List<GithubModel.ReviewComment>)
}