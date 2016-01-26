package at.droelf.codereview.ui.viewholder

import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import at.droelf.codereview.R
import at.droelf.codereview.model.GithubModel
import at.droelf.codereview.ui.adapter.PullRequestFilesAdapter
import at.droelf.codereview.utils.CircleTransform
import com.squareup.picasso.Picasso

class PullRequestFileViewHolder(val view: View): ViewHolderBinder<PullRequestFilesAdapter.PullRequestFileViewHolderData>(view) {

    val title: TextView
    val commentCount: TextView

    val secondRowContainer: LinearLayout

//    val secondLine1: TextView
//    val secondLine2: TextView

    init {
        title = view.findViewById(R.id.row_pr_file_name) as TextView
        commentCount = view.findViewById(R.id.row_pr_file_count) as TextView
        secondRowContainer = view.findViewById(R.id.row_pr_file_second_row_container) as LinearLayout

//        secondLine1 = view.findViewById(R.id.row_pr_file_repo) as TextView
//        secondLine2 = view.findViewById(R.id.row_pr_file_user) as TextView
    }

    override fun bind(data: PullRequestFilesAdapter.PullRequestFileViewHolderData) {
        title.text = data.file.first.filename

        if(data.file.second > 0){
            commentCount.text = if(data.file.second < 100) data.file.second.toString() else "99+"
            commentCount.visibility = View.VISIBLE

            val size = view.context.resources.getDimensionPixelOffset(R.dimen.row_pr_file_avatar_size)
            val marginLeft = view.context.resources.getDimensionPixelOffset(R.dimen.row_pr_file_avatar_margin_left)

            data.file.third.distinctBy { it.user.id }.forEach { comment ->
                val img = ImageView(view.context)
                val params = LinearLayout.LayoutParams(size, size)
                params.leftMargin = marginLeft
                img.layoutParams = params
                secondRowContainer.addView(img)
                Picasso.with(view.context).load(comment.user.avatarUrl).transform(CircleTransform()).into(img)
            }

        } else {
            commentCount.visibility = View.GONE
        }

        initClickListener(data)
    }

    fun initClickListener(data: PullRequestFilesAdapter.PullRequestFileViewHolderData){
        if(data.file.first.changes > 0){
            view.setOnClickListener { view ->
                val file = data.file.first
                data.controller.showFile(
                        data.fm,
                        file.contentsUrl,
                        file.patch,
                        file.filename,
                        data.pr.base.repo.owner.login,
                        data.pr.base.repo.name,
                        data.pr.number
                )
            }
        }else {
            view.setOnClickListener(null)
        }

    }

}