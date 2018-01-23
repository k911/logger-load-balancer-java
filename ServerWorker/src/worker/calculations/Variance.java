package ServerWorker.src.worker.calculations;

import java.util.ArrayList;

public class Variance {

    public void setList(ArrayList<Long> list) {
        this.list = list;
    }
    ArrayList<Long> list;

    /* below function returns Variance, unless list empty (then Long.MAX_VALUE)*/
    public static double CalculateVariance(ArrayList<Long> list)
    {
        try {
            double mean = MeanArithmetic.CalculateArithmeticMean(list);
            double temp = 0;
            for (double a : list)
                temp += (a - mean) * (a - mean);
            return temp / (list.size() - 1);
        }
        catch(java.lang.NullPointerException exception) {
            //System.out.println("Array is empty!!"); not needed, as Mean will already print this info
        }
        return Long.MAX_VALUE;
    }
}
