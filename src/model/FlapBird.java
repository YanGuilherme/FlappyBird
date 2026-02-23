package model;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Random;
import javax.sound.sampled.*;


import javax.swing.*;

import static constants.Constants.*;

public class FlapBird extends JPanel implements ActionListener, KeyListener {


    // Variáveis para armazenar as imagens
    Image passaroimage;
    Image fundoimage;
    Image canobaixoimage;
    Image canocimaimage;

    // Configurações iniciais do Pássaro
    double passaroX = LARGURA_TELA/ 8;
    double passaroY = ALTURA_TELA/2;
    double passaroLargura = 34;
    double passaroAltura = 24;

    // Configurações iniciais dos Canos
    double canox = LARGURA_TELA;
    double canoy = 0;
    double canolargura = 64;
    double canoaltura = 512;


    private Passaro passaro = new Passaro(passaroX, passaroY, passaroLargura, passaroAltura);
//    private Cano cano = new Cano(canox, canoy, canolargura, canoaltura);

    // Classe interna que representa o objeto Cano
//    public class cano {
//        double x = canox;
//        double y = canoy;
//        double largura = canolargura;
//        double altura = canoaltura;
//        Image img;
//        boolean passed = false;
//
//        cano(Image img) {
//            this.img = img;
//        }
//    }


    // Variáveis de Física e Estado do Jogo
    double velocidadeX = -5; // Aumentado para deixar o jogo mais rápido
    double velocidadeY = 0;
    double gravidade = 0.5; // Gravidade ajustada para ficar mais suave
    double pontuacao = 0;
    boolean gameOver = false;
    boolean gameStarted = false;

    // Objetos de controle (Timers, Listas, Sons)
    Random random = new Random();
    ArrayList<Cano> canos;
    Timer Gameloop;
    Timer colocarCanoTimer;

    // Clips de áudio pré-carregados
    Clip clipPulo;
    Clip clipPontuacao;
    Clip clipBatida;

    // Construtor: Configura o painel e carrega recursos
    public FlapBird(){
        setPreferredSize(new Dimension(LARGURA_TELA, ALTURA_TELA));
        setFocusable(true); // Permite que o painel receba foco do teclado
        addKeyListener(this); // Adiciona o "ouvinte" de teclas

        // Proteção contra erro de carregamento de imagem (Evita tela branca)
        try {
            passaroimage = new ImageIcon(getClass().getResource("../assets/flappybird.png")).getImage();
            fundoimage = new ImageIcon(getClass().getResource("../assets/flappybirdbg.png")).getImage();
            canobaixoimage = new ImageIcon(getClass().getResource("../assets/bottompipe.png")).getImage();
            canocimaimage = new ImageIcon(getClass().getResource("../assets/toppipe.png")).getImage();
        } catch (Exception e) {
            System.out.println("Erro ao carregar imagens: " + e.getMessage());
        }

        // Carregar sons na memória ao iniciar o jogo (evita lag)
        clipPulo = criarSomPulo(); // Gera o som via código em vez de carregar arquivo
//        clipPontuacao = carregarSom("./pontuacao.wav");
        clipBatida = carregarSom("../assets/batida.wav");

        passaro.setImg(passaroimage);
        canos = new ArrayList<>();
        gameStarted = false; // Garante que o jogo comece parado

        colocarCanoTimer = new Timer(1000, new ActionListener() { // Ajustado para 1000ms para acompanhar a velocidade mais rápida
            @Override
            public void actionPerformed(ActionEvent e) {
                colocarcano();
            }
        });
        // colocarCanoTimer.start(); // Removido: O jogo começa parado esperando o Start

        // Timer principal do jogo (Game Loop) - roda a aprox. 60 FPS
        Gameloop = new Timer(1000/60, this);
        Gameloop.start();
    }

    public void colocarcano(){
        // Define uma posição aleatória para o cano de cima
        // O cano tem 512px. Vamos esconder uma parte dele para cima (negativo)
        double randomY = (0 - canoaltura/4 - Math.random()*(canoaltura/2));
        double espaco = ALTURA_TELA/4; // Espaço entre os canos (1/4 da tela)

        Cano canoCima = new Cano(canox, canoy, canolargura, canoaltura, canocimaimage);
        canoCima.setY(randomY);
        canos.add(canoCima); // Adiciona o cano na lista para ser desenhado

        Cano canoBaixo = new Cano(canox, canoy, canolargura, canoaltura, canobaixoimage);
        canoBaixo.setY(randomY + canoaltura + espaco);
        canos.add(canoBaixo);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }

