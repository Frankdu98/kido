package dev.glidian.kido;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

/** A row of rounded pills ramping mint → amber → red, lit up to a fraction. Used for signal strength. */
final class PillsView extends View {
    private static final int[] RAMP = {0xFF3BD16F, 0xFF9BE04A, 0xFFFFB21E, 0xFFFF8A00, 0xFFE8231E};
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF r = new RectF();
    private final int count;
    private float level;

    PillsView(Context c, int count) {
        super(c);
        this.count = count;
    }

    void setLevel(float level) {
        this.level = level;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas c) {
        // Inset top and bottom so each lit pill's glow fits inside the view.
        float pad = getHeight() * 0.2f;
        float gap = getWidth() * 0.012f;
        float w = (getWidth() - gap * (count - 1)) / count;
        for (int i = 0; i < count; i++) {
            boolean lit = (float) i / count < level;
            int color = RAMP[Math.min(RAMP.length - 1, i * RAMP.length / count)];
            paint.setColor(lit ? color : Kido.withAlpha(Kido.AMBER, 0.1f));
            if (lit) paint.setShadowLayer(pad * 1.4f, 0, 0, color);
            else paint.clearShadowLayer();
            float x = i * (w + gap);
            r.set(x, pad, x + w, getHeight() - pad);
            c.drawRoundRect(r, w / 2, w / 2, paint);
        }
    }
}
