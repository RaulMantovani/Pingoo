package br.pingoo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.HashSet;

public class EditorAnotacoesActivity extends AppCompatActivity {
    int noteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.editor_anotacoes_activity);

        // Ajusta as margens para evitar sobreposição com a barra do sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Referência para os campos de nome da nota e conteúdo
        EditText noteNameEditText = findViewById(R.id.noteNameEditText); // EditText para o nome da nota
        EditText noteContentEditText = findViewById(R.id.noteContentEditText); // EditText para o conteúdo da nota

        // Obter o ID da nota a ser editada
        noteId = getIntent().getIntExtra("noteId", -1);

        if (noteId != -1) {
            // Preenche os campos com os dados da nota selecionada
            String note = AnotacoesActivity.notes.get(noteId);
            String[] parts = note.split(":"); // Assume que o nome da nota e o conteúdo estão separados por ':'
            if (parts.length > 1) {
                noteNameEditText.setText(parts[0]);
                noteContentEditText.setText(parts[1]);
            } else {
                noteNameEditText.setText(parts[0]); // Caso não haja conteúdo, preenche com o nome apenas
            }
        }

        // Listener para salvar as alterações
        noteNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Atualiza a lista de notas com o novo nome
                updateNote();
            }
        });

        noteContentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Atualiza a lista de notas com o novo conteúdo
                updateNote();
            }
        });
    }

    // Método para atualizar a nota após alteração
    private void updateNote() {
        EditText noteNameEditText = findViewById(R.id.noteNameEditText);
        EditText noteContentEditText = findViewById(R.id.noteContentEditText);
        String updatedNote = noteNameEditText.getText().toString() + ":" + noteContentEditText.getText().toString();

        // Atualiza a lista de notas na memória
        AnotacoesActivity.notes.set(noteId, updatedNote);
        AnotacoesActivity.arrayAdapter.notifyDataSetChanged();

        // Salva a nova lista de notas no SharedPreferences
        SharedPreferences sp = getApplicationContext().getSharedPreferences("br.pingoo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("notes_json", new org.json.JSONArray(AnotacoesActivity.notes).toString());
        editor.apply();

    }
}