    // Método para carregar o som uma única vez
    public Clip carregarSom(String nomeArquivo) {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(getClass().getResource(nomeArquivo));
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(-15.0f);
            return clip;
        } catch (Exception e) {
            return null;
        }
    }

    // Método que gera o som de pulo matematicamente (Sintetizador simples)
    public Clip criarSomPulo() {
        try {
            AudioFormat format = new AudioFormat(44100, 8, 1, true, true);
            byte[] dados = new byte[10000]; // Duração curta (~0.2s)

            for (int i = 0; i < dados.length; i++) {
                // Frequência mais grave (300Hz a 600Hz) para ser menos estridente
                double frequencia = 300 + (300.0 * i / dados.length);

                // Adiciona Fade-In (começo suave) e Fade-Out (final suave)
                double fadeIn = Math.min(1.0, i / 1000.0); // Suaviza os primeiros 1000 samples
                double fadeOut = 1.0 - ((double)i / dados.length);
                double volume = fadeIn * fadeOut;

                dados[i] = (byte) ((Math.sin(2 * Math.PI * i * frequencia / 44100) * 20) * volume); // Volume reduzido para 20
            }

            Clip clip = AudioSystem.getClip();
            clip.open(format, dados, 0, dados.length);
            return clip;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Toca um som a partir de um ponto específico (útil para pular silêncio inicial)
    public void tocarSom(Clip clip, long microsegundosInicio) {
        if (clip != null) {
            clip.stop();
            clip.setMicrosecondPosition(microsegundosInicio); // Define o ponto de partida
            clip.start();
        }
    }

    // Sobrecarga para tocar o som do início
    public void tocarSom(Clip clip) {
        tocarSom(clip, 0);
    }

    // Lógica de desenho (Renderização)
    public void draw(Graphics g){
        g.drawImage(fundoimage, 0, 0, LARGURA_TELA, ALTURA_TELA, null);

        // --- ROTAÇÃO SUAVE DO PÁSSARO ---
        Graphics2D g2d = (Graphics2D) g;
        AffineTransform oldTransform = g2d.getTransform();
        g2d.translate(passaro.getX() + passaro.getLargura()/2, passaro.getY() + passaro.getAltura()/2);
        double rotation = Math.max(-25, Math.min(90, velocidadeY * 3)); // Rotação baseada na velocidade
        if (!gameStarted) rotation = 0; // Mantém reto na tela inicial
        g2d.rotate(Math.toRadians(rotation));
        g2d.drawImage(passaro.getImg(), (int)-passaro.getLargura()/2, (int)-passaro.getAltura()/2, (int)passaro.getLargura(), (int)passaro.getAltura(), null);
        g2d.setTransform(oldTransform);

        if (!gameStarted) {
            // --- TELA DE START ---
            g.setFont(new Font("Monospaced", Font.BOLD, 45));
            g.setColor(Color.BLACK);
            g.drawString("FLAPPY BIRD", 25, ALTURA_TELA / 3); // Sombra do título
            g.setColor(Color.ORANGE);
            g.drawString("FLAPPY BIRD", 20, ALTURA_TELA / 3 - 5); // Título principal

            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.setColor(Color.WHITE);
            String msg = "Pressione ESPAÇO para começar";
            FontMetrics fm = g.getFontMetrics(); // Ajuda a centralizar o texto
            int x = (LARGURA_TELA - fm.stringWidth(msg)) / 2;
            g.drawString(msg, x, ALTURA_TELA / 2 + 80);

            // Créditos do Criador
            g.setFont(new Font("Arial", Font.PLAIN, 16));
            String autor = "Feito por Alvaro Ferreira";
            fm = g.getFontMetrics();
            int autorX = (LARGURA_TELA - fm.stringWidth(autor)) / 2;
            g.drawString(autor, autorX, ALTURA_TELA - 50);
            return; // Não desenha canos nem placar ainda
        }

        // Desenha todos os canos ativos
        for (Cano c : canos) {
            g.drawImage(c.getImg(), (int) c.getX(), (int) c.getY(), (int) c.getLargura(), (int) c.getAltura(), null);
        }

        // Estilizando o placar (Centralizado com borda preta)
        String textoPontuacao = String.valueOf((int) pontuacao);
        g.setFont(new Font("Arial", Font.BOLD, 32));
        FontMetrics fm = g.getFontMetrics();
        int textoX = (LARGURA_TELA - fm.stringWidth(textoPontuacao)) / 2;
        int textoY = 50;

        g.setColor(Color.BLACK); // Sombra/Borda
        g.drawString(textoPontuacao, textoX + 2, textoY + 2);
        g.setColor(Color.white);
        g.drawString(textoPontuacao, textoX, textoY);

        if (gameOver) {
            // Fundo escuro transparente
            g.setColor(new Color(0, 0, 0, 128));
            g.fillRect(0, 0, LARGURA_TELA, ALTURA_TELA);

            // Texto GAME OVER estilizado
            g.setFont(new Font("Monospaced", Font.BOLD, 50));
            g.setColor(Color.BLACK);
            g.drawString("GAME OVER", 45, ALTURA_TELA / 2); // Sombra
            g.setColor(Color.ORANGE);
            g.drawString("GAME OVER", 40, ALTURA_TELA / 2 - 5); // Texto principal

            // Instrução para reiniciar
            g.setFont(new Font("Monospaced", Font.BOLD, 20));
            g.setColor(Color.WHITE);
            g.drawString("Pressione ESPAÇO para reiniciar", 10, ALTURA_TELA / 2 + 50);
        }
    }

    // Lógica de Movimento e Física
    public void move() {
        // Aplica gravidade no pássaro
        velocidadeY += gravidade;
        passaro.setY(passaro.getY() + velocidadeY);
        passaro.setY(Math.max(passaro.getY(), 0));

        // Move os canos para a esquerda
        for (Cano c : canos) {
            c.setX(c.getX() + velocidadeX);

            // Verifica se o pássaro passou pelo cano para pontuar
            if (!c.isPassed() && passaro.getX() > c.getX() + c.getLargura()) {
                c.setPassed(true);
                pontuacao += 50; // 50 pontos por cano (x2 canos = 100 pontos)
                tocarSom(clipPontuacao);
            }

            // Detecção de Colisão
            if (colisao(passaro, c)) {
                gameOver = true;
                tocarSom(clipBatida, 200000); // Ajustado para 0.2s (evita cortar o som se for curto)
            }
        }

        // Game Over se cair no chão
        if (passaro.getY() > ALTURA_TELA) {
            gameOver = true;
            tocarSom(clipBatida, 200000); // Ajustado para 0.2s
        }
    }

    // Verifica colisão entre dois retângulos
    public boolean colisao(Passaro p, Cano c) {
        // Cria retângulos para verificar a interseção
        Rectangle rectPassaro = new Rectangle((int)p.getX(), (int)p.getY(), (int)p.getLargura(), (int)p.getAltura());
        Rectangle rectCano = new Rectangle((int)c.getX(), (int)c.getY(), (int)c.getLargura(), (int)c.getAltura());

        return rectPassaro.intersects(rectCano);
    }

    // Controle de Teclado
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (!gameStarted) {
                gameStarted = true;
                gameOver = false; // Garante que não comece com Game Over
                canos.clear(); // Limpa qualquer cano que possa ter ficado na memória
                colocarCanoTimer.start();
                velocidadeY = -9; // Pulo mais suave
                tocarSom(clipPulo);
            } else if (gameOver) {
                // Reiniciar o jogo
                passaro.setY(passaroY);
                velocidadeY = 0;
                canos.clear();
                pontuacao = 0;
                gameOver = false;
                gameStarted = false; // Volta para a tela de título (Start) em vez de cair direto
                Gameloop.start();
                // O timer dos canos só vai iniciar quando você apertar espaço na tela de título
            } else {
                velocidadeY = -9; // Pulo mais suave
                tocarSom(clipPulo);
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

    // Garante que o painel tenha foco assim que for exibido
    @Override
    public void addNotify() {
        super.addNotify();
        requestFocus(); // Garante que o jogo receba o foco do teclado ao iniciar
    }
}