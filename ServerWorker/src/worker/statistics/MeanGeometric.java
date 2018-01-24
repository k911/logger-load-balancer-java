package worker.statistics;

import java.util.ArrayList;

public class MeanGeometric {

    public void setList(ArrayList<Long> list) {
        this.list = list;
    }
    ArrayList<Long> list;

    public static double CalculateGeometricMean(ArrayList<Long> list) {
        try{
        double result = 1l;
        for (int i = 0; i < list.size(); i++) {
            result *= (Long)(list.get(i));
        }
        result =  Math.pow(result, 1.0/list.size());
        return result;
    }
        catch(java.lang.NullPointerException exception) {
        System.out.println("Array is empty!!");
    }
        return Long.MAX_VALUE;
}
}
