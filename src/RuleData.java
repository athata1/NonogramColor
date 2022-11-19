import java.util.Arrays;

public class RuleData {

    private int[] numRule;
    private char[] colorRule;
    public RuleData(String[] arr) {
        int size = arr.length/2;
        this.numRule = new int[size];
        this.colorRule = new char[size];

        for (int i = 0; i < size; i++) {
            numRule[i] = Integer.parseInt(arr[i]);
            colorRule[i] = arr[i + size].charAt(0);
        }
    }

    @Override
    public String toString() {
        return "RuleData{" +
                "numRule=" + Arrays.toString(numRule) +
                ", colorRule=" + Arrays.toString(colorRule) +
                '}';
    }
}
