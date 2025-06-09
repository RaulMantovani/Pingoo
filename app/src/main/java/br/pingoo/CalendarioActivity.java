package br.pingoo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
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

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

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

        calendario.setOnDateChangeListener((view, ano, mes, diaDoMes) -> {
            LocalDate dataSelecionada = LocalDate.of(ano, mes + 1, diaDoMes);
            long dataMillis = dataSelecionada.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

            carregarEventos(mostrarEventosSemana, dataMillis);
        });

        botaoAlternarSemana.setOnClickListener(v -> {
            mostrarEventosSemana = !mostrarEventosSemana;
            long dataSelecionada = calendario.getDate();
            botaoAlternarSemana.setText(mostrarEventosSemana ? "Ver eventos do dia" : "Ver eventos da semana");
            carregarEventos(mostrarEventosSemana, dataSelecionada);
        });

        findViewById(R.id.btAddEvento).setOnClickListener(v -> abrirDialogoAdicionarEvento());

        // Load events for today on start
        carregarEventos(false, calendario.getDate());
    }


    private void carregarEventos(boolean porSemana, long dataMillis) {
        eventos.clear();

        String url = IpConfig.API_IP + "/calendario/listar";

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        List<String> eventosFiltrados = new ArrayList<>();
                        LocalDate dataReferencia = LocalDate.ofEpochDay(dataMillis / (24 * 60 * 60 * 1000));
                        List<LocalDate> datasParaMostrar = new ArrayList<>();

                        if (porSemana) {
                            LocalDate inicio = obterInicioSemana(dataReferencia);
                            for (int i = 0; i < 7; i++) {
                                datasParaMostrar.add(inicio.plusDays(i));
                            }
                        } else {
                            datasParaMostrar.add(dataReferencia);
                        }

                        for (int i = 0; i < response.length(); i++) {
                            JSONObject item = response.getJSONObject(i);
                            String nome = item.getString("nomeEvento");
                            LocalDate data = LocalDate.parse(item.getString("dataEvento"));

                            if (datasParaMostrar.contains(data)) {
                                eventosFiltrados.add(nome + " - " + data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                            }
                        }

                        eventos.addAll(eventosFiltrados);
                        adaptadorEvento.notifyDataSetChanged();
                        textoEventos.setText(porSemana ? "Eventos da semana" : "Eventos do dia " + dataReferencia.getDayOfMonth() + "/" + dataReferencia.getMonthValue());

                    } catch (Exception e) {
                        Toast.makeText(this, "Erro ao processar eventos", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(this, "Erro ao buscar eventos: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }
        );

        Volley.newRequestQueue(this).add(request);
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
        // agendarNotificacao(evento); // optional
    }

    private void salvarEvento(Evento evento) {
        try {
            JSONObject json = new JSONObject();
            json.put("nomeEvento", evento.getNomeEvento());
            json.put("dataEvento", evento.getDataEvento().toString());

            String url = IpConfig.API_IP + "/calendario/registrar";

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    json,
                    response -> Toast.makeText(this, "Evento salvo com sucesso!", Toast.LENGTH_SHORT).show(),
                    error -> Toast.makeText(this, "Erro ao salvar evento: " + error.getMessage(), Toast.LENGTH_LONG).show()
            );

            Volley.newRequestQueue(this).add(request);

        } catch (Exception e) {
            Toast.makeText(this, "Erro ao criar JSON do evento", Toast.LENGTH_LONG).show();
        }
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
                (int) tempoMillis,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, tempoMillis, pendingIntent);
    }
}
