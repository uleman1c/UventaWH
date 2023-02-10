package com.example.uventawh;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.Nullable;

@SuppressLint("AppCompatCustomView")
public class ProgressTextView extends TextView {

    public void setMax(int max) {
        this.max = max;
        invalidate();
    }

    public int max;

    public void setProgress(int progress) {
        this.progress = progress;
        invalidate();
    }

    public int progress;

    public ProgressTextView(Context context) {
        super(context);
    }

    public ProgressTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (max > 0) {

            Paint paint = new Paint();
            paint.setColor(Color.BLACK);
            paint.setStrokeWidth(2);
            canvas.drawLine(0, 0, getWidth(), 0, paint);
            canvas.drawLine(0, 0, 0, getHeight(), paint);
            canvas.drawLine(0, getHeight(), getWidth(), getHeight(), paint);
            canvas.drawLine(getWidth(), 0, getWidth(), getHeight(), paint);

            LinearGradient shader = new LinearGradient(0, 0, 100, 100,
                    Color.RED, Color.YELLOW, Shader.TileMode.MIRROR);

            paint.setShader(shader);

            canvas.drawRect(2, 2, (getWidth() - 2) * progress / max, getBottom() - 2, paint);


        }
        super.onDraw(canvas);



    }
}
