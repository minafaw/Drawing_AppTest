package psystem.co.drawingtest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * Created by mina on 8/25/2016.
 */
public class PeekedImage extends ImageView {

    private final float radius;
    private boolean shouldDrawOpening = false;
    private float x;
    private float y;
    private Paint paint = null;
    private Path path;
    private static final float TOUCH_TOLERANCE = 0.8f;
    private float mX, mY;
    private boolean TouchUp= false;

    public PeekedImage(Context context) {
        super(context);
        this.path = new Path();
        radius = context.getResources().getDimensionPixelSize(R.dimen.peek_through_radius);
    }

    public PeekedImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.path = new Path();
        radius = context.getResources().getDimensionPixelSize(R.dimen.peek_through_radius);
    }

    public PeekedImage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.path = new Path();
        radius = context.getResources().getDimensionPixelSize(R.dimen.peek_through_radius);

    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        x = motionEvent.getX();
        y = motionEvent.getY();
        switch (action){
            case MotionEvent.ACTION_DOWN:
                shouldDrawOpening = true;
                onTouchStart(x, y);
                onTouchMove(x+ 0.8f, y+ 0.8f);
                invalidate();
                break;

            case MotionEvent.ACTION_MOVE:
                shouldDrawOpening = true;
                this.onTouchMove(x, y);
                invalidate();
                break;

            case MotionEvent.ACTION_UP:
                TouchUp = true;
                invalidate();
                break;
        }




        return true;


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

    @Override
    protected void onDraw(Canvas canvas) {
        if (paint == null) {
            Bitmap original = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            Canvas originalCanvas = new Canvas(original);
            super.onDraw(originalCanvas);

            Shader shader = new BitmapShader(original, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

            paint = new Paint();
            paint.setShader(shader);
          paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        }

      canvas.drawColor(Color.GRAY);
        if (shouldDrawOpening) {

            canvas.drawCircle(x, y - radius, radius, paint);
        }

        if(TouchUp){
            path.lineTo(mX, mY);
            canvas.drawPath(this.path, paint);
            TouchUp = false;
        }
    }




}
