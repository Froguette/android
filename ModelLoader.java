package froguette.code_fister.spaceshooter;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;

public class ModelLoader {

    public static ArrayList<double[][]> load(Context context, String s) throws FileNotFoundException {
        ArrayList<double[][]> o=new ArrayList<double[][]>();
        ArrayList<double[]> vers=new ArrayList<double[]>();

        /*
        try {
            File f=new File(file);
            Scanner scnr=new Scanner();
            while (scnr.hasNextLine()) {
                String data = scnr.nextLine();


            }
            scnr.close();
        } catch (FileNotFoundException e) {e.printStackTrace();}*/

        //File sdcard = Environment.getExternalStorageDirectory();
        //File file = new File(sdcard,s);
        StringBuilder text = new StringBuilder();
        InputStream is = context.getResources().openRawResource(R.raw.donut);
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;

            while ((line = br.readLine()) != null) {
                //text.append(line);
                //o.add(new double[][]{{0,0,0},{0,0,0}});
                //text.append('\n');
                if(line.charAt(0)=='v'){
                    String[] tex=line.split(" ");double[] v=new double[3];
                    v[0]=Double.parseDouble(tex[1]);
                    v[1]=Double.parseDouble(tex[2]);
                    v[2]=Double.parseDouble(tex[3]);
                    vers.add(v);
                }
                if(line.charAt(0)=='f'){
                    String[] tex=line.split(" ");int[] fcs=new int[3];
                    fcs[0]=Integer.parseInt(tex[1]);
                    fcs[1]=Integer.parseInt(tex[2]);
                    fcs[2]=Integer.parseInt(tex[3]);
                    double[][] h={vers.get(fcs[0]-1),vers.get(fcs[1]-1),vers.get(fcs[2]-1)};
                    o.add(h);
                }
            }
            br.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return o;
    }
}
