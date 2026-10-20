package dev.glidian.kido;

import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioTrack;

/**
 * Short console beeps, synthesized once at startup so there are no sound files to ship. Each sound is
 * a list of {start Hz, end Hz, milliseconds} parts; a start of 0 is a gap.
 */
final class Beeps {
    private static final int RATE = 22050;

    private final AudioTrack tick, section, launch, alert, boot;
    private boolean on;

    Beeps(boolean on) {
        this.on = on;
        tick = track(0.07f, new float[][]{{2400, 2400, 16}});
        section = track(0.09f, new float[][]{{1320, 1320, 45}, {0, 0, 25}, {1760, 1760, 60}});
        launch = track(0.09f, new float[][]{{880, 1760, 110}});
        alert = track(0.10f, new float[][]{{660, 660, 140}, {0, 0, 120}, {660, 660, 140}});
        boot = track(0.08f, new float[][]{{1046, 1046, 60}, {1318, 1318, 60}, {1568, 1568, 140}});
    }

    boolean isOn() {
        return on;
    }

    void setOn(boolean on) {
        this.on = on;
    }

    void tick() { play(tick); }
    void section() { play(section); }
    void launch() { play(launch); }
    void alert() { play(alert); }
    void boot() { play(boot); }

    void release() {
        for (AudioTrack t : new AudioTrack[]{tick, section, launch, alert, boot}) if (t != null) t.release();
    }

    private void play(AudioTrack t) {
        if (!on || t == null) return;
        try {
            t.stop();
            t.reloadStaticData();
            t.play();
        } catch (IllegalStateException ignored) {
        }
    }

    private static AudioTrack track(float volume, float[][] parts) {
        int total = 0;
        for (float[] p : parts) total += (int) (RATE * p[2] / 1000);
        short[] pcm = new short[total];
        int at = 0;
        for (float[] p : parts) {
            int n = (int) (RATE * p[2] / 1000);
            double phase = 0;
            for (int i = 0; i < n; i++, at++) {
                if (p[0] == 0) continue;
                double f = p[0] + (p[1] - p[0]) * i / n;
                phase += 2 * Math.PI * f / RATE;
                // Quick attack and a soft tail so the beeps don't click.
                double env = Math.min(1, i / (RATE * 0.003)) * Math.min(1, (n - i) / (RATE * 0.012));
                pcm[at] = (short) (Math.sin(phase) * env * volume * Short.MAX_VALUE);
            }
        }
        try {
            AudioTrack t = new AudioTrack.Builder()
                    .setAudioAttributes(new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build())
                    .setAudioFormat(new AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(RATE)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO).build())
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .setBufferSizeInBytes(pcm.length * 2)
                    .build();
            t.write(pcm, 0, pcm.length);
            return t;
        } catch (RuntimeException e) {
            return null;
        }
    }
}
