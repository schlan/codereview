package at.droelf.codereview.ui.viewholder

import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import at.droelf.codereview.R
import at.droelf.codereview.ui.adapter.PullRequestFilesAdapter
import at.droelf.codereview.ui.view.FileChangesView
import at.droelf.codereview.utils.CircleTransform
import com.squareup.picasso.Picasso

class PullRequestFileViewHolder(val view: View): ViewHolderBinder<PullRequestFilesAdapter.PullRequestFileViewHolderData>(view) {

    val fileName: TextView
    val filePath: TextView

    val commentCount: TextView
    val secondRowContainer: LinearLayout
    val fileChangesContainer: FrameLayout

    val renameContainer: View
    val renameFileName: TextView

    init {
        fileName = view.findViewById(R.id.row_pr_file_name) as TextView
        commentCount = view.findViewById(R.id.row_pr_file_count) as TextView
        filePath = view.findViewById(R.id.row_pr_file_name_path) as TextView
        secondRowContainer = view.findViewById(R.id.row_pr_file_second_row_container) as LinearLayout
        fileChangesContainer = view.findViewById(R.id.row_pr_file_changes_view_container) as FrameLayout

        renameContainer = view.findViewById(R.id.row_pr_file_rename_container)
        renameFileName = view.findViewById(R.id.row_pr_file_rename_name) as TextView
    }

    override fun bind(data: PullRequestFilesAdapter.PullRequestFileViewHolderData) {

        val titleFileName = if(data.file.first.status == "renamed") data.file.first.previousFilename!! else data.file.first.filename

        if(data.file.first.status == "renamed"){
            renameContainer.visibility = View.VISIBLE
            renameFileName.text = data.file.first.filename
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

        secondRowContainer.removeAllViews()
        fileChangesContainer.removeAllViews()
        if(data.file.first.changes > 0) {
            fileChangesContainer.addView(FileChangesView(view.context, data.file.first.additions, data.file.first.deletions, data.file.first.changes))
        }

        if(data.file.second > 0){
            commentCount.text = if(data.file.second < 100) data.file.second.toString() else "99+"
            commentCount.visibility = View.VISIBLE

            val size = view.context.resources.getDimensionPixelOffset(R.dimen.row_pr_file_avatar_size)
            val marginLeft = view.context.resources.getDimensionPixelOffset(R.dimen.row_pr_file_avatar_margin_left)

            data.file.third
                    .filter { it.path == data.file.first.filename && it.position != null }
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