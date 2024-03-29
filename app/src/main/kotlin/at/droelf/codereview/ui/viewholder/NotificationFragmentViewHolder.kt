package at.droelf.codereview.ui.viewholder

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
import at.droelf.codereview.utils.UiHelper
import com.squareup.picasso.Picasso
import rx.Subscription
import timber.log.Timber
import timber.log.Timber.*

class NotificationFragmentViewHolder(val view: View):
        ViewHolderBinder<NotificationFragmentViewHolder.NotificationFragmentViewHolderData>(view), UiHelper {

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
        secondLine.text = data.pr.base?.repo?.fullName ?: ""
        user.text = "@${data.pr.user.login}"

        timeStamp(timeStamp, data.pr.updatedAt)
        initSourceIndicator(data)

        if(issueCount.visibility == View.GONE) {
            setLoading(true)
        }

        lazyLoadThings(data.pr, data.controller, data.upToDate, data.commander)
        view.setOnClickListener {
            data.controller.displayFileFragment(data.fm, data.pr.base.repo.owner.login, data.pr.base.repo.name, data.pr.number)
        }
    }

    private fun initSourceIndicator(data: NotificationFragmentViewHolderData) {
        sourceIndicator.setBackgroundColor(colorForSource(data.source))
    }

    fun lazyLoadThings(pr: GithubModel.PullRequest, controller: NotificationFragmentController, upToDate: Boolean, commander: NotificationFragmentAdapterCommander){
        issueCount.visibility = View.GONE
        avatarBackground.background = null
        if(pr.base == null) return

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
                    e("Error during lazy loading data :(", error)
                }, {
                    setLoading(false)
                })
    }

    private fun initAvatarBackground(status: List<GithubModel.Status>, prDetail: GithubModel.PullRequestDetail) {
        val lastStatus = status.sortedBy { it.updatedAt }.lastOrNull()

        val color = if(lastStatus != null) {
            backgroundForBuildStatus(lastStatus.state)

        } else if(prDetail.mergeable.toBoolean()){
            R.drawable.background_build_pass

        } else {
            R.drawable.background_build_fail

        }

        avatarBackground.setBackgroundResource(color)
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