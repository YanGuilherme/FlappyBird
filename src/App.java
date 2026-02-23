import model.FlapBird;

import javax.swing.*;
import static constants.Constants.*;

public class App {
    public static void main(String[] args) throws Exception {

        JFrame janela = new JFrame(TITLE);
        janela.setSize(LARGURA_TELA, ALTURA_TELA);
        janela.setLocationRelativeTo(null);
        janela.setResizable(false);
        janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        

        FlapBird Flapbird = new FlapBird();
        janela.add(Flapbird);
        janela.pack();
        janela.setVisible(true);
    }
}
