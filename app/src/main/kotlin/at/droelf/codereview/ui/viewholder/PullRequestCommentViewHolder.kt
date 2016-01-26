package at.droelf.codereview.ui.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import at.droelf.codereview.R
import at.droelf.codereview.model.GithubModel
import at.droelf.codereview.ui.view.HtmlTextViewMagic
import at.droelf.codereview.utils.CircleTransform
import butterknife.Bind
import butterknife.ButterKnife
import com.squareup.picasso.Picasso
import org.sufficientlysecure.htmltextview.HtmlTextView

class PullRequestCommentViewHolder(val view: View): RecyclerView.ViewHolder(view) {

    @Bind(R.id.row_pr_comment_avatar) lateinit var avatar: ImageView
    @Bind(R.id.row_pr_name) lateinit var userName: TextView
    @Bind(R.id.row_pr_comment) lateinit var userComment: HtmlTextView
    //@Bind(R.id.row_pr_comment_webview) lateinit var webview: WebView

    init {
        ButterKnife.bind(this, view)
        avatar = view.findViewById(R.id.row_pr_comment_avatar) as ImageView
        userName = view.findViewById(R.id.row_pr_name) as TextView
        userComment = view.findViewById(R.id.row_pr_comment) as HtmlTextView
        //webview = view.findViewById(R.id.row_pr_comment_webview) as WebView
    }

    fun bind(comment: GithubModel.Comment){
//        val useWebView = comment.bodyHtml.contains("<table")
//        if(useWebView){
//            webview.visibility = View.VISIBLE
//            userComment.visibility = View.GONE
//        } else {
//            webview.visibility = View.GONE
//            userComment.visibility = View.VISIBLE
//        }
//
//        webview.clearCache(true)
        userComment.text = ""

        userName.text = "@${comment.user.login}"
        Picasso.with(view.context)
                .load(comment.user.avatarUrl)
                .transform(CircleTransform())
                .into(avatar)

//        if(!useWebView){
            HtmlTextViewMagic.apply(userComment, comment.bodyHtml)
//        } else {
//            webview.settings.javaScriptEnabled = true;
//            webview.setWebViewClient(object: WebViewClient() {
//                override fun onPageFinished(view: WebView, url: String) {
//                    webview.loadUrl("javascript:MyApp.resize(document.body.getBoundingClientRect().height)");
//                    super.onPageFinished(view, url);
//                }
//            });
//            webview.addJavascriptInterface(this, "MyApp")
//            webview.loadData(comment.bodyHtml, "text/html", "utf-8")
//        }
    }

//    @JavascriptInterface
//    fun resize(height: Float) {
//        Handler(Looper.getMainLooper())
//        Handler(Looper.getMainLooper()).post({
//                webview.layoutParams = FrameLayout.LayoutParams(view.context.resources.displayMetrics.widthPixels,
//                        (height * view.context.resources.displayMetrics.density).toInt());
//        });
//    }
}