package com.example.socialmediaapp.viewmodel;

import android.app.Application;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.AndroidViewModel;

import com.example.socialmediaapp.R;

public class LandingScreenViewModel extends AndroidViewModel {
    public SpannableString kickConnectSpannable;

    public LandingScreenViewModel(@NonNull Application application) {
        super(application);
        initSpannable(application);
    }

    private void initSpannable(Application application) {
        String text = "KickConnect";
        SpannableString spannableString = new SpannableString(text);

        Paint paint = new Paint();
        paint.setTextSize(24f);
        float textSize = paint.measureText("o");

        Drawable ballDrawable = ContextCompat.getDrawable(application, R.drawable.ball_o);
        if (ballDrawable != null) {
            int scaledSize = (int) textSize * 4;
            ballDrawable.setBounds(0, 0, scaledSize, scaledSize);

            ImageSpan imageSpan = new ImageSpan(ballDrawable, ImageSpan.ALIGN_BASELINE) {
                @Override
                public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
                    Drawable drawable = getDrawable();
                    canvas.save();
                    int transY = bottom - drawable.getBounds().bottom;
                    transY -= paint.getFontMetricsInt().descent / 2;
                    canvas.translate(x, transY);
                    drawable.draw(canvas);
                    canvas.restore();
                }
            };

            int start = text.indexOf('o');
            if (start >= 0) {
                int end = start + 1;
                spannableString.setSpan(imageSpan, start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            }
        }

        kickConnectSpannable = spannableString;
    }
}
