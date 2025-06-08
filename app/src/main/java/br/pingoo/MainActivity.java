package br.pingoo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button btnAulas, btnCalendario, btnAnotacoes, btnPerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAulas = findViewById(R.id.btnAulas);
        btnCalendario = findViewById(R.id.btnCalendario);
        btnAnotacoes = findViewById(R.id.btnAnotacoes);
        //btnPerfil = findViewById(R.id.btnPerfil); // PARA O FUTURO

        btnAulas.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, AulasActivity.class));
        });

        btnCalendario.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, CalendarioActivity.class));
        });

        btnAnotacoes.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, AnotacoesActivity.class));
        });

        // Se quiser usar o botão de perfil depois
        // btnPerfil.setOnClickListener(view -> {
        //     startActivity(new Intent(MainActivity.this, PerfilActivity.class));
        // });
    }
}
