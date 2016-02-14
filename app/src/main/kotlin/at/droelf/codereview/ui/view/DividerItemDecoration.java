package at.droelf.codereview.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import at.droelf.codereview.R;

public class DividerItemDecoration extends RecyclerView.ItemDecoration {

    private final Drawable divider;
    private final int leftPadding;

    public DividerItemDecoration(Context context, int leftPadding) {
        this.divider = ContextCompat.getDrawable(context, R.drawable.line_divider);
        this.leftPadding = leftPadding;
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left = parent.getPaddingLeft() + leftPadding;
        int right = parent.getWidth() - parent.getPaddingRight();

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + divider.getIntrinsicHeight();

            divider.setBounds(left, top, right, bottom);
            divider.draw(c);
        }
    }
}