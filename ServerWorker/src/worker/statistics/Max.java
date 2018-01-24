package worker.statistics;

import java.util.ArrayList;
import java.util.Collections;

public class Max {
    public void setList(ArrayList<Long> list) {
        this.list = list;
    }
    ArrayList<Long> list;

    public static long CalculateMax(ArrayList<Long> list)
    {
        try {
            return(Collections.max(list));
        }
        catch(java.lang.NullPointerException exception) {
            System.out.println("Array is empty!!");
        }
        return Long.MIN_VALUE;
    }
}
