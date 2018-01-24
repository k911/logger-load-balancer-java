package worker.statistics;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringSearch {
    public void setList(ArrayList<String> list) {
        this.list = list;
    }
    ArrayList<String> list;

    public static int countRepeats(ArrayList<String> list, String wanted){
        Pattern pattern=Pattern.compile(wanted);
        Matcher matcher;
        int count = 0;
        try {
            for( int i = 0; i < list.size(); i++) {
                matcher=pattern.matcher(list.get(i));
                while (matcher.find()) {
                    //System.out.println("Found a match in "+ list.get(i));
                    count++;
                }
            }
        }
        catch(java.lang.NullPointerException exception) {
            System.out.println("Array is empty!!");
        }
        return count;
    }


}
