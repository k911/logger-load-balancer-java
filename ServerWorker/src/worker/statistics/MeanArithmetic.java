package ServerWorker.src.worker.statistics;

import java.util.ArrayList;

public class MeanArithmetic {

    public void setList(ArrayList<Long> list) {
        this.list = list;
    }
    ArrayList<Long> list;

    /*below function calculates mean from given integer array. returns MAX_VALUE if array empty
    * TO DISCUSS: what if actual mean exuals MAX_VALUE ?*/
    public static double CalculateArithmeticMean(ArrayList<Long> list) {
        try{
        double sum = 0;
        for (int i = 0; i < list.size(); i++) {
            sum += list.get(i);
        }
        return sum /list.size();}
        catch(java.lang.NullPointerException exception) {
            System.out.println("Array is empty!!");
        }
        return Long.MAX_VALUE;
    }
}
