package at.droelf.codereview.ui.viewholder

import android.graphics.Color
import android.support.v4.app.FragmentManager
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import at.droelf.codereview.R
import at.droelf.codereview.model.GithubModel
import at.droelf.codereview.model.ResponseHolder
import at.droelf.codereview.ui.adapter.NotificationFragmentAdapterCommander
import at.droelf.codereview.ui.fragment.NotificationFragmentController
import at.droelf.codereview.utils.CircleTransform
import at.droelf.codereview.utils.HumanTime
import com.squareup.picasso.Picasso
import rx.Subscription
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit

class NotificationFragmentViewHolder(val view: View): ViewHolderBinder<NotificationFragmentViewHolder.NotificationFragmentViewHolderData>(view) {

    val avatar: ImageView = view.findViewById(R.id.row_notification_avatar) as ImageView
    val title: TextView = view.findViewById(R.id.row_notification_name) as TextView
    val secondLine: TextView = view.findViewById(R.id.row_notification_repo) as TextView
    val timeStamp: TextView = view.findViewById(R.id.row_notification_timestamp) as TextView
    val user: TextView = view.findViewById(R.id.row_notification_user) as TextView
    val issueCount: TextView = view.findViewById(R.id.row_notification_issue_count) as TextView
    val avatarBackground: RelativeLayout = view.findViewById(R.id.row_notification_avatar_background) as RelativeLayout
    val main: ViewGroup = view.findViewById(R.id.row_notification_background) as ViewGroup

    val progressbarBackground: View = view.findViewById(R.id.row_notification_avatar_progressbar_background)
    val progressbar: ProgressBar = view.findViewById(R.id.row_notification_avatar_progressbar) as ProgressBar
    val sourceIndicator: View = view.findViewById(R.id.row_notification_source_indicator)

    var subscription: Subscription? = null

    override fun bind(data: NotificationFragmentViewHolderData) {
        Picasso.with(view.context)
                .load(data.pr.user.avatarUrl)
                .transform(CircleTransform())
                .into(avatar)

        title.text = data.pr.title
        secondLine.text = data.pr.base.repo.fullName
        user.text = "@${data.pr.user.login}"

        initTimeStamp(data)
        initSourceIndicator(data)

        if(issueCount.visibility == View.GONE) {
            setLoading(true)
        }

        lazyLoadThings(data.pr, data.controller, data.upToDate, data.commander)
        view.setOnClickListener {
            data.controller.displayFileFragment(data.fm, data.pr.base.repo.owner.login, data.pr.base.repo.name, data.pr.number)
        }
    }

    private fun initTimeStamp(data: NotificationFragmentViewHolderData) {
        val timeSpan = System.currentTimeMillis() - data.pr.updatedAt.time
        if (timeSpan < TimeUnit.DAYS.toMillis(7)) {
            timeStamp.text = HumanTime.approximately(System.currentTimeMillis() - data.pr.updatedAt.time)
        } else {
            timeStamp.text = SimpleDateFormat("dd MMM yyyy").format(data.pr.updatedAt)
        }
    }

    private fun initSourceIndicator(data: NotificationFragmentViewHolderData) {
        val color = when (data.source) {
            ResponseHolder.Source.Memory -> Color.GREEN
            ResponseHolder.Source.Disc -> Color.YELLOW
            ResponseHolder.Source.Network -> Color.RED
        }
        sourceIndicator.setBackgroundColor(color)
    }

    fun lazyLoadThings(pr: GithubModel.PullRequest, controller: NotificationFragmentController, upToDate: Boolean, commander: NotificationFragmentAdapterCommander){
        issueCount.visibility = View.GONE
        avatarBackground.background = null
        subscription = controller.lazyLoadDataForPr(pr)
                .retry()
                .subscribe ({ data ->
                    val prDetail = data.first

                    if(prDetail.merged || prDetail.state == "closed"){
                        commander.removeItem(pr)

                    } else {
                        prDetail.merged

                        val comments = prDetail.reviewComments + prDetail.comments
                        val issueCountString = when {
                            comments > 0 && comments < 100 -> "$comments"
                            comments > 99 -> "99+"
                            else -> null
                        }
                        issueCount.visibility = if (issueCountString != null) View.VISIBLE else View.GONE
                        issueCount.text = issueCountString
                        initAvatarBackground(data.second, prDetail)
                    }

                }, { error ->
                    error.printStackTrace()
                    println("Error during lazy loading data :(")
                }, {
                    setLoading(!upToDate)
                })
    }

    private fun initAvatarBackground(status: List<GithubModel.Status>, prDetail: GithubModel.PullRequestDetail) {
        val background: Int = if (status.isNotEmpty()) {
            val lastStatus = status.sortedBy { it.updatedAt }.last()
            when (lastStatus.state) {
                "pending" -> R.drawable.background_build_pending
                "failure" -> R.drawable.background_build_fail
                "success" -> R.drawable.background_build_pass
                else -> {
                    if (prDetail.mergeable.toBoolean()) {
                        R.drawable.background_build_pass
                    } else {
                        R.drawable.background_build_fail
                    }
                }
            }
        } else {
            if (prDetail.mergeable.toBoolean()) {
                R.drawable.background_build_pass
            } else {
                R.drawable.background_build_fail
            }
        }

        avatarBackground.setBackgroundResource(background)
    }


    fun setLoading(loading: Boolean){
        val flag = if(loading) View.VISIBLE else View.GONE
        progressbar.visibility = flag
        progressbarBackground.visibility = flag
    }

    fun pause() {
        subscription?.unsubscribe()
    }

    data class NotificationFragmentViewHolderData(
            val pr: GithubModel.PullRequest,
            val fm: FragmentManager,
            val controller: NotificationFragmentController,
            val source: ResponseHolder.Source,
            val upToDate: Boolean,
            val commander: NotificationFragmentAdapterCommander
    )

}