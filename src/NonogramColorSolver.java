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

    /**
     *
     * Utilizes folder name to get all data required for solving a Nonogram
     *
     * @param folderName location of where all data files are located
     */

    public NonogramColorSolver(String folderName) {

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
                rowRules.add(new RuleData(data,numCols));
            }

            //Ensure that row file was properly created
            String s = scan.nextLine();
            assert s.equals("");

            for (int i = 0; i < numCols; i++) {
                String[] data = scan.nextLine().split(" ");
                colRules.add(new RuleData(data, numRows));
            }

            //Ensure that file ends at column data
            assert !scan.hasNextLine();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * This function will solve the nonogram as much as statistically possible
     * @return the board will return with as many values as it can
     */
    public char[][] solveNonogram() {

        runThroughFirstBoard();
        return output;
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

        //Apply rule 2 to rows
        char[][] temp1 = new char[output.length][output[0].length];
        for (int r = 0; r < temp1.length; r++)
        {
            Arrays.fill(temp1[r], ' ');
        }


        for (int i = 0; i < rowRules.size(); i++) {
            RuleData rd = rowRules.get(i);
            int[] numRule = rd.getNumRule();
            char[] colorRule = rd.getColorRule();

            if (numRule.length == 1)
                continue;

            int spacesLeft = output.length;
            for (int j = 0; j < numRule.length; j++) {
                spacesLeft -= numRule[j];
            }

            int spacesPerGap = spacesLeft / (numRule.length - 1);
            int remainder = spacesLeft % (numRule.length - 1);
            int placeholderRemainder = remainder;

            //Input rule sequence as evenly separated as possible
            int index = output[0].length - 1;
            for (int c = numRule.length - 1; c >= 0; c--) {
                for (int j = 0; j < numRule[c]; j++) {
                    temp1[i][index] = colorRule[c];
                    index--;
                }
                index -= spacesPerGap;
                if (remainder != 0) {
                    remainder--;
                    index--;
                }
            }

            remainder = placeholderRemainder;

            //Find difference value of rules array
            //Difference value is sum or rules + 1 if two same colors are sdjacent
            int diff = 0;
            for (int j = 1; j < numRule.length; j++) {
                if (colorRule[j] == colorRule[j - 1]) {
                    diff++;
                }
            }

            //Simplified version of output[0].length - (output[0].length - spacesLeft + total)
            diff = spacesLeft - diff;

            index = output[0].length - 1;
            for (int c = numRule.length - 1; c >= 0; c--) {
                //If diff > current rule, remove all data in temp for this rule
                if (diff > numRule[c]) {
                    for (int j = 0; j < numRule[c]; j++) {
                        temp1[i][index] = ' ';
                        index--;
                    }
                    index -= spacesPerGap;
                    if (remainder != 0) {
                        remainder--;
                        index--;
                    }
                    continue;
                }

                //Bound check for last rule
                if (c == numRule.length - 1) {
                    for (int j = 0; j < diff; j++) {
                        temp1[i][index] = ' ';
                        index--;
                    }

                    //for (int j = 0; j < numRule[c] - diff; j++) {
                      //  index--;
                    //}

                    index -= numRule[c] - diff;
                    index -= spacesPerGap;
                    if (remainder != 0) {
                        remainder--;
                        index--;
                    }
                    continue;
                }

                //Bound check for first rule in list
                if (c == 0) {
                    for (int j = 0; j < diff; j++) {
                        temp1[i][j] = ' ';
                    }
                    continue;
                }

                //Rule is in the middle of the field
                int firstHalf = diff / 2;
                int secondHalf = diff / 2 + diff % 2;
                for (int j = 0; j < firstHalf; j++) {
                    temp1[i][index] = ' ';
                    index--;
                }

                //for (int j = 0; j < numRule[c] - diff; j++) {
                  //  index--;
                //}
                index -= numRule[c] - diff;
                for (int j = 0; j < secondHalf; j++) {
                    temp1[i][index] = ' ';
                    index--;
                }
                index -= spacesPerGap;
                if (remainder != 0) {
                    remainder--;
                    index--;
                }
            }
        }

        //Apply rule 2 to columns
        char[][] temp2 = new char[output.length][output[0].length];
        for (int r = 0; r < temp2.length; r++)
        {
            Arrays.fill(temp2[r], ' ');
        }

        for (int i = 0; i < colRules.size(); i++) {
            RuleData rd = colRules.get(i);
            int[] numRule = rd.getNumRule();
            char[] colorRule = rd.getColorRule();

            if (numRule.length == 1)
                continue;

            int spacesLeft = output.length;
            for (int j = 0; j < numRule.length; j++) {
                spacesLeft -= numRule[j];
            }

            int spacesPerGap = spacesLeft / (numRule.length - 1);
            int remainder = spacesLeft % (numRule.length - 1);
            int placeholderRemainder = remainder;

            //Input rule sequence as evenly separated as possible
            int index = output.length - 1;
            for (int c = numRule.length - 1; c >= 0; c--) {
                for (int j = 0; j < numRule[c]; j++) {
                    temp2[index][i] = colorRule[c];
                    index--;
                }
                index -= spacesPerGap;
                if (remainder != 0) {
                    remainder--;
                    index--;
                }
            }

            remainder = placeholderRemainder;

            //Find difference value of rules array
            //Difference value is sum or rules + 1 if two same colors are sdjacent
            int diff = 0;
            for (int j = 1; j < numRule.length; j++) {
                if (colorRule[j] == colorRule[j - 1]) {
                    diff++;
                }
            }

            //Simplified version of output[0].length - (output[0].length - spacesLeft + total)
            diff = spacesLeft - diff;

            index = output.length - 1;
            for (int c = numRule.length - 1; c >= 0; c--) {
                //If diff > current rule, remove all data in temp for this rule
                if (diff > numRule[c]) {
                    for (int j = 0; j < numRule[c]; j++) {
                        temp2[index][i] = ' ';
                        index--;
                    }
                    index -= spacesPerGap;
                    if (remainder != 0) {
                        remainder--;
                        index--;
                    }
                    continue;
                }

                //Bound check for last rule
                if (c == numRule.length - 1) {
                    for (int j = 0; j < diff; j++) {
                        temp2[index][i] = ' ';
                        index--;
                    }

                    //for (int j = 0; j < numRule[c] - diff; j++) {
                      //  index--;
                    //}

                    index -= numRule[c] - diff;

                    index -= spacesPerGap;
                    if (remainder != 0) {
                        remainder--;
                        index--;
                    }
                    continue;
                }

                //Bound check for first rule in list
                if (c == 0) {
                    for (int j = 0; j < diff; j++) {
                        temp2[j][i] = ' ';
                    }
                    continue;
                }

                //Rule is in the middle of the field
                int firstHalf = diff / 2;
                int secondHalf = diff / 2 + diff % 2;
                for (int j = 0; j < firstHalf; j++) {
                    temp2[index][i] = ' ';
                    index--;
                }

                //for (int j = 0; j < numRule[c] - diff; j++) {
                  //  index--;
                //}
                index -= numRule[c] - diff;
                for (int j = 0; j < secondHalf; j++) {
                    temp2[index][i] = ' ';
                    index--;
                }
                index -= spacesPerGap;
                if (remainder != 0) {
                    remainder--;
                    index--;
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
                System.out.println(c + " " + numData + " " + color);
                for (int r = 0; r < numData; r++) {
                    output[output.length - 1 - r][c] = color;
                }
            }
        }
    }
}
