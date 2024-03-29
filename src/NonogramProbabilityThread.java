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

    private long start;
    public void run() {
        if (rd.getNumRule()[0] == 0) {
            Arrays.fill(arr, 'X');
            return;
        }
        if (rd.getStart() > rd.getEnd())
            return;

        start = System.currentTimeMillis();
        if (version == 0)
            determineProb2();
        else if(version == 1)
            determineProb();

    }

    private boolean found;
    private boolean ignore;
    public void determineProb2() {
        ignore = false;
        int[] numRules = rd.getNumRule();
        char[] colorRules = rd.getColorRule();
        int[][] bounds = new int[numRules.length][2];
        found = false;
        findUpperBound(bounds, rd.getStartIndex(), rd.getStart());
        if (ignore) {
            return;
        }
        found = false;
        findLowerBound(bounds, rd.getEndIndex(), rd.getEnd());
        if (ignore)
            return;

        int[] xChecker = new int[arr.length];
        for (int i = rd.getStartIndex(); i <= rd.getEndIndex(); i++) {
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
        if (System.currentTimeMillis() - start > 5_000) {
            ignore = true;
            System.out.println("Ignored");
            return;
        }

        int[] numRule = rd.getNumRule();
        char[] colorRule = rd.getColorRule();
        if (index < rd.getStartIndex()) {
            char[] temp = new char[arr.length];
            Arrays.fill(temp, 'X');
            for (int i = rd.getStartIndex(); i <= rd.getEndIndex(); i++) {
                for (int j = 0; j < numRule[i]; j++) {
                    temp[bounds[i][1] - j] = colorRule[i];
                }
            }

            for (int i = rd.getStart(); i <= rd.getEnd(); i++) {
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
        for (int i = index - 1; i >= rd.getStartIndex(); i--) {
            length += numRule[i];
            if (colorRule[i] == colorRule[i + 1])
                length += 1;
        }

        for (int i = arrIndex; i >= rd.getStart() + length - 1 && !found && !ignore; i--) {

            boolean continueFlag = false;
            if (i + 1 < rd.getEnd() + 1 && arr[i + 1] == colorRule[index]) {
                continue;
            }

            for (int j = 0; j < numRule[index] && !continueFlag && !ignore; j++) {
                if (arr[i - j] != '_' && arr[i - j] != colorRule[index]) {
                    i -= j;
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

            if (i - numRule[index] >= rd.getStart() && arr[i - numRule[index]] == colorRule[index]) {
                continue;
            }

            bounds[index][1] = i;
            findLowerBound(bounds, index - 1, i - numRule[index] - ((index - 1 >= 0 && colorRule[index] == colorRule[index - 1]) ? 1 : 0));
            if (found)
                break;
            bounds[index][1] = 0;
        }
    }

    public void findUpperBound(int[][] bounds, int index, int arrIndex) {
        if (System.currentTimeMillis() - start > 5_000) {
            ignore = true;
            System.out.println("Ignored");
            return;
        }
        int[] numRule = rd.getNumRule();
        char[] colorRule = rd.getColorRule();
        if (index > rd.getEndIndex()) {
            char[] temp = new char[arr.length];
            Arrays.fill(temp, 'X');
            for (int i = rd.getStartIndex(); i <= rd.getEndIndex(); i++) {
                for (int j = 0; j < numRule[i]; j++) {
                    temp[bounds[i][0] + j] = colorRule[i];
                }
            }

            for (int i = rd.getStart(); i <= rd.getEnd(); i++) {
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
        for (int i = index + 1; i <= rd.getEndIndex(); i++) {
            length += numRule[i];
            if (colorRule[i] == colorRule[i - 1])
                length += 1;
        }

        for (int i = arrIndex; i < rd.getEnd() + 1 - length + 1 && !found && !ignore; i++) {

            boolean continueFlag = false;
            if (i - 1 >= rd.getStart() && arr[i - 1] == colorRule[index]) {
                continue;
            }

            for (int j = 0; j < numRule[index] && !continueFlag && !ignore; j++) {
                if (arr[i + j] != '_' && arr[i + j] != colorRule[index]) {
                    i += j;
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

            if (i + numRule[index] < rd.getEnd() + 1 && arr[i + numRule[index]] == colorRule[index]) {
                continue;
            }

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
