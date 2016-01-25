package at.droelf.codereview.ui.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.text.Html
import android.widget.TextView
import com.squareup.picasso.Picasso
import org.sufficientlysecure.htmltextview.HtmlTagHandler
import org.sufficientlysecure.htmltextview.LocalLinkMovementMethod
import java.lang.ref.WeakReference

object HtmlTextViewMagic {

    fun apply(textView: TextView, text: String) {
        textView.text = trimTrailingWhiteSpace(Html.fromHtml(text, PicassoImageGetter(textView), HtmlTagHandler()))
        textView.movementMethod = LocalLinkMovementMethod.getInstance()
    }

    private fun trimTrailingWhiteSpace(source: CharSequence): CharSequence {
        var i = source.length
        while (--i >= 0 && source.get(i) == '\n') {
        }
        return source.subSequence(0, i + 1)
    }

    private class PicassoImageGetter(val container: TextView) : Html.ImageGetter {
        override fun getDrawable(source: String?): Drawable? {
            val drawable = HtmlDrawable()
            Loader(container.context, container, drawable).getDrawable(source)
            return drawable
        }
    }

    private class Loader(val context: Context, container: TextView, drawable: HtmlDrawable) {

        val drawableReference: WeakReference<HtmlDrawable> = WeakReference(drawable)
        val containerReference: WeakReference<TextView> = WeakReference(container)

        fun getDrawable(source: String?) {
            Picasso.with(context)
                    .load(source)
                    .into(object : com.squareup.picasso.Target {
                        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                        }

                        override fun onBitmapFailed(errorDrawable: Drawable?) {
                        }

                        override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom?) {
                            if (containerReference.get() == null) return
                            val rect = Rect()
                            containerReference.get().paint.getTextBounds("ABC", 0, 3, rect)
                            val height = (rect.height() * 1.5).toInt()

                            val d = BitmapDrawable(context.resources, bitmap)
                            d.setBounds(0, 0, height, height)

                            if (drawableReference.get() == null) return

                            drawableReference.get().setBounds(0, 0, height, height)
                            drawableReference.get().drawable = d

                            containerReference.get().invalidate()
                        }
                    })
        }
    }

    class HtmlDrawable : BitmapDrawable() {

        var drawable: Drawable? = null

        override fun draw(canvas: Canvas?) {
            drawable?.draw(canvas)
        }
    }

}