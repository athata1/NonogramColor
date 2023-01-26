import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;

public class TheoryTest {
    public static void main(String[] args) throws Exception{
        String[] rules = "4 1 1 1 1 1 3 1 2 J A E B E J I H J\n".split(" ");
        HashMap<Character, Color> map = new HashMap<Character, Color>();
        map.put('A', new Color(0,0,0));
        map.put('B', new Color(2,4,6));
        map.put('E', new Color(123, 234, 145));
        map.put('J', null);
        map.put('I', null);
        map.put('H', null);
        char[] arr = new char[20];
        Arrays.fill(arr, '_');
        RuleData rd = new RuleData(rules, arr.length, map);
        NonogramProbabilityThread th = new NonogramProbabilityThread(rd, arr, map, null, -1, 0);
        Thread t = new Thread(th);
        t.start();

        t.join();
        System.out.println(Arrays.toString(th.getArr()));

    }
}
