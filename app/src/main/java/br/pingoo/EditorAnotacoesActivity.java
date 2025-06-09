package br.pingoo;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

public class EditorAnotacoesActivity extends AppCompatActivity {

    EditText noteTitleEditText, noteContentEditText;
    int noteId;
    String baseUrl = IpConfig.API_IP + "/anotacoes";
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editor_anotacoes_activity);

        noteTitleEditText = findViewById(R.id.noteNameEditText);
        noteContentEditText = findViewById(R.id.noteContentEditText);

        requestQueue = Volley.newRequestQueue(this);
        noteId = getIntent().getIntExtra("noteId", -1);

        if (noteId == -1) {
            Toast.makeText(this, "ID inválido", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        carregarAnotacao();

        // Atualizar ao sair
        findViewById(R.id.btnSalvar).setOnClickListener(v -> salvarAlteracoes());
    }

    private void carregarAnotacao() {
        String url = baseUrl + "/" + noteId;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        noteTitleEditText.setText(response.getString("titulo"));
                        noteContentEditText.setText(response.getString("conteudo"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Erro ao carregar anotação", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Erro ao buscar anotação", Toast.LENGTH_SHORT).show();
                }
        );

        requestQueue.add(request);
    }

    private void salvarAlteracoes() {
        String titulo = noteTitleEditText.getText().toString();
        String conteudo = noteContentEditText.getText().toString();

        JSONObject body = new JSONObject();
        try {
            body.put("titulo", titulo);
            body.put("conteudo", conteudo);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, baseUrl + "/" + noteId, body,
                response -> finish(),
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Erro ao salvar", Toast.LENGTH_SHORT).show();
                }
        );

        requestQueue.add(request);
    }
}
