import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class DisplayNonogram extends JPanel {

    private NonogramColorSolver ncs;
    private int width;
    private int height;
    private char[][] board;
    private HashMap<Character, Color> colorMap;
    private ArrayList<RuleData> colRules;
    private ArrayList<RuleData> rowRules;
    private int maxRow;
    private int maxCol;
    private final int BOXSIZE = 20;
    public DisplayNonogram(String fileName) {
        setPreferredSize( new Dimension( 700, 700));

        this.ncs = new NonogramColorSolver(fileName);
        this.board = ncs.getBoard();
        this.width = board[0].length;
        this.height = board.length;
        this.colorMap = ncs.getColorMap();
        this.colRules = ncs.getColRules();
        this.rowRules = ncs.getRowRules();
        this.maxCol = Integer.MIN_VALUE;
        this.maxRow = Integer.MIN_VALUE;

        for (RuleData rd: rowRules) {
            maxRow = Math.max(maxRow, rd.getNumRule().length);
        }
        for (RuleData rd: colRules) {
            maxCol = Math.max(maxCol, rd.getNumRule().length);
        }
    }

    public void paintComponent(Graphics g) {
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(maxRow*BOXSIZE,0, width*BOXSIZE, maxCol*BOXSIZE);
        g.fillRect(0, maxCol*BOXSIZE, maxRow*BOXSIZE, height*BOXSIZE);


        g.setColor(Color.BLACK);
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < maxRow; c++) {
                g.drawRect(c*BOXSIZE, maxCol*BOXSIZE + r*BOXSIZE,BOXSIZE, BOXSIZE);
            }
        }
        for (int r = 0; r < width; r++) {
            for(int c = 0; c < maxCol; c++) {
                g.drawRect(maxRow*BOXSIZE + r*BOXSIZE, c*BOXSIZE, BOXSIZE, BOXSIZE);
            }
        }

        g.drawRect(maxRow*BOXSIZE, maxCol*BOXSIZE, width*BOXSIZE, height*BOXSIZE);
    }

    public static void main(String[] args) {
        DisplayNonogram t = new DisplayNonogram("Nonogram3");
        JFrame jf = new JFrame();
        jf.setTitle("Nonogram");
        jf.setSize(1000,1000);
        t.setBackground(new Color(250,251,245));
        jf.add(t);
        jf.setLayout(new FlowLayout(FlowLayout.LEFT));
        jf.pack();
        jf.setVisible(true);
        jf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
}
