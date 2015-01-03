package com.example.appgestoscontextuales.app;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.io.*;
import java.io.IOException;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.example.appgestoscontextuales.app.KNN;

public class WifiList extends Activity{

	private Element[] nets;
 	private WifiManager manWifi;
	private List<ScanResult> wifiList;
    private int [] datos = new int[6];

    public void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        this.manWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        this.manWifi.startScan();
        this.wifiList = this.manWifi.getScanResults();
        this.nets = new Element[wifiList.size()];

        try {
                for (int i = 0; i < wifiList.size(); i++) {


                    String item = wifiList.get(i).toString();
                    String[] vector_item = item.split(",");
                    String item_essid = vector_item[0];
                    String item_capabilities = vector_item[2];
                    String item_bssid = vector_item[1];
                    String item_ssid = vector_item[3];
                    String item_level = vector_item[4];
                    String item_frecuency = vector_item[5];

                    String ssid = item_essid.split(":")[1];
                    String security = item_capabilities.split(":")[1];
                    String mac = item_bssid.substring(7).split(" ")[1];
                    String bssid = item_ssid.split(":")[1].substring(1);
                    String ip = item_level.split(":")[1].split(" ")[1];
                    String rssi = item_frecuency.split(":")[1].split(" ")[1];

                    datos[i] = Integer.parseInt(bssid);
                    System.out.println(datos[i]);

                    nets[i] = new Element(ssid, security, ip, bssid, rssi, mac);
                }
        } catch (Exception e) {
            System.out.println(e);
        }

        if (datos == null)
        {
            System.out.println("El arreglo es null");
        }
        else
           System.out.println(datos);
        KNN result = new KNN();

        System.out.println(result.toString());
        try {
            double algo = KNN.triangulacion(datos);
            if (algo == 1.0){
            System.out.println("LAB A " + algo);}
            else if (algo == 2.0){
                System.out.println("JUNTAS " + algo);
            }
            else if (algo == 3.0){
                System.out.println("POSTGRADO " + algo);
            }
            else
                System.out.println("Fuera del rango " + algo);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
