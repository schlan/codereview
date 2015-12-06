package at.droelf.codereview.ui.fragment

import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.SpannableString
import android.view.*
import at.droelf.codereview.PatchAdapter
import at.droelf.codereview.PatchAdapterControllerImpl
import at.droelf.codereview.R
import at.droelf.codereview.dagger.fragment.PatchFragmentComponent
import at.droelf.codereview.dagger.fragment.PatchFragmentModule
import at.droelf.codereview.ui.activity.MainActivity
import butterknife.ButterKnife
import kotlinx.android.synthetic.fragment_main.*
import javax.inject.Inject

class PatchFragment : BaseFragment<PatchFragmentComponent>() {

    @Inject
    lateinit var controller: PatchFragmentController

    var maxWidth: Int = -1
    var minWidth: Int = -1
    var hscrollEnabled: Boolean = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_main, container, false)
        ButterKnife.bind(this, view)
        return view
    }

    override fun injectComponent(component: PatchFragmentComponent) {
        component.inject(this)
    }

    override fun createComponent(context: Context): PatchFragmentComponent {
        return (context as MainActivity).controller.userComponent().plus(PatchFragmentModule(this))
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
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        loadCode(
                arguments.getString("url"),
                arguments.getString("patch"),
                arguments.getString("fname"),
                activity
        )
    }

    fun loadCode(contentUrl: String, p: String, filename: String, context: Context) {
        controller.data(contentUrl, p, filename).subscribe({ result ->
            progressbar.visibility = View.GONE

            val maxLengthLine = result.fileContent.maxBy { it.length }
            maxWidth = maxLengthLine!!.lengthInPixel(context) + context.resources.getDimensionPixelOffset(R.dimen.code_text_margin_left)
            minWidth = main.width

            hscroll(hscrollEnabled)

            recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
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

    fun SpannableString.lengthInPixel(context: Context): Int {
        val p = Paint();
        p.setTypeface(Typeface.MONOSPACE)
        p.textSize = context.resources.getDimension(R.dimen.code_text_size)
        return p.measureText(this, 0, length).toInt()
    }
}