package br.com.slmm.desenho2;

import com.google.gson.annotations.SerializedName;

public class Comando {

    @SerializedName("angulo")
    Integer angulo;
    @SerializedName("red")
    Integer red;
    @SerializedName("blue")
    Integer blue;
    @SerializedName("green")
    Integer green;
    @SerializedName("efeito")
    Integer efeito;

    public Comando(int _angulo,int _red, int _green,int _blue , int _efeito) {
        this.angulo =_angulo;
        this.red = _red;
        this.blue =_blue;
        this.green = _green;
        this.efeito = _efeito;
    }

    public Integer getAngulo() {
        return angulo;
    }

    public void setAngulo(Integer angulo) {
        this.angulo = angulo;
    }

    public Integer getRed() {
        return red;
    }

    public void setRed(Integer red) {
        this.red = red;
    }

    public Integer getBlue() {
        return blue;
    }

    public void setBlue(Integer blue) {
        this.blue = blue;
    }

    public Integer getGreen() {
        return green;
    }

    public void setGreen(Integer green) {
        this.green = green;
    }

    public Integer getEfeito() { return efeito; }

    public void setEfeito(Integer efeito) { this.efeito = efeito; }
}
