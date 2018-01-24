package worker.statistics;

import java.util.ArrayList;
import java.util.Collections;

public class Median {


    public void setList(ArrayList<Long> list) {
        this.list = list;
    }
    static ArrayList<Long> list;


    private static void sort(){
        Collections.sort(list, new CustomComparator());
    }

    public static double CalculateMedian(ArrayList<Long> list) {
        try {
            sort();
            int middle = list.size() / 2;
            if (list.size() % 2 == 1) {
                return list.get(middle);
            } else {
                return (list.get(middle - 1) + list.get(middle)) / 2.0;
            }
        }
        catch(java.lang.NullPointerException exception) {
            System.out.println("Array is empty!!");
        }
        return Long.MAX_VALUE;
    }
}
