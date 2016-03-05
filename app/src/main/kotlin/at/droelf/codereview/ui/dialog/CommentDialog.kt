package at.droelf.codereview.ui.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import at.droelf.codereview.R
import at.droelf.codereview.dagger.fragment.CommentDialogModule
import at.droelf.codereview.ui.activity.MainActivity
import com.jakewharton.rxbinding.widget.RxTextView
import com.squareup.picasso.Picasso
import javax.inject.Inject

class CommentDialog: DialogFragment() {

    lateinit var input: EditText

    lateinit var emojiList: RecyclerView
    lateinit var listContainer: View
    lateinit var listProgressBar: ProgressBar

    lateinit var emojiButton: ImageView

    lateinit var sendButton: FloatingActionButton

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
        super.onResume()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog.window.attributes.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        return inflater.inflate(R.layout.dialog_comment, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        screenLock(true)
        val view = view ?: return

        input = view.findViewById(R.id.dialog_comment_input) as EditText

        emojiList = view.findViewById(R.id.dialog_comment_list) as RecyclerView
        listContainer = view.findViewById(R.id.dialog_comment_list_container) as View
        listProgressBar = view.findViewById(R.id.dialog_comment_list_progressbar) as ProgressBar

        sendButton = view.findViewById(R.id.dialog_comment_send) as FloatingActionButton

        emojiButton = view.findViewById(R.id.dialog_comment_emoji_button) as ImageView

        input.onFocusChangeListener = View.OnFocusChangeListener { p0, focused ->
            if(focused){
                dialog.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
            }
        }

        sendButton.hide()

        emojiButton.setOnClickListener {
            if(listContainer.visibility == View.GONE){

                listContainer.visibility = View.VISIBLE
                listProgressBar.visibility = View.VISIBLE

                controller.emojis().subscribe { fooBar ->
                    listProgressBar.visibility = View.GONE
                    emojiList.layoutManager = GridLayoutManager(context, 4, GridLayoutManager.HORIZONTAL, false)
                    emojiList.adapter = EmojiAdapter(fooBar, input)
                }

            } else {
                listContainer.visibility = View.GONE

            }
        }

        RxTextView.textChangeEvents(input).subscribe { text ->
            if(text.text().length > 0){
                sendButton.show()

            } else {
                sendButton.hide()
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface?) {
        screenLock(false)
        super.onDismiss(dialog)
    }

    private fun screenLock(enable: Boolean){
        val  flag = if(enable) ActivityInfo.SCREEN_ORIENTATION_NOSENSOR else ActivityInfo.SCREEN_ORIENTATION_SENSOR
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

    class EmojiAdapter(val emoji: Map<String, String>, val input: EditText): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

        val emojiList: List<Pair<String, String>>

        init {
            emojiList = emoji.toList()
        }

        override fun getItemCount(): Int {
            return emojiList.size
        }

        override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
            val emoji = emojiList[p1]
            val imageView = p0.itemView.findViewById(R.id.row_comment_dialog_icon) as ImageView
            Picasso.with(imageView.context).load(emoji.second).into(imageView)

            p0.itemView.findViewById(R.id.row_comment_dialog).setOnClickListener {
                input.append(":${emojiList[p1].first}:")
            }
        }

        override fun onCreateViewHolder(p0: ViewGroup?, p1: Int): RecyclerView.ViewHolder? {
            val view = LayoutInflater.from(p0!!.context).inflate(R.layout.row_dialog_emoji, p0, false)
            return object : RecyclerView.ViewHolder(view) {}
        }
    }

}