package ServerWorker.src.worker.calculations;

import java.util.ArrayList;
import java.util.Collections;

public class Min {
    public void setList(ArrayList<Long> list) {
        this.list = list;
    }
    ArrayList<Long> list;

    static long CalculateMin(ArrayList<Long> list)
    {
        try {
            return(Collections.min(list));
        }
        catch(java.lang.NullPointerException exception) {
            System.out.println("Array is empty!!");
        }
        return Long.MAX_VALUE;
    }
}
