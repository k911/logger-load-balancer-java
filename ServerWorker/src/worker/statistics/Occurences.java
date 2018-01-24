package ServerWorker.src.worker.statistics;

import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

public class Occurences {

    public void setWanted_element(int wanted_element) {
        this.wanted_element = wanted_element;
    }

    int wanted_element;

    public void setList(ArrayList<Long> list) {
        this.list = list;
    }

    ArrayList<Long> list;


    /* below function counts occurences of given wanted_element. returns number of occurences, 0 if none, or -1 if array empty*/
    public static Long CalculateOccurences(ArrayList<Long> list, Long wanted_element) {
        try{
        Map<Object, Long> counts=counts=list.stream().collect(Collectors.groupingBy(e -> e, Collectors.counting()));
        Long count= counts.get(wanted_element);
        return count;}
        catch(java.lang.NullPointerException exception) {
            System.out.println("Array is empty!!");
        }
        return-1L;
    }
}
