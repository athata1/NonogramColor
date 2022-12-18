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
    private int version;
    public NonogramProbabilityThread(RuleData rd, char[] arr, HashMap<Character, Color> map, ArrayList<RuleData> rowRule, int currIndex, int version) {
        this.version = version;
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

        if (version == 0)
            determineProb2();
        else if(version == 1)
            determineProb();

    }

    private boolean found;
    public void determineProb2() {
        int[] numRules = rd.getNumRule();
        char[] colorRules = rd.getColorRule();
        int[][] bounds = new int[numRules.length][2];
        found = false;
        findUpperBound(bounds, 0, 0);
        found = false;
        findLowerBound(bounds, bounds.length - 1, arr.length - 1);

        int[] xChecker = new int[arr.length];
        for (int i = 0; i < bounds.length; i++) {
            int diff = bounds[i][1] - bounds[i][0] + 1 - numRules[i];
            for (int j = 0; j < numRules[i] - diff; j++) {
                arr[bounds[i][0] + diff + j] = colorRules[i];
            }
            for (int j = bounds[i][0]; j <= bounds[i][1]; j++) {
                xChecker[j] = 1;
            }
        }
        for (int i = 0; i < xChecker.length; i++) {
            if (xChecker[i] != 1) {
                arr[i] = 'X';
            }
        }
    }

    public void findLowerBound(int[][] bounds, int index, int arrIndex) {
        int[] numRule = rd.getNumRule();
        char[] colorRule = rd.getColorRule();
        if (index == -1) {
            char[] temp = new char[arr.length];
            Arrays.fill(temp, 'X');
            for (int i = 0; i < bounds.length; i++) {
                for (int j = 0; j < numRule[i]; j++) {
                    temp[bounds[i][1] - j] = colorRule[i];
                }
            }

            for (int i = 0; i < temp.length; i++) {
                if (arr[i] == '_')
                    continue;
                if (arr[i] == temp[i])
                    continue;
                else
                    return;
            }
            found = true;
            return;
        }

        if (found)
            return;


        int length = numRule[index];
        for (int i = index - 1; i >= 0; i--) {
            length += numRule[i];
            if (colorRule[i] == colorRule[i + 1])
                length += 1;
        }

        for (int i = arrIndex; i >= length - 1 && !found; i--) {

            boolean continueFlag = false;
            if (i + 1 < arr.length && arr[i + 1] == colorRule[index]) {
                continue;
            }

            if (i - numRule[index] >= 0 && arr[i - numRule[index]] == colorRule[index]) {
                continue;
            }
            for (int j = 0; j < numRule[index]; j++) {
                if (arr[i - j] != '_' && arr[i - j] != colorRule[index]) {
                    continueFlag = true;
                    break;
                }

                RuleData perpendicularRule = perpRules.get(i - j);
                char color = colorRule[index];
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

            bounds[index][1] = i;
            findLowerBound(bounds, index - 1, i - numRule[index] - ((index - 1 >= 0 && colorRule[index] == colorRule[index - 1]) ? 1 : 0));
            if (found)
                break;
            bounds[index][1] = 0;
        }
    }

    public void findUpperBound(int[][] bounds, int index, int arrIndex) {
        int[] numRule = rd.getNumRule();
        char[] colorRule = rd.getColorRule();
        if (index == bounds.length) {
            char[] temp = new char[arr.length];
            Arrays.fill(temp, 'X');
            for (int i = 0; i < bounds.length; i++) {
                for (int j = 0; j < numRule[i]; j++) {
                    temp[bounds[i][0] + j] = colorRule[i];
                }
            }

            for (int i = 0; i < temp.length; i++) {
                if (arr[i] == '_')
                    continue;
                if (arr[i] == temp[i])
                    continue;
                else
                    return;
            }

            found = true;
            return;
        }
        if (found)
            return;

        int length = numRule[index];
        for (int i = index + 1; i < numRule.length; i++) {
            length += numRule[i];
            if (colorRule[i] == colorRule[i - 1])
                length += 1;
        }

        for (int i = arrIndex; i < arr.length - length + 1 && !found; i++) {

            boolean continueFlag = false;
            if (i - 1 >= 0 && arr[i - 1] == colorRule[index]) {
                continue;
            }

            if (i + numRule[index] < arr.length && arr[i + numRule[index]] == colorRule[index]) {
                continue;
            }
            for (int j = 0; j < numRule[index]; j++) {
                if (arr[i + j] != '_' && arr[i + j] != colorRule[index]) {
                    continueFlag = true;
                    break;
                }
                RuleData perpendicularRule = perpRules.get(i + j);
                char color = colorRule[index];
                int perpStart = perpendicularRule.getStartByColor(color);
                int perpEnd = perpendicularRule.getEndByColor(color);

                if (!perpendicularRule.containsColor(color)) {
                    continueFlag = true;
                }

                if (perpStart > currIndex || perpEnd < currIndex) {
                    continueFlag = true;
                }
            }

            if (continueFlag)
                continue;

            bounds[index][0] = i;

            findUpperBound(bounds, index + 1, i + numRule[index] + ((index + 1 < numRule.length && colorRule[index] == colorRule[index + 1]) ? 1 : 0));
            if (found)
                break;
            bounds[index][0] = 0;
        }
    }

    public void determineProb() {
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
