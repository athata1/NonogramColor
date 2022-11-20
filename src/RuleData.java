import java.util.Arrays;

public class RuleData {

    private int[] numRule;
    private char[] colorRule;
    private int start;
    private int end;
    public RuleData(String[] arr, int length) {
        start = 0;
        end = length - 1;
        int size = arr.length/2;
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
