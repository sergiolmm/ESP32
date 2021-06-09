/*
*  Projeto Neo_ring
*
*  controle de um neo ring ( anel de leds endereçáveis)
*
*  Objetivo: desenhar na tela uma aro dividido em 12 segmentos e
*  a medida que desenha fazer o led equivalente ligar no neo ring
*  Colocar tambem a opção de mostrar 3 efeitos visuais diferentes
*
*  Todos os comandos enviados serão no formato Json e utiliando um cabeçalho http
*  tentei usar retrofit mas essese mostrou lento demais, assim utilizei conexão
*  socket basica e formatação do dados enviados em http (POST)
* *
*  O ESP32 é um servidor que espera comandos de conexão com um protocolo HTTP.
*
* Sergio L M Marques
* sergio@unicamp
* sergio.marques3@etec.sp.gov.br
*
* ETEC BEnto Quirino
* COTUCA - Colégio Tecnico de Campinas da Unicamp
*
* */
package br.com.slmm.neo_ring;


import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    // Declaração das variáveis que serão utilizadas no projeto
    private ConstraintLayout main;  // para ter acesso ao layout e porder adiciona a view

    // botôes de controle para conectar e desconectar e dos efeitos
    private Button btnClock;
    private Button btnConectar;
    private Button btnClose;
    private Button btnRodar;
    private Button btnRainbow;
    // edittext para digitar o endereço do esp32 e sua porta
    private EditText txtEndereco;
    private EditText txtPorta;

    // instancia da classe arco a qual desenha o arco na tela e transmite
    // os comandos para o esp32 ou outro dispositivo.

    public arco _arco;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // liga váriável ao item do layout
        btnConectar= (Button)findViewById(R.id.btnConect);
        btnRainbow= (Button)findViewById(R.id.btnRainbow);
        btnRodar= (Button)findViewById(R.id.btnRodar);
        btnClose= (Button)findViewById(R.id.btnClose);
        btnClock= (Button)findViewById(R.id.btnClock);
        txtEndereco=(EditText) findViewById(R.id.edtIp);
        txtPorta=(EditText) findViewById(R.id.edtPort);

        main = (ConstraintLayout)findViewById(R.id.main);

        // Obtem o tamanho da tela e divide por 2 para setar a area de desenho como
        // metade da tela
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(width,height/2);
        _arco = new arco(this,null); // instacia a classe
        _arco.setLayoutParams(params);            // ajusta o tamanho
        main.addView(_arco);                      // adicona a view a tela principal

        // inicia uma conexão com o endereço e porta
        // não testo se deu certo.
        btnConectar.setOnClickListener((View)->{
            HabilaBotoes(false);
            String end = txtEndereco.getText().toString();
            String porta = txtPorta.getText().toString();
            _arco.connecta(end, Integer.parseInt(porta));
        });
        // fecha a conexão
        btnClose.setOnClickListener((View)->{
            HabilaBotoes(true);
            _arco.desconecta();
        });
        // aciona o efeito 1 - Rodar um led na luz branca
        btnRodar.setOnClickListener((View)->{
            _arco.efeito(1);
        });
        // aciona o efito de arco iris
        btnRainbow.setOnClickListener((View)->{
            _arco.efeito(2);
        });
        // aciona o efeito de relogio.
        btnClock.setOnClickListener((View)->{
            _arco.efeito(3);
        });

    }

    private void HabilaBotoes(boolean estado){
        btnConectar.setEnabled(estado);
        btnClose.setEnabled(!estado);
        btnRodar.setEnabled(!estado);
        btnRainbow.setEnabled(!estado);
        btnClock.setEnabled(!estado);
    }
    // ao para a aplicação para o envio de
    @Override
    protected void onStop(){
        super.onStop();
        HabilaBotoes(true);
        _arco.desconecta();

    }
}