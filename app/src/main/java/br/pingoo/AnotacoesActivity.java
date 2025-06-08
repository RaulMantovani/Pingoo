package br.pingoo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;

import java.util.ArrayList;
import androidx.core.content.ContextCompat;


public class AnotacoesActivity extends AppCompatActivity {

    static ArrayList<String> notes = new ArrayList<>();
    static ArrayAdapter<String> arrayAdapter;

    ListView listView;
    Button btnAdd, btnDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.anotacoes_activity);

        listView = findViewById(R.id.listView);
        btnAdd = findViewById(R.id.btnAddNote);
        btnDelete = findViewById(R.id.btnDeleteNote);

        // Carrega as notas salvas no formato JSON
        SharedPreferences sp = getApplicationContext().getSharedPreferences("br.pingoo", Context.MODE_PRIVATE);
        String notesJson = sp.getString("notes_json", null);

        if (notesJson != null) {
            try {
                JSONArray jsonArray = new JSONArray(notesJson);
                notes.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    notes.add(jsonArray.getString(i));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            notes.add("");
        }

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

        // Clique curto apenas seleciona
        listView.setOnItemClickListener((parent, view, position, id) -> {
            listView.setItemChecked(position, true);
        });

        // Clique longo abre para edição
        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(getApplicationContext(), EditorAnotacoesActivity.class);
            intent.putExtra("noteId", position);
            startActivity(intent);
            return true;
        });

        btnAdd.setOnClickListener(v -> {
            notes.add("");
            arrayAdapter.notifyDataSetChanged();
            salvarNotas();

            Intent intent = new Intent(getApplicationContext(), EditorAnotacoesActivity.class);
            intent.putExtra("noteId", notes.size() - 1);
            startActivity(intent);
        });

        btnDelete.setOnClickListener(v -> {
            int pos = listView.getCheckedItemPosition();
            if (pos != ListView.INVALID_POSITION) {
                new AlertDialog.Builder(this)
                        .setTitle("Excluir nota")
                        .setMessage("Tem certeza que deseja excluir essa nota?")
                        .setPositiveButton("Sim", (dialog, which) -> {
                            notes.remove(pos);
                            arrayAdapter.notifyDataSetChanged();
                            listView.clearChoices();
                            salvarNotas();
                        })
                        .setNegativeButton("Cancelar", null)
                        .show();
            }
        });
    }

    private void salvarNotas() {
        SharedPreferences sp = getApplicationContext().getSharedPreferences("br.pingoo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("notes_json", new JSONArray(notes).toString());
        editor.apply();
    }
}
