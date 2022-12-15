import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;

public class DisplayNonogram extends JPanel implements ActionListener {

    private NonogramColorSolver ncs;
    private int width;
    private int height;
    private char[][] board;
    private HashMap<Character, Color> colorMap;
    private ArrayList<RuleData> colRules;
    private ArrayList<RuleData> rowRules;
    private int maxRow;
    private int maxCol;
    private final int BOXSIZE = 10;
    private Timer tm = new Timer(10,this);
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

        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                ncs.solveNonogram();
            }
        });
        th.start();
    }

    public void actionPerformed(ActionEvent e) {
        tm.stop();
        repaint();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(maxRow*BOXSIZE,0, width*BOXSIZE, maxCol*BOXSIZE);
        g.fillRect(0, maxCol*BOXSIZE, maxRow*BOXSIZE, height*BOXSIZE);

        g.setColor(Color.BLACK);
        //Draw box separators for row rules
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < maxRow; c++) {
                g.drawRect(c*BOXSIZE, maxCol*BOXSIZE + r*BOXSIZE,BOXSIZE, BOXSIZE);
            }
        }

        //Draw box separators for col rules
        for (int r = 0; r < width; r++) {
            for(int c = 0; c < maxCol; c++) {
                g.drawRect(maxRow*BOXSIZE + r*BOXSIZE, c*BOXSIZE, BOXSIZE, BOXSIZE);
            }
        }
        g.drawRect(maxRow*BOXSIZE, maxCol*BOXSIZE, width*BOXSIZE, height*BOXSIZE);


        //Input numbers into rowRules
        for (int i = 0; i < rowRules.size(); i++) {
            int[] numRules = rowRules.get(i).getNumRule();
            char[] colorRules = rowRules.get(i).getColorRule();
            FontRenderContext frc = new FontRenderContext(null, true, true);
            for (int j = numRules.length - 1; j >= 0; j--) {
                int prev = BOXSIZE/2;
                Font f;
                while (true) {
                    f = new Font("Courier New", Font.BOLD, prev);
                    Rectangle2D r2d = f.getStringBounds("" + numRules[j], frc);
                    if (r2d.getWidth() <= BOXSIZE && r2d.getHeight() <= BOXSIZE) {
                        break;
                    }
                    prev--;
                }
                g.setColor(colorMap.get(colorRules[j]));
                centerString(g, new Rectangle((maxRow - 1 - (numRules.length - 1 - j))*BOXSIZE,maxCol*BOXSIZE + i*BOXSIZE,BOXSIZE, BOXSIZE), "" + numRules[j], f);
            }
        }

        //Input numbers into colRules
        for (int i = 0; i < colRules.size(); i++) {
            int[] numRules = colRules.get(i).getNumRule();
            char[] colorRules = colRules.get(i).getColorRule();
            FontRenderContext frc = new FontRenderContext(null, true, true);
            for (int j = numRules.length - 1; j >= 0; j--) {
                int prev = BOXSIZE/2;
                Font f;
                while (true) {
                    f = new Font("Courier New", Font.BOLD, prev);
                    Rectangle2D r2d = f.getStringBounds("" + numRules[j], frc);
                    if (r2d.getWidth() <= BOXSIZE && r2d.getHeight() <= BOXSIZE) {
                        break;
                    }
                    prev-=2;
                }
                g.setColor(colorMap.get(colorRules[j]));
                centerString(g, new Rectangle((maxRow*BOXSIZE + i*BOXSIZE),(maxCol - 1 - (numRules.length - 1 - j))*BOXSIZE,BOXSIZE, BOXSIZE), "" + numRules[j], f);
            }
        }
        g.translate(maxRow*BOXSIZE, maxCol*BOXSIZE);


        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board[0].length; c++) {
                if (board[r][c] == '_')
                    continue;
                else if (board[r][c] == 'X') {
                    g.setColor(Color.BLACK);
                    g.drawLine(c*BOXSIZE, r*BOXSIZE, (c+1)*BOXSIZE, (r+1)*BOXSIZE);
                    g.drawLine((c+1)*BOXSIZE, r*BOXSIZE, c*BOXSIZE, (r+1)*BOXSIZE);
                }
                else {
                    g.setColor(colorMap.get(board[r][c]));
                    g.fillRect(c*BOXSIZE, r*BOXSIZE, BOXSIZE,BOXSIZE);
                }
            }
        }
        g.setColor(Color.BLACK);
        g.drawRect(0, 0, width*BOXSIZE, height*BOXSIZE);
        g.translate(-maxRow*BOXSIZE, -maxCol*BOXSIZE);
        tm.start();
    }

    /**
     * This method centers a <code>String</code> in
     * a bounding <code>Rectangle</code>.
     * @param g - The <code>Graphics</code> instance.
     * @param r - The bounding <code>Rectangle</code>.
     * @param s - The <code>String</code> to center in the
     * bounding rectangle.
     * @param font - The display font of the <code>String</code>
     *
     * @see java.awt.Graphics
     * @see java.awt.Rectangle
     * @see java.lang.String
     */
    public void centerString(Graphics g, Rectangle r, String s,
                             Font font) {
        FontRenderContext frc =
                new FontRenderContext(null, true, true);

        Rectangle2D r2D = font.getStringBounds(s, frc);
        int rWidth = (int) Math.round(r2D.getWidth());
        int rHeight = (int) Math.round(r2D.getHeight());
        int rX = (int) Math.round(r2D.getX());
        int rY = (int) Math.round(r2D.getY());

        int a = (r.width / 2) - (rWidth / 2) - rX;
        int b = (r.height / 2) - (rHeight / 2) - rY;

        g.fillRect(r.x + a - 1,r.y + b + rY - 1,rWidth + 2,rHeight + 2);
        Color c = g.getColor();
        int avg = (c.getRed() + c.getBlue() + c.getGreen())/3;
        if (avg > 255/2) {
            g.setColor(Color.BLACK);
        }
        else {
            g.setColor(Color.WHITE);
        }
        g.setFont(font);
        g.drawString(s, r.x + a, r.y + b);

    }

    public static void main(String[] args) {
        DisplayNonogram t = new DisplayNonogram("Nonogram4");
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
