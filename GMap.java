package froguette.code_fister.spaceshooter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import static froguette.code_fister.spaceshooter.ModelLoader.*;

public class GMap {
    public Path pa=new Path();
    public Paint p=new Paint();

    double c=0;
    public float w,h;
    public double th=0.0;
    public double[] cam={0,0,0};
    public double[][] mtx={{0,0,0,0},{0,0,0,0},{0,0,0,0},{0,0,0,0}},
            pr={{0,0,0,0},{0,0,0,0},{0,0,0,0},{0,0,0,0}},rX,rZ;
    public ArrayList<double[][]> donut;
    public Comparator<double[][]> com=new Comparator<double[][]>() {
        @Override
        public int compare(double[][] o1, double[][] o2) {
            double a=(o1[0][2]+o1[1][2]+o1[2][2])/3,b=(o2[0][2]+o2[1][2]+o2[2][2])/3;
            return a>b?-1:1;
        }
    };
    public GMap(Context context, int dw, int dh) throws FileNotFoundException {
        w=dw;h=dh;
        donut=ModelLoader.load(context,"donut.obj");
        //System.out.println();


        double ar=h/w,fov=90.0,Rfov=1/Math.tan(fov*0.5),Fn=1.0,Ff=1000.0;///180.0*Math.PI
        pr[0][0]=ar*Rfov;
        pr[1][1]=Rfov;
        pr[2][2]=Ff/(Ff-Fn);
        pr[2][3]=1;
        pr[3][2]=(-Ff*Fn)/(Ff-Fn);

    }
    public double[] matMul(double[] i,double[][] m){
        double[] o={0,0,0,0};
        /*o[0]=i[0]*m[0][0]+i[1]*m[1][0]+i[2]*m[2][0]+m[3][0];
        o[1]=i[0]*m[0][1]+i[1]*m[1][1]+i[2]*m[2][1]+m[3][1];
        o[2]=i[0]*m[0][2]+i[1]*m[1][2]+i[2]*m[2][2]+m[3][2];*/
        for(int j=0;j<4;j++){o[j]=i[0]*m[0][j]+i[1]*m[1][j]+i[2]*m[2][j]+m[3][j];}
        if(o[3]!=0){for(int p=0;p<3;p++){o[p]/=o[3];}}
        return o;
    }
    public void update(){
        th+=0.05;
        rZ= new double[][]{{0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}};
        rZ[0][0] = Math.cos(th);
        rZ[0][1] = Math.sin(th);
        rZ[1][0] = -Math.sin(th);
        rZ[1][1] = Math.cos(th);
        rZ[2][2] = 1;
        rZ[3][3] = 1;

        rX= new double[][]{{0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}};
        rX[0][0] = 1;
        rX[1][1] = Math.cos(th*0.5);//
        rX[1][2] = Math.sin(th*0.5);//
        rX[2][1] = -Math.sin(th*0.5);//
        rX[2][2] = Math.cos(th*0.5);//
        rX[3][3] = 1;

    }
    public void draw(Canvas canvas){
        p.setStyle(Paint.Style.FILL);
        p.setColor(Color.rgb(0,0,255));
        canvas.drawRect(0,0,w,h,p);
        p.setColor(Color.rgb(0,255,255));
        canvas.drawRect(0,0,50,50,p);

        p.setTextSize(50);
        canvas.drawText("oui "+String.valueOf(donut.size()),50,50,p);//donut.size()donut.get(0))
        ArrayList<double[][]> td=new ArrayList<double[][]>();
        for(int i=0;i<donut.size();i++){
            double[][] tr=donut.get(i);
            double[]t0=matMul(tr[0],rZ),//copy(tr[0])
                    t1=matMul(tr[1],rZ),//
                    t2=matMul(tr[2],rZ);//
            t0=matMul(t0,rX);
            t1=matMul(t1,rX);
            t2=matMul(t2,rX);

            t0[2]+=8;
            t1[2]+=8;
            t2[2]+=8;

            double[] normal={0,0,0},line1={0,0,0},line2={0,0,0};
            line1[0]=t1[0]-t0[0];
            line1[1]=t1[1]-t0[1];
            line1[2]=t1[2]-t0[2];

            line2[0]=t2[0]-t0[0];
            line2[1]=t2[1]-t0[1];
            line2[2]=t2[2]-t0[2];

            normal[0]=line1[1]*line2[2]-line1[2]*line2[1];
            normal[1]=line1[2]*line2[0]-line1[0]*line2[2];
            normal[2]=line1[0]*line2[1]-line1[1]*line2[0];
            double l=Math.sqrt(normal[0]*normal[0]+normal[1]*normal[1]+normal[2]*normal[2]);
            normal[0]/=l;normal[1]/=l;normal[2]/=l;
            if(normal[0]*(t0[0]-cam[0])+
               normal[1]*(t0[1]-cam[1])+
               normal[2]*(t0[2]-cam[2])<0){double[][] k={t0,t1,t2,normal};td.add(k);}
        }

        Collections.sort(td,com);
        for(int i=0;i<td.size();i++){
            double[] LD = {0, 0, -1};
            double ldl = Math.sqrt(LD[0] * LD[0] + LD[1] * LD[1] + LD[2] * LD[2]);
            LD[0] /= ldl;
            LD[1] /= ldl;
            LD[2] /= ldl;
            double dp = td.get(i)[3][0] * LD[0] + td.get(i)[3][1] * LD[1] + td.get(i)[3][2] * LD[2];
            p.setColor(Color.rgb((int) (255 * dp), (int) (255 * dp), (int) (0 * dp)));
            double[] p0 = matMul(td.get(i)[0], pr), p1 = matMul(td.get(i)[1], pr), p2 = matMul(td.get(i)[2], pr);
            //p.setStyle(Paint.Style.STROKE);
            //p.setStrokeWidth(3);
            pa.reset();
            pa.moveTo( (float)((1+p0[0])*w/2), (float)((1+p0[1])*h/2));
            pa.lineTo( (float)((1+p1[0])*w/2), (float)((1+p1[1])*h/2));
            pa.lineTo( (float)((1+p2[0])*w/2), (float)((1+p2[1])*h/2));
            pa.lineTo( (float)((1+p0[0])*w/2), (float)((1+p0[1])*h/2));
            canvas.drawPath(pa, p);
        }
    }
}
