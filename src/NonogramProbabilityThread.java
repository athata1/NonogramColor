import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class NonogramProbabilityThread implements Runnable{


    private char[] arr;
    private RuleData rd;
    private HashMap<Character, int[]> prob;
    private HashMap<Character, Color> map;
    private int total;
    public NonogramProbabilityThread(RuleData rd, char[] arr, HashMap<Character, Color> map) {
        this.rd = rd;
        this.arr = arr;
        this.map = map;
        this.prob = new HashMap<>();
        for (Map.Entry<Character, Color> entry: map.entrySet()) {
            prob.put(entry.getKey(), new int[arr.length]);
        }
        this.total = 0;
    }

    public void run() {
        if (rd.getNumRule()[0] == 0) {
            return;
        }
        if (rd.getStart() > rd.getEnd())
            return;
        findProb(rd.getStartIndex(), rd.getStart(), new ArrayList<Integer>());
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] != '_')
                continue;

            int countZeroes = 0;
            for (Map.Entry<Character, Color> entry: map.entrySet()) {
                int counter = prob.get(entry.getKey())[i];
                if (counter == 0) {
                    countZeroes++;
                }
                else if (counter == total) {
                    arr[i] = entry.getKey();
                    break;
                }
                else {
                    break;
                }
            }
            if (countZeroes == prob.size()) {
                arr[i] = 'X';
            }
        }

    }

    public void findProb(int ruleIndex, int arrIndex, ArrayList<Integer> indexes) {

        int[] numRule = rd.getNumRule();
        char[] colorRule = rd.getColorRule();

        if (ruleIndex == numRule.length) {
            char[] temp = new char[arr.length];
            Arrays.fill(temp, 'X');

            for (int i = 0; i < rd.getStart(); i++) {
                temp[i] = arr[i];
            }
            for (int i = rd.getStartIndex(); i <= rd.getEndIndex(); i++) {
                for (int j = 0; j < numRule[i]; j++) {
                    temp[j + indexes.get(i - rd.getStartIndex())] = colorRule[i];
                }
            }

            for (int i = rd.getEnd() + 1; i < arr.length; i++) {
                temp[i] = arr[i];
            }

            for (int i = 0; i < arr.length; i++) {
                if (arr[i] == '_')
                    continue;
                if (arr[i] == temp[i])
                    continue;
                else
                    return;
            }
            total++;
            //System.out.println(Arrays.toString(temp));
            for (int i = 0; i < temp.length; i++) {
                if (temp[i] == 'X')
                    continue;

                prob.get(temp[i])[i]++;
            }
            return;
        }

        //Get total length of string
        int length = numRule[ruleIndex];
        for (int i = ruleIndex + 1; i < numRule.length; i++) {
            length += numRule[i];
            if (colorRule[i] == colorRule[i - 1])
                length += 1;
        }

        for (int i = arrIndex; i < arr.length - length + 1; i++) {
            indexes.add(i);
            int nextStart = i + numRule[ruleIndex];
            if (ruleIndex + 1 < numRule.length && colorRule[ruleIndex] == colorRule[ruleIndex + 1]) {
                nextStart++;
            }
            findProb(ruleIndex + 1, nextStart, indexes);
            indexes.remove(indexes.size() - 1);
        }
    }

    public char[] getArr() {
        return arr;
    }
}
