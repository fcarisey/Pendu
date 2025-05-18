import javax.swing.*;

public class Main {
    public static JFrame window;

    public static void main(String[] args) {
        window = new JFrame("Pendu");
        window.setSize(500, 700);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        HomePanel home = new HomePanel();

        window.setLayout(null);
        window.setContentPane(home);
        window.pack();
        window.setVisible(true);

        Database database = new Database();

    }
}