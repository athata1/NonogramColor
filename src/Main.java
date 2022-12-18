public class Main {
    public static void main(String[] args) {

        /*long sum = 0;
        int total = 100;
        for (int i = 0; i < total; i++) {
            long a = System.currentTimeMillis();
            NonogramColorSolver n = new NonogramColorSolver("Nonogram4");
            n.solveNonogram();
            sum += System.currentTimeMillis() - a;
            System.out.println(i + " " + (System.currentTimeMillis() - a));
        }
        System.out.println((double)sum/total);*/

        NonogramColorSolver n = new NonogramColorSolver("Nonogram4", false);
        n.solveNonogram();
        n.printCurrentBoard();
    }
}
