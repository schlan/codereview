package at.droelf.codereview.ui.viewholder

import android.support.v4.app.FragmentManager
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import at.droelf.codereview.R
import at.droelf.codereview.model.GithubModel
import at.droelf.codereview.ui.adapter.PullRequestFilesAdapter
import at.droelf.codereview.ui.fragment.StartFragmentController
import at.droelf.codereview.ui.view.FileChangesView
import at.droelf.codereview.utils.CircleTransform
import com.squareup.picasso.Picasso

class PullRequestFileViewHolder(val view: View): ViewHolderBinder<PullRequestFilesAdapter.PullRequestFileViewHolderData>(view) {

    val fileName: TextView
    val filePath: TextView

    val commentCountView: TextView
    val secondRowContainer: LinearLayout
    val commentContainer: View

    val fileChangesContainer: FrameLayout

    val renameContainer: View
    val renameFileName: TextView

    init {
        fileName = view.findViewById(R.id.row_pr_file_name) as TextView
        commentCountView = view.findViewById(R.id.row_pr_file_count) as TextView
        filePath = view.findViewById(R.id.row_pr_file_name_path) as TextView
        secondRowContainer = view.findViewById(R.id.row_pr_file_second_row_container) as LinearLayout
        fileChangesContainer = view.findViewById(R.id.row_pr_file_changes_view_container) as FrameLayout
        commentContainer = view.findViewById(R.id.row_pr_file_comment_container)

        renameContainer = view.findViewById(R.id.row_pr_file_rename_container)
        renameFileName = view.findViewById(R.id.row_pr_file_rename_name) as TextView
    }

    override fun bind(data: PullRequestFilesAdapter.PullRequestFileViewHolderData) {
        initTitle(data.file.first)
        initStateIndicator(data.file.first)
        initCommentRow(data.file.second, data.file.third, data.file.first)
        initClickListener(data.file.first, data.controller, data.fm, data.pr)
    }

    fun initCommentRow(commentCount: Int, reviewComments: List<GithubModel.ReviewComment>, file: GithubModel.PullRequestFile){
        secondRowContainer.removeAllViews()
        if(commentCount > 0){
            commentContainer.visibility = View.VISIBLE
            commentCountView.text = if(commentCount < 100) commentCount.toString() else "99+"
            commentCountView.visibility = View.VISIBLE

            val size = view.context.resources.getDimensionPixelOffset(R.dimen.row_pr_file_avatar_size)
            val marginLeft = view.context.resources.getDimensionPixelOffset(R.dimen.row_pr_file_avatar_margin_left)

            reviewComments
                    .filter { it.path == file.filename && it.position != null }
                    .distinctBy { it.user.id }
                    .forEach { comment ->
                        val img = ImageView(view.context)
                        val params = LinearLayout.LayoutParams(size, size)
                        params.leftMargin = marginLeft
                        img.layoutParams = params
                        secondRowContainer.addView(img)
                        Picasso.with(view.context).load(comment.user.avatarUrl).transform(CircleTransform()).into(img)
                    }

        } else {
            commentContainer.visibility = View.GONE
            commentCountView.visibility = View.GONE
        }
    }

    fun initStateIndicator(file: GithubModel.PullRequestFile){
        fileChangesContainer.removeAllViews()
        val size = view.context.resources.getDimensionPixelOffset(R.dimen.row_pr_file_file_change_indicator_image)
        val margin = view.context.resources.getDimensionPixelOffset(R.dimen.row_pr_file_file_change_indicator_box_margin)

        when(file.status) {
            "added" -> {
                setIndicatorIcon(size, margin, R.drawable.ic_add)
            }
            "removed" -> {
                setIndicatorIcon(size, margin, R.drawable.ic_remove)
            }
            "renamed" -> {
                if(file.changes > 0){
                    // files that are moved & modified
                    fileChangesContainer.addView(FileChangesView(view.context, file.additions, file.deletions, file.changes))
                }
            }
            "modified" -> {
                fileChangesContainer.addView(FileChangesView(view.context, file.additions, file.deletions, file.changes))
            }
        }
    }

    fun setIndicatorIcon(size: Int, margin: Int, drawable: Int){
        val view = ImageView(view.context)
        val layout = FrameLayout.LayoutParams(size, size)
        layout.rightMargin = margin
        layout.gravity = Gravity.CENTER
        view.layoutParams = layout
        fileChangesContainer.addView(view)
        Picasso.with(view.context).load(drawable).into(view)
    }

    fun initTitle(file: GithubModel.PullRequestFile) {
        val titleFileName = if(file.status == "renamed") file.previousFilename!! else file.filename

        if(file.status == "renamed"){
            renameContainer.visibility = View.VISIBLE
            renameFileName.text = file.filename
        } else {
            renameContainer.visibility = View.GONE
        }

        val lastSlash = titleFileName.indexOfLast { it == '/' }
        if(lastSlash == -1){
            fileName.text = titleFileName
        } else {
            fileName.text = titleFileName.substring(lastSlash + 1)
            filePath.text = titleFileName.substring(0, lastSlash + 1)
        }
    }


    fun initClickListener(file: GithubModel.PullRequestFile, controller: StartFragmentController, fm: FragmentManager, pr: GithubModel.PullRequestDetail){
        if(file.changes > 0){
            view.setOnClickListener { view ->
                controller.showFile(
                        fm,
                        file.contentsUrl,
                        file.patch,
                        file.filename,
                        pr.base.repo.owner.login,
                        pr.base.repo.name,
                        pr.number,
                        pr.head.sha,
                        file.filename
                )
            }
        }else {
            view.setOnClickListener(null)
        }
    }
}