package br.pingoo;

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

public class EditarAulaActivity extends AppCompatActivity {

    private EditText editMateria, editProfessor, editDiaSemana;
    private Button btnSalvar;
    private Aula aula;

    private static final String url_base = "http://192.168.1.254:8080/aulas/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_aula);

        editMateria = findViewById(R.id.editMateria);
        editProfessor = findViewById(R.id.editProfessor);
        editDiaSemana = findViewById(R.id.editDiaSemana);
        btnSalvar = findViewById(R.id.btnSalvarAula);

        aula = (Aula) getIntent().getSerializableExtra("AULA");

        editMateria.setText(aula.getMateria());
        editProfessor.setText(aula.getProfessor());
        editDiaSemana.setText(aula.getDiaSemana());

        btnSalvar.setText("Atualizar Aula");

        btnSalvar.setOnClickListener(view -> {
            String materia = editMateria.getText().toString();
            String professor = editProfessor.getText().toString();
            String dia = editDiaSemana.getText().toString();

            if (materia.isEmpty() || professor.isEmpty() || dia.isEmpty()) {
                Toast.makeText(EditarAulaActivity.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("materia", materia);
                jsonBody.put("professor", professor);
                jsonBody.put("diaSemana", dia);

                String url = url_base + aula.getId();

                JsonObjectRequest putRequest = new JsonObjectRequest(
                        Request.Method.PUT,
                        url,
                        jsonBody,
                        response -> {
                            Toast.makeText(EditarAulaActivity.this, "Aula atualizada com sucesso!", Toast.LENGTH_SHORT).show();
                            finish();
                        },
                        error -> {
                            Toast.makeText(EditarAulaActivity.this, "Erro ao atualizar aula", Toast.LENGTH_LONG).show();
                            Log.e("Volley", "Erro no PUT aula: " + error.toString());
                        }
                );

                Volley.newRequestQueue(this).add(putRequest);

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(EditarAulaActivity.this, "Erro ao criar JSON", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
