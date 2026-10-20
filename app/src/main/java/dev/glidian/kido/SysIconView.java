package dev.glidian.kido;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

/** Line icons for the system boxes: an HDMI port, Wi-Fi arcs or a gear, drawn on a 200x110 grid. */
final class SysIconView extends View {
    static final int PORT = 0, WIFI = 1, GEAR = 2;

    private final int kind;
    private final Paint stroke = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint fill = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path path = new Path();

    SysIconView(Context c, int kind) {
        super(c);
        this.kind = kind;
        stroke.setStyle(Paint.Style.STROKE);
        stroke.setStrokeWidth(4);
        stroke.setStrokeJoin(Paint.Join.MITER);
        setColor(Kido.AMBER);
    }

    void setColor(int color) {
        stroke.setColor(color);
        fill.setColor(color);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas c) {
        float s = Math.min(getWidth() / 200f, getHeight() / 110f);
        c.translate((getWidth() - 200 * s) / 2, (getHeight() - 110 * s) / 2);
        c.scale(s, s);
        path.reset();
        switch (kind) {
            case PORT:
                path.moveTo(40, 30);
                path.lineTo(160, 30);
                path.lineTo(150, 70);
                path.lineTo(50, 70);
                path.close();
                path.moveTo(100, 70);
                path.lineTo(100, 104);
                c.drawPath(path, stroke);
                c.drawRect(62, 42, 138, 52, fill);
                break;
            case WIFI:
                path.moveTo(40, 50);
                path.quadTo(100, 0, 160, 50);
                path.moveTo(60, 70);
                path.quadTo(100, 36, 140, 70);
                path.moveTo(80, 90);
                path.quadTo(100, 74, 120, 90);
                c.drawPath(path, stroke);
                c.drawRect(94, 96, 106, 108, fill);
                break;
            default:
                c.drawCircle(100, 56, 32, stroke);
                c.drawCircle(100, 56, 11, stroke);
                for (int i = 0; i < 8; i++) {
                    c.save();
                    c.rotate(i * 45, 100, 56);
                    c.drawRect(93, 10, 107, 24, fill);
                    c.restore();
                }
                break;
        }
    }
}
