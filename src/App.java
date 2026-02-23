import model.FlapBird;

import javax.swing.*;

public class App {
    public static void main(String[] args) throws Exception {
        
        int larguraborda  = 360;
        int alturaborda = 640;

        JFrame janela = new JFrame("Flap Bird");
        janela.setSize(larguraborda, alturaborda);
        janela.setLocationRelativeTo(null);
        janela.setResizable(false);
        janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        

        FlapBird Flapbird = new FlapBird();
        janela.add(Flapbird);
        janela.pack();
        janela.setVisible(true);
    }
}
