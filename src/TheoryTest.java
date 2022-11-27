import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;

public class TheoryTest {
    public static void main(String[] args) throws Exception{
        String[] rules = "1 1 1 1 1 2 1 5 1 2 1 A C A A A E J E J E A".split(" ");
        HashMap<Character, Color> map = new HashMap<Character, Color>();
        map.put('A', new Color(0,0,0));
        map.put('E', new Color(2,4,6));
        map.put('C', new Color(123, 234, 145));
        map.put('J', null);
        char[] arr = new char[20];
        Arrays.fill(arr, '_');
        RuleData rd = new RuleData(rules, arr.length);
        NonogramProbabilityThread th = new NonogramProbabilityThread(rd, arr, map);
        Thread t = new Thread(th);
        t.start();

        t.join();
        System.out.println(Arrays.toString(th.getArr()));

    }
}
