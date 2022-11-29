import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class RuleData {

    private int[] numRule;
    private char[] colorRule;
    private int startIndex;
    private int endIndex;
    private int start;
    private int end;
    private HashMap<Character, int[]> instanceMap;
    private HashSet<Character> colorsInRow;
    public RuleData(String[] arr, int length, HashMap<Character, Color> map) {
        this.instanceMap = new HashMap<>();
        for (Character color: map.keySet()) {
            instanceMap.put(color, new int[]{-1,-1});
        }
        this.colorsInRow = new HashSet<>();
        startIndex = 0;
        this.start = 0;
        this.end = length - 1;
        int size = arr.length/2;
        endIndex = size - 1;
        this.numRule = new int[size];
        this.colorRule = new char[size];

        for (int i = 0; i < size; i++) {
            numRule[i] = Integer.parseInt(arr[i]);
            colorRule[i] = arr[i + size].charAt(0);
        }
        updateColorIndexes();
    }

    public int[] getNumRule() {
        return numRule;
    }

    public char[] getColorRule() {
        return colorRule;
    }

    public int getStart() {
        return start;
    }

    /**
     * Reset all colorIndexes once the start, end, startIndex, and endIndex have all been updated
     */
    public void updateColorIndexes() {
        if (startIndex > endIndex)
            return;
        if (start > end)
            return;


        int length = 0;
        for (int i = startIndex; i <= endIndex; i++) {
            length += numRule[i];
            if (i + 1 <= endIndex && colorRule[i] == colorRule[i+1])
                length++;
            colorsInRow.add(colorRule[i]);
        }

        char[] arr = new char[length];
        Arrays.fill(arr, ' ');
        int index = 0;
        for (int i = startIndex; i <= endIndex; i++) {
            int num = numRule[i];
            char color = colorRule[i];
            for (int j = 0; j < num; j++) {
                arr[index++] = color;
            }
            if (i + 1 <= endIndex && colorRule[i] == colorRule[i + 1])
                index++;
        }

        for (int i = arr.length - 1; i >= 0; i--) {
            if (arr[i] == ' ')
                continue;
            instanceMap.get(arr[i])[0] = i + start;
        }

        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == ' ')
                continue;
            instanceMap.get(arr[i])[1] = end - (arr.length - 1 - i);
        }
    }

    public int getStartByColor(char c) {
        return instanceMap.get(c)[0];
    }

    public boolean containsColor(char c) {
        return colorsInRow.contains(c);
    }

    public int getEndByColor(char c) {
        return instanceMap.get(c)[1];
    }

    public void setNumRule(int[] numRule) {
        this.numRule = numRule;
    }

    public void setColorRule(char[] colorRule) {
        this.colorRule = colorRule;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public int getEnd() {
        return end;
    }

    @Override
    public String toString() {
        return "RuleData{" +
                "numRule=" + Arrays.toString(numRule) +
                ", colorRule=" + Arrays.toString(colorRule) +
                '}';
    }
}
