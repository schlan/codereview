package at.droelf.codereview.utils

import android.graphics.Color
import android.view.View
import android.widget.TextView
import at.droelf.codereview.R
import at.droelf.codereview.model.GithubModel
import at.droelf.codereview.model.ResponseHolder
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


interface UiHelper {

    fun timeStamp(textView: TextView, dateN: Date?) {
        val date = dateN ?: return
        val timeSpan = System.currentTimeMillis() - date.time
        if (timeSpan < TimeUnit.DAYS.toMillis(7)) {
            textView.text = HumanTime.approximately(System.currentTimeMillis() - date.time)
        } else {
            textView.text = SimpleDateFormat("dd MMM yyyy").format(date)
        }
    }

    fun colorForBuildStatus(state: String): Int {
        return when (state) {
            "pending" -> R.color.build_pending
            "failure" -> R.color.build_fail
            "success" -> R.color.build_pass
            else -> R.color.build_pending
        }
    }

    fun backgroundForBuildStatus(state: String): Int {
        return when (state) {
            "pending" -> R.drawable.background_build_pending
            "failure" -> R.drawable.background_build_fail
            "success" -> R.drawable.background_build_pass
            else -> R.drawable.background_build_pass
        }
    }

    fun colorForSource(source: ResponseHolder.Source): Int{
        return when (source) {
            ResponseHolder.Source.Memory -> Color.GREEN
            ResponseHolder.Source.Disc -> Color.YELLOW
            ResponseHolder.Source.Network -> Color.RED
        }
    }

    fun booleanToViewVisibilityFlag(visible: Boolean): Int {
        return if(visible) View.VISIBLE else View.GONE
    }
}
