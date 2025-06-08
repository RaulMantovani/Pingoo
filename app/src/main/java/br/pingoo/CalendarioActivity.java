package br.pingoo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.DatePicker;
import android.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.time.ZoneId;

public class CalendarioActivity extends AppCompatActivity {

    private RecyclerView listaEventos;
    private AdaptadorEvento adaptadorEvento;
    private List<String> eventos;
    private CalendarView calendario;
    private TextView textoEventos;
    private Button botaoAlternarSemana;
    private boolean mostrarEventosSemana = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendario);

        calendario = findViewById(R.id.calendario);
        listaEventos = findViewById(R.id.listaEventos);
        textoEventos = findViewById(R.id.textoEventos);
        botaoAlternarSemana = findViewById(R.id.botaoAlternarSemana);

        eventos = new ArrayList<>();
        adaptadorEvento = new AdaptadorEvento(eventos);
        listaEventos.setLayoutManager(new LinearLayoutManager(this));
        listaEventos.setAdapter(adaptadorEvento);

        carregarEventosSalvos();

        calendario.setOnDateChangeListener((view, ano, mes, diaDoMes) -> {
            LocalDate dataSelecionada = LocalDate.of(ano, mes + 1, diaDoMes);
            long dataMillis = dataSelecionada.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

            if (!mostrarEventosSemana) {
                carregarEventosDoDia(dataMillis);
            } else {
                carregarEventosDaSemana(dataMillis);
            }
        });

        botaoAlternarSemana.setOnClickListener(v -> {
            mostrarEventosSemana = !mostrarEventosSemana;
            long dataSelecionada = calendario.getDate();
            if (mostrarEventosSemana) {
                botaoAlternarSemana.setText("Ver eventos do dia");
                carregarEventosDaSemana(dataSelecionada);
            } else {
                botaoAlternarSemana.setText("Ver eventos da semana");
                carregarEventosDoDia(dataSelecionada);
            }
        });

        findViewById(R.id.btAddEvento).setOnClickListener(v -> abrirDialogoAdicionarEvento());
    }

    private void carregarEventosDoDia(long dataMillis) {
        eventos.clear();

        LocalDate data = LocalDate.ofEpochDay(dataMillis / (24 * 60 * 60 * 1000));
        SharedPreferences prefs = getSharedPreferences("events", MODE_PRIVATE);
        String chaveData = data.toString();

        String eventosSalvos = prefs.getString(chaveData, "");
        if (!eventosSalvos.isEmpty()) {
            String[] itens = eventosSalvos.split(";");
            for (String item : itens) {
                eventos.add(item + " - " + data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            }
        }

        adaptadorEvento.notifyDataSetChanged();
        textoEventos.setText("Eventos do dia " + data.getDayOfMonth() + "/" + data.getMonthValue());
    }

    private void carregarEventosDaSemana(long dataMillis) {
        eventos.clear();

        LocalDate selecionada = LocalDate.ofEpochDay(dataMillis / (24 * 60 * 60 * 1000));
        LocalDate inicioSemana = obterInicioSemana(selecionada);
        SharedPreferences prefs = getSharedPreferences("events", MODE_PRIVATE);

        for (int i = 0; i < 7; i++) {
            LocalDate dia = inicioSemana.plusDays(i);
            String eventosSalvos = prefs.getString(dia.toString(), "");
            if (!eventosSalvos.isEmpty()) {
                String[] itens = eventosSalvos.split(";");
                for (String item : itens) {
                    eventos.add(item + " - " + dia.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                }
            }
        }

        adaptadorEvento.notifyDataSetChanged();
        textoEventos.setText("Eventos da semana");
    }

    private LocalDate obterInicioSemana(LocalDate data) {
        return data.with(DayOfWeek.MONDAY);
    }

    private void abrirDialogoAdicionarEvento() {
        AlertDialog.Builder construtor = new AlertDialog.Builder(this);
        construtor.setTitle("Adicionar Evento");

        final EditText campoNomeEvento = new EditText(this);
        campoNomeEvento.setHint("Nome do Evento");

        final DatePicker seletorData = new DatePicker(this);
        seletorData.setMinDate(System.currentTimeMillis() - 1000);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(campoNomeEvento);
        layout.addView(seletorData);

        construtor.setView(layout);

        construtor.setPositiveButton("Adicionar", (dialogo, qual) -> {
            String nome = campoNomeEvento.getText().toString().trim();
            int ano = seletorData.getYear();
            int mes = seletorData.getMonth() + 1;
            int dia = seletorData.getDayOfMonth();

            if (!nome.isEmpty()) {
                LocalDate dataEvento = LocalDate.of(ano, mes, dia);
                Evento evento = new Evento(nome, dataEvento);
                adicionarEvento(evento);
            } else {
                Toast.makeText(this, "Digite um nome válido para o evento", Toast.LENGTH_SHORT).show();
            }
        });

        construtor.setNegativeButton("Cancelar", (dialogo, qual) -> dialogo.dismiss());

        construtor.show();
    }

    private void adicionarEvento(Evento evento) {
        eventos.add(evento.toString());
        salvarEvento(evento);
        adaptadorEvento.notifyDataSetChanged();
        agendarNotificacao(evento);

    }
    private void agendarNotificacao(Evento evento) {
        long tempoMillis = evento.getDataEvento()
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();

        Intent intent = new Intent(this, AlarmeReceiver.class);
        intent.putExtra("titulo", evento.getNomeEvento());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                (int) tempoMillis, // ID único
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, tempoMillis, pendingIntent);
    }


    private void salvarEvento(Evento evento) {
        SharedPreferences prefs = getSharedPreferences("events", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        String chaveData = evento.getDataEvento().toString();
        String eventosExistentes = prefs.getString(chaveData, "");

        String eventosAtualizados = eventosExistentes.isEmpty() ? evento.getNomeEvento() : eventosExistentes + ";" + evento.getNomeEvento();

        editor.putString(chaveData, eventosAtualizados);
        editor.apply();
    }

    private void carregarEventosSalvos() {
        SharedPreferences prefs = getSharedPreferences("events", MODE_PRIVATE);
        String listaEventosStr = prefs.getString("eventList", "");

        String[] arrayEventos = listaEventosStr.split(",");

        eventos.clear();
        for (String evento : arrayEventos) {
            if (!evento.isEmpty()) {
                eventos.add(evento);
            }
        }

        adaptadorEvento.notifyDataSetChanged();
    }
}
