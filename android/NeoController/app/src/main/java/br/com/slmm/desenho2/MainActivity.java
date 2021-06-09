package br.com.slmm.desenho2;

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
    private ConstraintLayout main;
    private View  vDraw;
    private Button btnClock;
    private Button btnConectar;
    private Button btnClose;
    private Button btnRodar;
    private Button btnRainbow;
    private EditText txtEndereco;
    private EditText txtPorta;


    public static final String BASE_URL = "http://192.168.15.134:80/";
    ProgressDialog progressDoalog;
    public arco _arco;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnConectar= (Button)findViewById(R.id.btnConect);
        btnRainbow= (Button)findViewById(R.id.btnRainbow);
        btnRodar= (Button)findViewById(R.id.btnRodar);
        btnClose= (Button)findViewById(R.id.btnClose);
        btnClock= (Button)findViewById(R.id.btnClock);
        txtEndereco=(EditText) findViewById(R.id.edtIp);
        txtPorta=(EditText) findViewById(R.id.edtPort);

        main = (ConstraintLayout)findViewById(R.id.main);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(width,height/2);
         _arco = new arco(this,null);
        _arco.setLayoutParams(params);
        main.addView(_arco);

        findViewById(R.id.btnClose).setOnClickListener((view)->{

            _arco.onClose();
        });

        btnConectar.setOnClickListener((View)->{
            HabilaBotoes(false);
            String end = txtEndereco.getText().toString();
            String porta = txtPorta.getText().toString();
            _arco.connecta(end, Integer.parseInt(porta));
        });
        btnClose.setOnClickListener((View)->{
            HabilaBotoes(true);
            _arco.desconecta();
        });
        btnRodar.setOnClickListener((View)->{
            _arco.efeito(1);
        });
        btnRainbow.setOnClickListener((View)->{
            _arco.efeito(2);
        });
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
    @Override
    protected void onStop(){
        super.onStop();
        _arco.onClose();
    }
}