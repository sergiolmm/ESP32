/*  Programa para desenhar o arco na tela e obter as informações
*   para ser transmitida ao servidor de controle do neo ring
*
*  Dividido em duas partes
*  A view para desenho e a thread para criação do soquete de envio.
*
* */
package br.com.slmm.neo_ring;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.Nullable;

import com.google.gson.Gson;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;


import static java.lang.Math.atan2;

public class arco extends View {
    Thread cliThread = null;
    private Paint paint= new Paint();
    private Random rnd = new Random();
    // variávies de controle do desenho
    public Integer lastValor;
    public Integer valor = 2;
    public float lastAngle = 180;
    public float angle = 180;
    public float posIniX = 100;
    public float posIniY = 100;
    public float espessura = 80;
    public float LarguraX = posIniX+ espessura ;
    public float LarguraY = posIniY+ espessura ;
    public String cor;
    // variáveis para definição do efeito e da cor
    private int efeito = 0;
    public int[] CorMatriz;
    // variáveis de controle do socket
    private Socket socket = null;;
    private DataOutputStream out;
    // definição dos valores padrões.
    private int SERVERPORT = 80;
    private String SERVER_IP = "192.168.15.134";

    public arco(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setFocusable(true);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(12f);
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE); //FILL_AND_STROKE);//
        paint.setStrokeJoin(Paint.Join.ROUND);
        // definições das cores a serem usadas para desenho do arco
        CorMatriz = new int[12];
        CorMatriz[0]= Color.rgb(126 , 1 , 0);
        CorMatriz[1]= Color.rgb(114 , 13 , 0);
        CorMatriz[2]= Color.rgb(102 , 25 , 0);
        CorMatriz[3]= Color.rgb(90 , 37 , 0);
        CorMatriz[4]= Color.rgb(78 , 49 , 0);
        CorMatriz[5]= Color.rgb(66 , 61 , 0);
        CorMatriz[6]= Color.rgb(54 , 73 , 0);
        CorMatriz[7]= Color.rgb(42 , 85 , 0);
        CorMatriz[8]= Color.rgb(30 , 97 , 0);
        CorMatriz[9]= Color.rgb(18 , 109 , 0);
        CorMatriz[10]= Color.rgb(6 , 121 , 0);
        CorMatriz[11]= Color.rgb(0 , 122 , 5);
    }

    public void setPos(){
        invalidate();
    }

    // onde desenho o arco
    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        canvas.save();

        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPaint(paint);

        paint.setStyle(Paint.Style.FILL);

        RectF rectF = new RectF(posIniX, posIniY,
                          this.getWidth()-posIniX, this.getHeight()-posIniY);
        RectF rectF1 = new RectF(LarguraX, LarguraY,
                          this.getWidth()-LarguraX, this.getHeight()-LarguraY);

        for (int i = 0 ; i<= valor; i++)
           desenhaPedacoArco(canvas,rectF,rectF1,30 * i,false,CorMatriz[i]);


        // fechar o desenho
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        rectF1 = new RectF(LarguraX+1, LarguraY+1,
                     this.getWidth()-(LarguraX+1), this.getHeight()-(LarguraY+1));
        canvas.drawArc (rectF1, 0, 360, true, paint);


        paint.setColor(Color.BLACK);
        paint.setTextSize(40);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(4f);
        String angleStr = "Ang: "+ String.valueOf((int)angle) + " Pos : "+
                            String.valueOf(((int)angle)/12);
        canvas.drawText(angleStr,20,40, paint);
        canvas.drawText(cor,20,90, paint);
        paint.setStrokeWidth(12f);

        canvas.restore();

    }

    // manipulação do toque na tela para desenhar o arco
    @Override
    public boolean onTouchEvent(MotionEvent event){

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                double ax1 =  (event.getX() - (this.getWidth() *0.5f));
                double ay1 =  (event.getY() - (this.getHeight() *0.5f));
                angle = (float)Math.toDegrees( (atan2(ay1, ax1) + Math.PI / 2));
                if (angle < 0)
                    angle = angle + 360;
                invalidate();
                return true;
            case  MotionEvent.ACTION_MOVE:
                double ax =  (event.getX() - (this.getWidth() *0.5f));
                double ay =  (event.getY() - (this.getHeight() *0.5f));
                angle = (float)Math.toDegrees( (atan2(ay, ax) + Math.PI / 2));
                if (angle < 0)
                    angle = angle + 360;

                valor = ((int) angle)/30;
                lastValor = ((int) lastAngle) /30;
                System.out.println("valor :"+valor+"lastValor: "+lastValor);

                if (lastValor != valor) {
                    efeito = 0;
                    transmite2();
                    invalidate();
                }
                lastAngle = angle;
                break;

            case  MotionEvent.ACTION_UP:

                break;
            default:
                return false;
        }

        return true;
    }

    void desenhaPedacoArco(Canvas canvas,RectF rectF,RectF rectF1,
                           float angle, boolean tipoCor,int _cor){
        if (tipoCor) {
            paint.setColor(getRandomColor());
        }
        else {
            paint.setColor(_cor);
            cor = "Random";
        }
        paint.setStyle(Paint.Style.FILL);
        canvas.drawArc (rectF, angle-90, 30, true, paint);
        canvas.drawArc (rectF1, angle-90, 30,  true, paint);

    }


    int getRandomColor(){
        int red = (int) rnd.nextInt(256);
        int green =rnd.nextInt(256);
        int blue =  rnd.nextInt(256);
        cor = "R: " +String.valueOf(red) + " G: "+String.valueOf(green) + " B: "
                +String.valueOf(blue) +" - "+ String.valueOf(valor) +"  " + String.valueOf(lastValor) ;
        return Color.rgb(red,green, blue );
    }

    // ação de conectar (inicia a thread de conexão)
    // criando o socket
    void connecta(String end , int porta){
        SERVERPORT = porta;
        SERVER_IP = end;
        new Thread(new ClientThread()).start();
    }

    void connecta(){
        new Thread(new ClientThread()).start();
    }

    // desconecta
    // o servidor desconecta quando receve o comando
    // Connection : close
    void desconecta(){
        try {
            String str =  "HTTP/1.0 200 OK\r\nConnection: close\r\n\r\n";
            byte[] msg = str.getBytes();
            out.write(msg, 0 ,msg.length);
            out.flush();
            if (socket.isConnected())
                socket.close();
            socket = null;
        }
        catch (Exception e){
            System.out.println("Fechando o sockect");
        }
    }
    // manda para o servidor o efeito que deseja executar
    public void efeito(int _efeito){
        efeito = _efeito;
        transmite2();
        efeito = 0;
    }


    class ClientThread implements Runnable {

        @Override
        public void run() {

            try {
                InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
                if (socket == null) {
                    socket = new Socket(serverAddr, SERVERPORT);
                    out = new DataOutputStream(socket.getOutputStream());
                    System.out.println("Abriu");
                }
            } catch (UnknownHostException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

        }

    }
    // transmite o comando para o servidor

    public void transmite2(){
        try
        {
            String str =  "POST / HTTP/1.1\r\nContent-type: application/json\r\n\r\n";

            Comando cmd = new Comando(valor, Color.red(CorMatriz[valor]),
                    Color.green(CorMatriz[valor]),Color.blue(CorMatriz[valor]), efeito );
            String jStr = new Gson().toJson(cmd);
            System.out.println(str);
            System.out.println(jStr);
            str = str + jStr;

            byte[] msg = str.getBytes();
            out.write(msg, 0 ,msg.length);
            out.flush();
            System.out.println("transmitiu");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
/*
POST / HTTP/1.1\r\n
Content-type: application/json\r\n
\r\n
{ "angulo":"3", "red":"255", "green":"255","blue":"255", "efeito":"0"}
*/