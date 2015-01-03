package com;

import android.content.Context;
/*
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.PrintWriter;*/
import android.content.res.*;
import java.io.*;
import com.example.appgestoscontextuales.app.R;

/**
 * Created by root on 11/07/14.
 */
public class CrearArch {

    public static void escribir(){
        String[] algo= new String[3];
        algo[0]="some";
        algo[1]="some2";
        algo[2]="some3";
        try
        {
/*            InputStream j = getResources().openRawResource(R.raw.circ1);
*/          PrintWriter pr = new PrintWriter("algo.txt");
            for (int i=0; i<algo.length ; i++)
            {
                pr.println(algo[i]);
            }
            pr.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("No such file exists.");
        }
    }
    public static void crear(){
        try {
            /*String FILENAME = "hello_file";
            String string = "hello world!";

            FileOutputStream fos = ;
            fos.write(string.getBytes());
            fos.close();
           /*
            PrintWriter writer = new PrintWriter("new.txt", "UTF-8");
            writer.println("The first line");
            writer.println("The second line");
            writer.close();
            System.out.print("se creo");*/
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("No se pudo crear.");
        }
    }
    private void ejemplo() throws IOException {
        String source = "Now is the time for all good men\\n"
                + " to come to the aid of their country\\n"
                + " and pay their due taxes.";
        char buffer[] = new char[source.length()];
        source.getChars(0, source.length(), buffer, 0);
        FileWriter f0 = new FileWriter("file1.txt");
        System.out.println("SE CREO");
        for (int i=0; i < buffer.length; i += 2) {
            f0.write(buffer[i]);
        }
        f0.close();
        FileWriter f1 = new FileWriter("file2.txt");
        f1.write(buffer);
        f1.close();
        FileWriter f2 = new FileWriter("file3.txt");
        f2.write(buffer,buffer.length/4,buffer.length/4);
        f2.close();

    }

    public static void leer(String path) throws IOException {
        FileReader fr = new FileReader(path+"/file2.txt");
        BufferedReader br = new BufferedReader(fr);
        String s;
        while((s = br.readLine()) != null) {
            System.out.println(s);
        }
        fr.close();
    }



}
