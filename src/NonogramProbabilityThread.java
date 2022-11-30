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
    private ArrayList<RuleData> perpRules;
    private int currIndex;
    public NonogramProbabilityThread(RuleData rd, char[] arr, HashMap<Character, Color> map, ArrayList<RuleData> rowRule, int currIndex) {
        this.currIndex = currIndex;
        this.rd = rd;
        this.arr = arr;
        this.map = map;
        this.prob = new HashMap<>();
        this.perpRules = rowRule;
        for (Map.Entry<Character, Color> entry: map.entrySet()) {
            prob.put(entry.getKey(), new int[arr.length]);
        }
        this.total = 0;
    }

    public void run() {
        if (rd.getNumRule()[0] == 0) {
            Arrays.fill(arr, 'X');
            return;
        }
        if (rd.getStart() > rd.getEnd())
            return;

        findProb(rd.getStartIndex(), rd.getStart(), new ArrayList<Integer>());
        int start = rd.getStart();
        int end = rd.getEnd();
        for (int i = start; i <= end; i++) {
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

        if (ruleIndex == rd.getEndIndex() + 1) {
            char[] temp = new char[arr.length];
            Arrays.fill(temp, 'X');

            /*for (int i = 0; i < rd.getStart(); i++) {
                temp[i] = arr[i];
            }
            for (int i = rd.getStartIndex(); i < numRule.length; i++) {
                for (int j = 0; j < numRule[i]; j++) {
                    temp[j + indexes.get(i - rd.getStartIndex())] = colorRule[i];
                }
            }*/


            int startIndex = rd.getStartIndex();
            int endIndex = rd.getEndIndex();
            for (int i = startIndex; i <= endIndex; i++) {
                for (int j = 0; j < numRule[i]; j++) {
                    temp[j + indexes.get(i - rd.getStartIndex())] = colorRule[i];
                }
            }

            int start = rd.getStart();
            int end = rd.getEnd();
            for (int i = start; i <= end; i++) {
                if (arr[i] == '_')
                    continue;
                if (arr[i] == temp[i])
                    continue;
                else
                    return;
            }
            total++;
            //System.out.println(Arrays.toString(temp));
            for (int i = start; i <= end; i++) {
                if (temp[i] == 'X')
                    continue;

                prob.get(temp[i])[i]++;
            }
            return;
        }

        if (rd.getStartIndex() > rd.getEndIndex())
            return;

        //Get total length of string
        int length = numRule[ruleIndex];
        for (int i = ruleIndex + 1; i <= rd.getEndIndex(); i++) {
            length += numRule[i];
            if (colorRule[i] == colorRule[i - 1])
                length += 1;
        }

        for (int i = arrIndex; i <= rd.getEnd() - length + 1; i++) {

            //Check if num rule can start at this position
            boolean continueFlag = false;
            if (i > 0 && arr[i - 1] == colorRule[ruleIndex]) {
                continue;
            }

            if (i + numRule[ruleIndex] < arr.length && arr[i + numRule[ruleIndex]] == colorRule[ruleIndex]) {
                continue;
            }

            for (int j = 0; j < numRule[ruleIndex] && !continueFlag; j++) {
                if (arr[j + i] != '_' && arr[j + i] != colorRule[ruleIndex]) {
                    continueFlag = true;
                    break;
                }
                RuleData perpendicularRule = perpRules.get(i + j);
                char color = colorRule[ruleIndex];
                int perpStart = perpendicularRule.getStartByColor(color);
                int perpEnd = perpendicularRule.getEndByColor(color);

                if (!perpendicularRule.containsColor(color)) {
                    continueFlag = true;
                }

                if (perpStart > currIndex || perpEnd < currIndex) {
                    //System.out.println(currIndex + " " + perpStart + " " + perpEnd);
                    continueFlag = true;
                }
            }
            if (continueFlag)
                continue;

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
