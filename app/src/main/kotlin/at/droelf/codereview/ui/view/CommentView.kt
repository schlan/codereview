package at.droelf.codereview.ui.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.Image
import android.support.v7.widget.CardView
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import at.droelf.codereview.R
import at.droelf.codereview.model.Model
import at.droelf.codereview.utils.CircleTransform
import com.squareup.picasso.Picasso
import org.jsoup.Jsoup
import org.sufficientlysecure.htmltextview.HtmlTagHandler
import org.sufficientlysecure.htmltextview.HtmlTextView
import org.sufficientlysecure.htmltextview.LocalLinkMovementMethod
import java.lang.ref.WeakReference


class CommentView(val comment: Model.ReviewComment, context: Context) : FrameLayout(context) {

    lateinit var commentView: HtmlTextView
    lateinit var commentTitle: TextView
    lateinit var avatar: ImageView

    lateinit var container: LinearLayout
    lateinit var cardView: CardView

    init {
        LayoutInflater.from(context).inflate(R.layout.row_patchadapter_comment_single, this, true)
        commentView = findViewById(R.id.row_patch_comment) as HtmlTextView
        commentTitle = findViewById(R.id.row_patch_name) as TextView
        avatar = findViewById(R.id.row_patch_comment_avatar) as ImageView
        container = findViewById(R.id.row_patch_comment_card_container) as LinearLayout
        cardView = findViewById(R.id.row_patch_comment_card) as CardView
        commentTitle.text = comment.user.login


        commentView.text = trimTrailingWhiteSpace(Html.fromHtml(comment.bodyHtml, PicassoImageGetter(commentView), HtmlTagHandler()))
        commentView.movementMethod = LocalLinkMovementMethod.getInstance()

        Picasso.with(context)
                .load(comment.user.avatarUrl)
                .transform(CircleTransform())
                .into(avatar)
    }

    fun trimTrailingWhiteSpace(source: CharSequence): CharSequence {
        var i  = source.length
        while(--i >= 0 && source.get(i) == '\n'){}
        return source.subSequence(0, i + 1)
    }

    fun first(){
        val params = (cardView.layoutParams as FrameLayout.LayoutParams)
        params.topMargin = context.resources.getDimensionPixelSize(R.dimen.row_patchadapter_comment_padding_top)
    }

    fun last(){
        val params = (cardView.layoutParams as FrameLayout.LayoutParams)
        params.bottomMargin = context.resources.getDimensionPixelSize(R.dimen.row_patchadapter_comment_padding_bottom)
    }

    class HtmlDrawable : BitmapDrawable() {

        var drawable: Drawable? = null

        override fun draw(canvas: Canvas?) {
            drawable?.draw(canvas)
        }
    }

    class PicassoImageGetter(val container: TextView) : Html.ImageGetter {
        override fun getDrawable(source: String?): Drawable? {
            val drawable = HtmlDrawable()
            Loader(container.context, container, drawable).getDrawable(source)
            return drawable
        }
    }

    class Loader(val context: Context, container: TextView, drawable: HtmlDrawable)  {

        val drawableReference: WeakReference<HtmlDrawable> = WeakReference(drawable)
        val containerReference: WeakReference<TextView> = WeakReference(container)

        fun getDrawable(source: String?) {
            Picasso.with(context)
                    .load(source)
                    .into(object: com.squareup.picasso.Target{
                        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}

                        override fun onBitmapFailed(errorDrawable: Drawable?) {}

                        override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom?) {
                            if(containerReference.get() == null) return
                            val rect = Rect()
                            containerReference.get().paint.getTextBounds("ABC", 0, 3, rect)
                            val height = (rect.height() * 1.5).toInt()

                            val d = BitmapDrawable(context.resources, bitmap)
                            d.setBounds(0, 0, height, height)

                            if(drawableReference.get() == null) return

                            drawableReference.get().setBounds(0, 0, height, height)
                            drawableReference.get().drawable = d

                            containerReference.get().invalidate()
                        }
                    })
        }
    }
}
