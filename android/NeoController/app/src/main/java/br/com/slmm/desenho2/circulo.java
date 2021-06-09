package br.com.slmm.desenho2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.Random;

import static java.lang.Math.atan2;

public class circulo extends View {

    private Paint paint= new Paint();
    public  float scaleFacto = 1.0f;
    private Random rnd = new Random();
    private Path path = new Path();
    public float x;
    public float y;
    public float lastx;
    public float lasty;
    public float z;
    public float x2 = 110;
    public float y2 = 110;
    public float angle = 180;

    public circulo(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setFocusable(true);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(6f);
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE); //FILL_AND_STROKE);//
        paint.setStrokeJoin(Paint.Join.ROUND);
        x = -1;
        y = -1;
        z = 3;
    }

    public void setPos(){
        x2 = 200;
        y2 = 200;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        canvas.save();

        canvas.drawCircle(x2,y2,50,paint);

        if (z>=0){
            canvas.drawCircle(x,y,z,paint);
            canvas.drawLine(110,110,x,y,paint);
        }

        paint.setColor(Color.BLACK);
//Calculate the rect / bounds of oval
        RectF rectF = new RectF(200, 20, 400, 220);
        //Do the drawing in onDraw() method of View.
        canvas.drawArc (rectF, 0, angle, false, paint);


        paint.setColor(Color.rgb(rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)));
        canvas.drawPath(drawPoligon(60, 350, 50, 3), paint);

        paint.setColor(Color.rgb(rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)));
        canvas.drawPath(drawPoligon(180, 350, 50, 6), paint);

        paint.setColor(Color.rgb(rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)));
        canvas.drawPath(drawPoligon(300, 350, 50, 8), paint);

        paint.setColor(Color.rgb(rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)));
        canvas.drawPath(drawPoligon(420, 350, 50, 12), paint);

        canvas.restore();
    }
    @Override
    public boolean onTouchEvent(MotionEvent event){

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                x = event.getX();
                y = event.getY();
                lastx = x;
                lasty = y;
                z = 10;
                return true;
            case  MotionEvent.ACTION_MOVE:
                    if (lastx != event.getX())
                       z++;
                    lastx = event.getX();

                    if (lasty != event.getY()) {
                        z--;
                        if (z < 0) z =0;
                    }
                    lasty = event.getY();

                double ax =  (event.getX() - (200 *0.5f));
                double ay =  (event.getY() - (200 *0.5f));
                angle = (float)Math.toDegrees( (atan2(ay, ax) + Math.PI / 2));
                if (angle < 0)
                    angle = angle + 360;

                /*
                if (lastx > event.getX()) {
                    if (z > 1)
                        z--;
                }
                else
                    z++;
                lastx = event.getX();
                if (lasty > event.getY()) {
                    if (z > 1)
                        z--;
                }
                else
                    z++;
                lasty = event.getY();

                 */
                break;

            case  MotionEvent.ACTION_UP:

                break;
            default:
                return false;
        }
        invalidate();
        return true;
    }

    Path drawPoligon(int centerX, int centerY, int raio, int nLados) {
        Path poligon = new Path();

        double ang = Math.PI * 2 / nLados;
        poligon.moveTo((float)(centerX + raio * Math.cos(0)), (float)(centerY + raio * Math.sin(0)));
        for (int i=1; i<nLados; i++)
            poligon.lineTo((float)(centerX + raio * Math.cos(ang * i)), (float)(centerY + raio * Math.sin(ang * i)));
        poligon.close();

        return poligon;
    }



}
