package com.example.appgestoscontextuales.app;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.graphics.Color;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import com.FastDtwTest;

public class menuStart extends Activity implements SensorEventListener{
    private static Context c;
    String []some=new String[3];
    private double fdtwr=1.00;
    private int detecLugar; //  Oficina=1, Juntas=2, Descanso=3
    private float curX, curY, curZ;
    double m;
    private ArrayList<ArrayList<Float>> magnitud = new ArrayList<ArrayList<Float>>();
    private MediaPlayer encender = null;
    private MediaPlayer apagar = null;
    SensorManager sm;
    ImageView imagen, bairea, bfoco;
    LinearLayout principal, lugar;
    JSONObject colour = new JSONObject();
    String colourcvt, datascktst, colorDA, scktDA;
    JSONObject datasckt = new JSONObject();
    HttpClient httpclient = new DefaultHttpClient();
    HttpPost httppost = new HttpPost("https://api.ninja.is:443/rest/v0/device/1313BB000464_0_0_1007?user_access_token=245ffab7e4d4d441b6ab788df649ac4626736ee4");
    HttpClient httpclientsckt = new DefaultHttpClient();
    HttpPost httppostsckt = new HttpPost("https://api.ninja.is:443/rest/v0/device/1313BB000464_0_0_11?user_access_token=245ffab7e4d4d441b6ab788df649ac4626736ee4");
    EnvPet ninja = new EnvPet();
    String path;
    private Element[] nets;
    private WifiManager manWifi;
    private List<ScanResult> wifiList;
    boolean stateRedes;
    int cont;
    int [] datos;
    dtcRed dred = new dtcRed();
    KNN result = new KNN();

