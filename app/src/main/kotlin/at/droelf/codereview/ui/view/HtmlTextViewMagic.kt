package at.droelf.codereview.ui.view

import android.app.ActivityOptions
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.Browser
import android.support.v4.content.ContextCompat
import android.text.Html
import android.text.SpannableString
import android.text.style.ClickableSpan
import android.text.style.URLSpan
import android.util.Log
import android.view.View
import android.widget.TextView
import at.droelf.codereview.R
import com.squareup.picasso.Picasso
import org.sufficientlysecure.htmltextview.HtmlTagHandler
import org.sufficientlysecure.htmltextview.LocalLinkMovementMethod
import java.lang.ref.WeakReference

object HtmlTextViewMagic {

    fun apply(textView: TextView, text: String) {
        val textFormatted: SpannableString = SpannableString(trimTrailingWhiteSpace(Html.fromHtml(text, PicassoImageGetter(textView), HtmlTagHandler())))

        val spans = textFormatted.getSpans(0, textFormatted.length, URLSpan::class.java)

        spans.forEach { span ->
            val start = textFormatted.getSpanStart(span)
            val end = textFormatted.getSpanEnd(span)
            val flags = textFormatted.getSpanFlags(span)
            val url = (span as URLSpan).url

            textFormatted.removeSpan(span)
            textFormatted.setSpan(CoolUrlSpan(url), start, end, flags)
        }

        textView.text = textFormatted
        textView.movementMethod = LocalLinkMovementMethod.getInstance()
    }

    private fun trimTrailingWhiteSpace(source: CharSequence): CharSequence {
        var i = source.length
        while (--i >= 0 && source[i] == '\n') {
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

                            val minHeight = (rect.height() * 1).toInt()

                            val right = if(bitmap.width < minHeight) minHeight else bitmap.width
                            val bottom = if(bitmap.height < minHeight) minHeight else bitmap.height

                            val d = BitmapDrawable(context.resources, bitmap)
                            d.setBounds(0, 0, right, bottom)

                            if (drawableReference.get() == null) return

                            drawableReference.get().setBounds(0, 0, right, bottom)
                            drawableReference.get().drawable = d

                            val txtView = containerReference.get()
                            txtView.text = txtView.text
                            txtView.ellipsize = null
                            txtView.invalidate()
                            txtView.requestLayout()
                        }
                    })
        }
    }

    @Suppress("DEPRECATION")
    class HtmlDrawable() : BitmapDrawable() {

        var drawable: Drawable? = null

        override fun draw(canvas: Canvas?) {
            drawable?.draw(canvas)
        }

    }

    class CoolUrlSpan(url: String): URLSpan(url) {

        val EXTRA_CUSTOM_TABS_SESSION: String = "android.support.customtabs.extra.SESSION"
        val EXTRA_CUSTOM_TABS_COLOR: String = "android.support.customtabs.extra.TOOLBAR_COLOR"
        val EXTRA_CUSTOM_TABS_EXIT_ANIMATION_BUNDLE: String = "android.support.customtabs.extra.EXIT_ANIMATION_BUNDLE"


        override fun onClick(widget: View) {
            val uri = Uri.parse(url)
            val context = widget.context

            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.putExtra(Browser.EXTRA_APPLICATION_ID, context.packageName)

            val startBundle = ActivityOptions.makeCustomAnimation(context, R.anim.enter_from_right, R.anim.exit_to_left).toBundle()
            val finishBundle = ActivityOptions.makeCustomAnimation(context, R.anim.enter_from_left, R.anim.exit_to_right).toBundle()
            intent.putExtra(EXTRA_CUSTOM_TABS_EXIT_ANIMATION_BUNDLE, finishBundle);

            val extras: Bundle = Bundle()
            extras.putBinder(EXTRA_CUSTOM_TABS_SESSION, null)
            extras.putInt(EXTRA_CUSTOM_TABS_COLOR, ContextCompat.getColor(context, R.color.colorPrimary))
            intent.putExtras(extras);

            try {
                context.startActivity(intent, startBundle)
            } catch (e: ActivityNotFoundException) {
                Log.w("URLSpan", "Actvity was not found for intent, " + intent.toString())
            }
        }
    }

}