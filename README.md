# Flappy Bird Clone em Java 

Esse é um projeto que eu desenvolvi para praticar lógica de programação e desenvolvimento de jogos usando Java puro. Tentei recriar a mecânica clássica do Flappy Bird com alguns toques pessoais e otimizações.

## O que eu adicionei no jogo

- **Física Personalizada:** Implementei gravidade e velocidade de pulo ajustadas para dar uma sensação fluida e suave de voo.
- **Sistema de Colisão:** O jogo detecta colisão precisa (usando Hitboxes/Retângulos) quando o pássaro bate nos canos ou no chão.
- **Geração Procedural de Canos:** Os obstáculos aparecem em alturas aleatórias e com espaçamento dinâmico para o jogo nunca ser igual.
- **Efeitos Visuais:**
    - O pássaro gira suavemente (`Rotation`) dependendo se está subindo ou caindo, usando `AffineTransform`.
    - Tela de Start e Game Over estilizadas com fontes personalizadas e sombras.
- **Áudio Dinâmico e Matemático:**
    - **Destaque:** Criei um sintetizador de som básico no código para gerar o som de pulo (onda senoidal com fade-in/out) matematicamente, sem precisar de arquivo externo!
    - Sistema de pré-carregamento de áudio (`Clip`) para evitar lag na hora de tocar.

## Tecnologias que eu usei

- **Java (JDK):** A linguagem base do projeto.
- **Java Swing (`javax.swing`):** Para criar a janela (`JFrame`), o painel de desenho (`JPanel`) e gerenciar o `Timer` do loop do jogo.
- **Java AWT (`java.awt`):**
    - `Graphics2D` para renderizar as imagens e realizar transformações geométricas (rotação).
    - `KeyListener` para capturar a entrada do teclado (Espaço).
    - `Rectangle` para a lógica de interseção e colisão.
- **Java Sound API (`javax.sound.sampled`):** Para manipular os clipes de áudio, controlar volume (`FloatControl`) e gerar áudio via array de bytes.

## Como eu fiz funcionar

Usei o conceito de **Game Loop** rodando a aproximadamente 60 FPS. A cada atualização (tick), eu recalculo a posição do pássaro aplicando a gravidade, movo os canos para a esquerda e verifico se houve pontuação ou colisão.

Para resolver problemas de performance, implementei uma lógica de limpeza de memória (`canos.clear()`) ao reiniciar e pré-carregamento de recursos para evitar telas brancas ou travamentos durante o jogo.
