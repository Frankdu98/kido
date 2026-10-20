package dev.glidian.kido;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

/**
 * A section's outline frame that can draw itself in: two lines start at the top-left corner, run round
 * opposite ways and meet at the bottom-right, each with a bright head.
 */
final class TraceView extends View {
    private final Paint line = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint head = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path rect = new Path(), part = new Path();
    private final PathMeasure measure = new PathMeasure();
    private float progress = 1f;
    private ValueAnimator anim;

    TraceView(Context c) {
        super(c);
        line.setStyle(Paint.Style.STROKE);
        line.setStrokeWidth(Math.max(1, Kido.px(2)));
        line.setColor(Kido.LINE);
        head.setStyle(Paint.Style.STROKE);
        head.setStrokeWidth(Math.max(2, Kido.px(3)));
        head.setColor(Kido.INK);
    }

    @Override
    protected void onSizeChanged(int w, int h, int ow, int oh) {
        float i = line.getStrokeWidth() / 2;
        rect.reset();
        rect.addRect(i, i, w - i, h - i, Path.Direction.CW);
        measure.setPath(rect, false);
    }

    void trace(long ms) {
        if (anim != null) anim.cancel();
        anim = ValueAnimator.ofFloat(0f, 1f);
        anim.setDuration(ms);
        anim.setInterpolator(new DecelerateInterpolator(1.6f));
        anim.addUpdateListener(a -> {
            progress = (float) a.getAnimatedValue();
            invalidate();
        });
        anim.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        if (anim != null) anim.cancel();
        super.onDetachedFromWindow();
    }

    @Override
    protected void onDraw(Canvas c) {
        if (progress >= 1f) {
            c.drawPath(rect, line);
            return;
        }
        float len = measure.getLength(), half = len * progress / 2, tail = Kido.px(140);
        segment(c, 0, half, line);
        segment(c, len - half, len, line);
        segment(c, Math.max(0, half - tail), half, head);
        segment(c, len - half, Math.min(len, len - half + tail), head);
    }

    private void segment(Canvas c, float from, float to, Paint p) {
        part.reset();
        measure.getSegment(from, to, part, true);
        c.drawPath(part, p);
    }
}
