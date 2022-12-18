import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

public class NonogramColorSolver {

    private ArrayList<RuleData> rowRules;
    private ArrayList<RuleData> colRules;
    private char[][] output;
    private HashMap<Character, Color> charMap;
    private boolean isSlow;

    /**
     *
     * Utilizes folder name to get all data required for solving a Nonogram
     *
     * @param folderName location of where all data files are located
     */

    public NonogramColorSolver(String folderName, boolean isSlow) {
        this.isSlow = isSlow;


        //Get data for colors
        this.charMap = new HashMap<Character, Color>();
        try {
            Scanner scan = new Scanner(new File(folderName + "/colors.txt"));
            while (scan.hasNextLine()) {
                String[] colorData = scan.nextLine().split(" ");
                char colorChar = colorData[0].charAt(0);
                int r = Integer.parseInt(colorData[1]);
                int g = Integer.parseInt(colorData[2]);
                int b = Integer.parseInt(colorData[3]);
                charMap.put(colorChar, new Color(r, g, b));
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Get data for rules
        this.rowRules = new ArrayList<RuleData>();
        this.colRules = new ArrayList<RuleData>();
        try {
            Scanner scan = new Scanner(new File(folderName + "/rules.txt"));

            //Get row rules
            int numRows = Integer.parseInt(scan.nextLine());
            int numCols = Integer.parseInt(scan.nextLine());
            this.output = new char[numRows][numCols];
            for (int i = 0; i < output.length; i++) {
                Arrays.fill(output[i], '_');
            }
            for (int i = 0; i < numRows; i++) {
                String[] data = scan.nextLine().split(" ");
                rowRules.add(new RuleData(data,numCols, charMap));
            }

            //Ensure that row file was properly created
            String s = scan.nextLine();
            assert s.equals("");

            for (int i = 0; i < numCols; i++) {
                String[] data = scan.nextLine().split(" ");
                colRules.add(new RuleData(data, numRows, charMap));
            }

            //Ensure that file ends at column data
            assert !scan.hasNextLine();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public char[][] getBoard() {
        return output;
    }

    public HashMap<Character, Color> getColorMap() {
        return charMap;
    }

    public ArrayList<RuleData> getRowRules() {
        return rowRules;
    }

    public ArrayList<RuleData> getColRules() {
        return colRules;
    }

    /**
     * This function will solve the nonogram as much as statistically possible
     * @return the board will return with as many values as it can
     */
    public char[][] solveNonogram() {

        runThroughFirstBoard();
        printCurrentBoard();
        while (true) {
            char[][] prev = copyOfBoard(output);
            updateRuleData();
            determineNonogram(0);
            if (prevEqualsBoard(prev))
                break;
            printCurrentBoard();
        }

        /*while (true) {
            char[][] prev = copyOfBoard(output);
            updateRuleData();
            determineNonogram(1);
            if (prevEqualsBoard(prev))
                break;
            printCurrentBoard();
        }*/
        return output;
    }

    public void updateColorIndexes() {
        for (RuleData rd: rowRules) {
            rd.updateColorIndexes();
        }
        for (RuleData rd: colRules) {
            rd.updateColorIndexes();
        }
    }

    private char[][] copyOfBoard(char[][] board) {
        char[][] out = new char[board.length][board[0].length];
        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board[r].length; c++) {
                out[r][c] = board[r][c];
            }
        }
        return out;
    }

    /**
     * This method utilizes the Nonogram Probability Thread to find more positions than the preprocessor can
     */
    private void determineNonogram(int n) {

        //Rows
        for (int row = 0; row < output.length; row++) {
            char[] temp = new char[output[0].length];
            for (int j = 0; j < temp.length; j++) {
                temp[j] = output[row][j];
            }
            NonogramProbabilityThread npt = new NonogramProbabilityThread(rowRules.get(row), temp, charMap, colRules, row, n);
            Thread th = new Thread(npt);
            th.start();
            try {
                th.join();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            temp = npt.getArr();
            //System.out.println(row + " " + Arrays.toString(temp));
            for (int j = 0; j < temp.length; j++) {
                if (temp[j] != '_' && output[row][j] == '_') {
                    output[row][j] = temp[j];
                    if (isSlow) {
                        try {
                            Thread.sleep(50);
                        }
                        catch (InterruptedException e) {}
                    }
                }
            }
            //System.out.println(row);
        }

        //Col
        for (int i = 0; i < output[0].length; i++) {
            char[] temp = new char[output.length];
            for (int j = 0; j < output.length; j++) {
                temp[j] = output[j][i];
            }
            NonogramProbabilityThread npt = new NonogramProbabilityThread(colRules.get(i), temp, charMap, rowRules, i, n);
            Thread th = new Thread(npt);
            th.start();
            try {
                th.join();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            temp = npt.getArr();
            for (int j = 0; j < temp.length; j++) {
                if (temp[j] != '_' && output[j][i] =='_') {
                    output[j][i] = temp[j];
                    if (isSlow) {
                        try {
                            Thread.sleep(50);
                        }
                        catch (InterruptedException e) {}
                    }
                }
            }
            //System.out.println(i);
        }
    }

    /**
     * Updates all rule data to new output standards
     * Changes the start, end, start index, and end index
     */
    private void updateRuleData() {
        for (int i = 0; i < rowRules.size(); i++) {
            updateRule(rowRules.get(i), i, true);
        }

        for (int i = 0; i < colRules.size(); i++) {
            updateRule(colRules.get(i), i, false);
        }
    }

    /**
     * helper function for updateRuleData to update individual rules at a time
     * @param rd ruleData being analyzed
     * @param index rule index on board
     * @param isRow true = row Rule, false = col Rule
     */
    private void updateRule(RuleData rd, int index, boolean isRow) {
        if (rd.getStartIndex() > rd.getEndIndex())
            return;
        int limit = (isRow) ? output[0].length: output.length;

        int[] numRules = rd.getNumRule();
        char[] colorRules = rd.getColorRule();
        int startIndex = 0;

        //Update start and startIndex by iterating from left -> right and up -> down
        for (int i = 0; i < limit; i++) {
            if (isRow) {
                //If data is blank, nothing further can be done
                if (output[index][i] == '_') {
                    rd.setStart(i);
                    break;
                }
                else if (output[index][i] == 'X') {
                    continue;
                }//If data equals currentColor rule, add full rule and increment startIndex
                else if (output[index][i] == colorRules[startIndex]) {
                    for (int j = 0; j < numRules[startIndex]; j++) {
                        output[index][i++] = colorRules[startIndex];
                    }
                    if (startIndex + 1 < colorRules.length && colorRules[startIndex] == colorRules[startIndex + 1]) {
                        output[index][i++] = 'X';
                    }
                    i--;
                    startIndex++;
                    rd.setStartIndex(startIndex);
                }
            }
            else {
                if (output[i][index] == '_') {
                    rd.setStart(i);
                    break;
                }
                else if (output[i][index] == 'X') {
                    continue;
                }
                else if (output[i][index] == colorRules[startIndex]) {
                    for (int j = 0; j < numRules[startIndex]; j++) {
                        output[i++][index] = colorRules[startIndex];
                    }
                    if (startIndex + 1 < colorRules.length && colorRules[startIndex] == colorRules[startIndex + 1]) {
                        output[i++][index] = 'X';
                    }
                    i--;
                    startIndex++;
                    rd.setStartIndex(startIndex);
                }
            }
        }

        int endIndex = colorRules.length - 1;
        //Update end and endIndex by iterating from right -> left and down -> up
        for (int i = limit - 1; i >= 0; i--) {
            if (isRow) {
                if (output[index][i] == '_') {
                    rd.setEnd(i);
                    break;
                }
                else if (output[index][i] == 'X') {
                    continue;
                }
                else if (output[index][i] == colorRules[endIndex]) {
                    for (int j = 0; j < numRules[endIndex]; j++) {
                        output[index][i--] = colorRules[endIndex];
                    }
                    if (endIndex - 1 >= 0 && colorRules[endIndex] == colorRules[endIndex - 1]) {
                        output[index][i--] = 'X';
                    }
                    i++;
                    endIndex--;
                    rd.setEndIndex(endIndex);

                }
            }
            else {
                if (output[i][index] == '_') {
                    rd.setEnd(i);
                    break;
                }
                else if (output[i][index] == 'X') {
                    continue;
                }
                else if (output[i][index] == colorRules[endIndex]) {
                    for (int j = 0; j < numRules[endIndex]; j++) {
                        output[i--][index] = colorRules[endIndex];
                    }
                    if (endIndex - 1 >= 0 && colorRules[endIndex] == colorRules[endIndex - 1]) {
                        output[i--][index] = 'X';
                    }
                    i++;
                    endIndex--;
                    rd.setEndIndex(endIndex);
                }
            }
        }
    }

    private boolean prevEqualsBoard(char[][] prev) {
        for (int r = 0; r < prev.length; r++) {
            for (int c = 0; c < prev[r].length; c++) {
                if (prev[r][c] != output[r][c])
                    return false;
            }
        }
        return true;
    }
    /**
     * This function acts as a preprocessor that finds as many guaranteed board states as
     * possible before running the main portion of the algorithm
     */
    private void runThroughFirstBoard() {
        runRule1();
        runRule2();
        runRule3();
    }

    /**
     * This subprocess prints out the current board that has been solved into text format
     */
    public void printCurrentBoard() {
        for (char[] row: output) {
            for (char pos: row) {
                System.out.print(pos + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    /**
     * Runs through all rules that only have length of 1 and finds all locations where
     * values must exist
     */
    private void runRule1() {

        //Apply rule 1 onto rows
        char[][] temp1 = new char[output.length][output[0].length];
        for (int r = 0; r < temp1.length; r++)
        {
            Arrays.fill(temp1[r], ' ');
        }

        for (int i = 0; i < rowRules.size(); i++) {
            RuleData rd = rowRules.get(i);
            int[] rule = rd.getNumRule();
            char[] color = rd.getColorRule();

            //Only apply rule 1 to rows with length of 1
            if (rule.length == 1) {
                int ruleVal = output[0].length - rule[0];
                for (int j = ruleVal; j < output[0].length - ruleVal; j++) {
                    temp1[i][j] = color[0];
                }
            }
        }

        //Apply rule 1 onto all columns
        char[][] temp2 = new char[output.length][output[0].length];
        for (int r = 0; r < temp1.length; r++)
        {
            Arrays.fill(temp2[r], ' ');
        }
        for (int i = 0; i < colRules.size(); i++) {
            RuleData rd = colRules.get(i);
            int[] rule = rd.getNumRule();
            char[] color = rd.getColorRule();

            //Only apply rule 1 to rows with length of 1
            if (rule.length == 1) {
                int ruleVal = output.length - rule[0];
                for (int j = ruleVal; j < output.length - ruleVal; j++) {
                    temp2[j][i] = color[0];
                }
            }
        }

        //Find all newfound values into output and determine if valid
        for (int r = 0; r < output.length; r++) {
            for (int c = 0; c < output[r].length; c++) {
                if (temp1[r][c] != ' ') {
                    output[r][c] = temp1[r][c];
                }
                else if (temp2[r][c] != ' ') {
                    output[r][c] = temp2[r][c];
                }
            }
        }
    }

    /**
     * This method is meant for rules with more than 1 number in the rule
     * and find locations where values are meant to exist
     */
    private void runRule2() {

        char[][] temp = new char[output.length][output[0].length];

        for (int i = 0 ; i < temp.length; i++) {
            Arrays.fill(temp[i], ' ');
        }

        for (int r = 0; r < output.length; r++) {
            RuleData rd = rowRules.get(r);
            int[] numRule = rd.getNumRule();
            char[] colorRule = rd.getColorRule();

            char[] temp1 = new char[output[0].length];

            int[] start = new int[numRule.length];
            int[] end = new int[numRule.length];

            int index = 0;
            for (int i = 0; i < numRule.length; i++) {
                start[i] = index;
                index += numRule[i];
                if (i + 1 < numRule.length && colorRule[i] == colorRule[i + 1]) {
                    i++;
                }
            }

            index = output[0].length - 1;
            for (int i = numRule.length - 1; i >= 0; i--) {
                end[i] = index;
                index -= numRule[i];
                if (i > 0 && colorRule[i] == colorRule[i - 1])
                    index--;
            }

            for (int i = 0; i < start.length; i++) {
                for (int j = start[i]; j <= end[i]; j++) {
                    temp1[j] = '.';
                }
                int val = (end[i] - start[i] + 1) - numRule[i];
                if (val > numRule[i])
                    continue;

                index = start[i];
                for (int j = 0; j < val; j++) {
                    index++;
                }
                for (int j = 0; j < numRule[i] - val; j++) {
                    output[r][index++] = colorRule[i];
                }
            }

            for (int i = 0; i < temp1.length; i++) {
                if (temp1[i] != '.') {
                    output[r][i] = 'X';
                }
            }
        }

        for (int c = 0; c < output[0].length; c++) {
            RuleData rd = colRules.get(c);
            int[] numRule = rd.getNumRule();
            char[] colorRule = rd.getColorRule();

            int[] start = new int[numRule.length];
            int[] end = new int[numRule.length];

            int[] temp1 = new int[output.length];

            int index = 0;
            for (int i = 0; i < numRule.length; i++) {
                start[i] = index;
                index += numRule[i];
                if (i + 1 < numRule.length && colorRule[i] == colorRule[i + 1]) {
                    i++;
                }
            }

            index = output.length - 1;
            for (int i = numRule.length - 1; i >= 0; i--) {
                end[i] = index;
                index -= numRule[i];
                if (i > 0 && colorRule[i] == colorRule[i - 1])
                    index--;
            }

            for (int i = 0; i < start.length; i++) {
                for (int j = start[i]; j <= end[i]; j++) {
                    temp1[j] = '.';
                }
                int val = (end[i] - start[i] + 1) - numRule[i];
                if (val > numRule[i])
                    continue;

                index = start[i];
                for (int j = 0; j < val; j++) {
                    index++;
                }
                for (int j = 0; j < numRule[i] - val; j++) {
                    output[index++][c] = colorRule[i];
                }
            }

            for (int i = 0; i < temp1.length; i++) {
                if (temp1[i] != '.') {
                    output[i][c] = 'X';
                }
            }
        }
    }

    public void runRule3() {
        for (int r = 0; r < output.length; r++) {
            if (output[r][0] != '_') {
                RuleData rd = rowRules.get(r);
                int numData = rd.getNumRule()[0];
                char color = rd.getColorRule()[0];
                for (int c = 0; c < numData; c++) {
                    output[r][c] = color;
                }
            }
            if (output[r][output[0].length - 1] != '_') {
                RuleData rd = rowRules.get(r);
                int numData = rd.getNumRule()[rd.getNumRule().length - 1];
                char color = rd.getColorRule()[rd.getColorRule().length - 1];
                for (int c = 0; c < numData; c++) {
                    output[r][output[0].length - 1 - c] = color;
                }
            }
        }

        for (int c = 0; c < output[0].length; c++) {
            if (output[0][c] != '_') {
                RuleData rd = colRules.get(c);
                int numData = rd.getNumRule()[0];
                char color = rd.getColorRule()[0];
                for (int r = 0; r < numData; r++) {
                    output[r][c] = color;
                }
            }
            if (output[output.length - 1][c] != '_') {
                RuleData rd = colRules.get(c);
                int numData = rd.getNumRule()[rd.getNumRule().length - 1];
                char color = rd.getColorRule()[rd.getColorRule().length - 1];
                for (int r = 0; r < numData; r++) {
                    output[output.length - 1 - r][c] = color;
                }
            }
        }
    }
}
