package froguette.code_fister.spaceshooter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.MotionEventCompat;

import java.io.FileNotFoundException;

public class Game extends SurfaceView implements SurfaceHolder.Callback {

    private final Joystick joystick;
    //private BG bg;
    private GameLoop gameLoop;
    private Context context;
    public int x=0,y=0,Dw,Dh;
    public boolean clkd=false;
    public GMap gmap;
    public Paint p;

    public Game(Context context) throws FileNotFoundException {
        super(context);
        SurfaceHolder surfaceHolder=getHolder();
        surfaceHolder.addCallback(this);
        this.context=context;
        this.gameLoop=new GameLoop(this,surfaceHolder);
        p=new Paint();

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        Point size = new Point();
        display.getSize(size);
        Dw = size.x;Dh = size.y;


        joystick=new Joystick(context,Dw,Dh);//context,this,250,650,100
        gmap=new GMap(context,Dw,Dh);

        setFocusable(true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        switch(e.getActionMasked()){
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                int id=e.getPointerId(e.getActionIndex());
                if(id==0){
                    x=(int)e.getX();y=(int)e.getY();
                    //clkd=true;
                }
                if(id==1){clkd=true;}
                return true;
            case MotionEvent.ACTION_MOVE:
                for(int i=0;i<e.getPointerCount();i++){
                    id=e.getPointerId(i);
                    if(id==0){
                        x=(int)e.getX();y=(int)e.getY();
                        clkd=true;
                    }else{}
                }

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                //player.setPos((double)e.getX(),(double)e.getY());
                //clkd=false;

                return true;
        }
        return super.onTouchEvent(e);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        gameLoop.startLoop();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder,int i,int i1,int i2) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {

    }
    public void update(){
        gmap.update();//x,y,clkd
        joystick.update();//joystick.dirs
    }
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        //drawUPS(canvas);

        p.setColor(Color.rgb(255,255,255));
        //canvas.drawRect(0,0,800,600,p);
        gmap.draw(canvas);
        joystick.draw(canvas);
    }
    public void drawUPS(Canvas canvas){
        String t=Double.toString(gameLoop.getAverageUPS());
        Paint paint=new Paint();
        int c=Color.rgb(255,128,0);
        paint.setColor(c);
        paint.setTextSize(50);
        canvas.drawText("UPS: "+t,100,90,paint);
    }

}