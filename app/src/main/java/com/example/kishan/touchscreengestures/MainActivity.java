package com.example.kishan.touchscreengestures;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity
{
    TextView pointerId;
    TextView xCoord;
    TextView yCoord;
    ImageView circle;
    ImageView circle2;
    ImageView circle3;
    ImageView circle4;
    ImageView circle5;
    Paint mPaint;
    View dv;
    Button clear;
    private static final String TAG = "MyActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Gestures");
        centerTitle();

        dv = new DrawingView(this);
        setContentView(dv);
    }

    public void centerTitle()
    {
        ArrayList<View> textViews = new ArrayList<>();
        getWindow().getDecorView().findViewsWithText(textViews, getTitle(), View.FIND_VIEWS_WITH_TEXT);

        if(textViews.size() > 0)
        {
            AppCompatTextView appCompatTextView = null;
            if(textViews.size() == 1)
            {
                appCompatTextView = (AppCompatTextView) textViews.get(0);
            }
            else
            {
                for(View v : textViews)
                {
                    if(v.getParent() instanceof Toolbar)
                    {
                        appCompatTextView = (AppCompatTextView) v;
                        break;
                    }
                }
            }

            if(appCompatTextView != null)
            {
                ViewGroup.LayoutParams params = appCompatTextView.getLayoutParams();
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                appCompatTextView.setLayoutParams(params);
                appCompatTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            }
        }
    }

    public class DrawingView extends RelativeLayout
    {

        int width;
        int height;
        Bitmap mBitmap;
        Canvas mCanvas;
        Path mPath;
        Paint mBitmapPaint;
        Context context;
        Paint circlePaint;
        Path circlePath;
        View view;
        Map<Integer, ImageView> id_circle;

        public DrawingView(Context c)
        {
            super(c);
            context=c;

            view = LayoutInflater.from(context).inflate(R.layout.activity_main, this);
            setWillNotDraw(false);

            pointerId = findViewById(R.id.main_id);
            xCoord = findViewById(R.id.main_x);
            yCoord = findViewById(R.id.main_y);

            circle = findViewById(R.id.main_circle);
            circle2 = findViewById(R.id.main_circle2);
            circle3 = findViewById(R.id.main_circle3);
            circle4 = findViewById(R.id.main_circle4);
            circle5 = findViewById(R.id.main_circle5);

            clear = findViewById(R.id.main_clear);

            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setColor(Color.argb(180, 255, 150, 150));
            mPaint.setStrokeWidth(4);

            mPath = new Path();
            mBitmapPaint = new Paint(Paint.DITHER_FLAG);
            circlePaint = new Paint();
            circlePath = new Path();
            circlePaint.setAntiAlias(true);
            circlePaint.setColor(Color.BLACK);
            circlePaint.setStyle(Paint.Style.STROKE);
            circlePaint.setStrokeJoin(Paint.Join.MITER);
            circlePaint.setStrokeWidth(5f);

            id_circle = new HashMap<>();
            id_circle.put(0, circle);
            id_circle.put(1, circle2);
            id_circle.put(2, circle3);
            id_circle.put(3, circle4);
            id_circle.put(4, circle5);

            clearView();
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh)
        {
            super.onSizeChanged(w, h, oldw, oldh);

            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);
        }

        @Override
        protected void onDraw(Canvas canvas)
        {
            super.onDraw(canvas);

            canvas.drawBitmap( mBitmap, 0, 0, mBitmapPaint);
            canvas.drawPath( mPath,  mPaint);
            canvas.drawPath( circlePath,  circlePaint);
        }

        private float mX, mY;
        private static final float TOUCH_TOLERANCE = 4;

        private void touch_start(float x, float y)
        {
            mPath.reset();
            mPath.moveTo(x, y);
            mX = x;
            mY = y;
        }

        private void touch_move(float x, float y)
        {
            float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
                mX = x;
                mY = y;

                circlePath.reset();
                circlePath.addCircle(mX, mY, 2, Path.Direction.CW);
            }
        }

        private void touch_up()
        {
            mPath.lineTo(mX, mY);
            circlePath.reset();
            mCanvas.drawPath(mPath,  mPaint);
            mPath.reset();
        }

        @Override
        public boolean onTouchEvent(MotionEvent event)
        {

            int idx = event.getActionIndex();
            int id = event.getPointerId(idx);
            int x = (int)event.getX(idx);
            int y = (int)event.getY(idx);

            pointerId.setText(id + "");
            xCoord.setText(x + "");
            yCoord.setText(y + "");

            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    placeImage(x, y, circle);
                    id_circle.get(id).setVisibility(View.VISIBLE);
                    touch_start(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    placeImage(x, y, id_circle.get(id));
                    id_circle.get(id).setVisibility(View.VISIBLE);
                    touch_start(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:

                    for(int i = 0; i < event.getPointerCount(); i++)
                    {
                        id = event.getPointerId(i);
                        Log.v(TAG, id + "");
                        x = (int)event.getX(i);
                        y = (int)event.getY(i);
                        placeImage(x, y, id_circle.get(id));
                        id_circle.get(id).setVisibility(View.VISIBLE);
                        touch_move(x, y);
                        invalidate();
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    placeImage(x, y, id_circle.get(id));
                    id_circle.get(id).setVisibility(View.INVISIBLE);
                    touch_up();
                    invalidate();
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    placeImage(x, y, id_circle.get(id));
                    id_circle.get(id).setVisibility(View.INVISIBLE);
                    invalidate();
                    break;
            }
            return true;
        }

        private void placeImage(int x, int y, ImageView circle)
        {
            int viewWidth = circle.getWidth() / 2;
            int viewHeight = circle.getHeight() / 2;

            circle.setX(x-viewWidth);
            circle.setY(y-viewHeight);
        }

        private void clearView()
        {
            clear.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    dv = new DrawingView(context);
                    setContentView(dv);
                }
            });
        }
    }
}
