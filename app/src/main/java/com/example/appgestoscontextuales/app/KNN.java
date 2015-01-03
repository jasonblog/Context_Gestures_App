package com.example.appgestoscontextuales.app;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import java.util.*;
import java.io.*;

public class KNN {

    public static double triangulacion(int[] AnArray) throws FileNotFoundException {
//extSdCard/GC
            Scanner iFile = new Scanner(new FileReader("storage/extSdCard/GC/demo.txt"));
            int K = iFile.nextInt();            // K Neighbor
            int nAttrib = iFile.nextInt();      // Number of Attributes or Features
            int nTestData = iFile.nextInt();    // Number of Test Data
            Vector<DataObject> data = new Vector<DataObject>();
            // Read all Test Data with nAttribe Features
            for (int i = 0; i < nTestData; i++) {
                DataObject ob = new DataObject(nAttrib);
                for (int j = 0; j < nAttrib; j++)
                    ob.attrib[j] = iFile.nextDouble();
                // Read the classification of the object
                ob.c = iFile.nextDouble();
                data.add(ob);
            }
            // Read the instance object
            DataObject inst = new DataObject(nAttrib);
            for (int j = 0; j < nAttrib; j++)
                inst.attrib[j] = AnArray[j];
            // Compute for the Distance of all the Test Data
            for (int i = 0; i < nTestData; i++)
                for (int j = 0; j < nAttrib; j++)
                    data.elementAt(i).dist += Math.pow(data.elementAt(i).attrib[j] - inst.attrib[j], 2);
            // Sort all the test data according to distance
            Collections.sort(data);
            //for(int i = 0; i < nTestData; i++)
            //  System.out.println(data.elementAt(i));
            // Rank all the K neighbors
            Vector<Double> gMode = new Vector<Double>();
            double val = data.elementAt(0).dist;
            for (int i = 0, rank = 1; i < nTestData && rank < K; i++) {
                if (val < data.elementAt(i).dist) rank++;
                gMode.add(data.elementAt(i).c);
                //System.out.println(data[i] + " " + rank);
            }
            // Classify the new object
            // If the classification is qualitative, use the MODE
            inst.c = getMode(gMode);        // Find the mode of the neighbors
            //System.out.println("Aqui estoy " + inst.c);
            // If the classification is quantitative, use the AVERAGE
            //inst.c = getAverage(gMode);       // Find the average of the neighbors
            //System.out.println("The new object is classified as: ");
            return (inst.c); //VALOR QUE SE OCUPA********************
            // add to the training data
            //data.add(inst);
    }

    public static double getMode(Vector<Double> data) {
        HashMap<Double, Integer> dict = new HashMap<Double, Integer>();
        for(int i = 0; i < data.size(); i++) {
            double d = data.elementAt(i);
            if(dict.containsKey(d))
                dict.put(d, dict.get(d)+1);
            else
                dict.put(d, 1);
        }
        //System.out.println(dict);
        Double maxMode = null;
        int maxCount = 0;
        Set<Double> keys = dict.keySet();
        for(Double d : keys) {
            //System.out.println("Key: " + d + " : " + dict.get(d));
            int tCount = dict.get(d);
            if(tCount > maxCount) {
                maxCount = tCount;
                maxMode = d;
                //System.out.println("\tSetting mode to " + d);
            }
        }
        return maxMode.doubleValue();
    }

    public static double getAverage(Vector<Double> data) {
        double sum = 0.0;
        for(int i = 0; i < data.size(); i++)
            sum = sum + data.elementAt(i);
        return sum / data.size();
    }
}

class DataObject implements Comparable<DataObject> {
    public double attrib[];
    public double dist, c;
    public DataObject(int nAttrib) {
        this.attrib = new double[nAttrib];
        this.dist = 0;
        this.c = 0;
    }

    public int compareTo(DataObject ob) {
        return Double.compare(this.dist, ob.dist);
    }

    public String toString() {
        String out = new String();
        for (int i = 0; i < attrib.length; i++)
            out = out + attrib[i] + " ";
        return out + this.c;
    }
}

