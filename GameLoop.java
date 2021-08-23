package froguette.code_fister.spaceshooter;

import android.graphics.Canvas;
import android.view.Surface;
import android.view.SurfaceHolder;

public class GameLoop extends Thread{
    private boolean isRunning=false;
    private SurfaceHolder surfaceHolder;
    private Game game;
    private double averageUPS;
    private static final double MAX_UPS=30.0;
    private static final double UPS_PERIOD=1E+3/MAX_UPS;

    public GameLoop(Game game, SurfaceHolder surfaceHolder) {
        this.game=game;
        this.surfaceHolder=surfaceHolder;
    }

    public double getAverageUPS() {
        return averageUPS;
    }

    public void startLoop() {
        isRunning=true;start();
    }

    @Override
    public void run() {
        super.run();
        int uc=0;long strt=0,et=0,slpt=0;
        strt=System.currentTimeMillis();
        Canvas canvas;
        while(isRunning){
            try {
                canvas=surfaceHolder.lockCanvas();
                synchronized (surfaceHolder){game.update();uc++;game.draw(canvas);}

                surfaceHolder.unlockCanvasAndPost(canvas);
            }catch(IllegalArgumentException e){e.printStackTrace();}

            et=System.currentTimeMillis()-strt;
            slpt=(long)(uc*UPS_PERIOD-et);
            if(slpt>0){
                try {sleep(slpt);} catch (InterruptedException e) {e.printStackTrace();}
            }
            if(slpt<0){
                game.update();
                uc++;
                et=System.currentTimeMillis()-strt;
                slpt=(long)(uc*UPS_PERIOD-et);
            }
            et=System.currentTimeMillis()-strt;
            if(et<1000){averageUPS=uc/(1E-3*et);uc=0;strt=System.currentTimeMillis();}
        }
    }

}

