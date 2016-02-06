package at.droelf.codereview.ui.fragment

import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.text.SpannableString
import android.util.DisplayMetrics
import android.view.*
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.ProgressBar
import at.droelf.codereview.R
import at.droelf.codereview.dagger.fragment.PatchFragmentComponent
import at.droelf.codereview.dagger.fragment.PatchFragmentModule
import at.droelf.codereview.ui.activity.MainActivity
import at.droelf.codereview.ui.adapter.PatchAdapter
import at.droelf.codereview.ui.adapter.PatchAdapterControllerImpl
import at.droelf.codereview.ui.listener.HidingScrollListener
import at.droelf.codereview.ui.view.HScrollView
import javax.inject.Inject

class PatchFragment : BaseFragment<PatchFragmentComponent>() {

    @Inject lateinit var controller: PatchFragmentController

    lateinit var main: ViewGroup
    lateinit var recyclerView: RecyclerView
    lateinit var recyclerViewBounds: ViewGroup
    lateinit var horizontalScrollView: HScrollView
    lateinit var progressbar: ProgressBar
    lateinit var toolbar: Toolbar

    var maxWidth: Int = -1
    var minWidth: Int = -1
    var hscrollEnabled: Boolean = false


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        main = view?.findViewById(R.id.main) as ViewGroup
        recyclerView = view?.findViewById(R.id.recyclerView) as RecyclerView
        recyclerViewBounds = view?.findViewById(R.id.recyclerViewBounds) as ViewGroup
        horizontalScrollView = view?.findViewById(R.id.horizontalScrollView) as HScrollView
        progressbar = view?.findViewById(R.id.progressbar) as ProgressBar
        toolbar = view?.findViewById(R.id.patch_toolbar) as Toolbar
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun injectComponent(component: PatchFragmentComponent) {
        component.inject(this)
    }

    override fun createComponent(mainActivity: MainActivity): PatchFragmentComponent? {
        return mainActivity.getOrInit().userComponent().plus(PatchFragmentModule(this))
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_code, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_code_hscroll -> {
                hscroll(!hscrollEnabled)
                return true
            }
            android.R.id.home -> {
                activity.onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()
        if(recyclerView.adapter == null) {
            progressbar.visibility = View.VISIBLE
            loadCode(
                    arguments.getString("url"),
                    arguments.getString("patch"),
                    arguments.getString("fname"),
                    arguments.getString("owner"),
                    arguments.getString("repo"),
                    arguments.getLong("pr"),
                    activity
            )
        }
        println("Load code")
    }

    fun loadCode(contentUrl: String, p: String, filename: String, owner: String, repo: String, pullRequest: Long, context: Context) {
        controller
                .data(activity, contentUrl, p, filename, owner, repo, pullRequest).subscribe({ result ->
            progressbar.visibility = View.GONE

            val maxLengthLine = result.fileContent.maxBy { it.length }
            maxWidth = maxLengthLine!!.lengthInPixel(context) + context.resources.getDimensionPixelOffset(R.dimen.code_text_margin_left)

            minWidth = when(main.width){
                0 -> {
                    val metrics = DisplayMetrics()
                    activity.windowManager.defaultDisplay.getMetrics(metrics)
                    metrics.widthPixels
                }
                else -> main.width
            }

            hscroll(hscrollEnabled)

            recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            recyclerView.adapter = PatchAdapter(PatchAdapterControllerImpl(result))

        }, { error ->
            println(error)
            error.printStackTrace()
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

    fun SpannableString.lengthInPixel(context: Context): Int {
        val p = Paint();
        p.typeface = Typeface.MONOSPACE
        p.textSize = context.resources.getDimension(R.dimen.code_text_size)
        return p.measureText(this, 0, length).toInt()
    }
}