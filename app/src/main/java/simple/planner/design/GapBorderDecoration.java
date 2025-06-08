package simple.planner.design;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class GapBorderDecoration extends RecyclerView.ItemDecoration {
    private int gapSize;
    private int borderColor;

    private Paint borderPaint;

    public GapBorderDecoration(Context context, int gapSizeDp, int borderColor) {
        // Convert dp to pixels for gap size
        this.gapSize = (int) (context.getResources().getDisplayMetrics().density * gapSizeDp);
        this.borderColor = borderColor;

        // Initialize Paint for the border
        borderPaint = new Paint();
        borderPaint.setColor(borderColor);
        borderPaint.setStrokeWidth(gapSize);
        borderPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        // Apply the gap (border) only to the bottom of each item
        outRect.bottom = gapSize;
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);

            // Calculate the bottom position of the current item
            float bottom = child.getBottom() + child.getTranslationY();

            // Draw the border at the bottom of each item
            float left = child.getLeft() + child.getTranslationX();
            float right = child.getRight() + child.getTranslationX();

            c.drawRect(left, bottom, right, bottom + gapSize, borderPaint);
        }
    }
}