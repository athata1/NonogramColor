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
                System.out.println(rd);
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
}
