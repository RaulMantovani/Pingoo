package br.pingoo;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AnotacoesActivity extends AppCompatActivity {

    ArrayList<JSONObject> anotacoes = new ArrayList<>();
    static ArrayList<String> notes = new ArrayList<>();
    static ArrayAdapter<String> arrayAdapter;

    ListView listView;
    Button btnAdd, btnDelete;

    RequestQueue requestQueue;
    String baseUrl = "http://192.168.1.254:8080/anotacoes";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.anotacoes_activity);

        listView = findViewById(R.id.listView);
        btnAdd = findViewById(R.id.btnAddNote);
        btnDelete = findViewById(R.id.btnDeleteNote);

        requestQueue = Volley.newRequestQueue(this);

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_activated_1, notes) {
            @Override
            public View getView(int position, View convertView, android.view.ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                String note = getItem(position);
                String nameOnly = note != null && note.contains(":") ? note.split(":", 2)[0] : note;

                TextView textView = view.findViewById(android.R.id.text1);
                textView.setText(nameOnly);
                textView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.note_text));

                return view;
            }
        };

        listView.setAdapter(arrayAdapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        carregarAnotacoes();

        listView.setOnItemClickListener((parent, view, position, id) -> {
            listView.setItemChecked(position, true);
        });

        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            try {
                int anotacaoId = anotacoes.get(position).getInt("id");
                Intent intent = new Intent(getApplicationContext(), EditorAnotacoesActivity.class);
                intent.putExtra("noteId", anotacaoId);
                startActivity(intent);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
        });

        btnAdd.setOnClickListener(v -> {
            JSONObject body = new JSONObject();
            try {
                body.put("titulo", "Nova anotação");     // CORRETO: Título visível na lista
                body.put("conteudo", "");                // Começa com conteúdo vazio
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, baseUrl, body,
                    response -> carregarAnotacoes(),
                    error -> error.printStackTrace()
            );

            requestQueue.add(request);
        });

        btnDelete.setOnClickListener(v -> {
            int pos = listView.getCheckedItemPosition();
            if (pos != ListView.INVALID_POSITION) {
                new AlertDialog.Builder(this)
                        .setTitle("Excluir anotação")
                        .setMessage("Tem certeza que deseja excluir esta anotação?")
                        .setPositiveButton("Sim", (dialog, which) -> {
                            try {
                                int id = anotacoes.get(pos).getInt("id");
                                String url = baseUrl + "/" + id;

                                StringRequest request = new StringRequest(Request.Method.DELETE, url,
                                        response -> carregarAnotacoes(),
                                        error -> error.printStackTrace()
                                );

                                requestQueue.add(request);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        })
                        .setNegativeButton("Cancelar", null)
                        .show();
            }
        });
    }

    private void carregarAnotacoes() {
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, baseUrl, null,
                response -> {
                    anotacoes.clear();
                    notes.clear();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            anotacoes.add(obj);
                            notes.add(obj.getString("titulo"));  // agora mostra o título corretamente
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    arrayAdapter.notifyDataSetChanged();
                },
                error -> error.printStackTrace()
        );

        requestQueue.add(request);
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarAnotacoes(); // atualiza lista ao voltar da tela de edição
    }
}
