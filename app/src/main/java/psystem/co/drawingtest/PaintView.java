package psystem.co.drawingtest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by mina on 8/25/2016.
 */
public class PaintView extends View {
    private static final float TOUCH_TOLERANCE = 0.8f;
    Context mContext;
    Bitmap bgImg;
    private Paint bmpPaint;
    private Path path;
    private float mX, mY;
    private Paint paint;
    private int colour;
    private Canvas canvas;
    private Bitmap bgImage;
    private Bitmap bmp;
    private Point StartPoint = new Point();

    public PaintView(Context context) {
        super(context);
        this.mContext = context;
        this.bmpPaint = new Paint();
        this.path = new Path();

        colour = Color.GREEN; // color of brush

        this.paint = new Paint();
        this.paint.setAntiAlias(true);
        this.paint.setDither(true);
        this.paint.setColor(this.colour);
        this.paint.setStyle(Paint.Style.STROKE);
        this.paint.setStrokeJoin(Paint.Join.ROUND);
        this.paint.setStrokeCap(Paint.Cap.ROUND);
        this.paint.setStrokeWidth(3);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.v("ScreenSize", " Width: " + w + " Height: " + h);
        this.bgImg = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        this.bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        this.canvas = new Canvas(this.bmp);
        this.bgImg = BitmapFactory.decodeResource(getResources(), R.drawable.flower)
                .copy(Bitmap.Config.ARGB_8888, true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint mTextPaint = new Paint();
        mTextPaint.setStyle(Paint.Style.STROKE);
        mTextPaint.setStrokeWidth(3);
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setTextSize(200);

        Paint mFillPaint = new Paint();
        mFillPaint.setStyle(Paint.Style.FILL);
        mFillPaint.setColor(Color.BLUE);

        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(this.bgImg, 0, 0, bmpPaint);
        String ArabicText = "ا";
        Rect bounds = new Rect();
        canvas.drawText(ArabicText, 200, 200, mTextPaint);
        mTextPaint.getTextBounds(ArabicText , 0 , ArabicText.length() , bounds);
        Log.v("TextBounds " , "The text bounds Width: " + bounds.width()
                + " Height : "+ bounds.height() );
        // to draw point above letter
        StartPoint.x = (200 + bounds.width()) ;
        StartPoint.y= (int) (200 - (bounds.height() + mFillPaint.ascent() ));

        canvas.drawCircle(StartPoint.x , StartPoint.y , 10 ,mFillPaint );
        canvas.drawBitmap(this.bmp, 0, 0, this.bmpPaint);

        canvas.drawPath(this.path, paint);

    }

    private void onTouchStart(float x, float y) {
        path.reset();
        path.moveTo(x, y);
        this.mX = x;
        this.mY = y;
    }

    private void onTouchMove(float x, float y) {
        float dx = Math.abs(x - this.mX);
        float dy = Math.abs(y - this.mY);

        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            this.path.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;


        }
    }

    private void onTouchUp() {
        this.path.lineTo(mX, mY);
        this.canvas.drawPath(this.path, paint);
        this.path.reset();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        Log.v("Drawing_APP", " X value is: " + x + " Y value is: " + y);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(StartPoint.x == x && StartPoint.y == y){
                    onTouchStart(x, y);
                    onTouchMove(x+ 0.8f, y+ 0.8f);
                }

//                    if (bgImg != null) {
//                        int sourceColor = bgImg.getPixel((int) x, (int) y);
//                        int desColor = paint.getColor();
//                        Log.v("Colors" , " SourceColor : " + sourceColor + " desColor " + desColor);
//                        FloodFill(bgImg, new Point((int) mX, (int) mY), sourceColor, desColor);
//                    }

                invalidate();
                break;

            case MotionEvent.ACTION_MOVE:
                if(StartPoint.x == x && StartPoint.y == y) {
                    this.onTouchMove(x, y);
                    invalidate();
                }

                break;
            case MotionEvent.ACTION_UP:
                if(StartPoint.x == x && StartPoint.y == y) {
                    this.onTouchUp();
                    invalidate();
                }
                break;
        }

        return true;
    }


    private void FloodFill(Bitmap bmp, Point pt, int targetColor, int replacementColor) {
        Queue<Point> q = new LinkedList<Point>();
        q.add(pt);
        while (q.size() > 0) {
            Point n = q.poll();
            if (bmp.getPixel(n.x, n.y) != targetColor)
                continue;

            Point w = n, e = new Point(n.x + 1, n.y);
            while ((w.x > 0) && (bmp.getPixel(w.x, w.y) == targetColor)) {
                bmp.setPixel(w.x, w.y, replacementColor);
                if ((w.y > 0) && (bmp.getPixel(w.x, w.y - 1) == targetColor))
                    q.add(new Point(w.x, w.y - 1));
                if ((w.y < bmp.getHeight() - 1)
                        && (bmp.getPixel(w.x, w.y + 1) == targetColor))
                    q.add(new Point(w.x, w.y + 1));
                w.x--;
            }
            while ((e.x < bmp.getWidth() - 1)
                    && (bmp.getPixel(e.x, e.y) == targetColor)) {
                bmp.setPixel(e.x, e.y, replacementColor);

                if ((e.y > 0) && (bmp.getPixel(e.x, e.y - 1) == targetColor))
                    q.add(new Point(e.x, e.y - 1));
                if ((e.y < bmp.getHeight() - 1)
                        && (bmp.getPixel(e.x, e.y + 1) == targetColor))
                    q.add(new Point(e.x, e.y + 1));
                e.x++;
            }


        }
    }
}
