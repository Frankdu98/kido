package dev.glidian.kido;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

import java.util.Locale;

/** A vertical measuring scale: a tick every 20px, a longer one with a number every 100px. */
final class RulerView extends View {
    private final Paint line = new Paint();
    private final Paint label = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final int start;

    RulerView(Context c, int start) {
        super(c);
        this.start = start;
        line.setColor(Kido.LINE);
        line.setStrokeWidth(Math.max(1, Kido.px(2)));
        label.setColor(Kido.DIM);
        label.setTypeface(Kido.mono);
        label.setTextSize(Kido.px(13));
    }

    @Override
    protected void onDraw(Canvas c) {
        float step = Kido.px(20);
        float x = getWidth();
        for (int i = 0; i * step <= getHeight(); i++) {
            float y = i * step;
            boolean major = i % 5 == 0;
            c.drawLine(x - Kido.px(major ? 18 : 8), y, x, y, line);
            if (major && i > 0) c.drawText(String.format(Locale.ROOT, "%04d", start + i * 20), 0, y + Kido.px(4), label);
        }
    }
}
