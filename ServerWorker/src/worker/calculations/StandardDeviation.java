package ServerWorker.src.worker.calculations;

import java.util.ArrayList;

public class StandardDeviation {

    public void setList(ArrayList<Long> list) {
        this.list = list;
    }
    ArrayList<Long> list;

    static double CalculateStandardDeviation(ArrayList<Long> list)
    {
       try {
           return Math.sqrt(Variance.CalculateVariance(list));
       }
       catch(java.lang.NullPointerException exception) {
           System.out.println("Array is empty!!");
       }
        return Long.MAX_VALUE;
    }
}
