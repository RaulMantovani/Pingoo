package br.pingoo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class AdicionarAulaActivity extends AppCompatActivity {

    private EditText editMateria, editProfessor, editDiaSemana;
    private Button btnSalvar;
    private static final String url = IpConfig.API_IP + "/aulas";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_aula);

        editMateria = findViewById(R.id.editMateria);
        editProfessor = findViewById(R.id.editProfessor);
        editDiaSemana = findViewById(R.id.editDiaSemana);
        btnSalvar = findViewById(R.id.btnSalvarAula);

        btnSalvar.setOnClickListener(v -> {
            String materia = editMateria.getText().toString();
            String professor = editProfessor.getText().toString();
            String dia = editDiaSemana.getText().toString();

            if (materia.isEmpty() || professor.isEmpty() || dia.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("materia", materia);
                jsonBody.put("professor", professor);
                jsonBody.put("diaSemana", dia);

                JsonObjectRequest request = new JsonObjectRequest(
                        Request.Method.POST,
                        url,
                        jsonBody,
                        response -> {
                            Toast.makeText(this, "Aula salva com sucesso!", Toast.LENGTH_SHORT).show();
                            finish(); // Fecha a Activity
                        },
                        error -> {
                            Toast.makeText(this, "Erro ao salvar aula", Toast.LENGTH_LONG).show();
                            Log.e("Volley", "Erro ao enviar aula: " + error.toString());
                        }
                );

                Volley.newRequestQueue(this).add(request);
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(this, "Erro ao criar JSON", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
