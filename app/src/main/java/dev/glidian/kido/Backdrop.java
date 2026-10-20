package dev.glidian.kido;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/** Tiled textures: a faint hex grid behind the board and CRT scanlines over everything. Each is one small bitmap. */
final class Backdrop {
    private Backdrop() {
    }

    static Drawable hexGrid(Resources res, int color) {
        // One tile holds a full pointy-top hex row and the offset row below it, so it repeats seamlessly.
        int w = Math.max(8, Kido.px(70)), h = Math.max(12, Kido.px(121));
        float u = h / 6f;
        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(Math.max(1, Kido.px(1.5f)));
        p.setColor(color);
        Path path = new Path();
        float[][] centers = {{0, 0}, {w, 0}, {w / 2f, 3 * u}, {0, 6 * u}, {w, 6 * u}};
        for (float[] o : centers) {
            float x = o[0], y = o[1];
            path.moveTo(x, y - 2 * u);
            path.lineTo(x + w / 2f, y - u);
            path.lineTo(x + w / 2f, y + u);
            path.lineTo(x, y + 2 * u);
            path.lineTo(x - w / 2f, y + u);
            path.lineTo(x - w / 2f, y - u);
            path.close();
        }
        c.drawPath(path, p);
        return tiled(res, bmp);
    }

    static Drawable scanlines(Resources res, int color) {
        int h = Math.max(3, Kido.px(3));
        Bitmap bmp = Bitmap.createBitmap(1, h, Bitmap.Config.ARGB_8888);
        bmp.setPixel(0, 0, color);
        return tiled(res, bmp);
    }

    private static Drawable tiled(Resources res, Bitmap bmp) {
        BitmapDrawable d = new BitmapDrawable(res, bmp);
        d.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        return d;
    }
}
