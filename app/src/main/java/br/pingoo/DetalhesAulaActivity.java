package br.pingoo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.text.InputType;
import android.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DetalhesAulaActivity extends AppCompatActivity {

    private TextView tvMateria, tvProfessor, tvDiaSemana, tvSemNotas, tvSemAnotacoes;
    private Aula aula;
    private ListView listaNotas, listaAnotacoes;
    private Button btnAdicionarNota, btnAdicionarAnotacao, btnExcluirAula;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_aula);


        tvMateria = findViewById(R.id.tvMateriaDetalhe);
        tvProfessor = findViewById(R.id.tvProfessorDetalhe);
        tvDiaSemana = findViewById(R.id.tvDiaSemanaDetalhe);
        tvSemNotas = findViewById(R.id.tvListaNotas);
        tvSemAnotacoes = findViewById(R.id.tvListaAnotacoes);
        listaNotas = findViewById(R.id.listaNotas);
        listaAnotacoes = findViewById(R.id.listaAnotacoes);
        btnAdicionarNota = findViewById(R.id.btnAdicionarNota);
        btnAdicionarAnotacao = findViewById(R.id.btnAdicionarAnotacao);
        btnExcluirAula = findViewById(R.id.btnExcluirAula);

        int aulaId = getIntent().getIntExtra("AULA_ID", -1);
        if (aulaId != -1) {
            buscarAulaDoServidor(aulaId);
        } else {
            Toast.makeText(this, "ID da aula inválido", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Adiciona uma nova nota com descrição
        btnAdicionarNota.setOnClickListener(v -> {
            View view = getLayoutInflater().inflate(R.layout.dialog_nota, null);
            EditText inputNota = view.findViewById(R.id.inputNota);
            EditText inputDescricao = view.findViewById(R.id.inputDescricao);

            inputNota.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

            new AlertDialog.Builder(this)
                    .setTitle("Adicionar Nota")
                    .setView(view)
                    .setPositiveButton("Salvar", (dialog, which) -> {
                        try {
                            float valorNota = Float.parseFloat(inputNota.getText().toString());
                            String descricao = inputDescricao.getText().toString().trim();

                            if (descricao.isEmpty()) {
                                Toast.makeText(this, "Descrição não pode ser vazia", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            // Requisição POST via Volley
                            String url = IpConfig.API_IP + "/aulas/" + aula.getId() + "/notas";

                            JSONObject jsonBody = new JSONObject();
                            jsonBody.put("valor", valorNota);
                            jsonBody.put("descricao", descricao);

                            JsonObjectRequest request = new JsonObjectRequest(
                                    Request.Method.POST,
                                    url,
                                    jsonBody,
                                    response -> {
                                        // Atualizar dados da tela após salvar no backend
                                        Toast.makeText(this, "Nota adicionada", Toast.LENGTH_SHORT).show();
                                        buscarAulaDoServidor(aula.getId()); // método que recarrega aula do backend
                                    },
                                    error -> {
                                        Toast.makeText(this, "Erro ao adicionar nota", Toast.LENGTH_SHORT).show();
                                        error.printStackTrace();
                                    }
                            );

                            // Adiciona a requisição na fila
                            Volley.newRequestQueue(this).add(request);

                        } catch (NumberFormatException | JSONException e) {
                            Toast.makeText(this, "Valor inválido", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });


        // Adiciona uma anotação
        btnAdicionarAnotacao.setOnClickListener(v -> {
            EditText inputAnotacao = new EditText(this);
            inputAnotacao.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);

            new AlertDialog.Builder(this)
                    .setTitle("Adicionar Anotação")
                    .setView(inputAnotacao)
                    .setPositiveButton("Salvar", (dialog, which) -> {
                        String texto = inputAnotacao.getText().toString().trim();
                        if (texto.isEmpty()) {
                            Toast.makeText(this, "Anotação não pode ser vazia", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String url = IpConfig.API_IP + "/aulas/" + aula.getId() + "/anotacoes";

                        JSONObject jsonBody = new JSONObject();
                        try {
                            jsonBody.put("texto", texto);
                        } catch (JSONException e) {
                            Toast.makeText(this, "Erro ao criar anotação", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        JsonObjectRequest request = new JsonObjectRequest(
                                Request.Method.POST,
                                url,
                                jsonBody,
                                response -> {
                                    Toast.makeText(this, "Anotação adicionada", Toast.LENGTH_SHORT).show();
                                    buscarAulaDoServidor(aula.getId()); // recarrega os dados
                                },
                                error -> {
                                    Toast.makeText(this, "Erro ao adicionar anotação", Toast.LENGTH_SHORT).show();
                                    error.printStackTrace();
                                }
                        );

                        Volley.newRequestQueue(this).add(request);
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });

        //Exclui a aula
        btnExcluirAula.setOnClickListener(v -> {
            String url = IpConfig.API_IP + "/aulas/" + aula.getId();

            StringRequest request = new StringRequest(
                    Request.Method.DELETE,
                    url,
                    response -> {
                        Toast.makeText(this, "Aula excluída", Toast.LENGTH_SHORT).show();
                        finish(); // Volta para a tela anterior (AulasActivity)
                    },
                    error -> {
                        Toast.makeText(this, "Erro ao excluir aula", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
            );

            Volley.newRequestQueue(this).add(request);
        });
        // Edita ou exclui uma nota
        //excluído por agora

        // Edita ou exclui uma anotação
        //excluido por agora
    }

    private void buscarAulaDoServidor(int id) {
        String url = IpConfig.API_IP + "/aulas/" + id;

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        // Constrói o objeto Aula manualmente com base no JSON retornado
                        int aulaId = response.getInt("id");
                        String materia = response.getString("materia");
                        String professor = response.getString("professor");
                        String diaSemana = response.getString("diaSemana");

                        aula = new Aula(aulaId, materia, professor, diaSemana);

                        // Inicializa as listas vazias
                        List<Nota> listaNotas = new ArrayList<>();
                        JSONArray notasArray = response.getJSONArray("notas");
                        Log.d("DEBUG", "JSON recebido: " + response.toString());
                        for (int i = 0; i < notasArray.length(); i++) {
                            JSONObject notaJson = notasArray.getJSONObject(i);
                            float valor = (float) notaJson.getDouble("valor");
                            String descricao = notaJson.getString("descricao");
                            listaNotas.add(new Nota(valor, descricao));
                        }
                        aula.setNotas(listaNotas);

                        List<String> listaAnotacoes = new ArrayList<>();
                        JSONArray anotacoesArray = response.getJSONArray("anotacoes");
                        for (int i = 0; i < anotacoesArray.length(); i++) {
                            String anotacao = anotacoesArray.getString(i);
                            listaAnotacoes.add(anotacao);
                        }
                        aula.setAnotacoes(listaAnotacoes);

                        tvMateria.setText("Matéria: " + aula.getMateria());
                        tvProfessor.setText("Professor: " + aula.getProfessor());
                        tvDiaSemana.setText("Dia: " + aula.getDiaSemana());

                        Log.d("DEBUG", "Atualizando dados: " + aula.getNotas().size() + " notas");
                        atualizarDados();
                    } catch (JSONException e) {
                        Toast.makeText(this, "Erro ao interpretar dados", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Erro ao carregar aula", Toast.LENGTH_SHORT).show()
        );

        queue.add(request);
    }



    private void atualizarDados() {
        if (aula == null) return;

        ArrayAdapter<Nota> adapterNotas = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, aula.getNotas());
        listaNotas.setAdapter(adapterNotas);

        ArrayAdapter<String> adapterAnotacoes = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, aula.getAnotacoes());
        listaAnotacoes.setAdapter(adapterAnotacoes);

        if (aula.getNotas().isEmpty()) {
            tvSemNotas.setVisibility(View.VISIBLE);
        } else {
            tvSemNotas.setVisibility(View.GONE);
        }

        if (aula.getAnotacoes().isEmpty()) {
            tvSemAnotacoes.setVisibility(View.VISIBLE);
        } else {
            tvSemAnotacoes.setVisibility(View.GONE);
        }
    }
}

