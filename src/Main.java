public class Main {
    public static void main(String[] args) {

        /*while (true) {
            long a = System.currentTimeMillis();
            NonogramColorSolver n = new NonogramColorSolver("Nonogram3");
            n.solveNonogram();
            System.out.println(System.currentTimeMillis() - a);
        }*/

        NonogramColorSolver n = new NonogramColorSolver("Nonogram2");
        n.solveNonogram();
        n.printCurrentBoard();
    }
}
