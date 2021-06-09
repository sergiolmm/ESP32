package br.com.slmm.desenho2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.gson.Gson;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static java.lang.Math.atan2;

public class arco extends View {
    Thread cliThread = null;
    private Paint paint= new Paint();
    public  float scaleFacto = 1.0f;
    private Random rnd = new Random();
    private Path path = new Path();
    public float x;
    public float y;
    public float lastx;
    public float lasty;
    public float z;
    public float x2 = 110;
    public float y2 = 110;
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
    private boolean CorRandomica = true;
    private int efeito = 0;
    public static final String BASE_URL = "http://192.168.15.134/";

    public int[] CorMatriz;
    MyApiEndpointInterface apiService ;

    private Socket socket = null;;
    //private PrintWriter out;
    private DataOutputStream out;
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
        x = -1;
        y = -1;
        z = 3;
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
        //[0 , 110 , 17],[0 , 98 , 29],[0 , 86 , 41],[0 , 74 , 53],[0 , 62 , 65],[0 , 50 , 77],[0 , 38 , 89],[0 , 26 , 101],[0 , 14 , 113],[0 , 2 , 125],[9 , 0 , 118],[21 , 0 , 106],[33 , 0 , 94],[45 , 0 , 82],[57 , 0 , 70],[69 , 0 , 58],[81 , 0 , 46],[93 , 0 , 34],[105 , 0 , 22],[117 , 0 , 10]};
        /*Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService =
                retrofit.create(MyApiEndpointInterface.class);
*/

    }

    public void setPos(){
        x2 = 200;
        y2 = 200;
        invalidate();
    }

    public void setCorRandomica(boolean value) { this.CorRandomica = value;}
    public boolean getCorRandomica() { return this.CorRandomica ;}


    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        canvas.save();

        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPaint(paint);

        paint.setStyle(Paint.Style.FILL);

        RectF rectF = new RectF(posIniX, posIniY, this.getWidth()-posIniX, this.getHeight()-posIniY);
        RectF rectF1 = new RectF(LarguraX, LarguraY, this.getWidth()-LarguraX, this.getHeight()-LarguraY);

        switch (valor){
            case 11:
                desenhaPedacoArco(canvas,rectF,rectF1,330,false,CorMatriz[11]);
            case 10:
                desenhaPedacoArco(canvas,rectF,rectF1,300,false,CorMatriz[10]);
            case 9:
                desenhaPedacoArco(canvas,rectF,rectF1,270,false,CorMatriz[9]);
            case 8:
                desenhaPedacoArco(canvas,rectF,rectF1,240,false,CorMatriz[8]);
            case 7:
                desenhaPedacoArco(canvas,rectF,rectF1,210,false,CorMatriz[7]);
            case 6:
                desenhaPedacoArco(canvas,rectF,rectF1,180,false,CorMatriz[6]);
            case 5:
                desenhaPedacoArco(canvas,rectF,rectF1,150,false,CorMatriz[5]);
            case 4:
                desenhaPedacoArco(canvas,rectF,rectF1,120,false,CorMatriz[4]);
            case 3:
                desenhaPedacoArco(canvas,rectF,rectF1,90,false,CorMatriz[3]);
            case 2:
                desenhaPedacoArco(canvas,rectF,rectF1,60,false,CorMatriz[2]);
            case 1:
                desenhaPedacoArco(canvas,rectF,rectF1,30,false,CorMatriz[1]);
            case 0:
                desenhaPedacoArco(canvas,rectF,rectF1,0,false,CorMatriz[0]);
        }


        /*
        if (CorRandomica) {
            paint.setColor(getRandomColor());
        }
        else {
            paint.setColor(Color.BLUE);
            cor = "Azul";
        }
        paint.setStyle(Paint.Style.FILL);
        RectF rectF = new RectF(posIniX, posIniY, this.getWidth()-posIniX, this.getHeight()-posIniY);
        canvas.drawArc (rectF, -90, 30, true, paint);
        RectF rectF1 = new RectF(LarguraX, LarguraY, this.getWidth()-LarguraX, this.getHeight()-LarguraY);
        canvas.drawArc (rectF1, -90, 30, true, paint);
        //paint.setColor(Color.WHITE);
        //paint.setStyle(Paint.Style.FILL);
        //rectF1 = new RectF(LarguraX+1, LarguraY+1, this.getWidth()-(LarguraX+1), this.getHeight()-(LarguraY+1));
        //canvas.drawArc (rectF1, 0, 360, true, paint);
        if (CorRandomica) {
            paint.setColor(getRandomColor());
        }
        else {
            paint.setColor(Color.BLUE);
            cor = "Azul";
        }
//        rectF = new RectF(posIniX, posIniY, this.getWidth()-posIniX, this.getHeight()-posIniY);
        canvas.drawArc (rectF, -60, 30, true, paint);
//         rectF1 = new RectF(LarguraX, LarguraY, this.getWidth()-LarguraX, this.getHeight()-LarguraY);
        canvas.drawArc (rectF1, -60, 30, true, paint);
   */
        // fechar o desenho
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        rectF1 = new RectF(LarguraX+1, LarguraY+1, this.getWidth()-(LarguraX+1), this.getHeight()-(LarguraY+1));
        canvas.drawArc (rectF1, 0, 360, true, paint);



        paint.setColor(Color.BLACK);
        paint.setTextSize(40);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(4f);
        String angleStr = "Ang: "+ String.valueOf((int)angle) + " Pos : "+ String.valueOf(((int)angle)/12);
        canvas.drawText(angleStr,20,40, paint);
        canvas.drawText(cor,20,90, paint);
        paint.setStrokeWidth(12f);
        canvas.restore();


    }

    void transmite(){
        Comando cmd = new Comando(valor, 1,1,1, efeito);
        Call<ResponseBody> call = apiService.getCmd(cmd);
        call.enqueue (new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                System.out.println(response.body());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                System.out.println(t.getMessage());
            }
        });

    }

    @Override
    public boolean onTouchEvent(MotionEvent event){

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                x = event.getX();
                y = event.getY();
                lastx = x;
                lasty = y;
                z = 10;
                double ax1 =  (event.getX() - (this.getWidth() *0.5f));
                double ay1 =  (event.getY() - (this.getHeight() *0.5f));
                angle = (float)Math.toDegrees( (atan2(ay1, ax1) + Math.PI / 2));
                if (angle < 0)
                    angle = angle + 360;
                invalidate();
                return true;
            case  MotionEvent.ACTION_MOVE:
                if (lastx != event.getX())
                    z++;
                lastx = event.getX();

                if (lasty != event.getY()) {
                    z--;
                    if (z < 0) z =0;
                }
                lasty = event.getY();

                double ax =  (event.getX() - (this.getWidth() *0.5f));
                double ay =  (event.getY() - (this.getHeight() *0.5f));
                angle = (float)Math.toDegrees( (atan2(ay, ax) + Math.PI / 2));
                if (angle < 0)
                    angle = angle + 360;

                valor = ((int) angle)/30;
                lastValor = ((int) lastAngle) /30;
                System.out.println("valor :"+valor+"lastValor: "+lastValor);

                if (lastValor != valor) {
  //                  transmite();
                    efeito = 0;
                    transmite2();
                    invalidate();
                }
                lastAngle = angle;

                /*
                if (lastx > event.getX()) {
                    if (z > 1)
                        z--;
                }
                else
                    z++;
                lastx = event.getX();
                if (lasty > event.getY()) {
                    if (z > 1)
                        z--;
                }
                else
                    z++;
                lasty = event.getY();

                 */
                break;

            case  MotionEvent.ACTION_UP:

                break;
            default:
                return false;
        }

        return true;
    }

    void desenhaPedacoArco(Canvas canvas,RectF rectF,RectF rectF1, float angle, boolean tipoCor,int _cor){
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
                +String.valueOf(blue) +" - "+ String.valueOf(valor) +String.valueOf(lastValor) ;

        return Color.rgb(red,green, blue );
    }


    Path drawPoligon(int centerX, int centerY, int raio, int nLados) {
        Path poligon = new Path();

        double ang = Math.PI * 2 / nLados;
        poligon.moveTo((float)(centerX + raio * Math.cos(0)), (float)(centerY + raio * Math.sin(0)));
        for (int i=1; i<nLados; i++)
            poligon.lineTo((float)(centerX + raio * Math.cos(ang * i)), (float)(centerY + raio * Math.sin(ang * i)));
        poligon.close();

        return poligon;
    }

    void connecta(String end , int porta){

        SERVERPORT = porta;
        SERVER_IP = end;

        new Thread(new ClientThread()).start();

    }

    void connecta(){
        new Thread(new ClientThread()).start();
    }

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
                    //out = new PrintWriter(new BufferedWriter(
                    //        new OutputStreamWriter(socket.getOutputStream())),
                    //        true);
                    System.out.println("Abriu");
                }
            } catch (UnknownHostException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

        }

    }
    public void transmite2(){
        try
        {
        String str =  "POST / HTTP/1.1\r\nContent-type: application/json\r\n\r\n";
        //CorMatriz[11]
        Comando cmd = new Comando(valor, Color.red(CorMatriz[valor]),
                Color.green(CorMatriz[valor]),Color.blue(CorMatriz[valor]), efeito );
        String jStr = new Gson().toJson(cmd);
           System.out.println(str);
           System.out.println(jStr);
           str = str + jStr;

           byte[] msg = str.getBytes();
//           out.print(str);
//           out.print(jStr);
            out.write(msg, 0 ,msg.length);
            out.flush();
            System.out.println("transmitiu");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void onClose(){
       try {
           String str =  "HTTP/1.0 200 OK\r\nConnection: close\r\n\r\n";
           byte[] msg = str.getBytes();
           out.write(msg, 0 ,msg.length);
           out.flush();
           //out.print(str);
           if (socket.isConnected())
              socket.close();
              socket = null;
       }
       catch (Exception e){
           System.out.println("Fechando o sockect");
       }
    }
}
