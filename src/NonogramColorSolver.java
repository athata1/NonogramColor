import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class NonogramColorSolver {

    private String folderName;
    private ArrayList<RuleData> rowRules;
    private ArrayList<RuleData> colRules;
    private char[] output;
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
            for (int i = 0; i < numRows; i++) {
                String[] data = scan.nextLine().split(" ");
                rowRules.add(new RuleData(data));
            }

            //Ensure that row file was properly created
            String s = scan.nextLine();
            assert s.equals("");

            int numCols = Integer.parseInt(scan.nextLine());
            for (int i = 0; i < numCols; i++) {
                String[] data = scan.nextLine().split(" ");
                colRules.add(new RuleData(data));
            }

            //Ensure that file ends at column data
            assert !scan.hasNextLine();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
