package model;

import java.awt.*;

public class Cano {
    private double x;
    private double y;
    private double largura;
    private double altura;
    private Image img;
    private boolean passed = false;

    public Cano(){
    }

    public Cano(double x, double y, double largura, double altura, Image img) {
        this.x = x;
        this.y = y;
        this.largura = largura;
        this.altura = altura;
        this.img = img;
    }

    public boolean isPassed() {
        return passed;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }

    public Image getImg() {
        return img;
    }

    public void setImg(Image img) {
        this.img = img;
    }

    public double getAltura() {
        return altura;
    }

    public void setAltura(double altura) {
        this.altura = altura;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getLargura() {
        return largura;
    }

    public void setLargura(double largura) {
        this.largura = largura;
    }
}
