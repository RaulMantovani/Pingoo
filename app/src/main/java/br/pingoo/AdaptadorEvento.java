package br.pingoo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

// Adaptador do RecyclerView para exibir eventos
public class AdaptadorEvento extends RecyclerView.Adapter<AdaptadorEvento.VisualizadorEvento> {
    private List<String> eventos;

    public AdaptadorEvento(List<String> eventos) {
        this.eventos = eventos;
    }

    @NonNull
    @Override
    public VisualizadorEvento onCreateViewHolder(@NonNull ViewGroup pai, int tipoDeVisualizacao) {
        View visual = LayoutInflater.from(pai.getContext()).inflate(android.R.layout.simple_list_item_1, pai, false);
        return new VisualizadorEvento(visual);
    }

    @Override
    public void onBindViewHolder(@NonNull VisualizadorEvento segurador, int posicao) {
        segurador.textoEvento.setText(eventos.get(posicao));
    }

    @Override
    public int getItemCount() {
        return eventos.size();
    }

    public static class VisualizadorEvento extends RecyclerView.ViewHolder {
        TextView textoEvento;

        public VisualizadorEvento(View itemView) {
            super(itemView);
            textoEvento = itemView.findViewById(android.R.id.text1);
        }
    }
}