    public static Context getAppContext() {
        return c;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.c = getApplicationContext();
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_menu_start);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        encender = MediaPlayer.create(this, R.raw.enc);
        apagar = MediaPlayer.create(this, R.raw.apa);
        imagen = (ImageView)findViewById(R.id.imgCambia);
        bairea = (ImageView)findViewById(R.id.imgAirea);
        bfoco = (ImageView)findViewById(R.id.imgLuces);
        principal =(LinearLayout) findViewById(R.id.layoutPrincipal);
        lugar = (LinearLayout) findViewById(R.id.layoutEtiqueta);
        cambiarOficina();
        path= menuStart.getAppContext().getFilesDir().getParentFile().getPath();
        manWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        manWifi.startScan();
        wifiList = this.manWifi.getScanResults();
        nets = new Element[wifiList.size()];
        stateRedes=true;
        cont=0;
    }

    public void sensorGet(){
        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        List<Sensor> sensors = sm.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if (sensors.size() > 0) {
            boolean b = sm.registerListener(this, sensors.get(0), SensorManager.SENSOR_DELAY_GAME);
        }
    }

    public void sensorOff(){
        sm.unregisterListener(this);
    }

    public void escribir() throws IOException {
        FileWriter csvWrote = new FileWriter(path+"/gesto.csv");
        for (ArrayList<Float> tmp : magnitud) {
            csvWrote.write(tmp.get(0).toString());
            csvWrote.write("\r\n");
        }
        csvWrote.close();
    }

    @Override
    protected void onResume(){
        super.onResume();

        sensorGet();
        bairea.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    bairea.setAlpha(100);
                    magnitud = new ArrayList<ArrayList<Float>>();
                    stateRedes=true;
                }
                else if (event.getAction() == MotionEvent.ACTION_UP) {
                    bairea.setAlpha(255);
                    stateRedes=false;
                    try {
                        escribir();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    int gesto = dtGesto(chckCirculo(),chckCuadro(),chckTriangulo());
                    switch (gesto){
                        case 1:
                            if (fdtwr == 2.00){
                                colorDA="00ffff";
                            }else{
                                colorDA="00ff00";
                            }
                            scktDA="110110101101101011011010";
                            ((TextView) findViewById(R.id.gesto)).setText("Aire-Circulo-Encendido");
                            encender.start();
                            break;
                        case 2:
                            if (fdtwr == 2.00){
                                colorDA="ff00ff";
                            }else{
                                colorDA="ff0000";
                            }
                            scktDA="110110101101101011010010";
                            ((TextView) findViewById(R.id.gesto)).setText("Aire-Cuadro-Apagado");
                            apagar.start();
                            break;
                        case 3:
                            colorDA="0000ff";
                            scktDA="110110101101101011011010";
                            ((TextView) findViewById(R.id.gesto)).setText("Aire-Triangulo-EncAlt");
                            encender.start();
                            break;
                    }
                    execPet(colorDA,scktDA);
                }
                return true;
            }
        });
        bfoco.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    bfoco.setAlpha(100);
                    magnitud = new ArrayList<ArrayList<Float>>();
                    stateRedes=true;
                }
                else if (event.getAction() == MotionEvent.ACTION_UP) {
                    bfoco.setAlpha(255);
                    stateRedes=false;
                    try {
                        escribir();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    int gesto = dtGesto(chckCirculo(),chckCuadro(),chckTriangulo());
                    switch (gesto){
                        case 1:
                            if (fdtwr == 2.00){
                                colorDA="00ffff";
                            }else{
                                colorDA="00ff00";
                            }
                            scktDA="110110101101101011011010";
                            ((TextView) findViewById(R.id.gesto)).setText("Foco-Circulo-Encendido");
                            encender.start();
                            break;
                        case 2:
                            if (fdtwr == 2.00){
                                colorDA="ff00ff";
                            }else{
                                colorDA="ff0000";
                            }
                            scktDA="110110101101101011010010";
                            ((TextView) findViewById(R.id.gesto)).setText("Foco-Cuadro-Apagado");
                            apagar.start();
                            break;
                        case 3:
                            colorDA="0000ff";
                            scktDA="110110101101101011011010";
                            ((TextView) findViewById(R.id.gesto)).setText("Foco-Triangulo-EncAlt");
                            encender.start();
                            break;
                    }
                    execPet(colorDA,scktDA);
                }
                return true;
            }
        });
        imagen.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    imagen.setAlpha(100);
                    magnitud = new ArrayList<ArrayList<Float>>();
                    stateRedes=true;
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    imagen.setAlpha(255);
                    stateRedes=false;
                    try {
                        escribir();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    int gesto = dtGesto(chckCirculo(),chckCuadro(),chckTriangulo());
                    String lugar="";
                    switch (detecLugar){
                        case 1:
                            lugar = "Oficina";
                            break;
                        case 2:
                            lugar = "Juntas";
                            break;
                        case 3:
                            lugar = "Descanso";
                            break;
                    }
                    switch (gesto){
                        case 1:
                            if (fdtwr == 2.00){
                                colorDA="00ffff";
                            }else{
                                colorDA="00ff00";
                            }
                            scktDA="110110101101101011011010";
                            ((TextView) findViewById(R.id.gesto)).setText(lugar+"-Foco-Circulo-Encendido");
                            encender.start();
                            break;
                        case 2:
                            if (fdtwr == 2.00){
                                colorDA="ff00ff";
                            }else{
                                colorDA="ff0000";
                            }
                            scktDA="110110101101101011010010";
                            ((TextView) findViewById(R.id.gesto)).setText(lugar+"-Foco-Cuadro-Apagado");
                            apagar.start();
                            break;
                        case 3:
                            colorDA="0000ff";
                            scktDA="110110101101101011011010";
                            ((TextView) findViewById(R.id.gesto)).setText(lugar+"-Foco-Triangulo-EncAlt");
                            encender.start();
                            break;
                    }
                    execPet(colorDA,scktDA);
                }
                return true;
            }
        });
    }

    public double chckCuadro(){
        some[0]= "storage/extSdCard/GC/cuadradon.csv";
        some[1]= path + "/gesto.csv";
        some[2]= "10";
        double result = 0;
        try{
            result = FastDtwTest.reconocimiento(some);
            return result;
        }
        catch (NullPointerException e){
            e.printStackTrace();
            return result;
        }
    }
    public double chckCirculo(){
        some[0]= "storage/extSdCard/GC/circulon.csv";
        some[1]= path + "/gesto.csv";
        some[2]= "10";
        double result = 0;
        try {
            result = FastDtwTest.reconocimiento(some);
            return result;
        }
        catch (NullPointerException e){
            e.printStackTrace();
            return result;
        }
    }
    public double chckTriangulo(){
        some[0]= "storage/extSdCard/GC/triangulon.csv";
        some[1]= path + "/gesto.csv";
        some[2]= "10";
        double result = 0;
        try{
            result = FastDtwTest.reconocimiento(some);
            return result;
        }
        catch (NullPointerException e){
            e.printStackTrace();
            return result;
        }
    }

    public int dtGesto(double rCirc, double rCuad, double rTrian){
        // 1= Circ, 2= Cuad, 3= Trian
        double min;
        min=Math.min(Math.min(rCuad,rCirc),rTrian);
        if (min == rCirc){
            return 1;
        }
        else if (min == rCuad){
            return 2;
        }
        else{
            return 3;
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        sensorOff();
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        synchronized (this) {
            if(stateRedes) {
                curX = event.values[0];
                curY = event.values[1];
                curZ = event.values[2];
                ArrayList<Float> coords = new ArrayList<Float>(3);
                coords.add((float) (Math.round(event.values[0] * 1000.0) / 1000.0));
                coords.add((float) (Math.round(event.values[1] * 1000.0) / 1000.0));
                coords.add((float) (Math.round(event.values[2] * 1000.0) / 1000.0));
                ArrayList<Float> magn = new ArrayList<Float>(1);
                m = Math.sqrt(Math.pow(coords.get(0), 2) + Math.pow(coords.get(1), 2) + Math.pow(coords.get(2), 2));
                magn.add((float) (Math.round(m * 1000.0) / 1000.0));
                magnitud.add(magn);
            }
            cont++;
            if(cont==300){
                detectRed();
                cont=0;
            }
        }
    }

    private class EnvPet extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... urls) {
            try {
                httppost.setEntity(new StringEntity(colourcvt));
                httppost.setHeader("Content-type", "application/json");
                httppostsckt.setEntity(new StringEntity(datascktst));
                httppostsckt.setHeader("Content-type", "application/json");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            try {
                HttpResponse resp = httpclient.execute(httppost);
                HttpEntity ent = resp.getEntity();
                HttpResponse respsckt = httpclientsckt.execute(httppostsckt);
                HttpEntity entsckt = respsckt.getEntity();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
        }
    }
    private class dtcRed extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... urls) {
            manWifi.startScan();
            wifiList = manWifi.getScanResults();
            datos = new int[wifiList.size()];
            try {
                for (int i = 0; i < wifiList.size(); i++) {
                    String item = wifiList.get(i).toString();
                    String[] vector_item = item.split(",");
                    String item_level = vector_item[2];
                    String level = item_level.split(":")[1].split(" ")[1];
                    datos[i] = Integer.parseInt(level);
                }
            } catch (Exception e) {
                System.out.println(e);
            }
            result = new KNN();
            try {
                fdtwr = result.triangulacion(datos);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
        }
    }

    private void cambiarLugar(int id1, int id2, int color1, int color2) {
        ((TextView) findViewById(R.id.lblLugar)).setText(id1);
        imagen.setImageResource(id2);
        principal.setBackgroundColor(color1);
        lugar.setBackgroundColor(color2);
    }

    public void cambiarOficina(){
        detecLugar=1;
        cambiarLugar(R.string.oficina, R.drawable.icon_pers, Color.parseColor("#33cc66"), Color.parseColor("#009933"));
    }

    public void cambiarJuntas(){
        detecLugar=2;
        cambiarLugar(R.string.juntas, R.drawable.icon_pres, Color.parseColor("#e0756e"), Color.parseColor("#a52f25"));
    }

    public void cambiarDescanso(){
        detecLugar=3;
        cambiarLugar(R.string.descanso, R.drawable.icon_tv, Color.parseColor("#52658c"), Color.parseColor("#003399"));
    }
    public void execPet(String DAC, String DAS){
        try{
            colour.put("DA", DAC);
            datasckt.put("DA", DAS);
        }catch(JSONException e){
            e.getCause();
        }
        colourcvt = colour.toString();
        datascktst = datasckt.toString();
        ninja = new EnvPet();
        ninja.execute();
    }
    public double redes(){
        dred = new dtcRed();
        dred.execute();
        return fdtwr;
    }
    public void detectRed(){
        double lg=redes();
        if (lg == 1.00 && detecLugar!=1){
            cambiarOficina();
        }
        else if (lg == 2.00 && detecLugar!=2){
            cambiarJuntas();
        }
        else if (lg == 3.00 && detecLugar!=3){
            cambiarDescanso();
        }
    }
}
