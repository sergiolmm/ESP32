/* Usada para gerar uma class em java o qual será convertido para Json
*
*  By SLMM para o curso de microPython
*
*
* */
package br.com.slmm.neo_ring;



import com.google.gson.annotations.SerializedName;

/* utiliza a biblioteca gson a qual permite que uma classe seja serializada no
   formato json.
   Nessa classe temos 5 variaveis
    angulo - indica o angulo que o grafico vai desenhar de 0 a 11
    red, green e blue indica o valor para a cor RGB
    efeito indica o efeito a ser utilizado.
        0 - desenha o arco
        1   pisca o led em branco fazendo este rodar
        2   cria um efeito arco iris
        3   cria um efeito tipo relogio.

   o json será processado no lado do servidor pelo microPython
 */

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
