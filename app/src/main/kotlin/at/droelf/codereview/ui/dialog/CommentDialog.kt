package at.droelf.codereview.ui.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.view.*
import android.widget.EditText
import at.droelf.codereview.R
import at.droelf.codereview.dagger.fragment.CommentDialogModule
import at.droelf.codereview.ui.activity.MainActivity
import javax.inject.Inject

class CommentDialog: DialogFragment() {

    lateinit var input: EditText

    @Inject lateinit var controller: CommentDialogController

    companion object {
        fun startPrComment(fragmentManager: FragmentManager){
            CommentDialog().show(fragmentManager, "comment_dialog")
        }

        fun startReviewCommentReply(fragmentManager: FragmentManager){
            CommentDialog().show(fragmentManager, "comment_dialog")
        }

        fun startReviewCommentLine(fragmentManager: FragmentManager){
            CommentDialog().show(fragmentManager, "comment_dialog")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MainActivity).getOrInit().userComponent().plus(CommentDialogModule()).inject(this)

        controller.emojis().subscribe { fooBar ->
            println(fooBar)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val d = super.onCreateDialog(savedInstanceState)
        d.requestWindowFeature(Window.FEATURE_NO_TITLE)
        d.setCanceledOnTouchOutside(false)
        initHeight(d)
        return d
    }

    override fun onResume() {
        initWidth(dialog)
        input.isFocusable = true
        input.requestFocus()
        super.onResume()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog.window.attributes.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        return inflater.inflate(R.layout.dialog_comment, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        input = view?.findViewById(R.id.dialog_comment_input) as EditText
        screenLock(true)

        input.onFocusChangeListener = View.OnFocusChangeListener { p0, focused ->
            if(focused){
                dialog.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface?) {
        screenLock(false)
        super.onDismiss(dialog)
    }

    private fun screenLock(enable: Boolean){
        val flag = if(enable) ActivityInfo.SCREEN_ORIENTATION_NOSENSOR else ActivityInfo.SCREEN_ORIENTATION_SENSOR
        activity.requestedOrientation = flag
    }

    private fun initHeight(d: Dialog){
        val params = d.window.attributes
        params.height = WindowManager.LayoutParams.MATCH_PARENT
        params.gravity = Gravity.TOP
        params.y = context.resources.getDimensionPixelOffset(R.dimen.comment_dialog_margin_top)
        d.window.attributes = params
    }

    private fun initWidth(d: Dialog){
        val params = d.window.attributes
        params.width = WindowManager.LayoutParams.MATCH_PARENT
        d.window.attributes = params
    }

    private fun foo(){
        (activity as MainActivity).getOrInit()
    }

}