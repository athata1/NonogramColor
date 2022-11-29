import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;

public class RuleData {

    private int[] numRule;
    private char[] colorRule;
    private int startIndex;
    private int endIndex;
    private int start;
    private int end;
    private HashMap<Character, int[]> instanceMap;
    public RuleData(String[] arr, int length, HashMap<Character, Color> map) {
        this.instanceMap = new HashMap<>();
        for (Character color: map.keySet()) {
            instanceMap.put(color, new int[]{-1,-1});
        }
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

    public void getColorIndexes() {
        int index = start;
        for (int i = startIndex; i <= endIndex; i++) {
            instanceMap.get(colorRule[i])[0] = index;
            index += numRule[i];
        }

        index = end;
        for (int i = endIndex; i >= start; i--) {
            instanceMap.get(colorRule[i])[1] = index;
            index -= numRule[i];
        }
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
