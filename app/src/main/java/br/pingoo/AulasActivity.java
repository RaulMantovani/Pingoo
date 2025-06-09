package br.pingoo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AulasActivity extends AppCompatActivity {

    private static final String url = IpConfig.API_IP + "/aulas";
    private RecyclerView recyclerView;
    private AulaAdapter adapter;
    private List<Aula> listaAulas;
    private Button btnAdicionarAula;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aulas);

        recyclerView = findViewById(R.id.recyclerViewAulas);
        btnAdicionarAula = findViewById(R.id.btnAdicionarAula);

        // Inicializa a lista (evita null)
        listaAulas = new ArrayList<>();

        // Configura o RecyclerView
        adapter = new AulaAdapter(listaAulas, new AulaAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Aula aula) {
                // Navega para a tela de visualização ou edição
                Intent intent = new Intent(AulasActivity.this, EditarAulaActivity.class);
                intent.putExtra("AULA", aula);  // Passa o objeto Aula para a nova atividade
                startActivity(intent);
            }

            @Override
            public void onEditarClick(Aula aula) {
                // Quando o botão de editar for clicado, navega para a tela de edição
                Intent intent = new Intent(AulasActivity.this, EditarAulaActivity.class);
                intent.putExtra("AULA", aula);  // Passa o objeto Aula para a nova atividade
                startActivity(intent);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Botão para adicionar aula
        btnAdicionarAula.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AulasActivity.this, AdicionarAulaActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        buscarAulasDoServidor();
    }

    private void buscarAulasDoServidor() {
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    listaAulas.clear();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            int id = obj.getInt("id");
                            String materia = obj.getString("materia");
                            String professor = obj.getString("professor");
                            String diaSemana = obj.getString("diaSemana");

                            Aula aula = new Aula(id, materia, professor, diaSemana);
                            listaAulas.add(aula);
                        }
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        Toast.makeText(this, "Erro ao processar aulas", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                },
                error -> {
                    Toast.makeText(this, "Erro ao buscar aulas do servidor", Toast.LENGTH_LONG).show();
                    Log.e("Volley", "Erro: " + error.toString());
                });

        queue.add(request);
    }
}
