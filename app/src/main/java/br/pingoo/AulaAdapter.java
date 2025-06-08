package br.pingoo;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AulaAdapter extends RecyclerView.Adapter<AulaAdapter.AulaViewHolder> {

    private List<Aula> aulas;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Aula aula);
        void onEditarClick(Aula aula);
    }

    public AulaAdapter(List<Aula> aulas, OnItemClickListener listener) {
        this.aulas = aulas;
        this.listener = listener;
    }

    @Override
    public AulaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_aula, parent, false);
        return new AulaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AulaViewHolder holder, int position) {
        Aula aula = aulas.get(position);
        holder.bind(aula, listener);
    }

    @Override
    public int getItemCount() {
        return aulas.size();
    }

    public static class AulaViewHolder extends RecyclerView.ViewHolder {
        private TextView textMateria, textProfessor, textDiaSemana;
        private Button btnEditar, btnEntrar;

        public AulaViewHolder(View itemView) {
            super(itemView);
            textMateria = itemView.findViewById(R.id.tvMateria);
            textProfessor = itemView.findViewById(R.id.tvProfessor);
            textDiaSemana = itemView.findViewById(R.id.tvDiaSemana);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnEntrar = itemView.findViewById(R.id.btnEntrar);
        }

        public void bind(final Aula aula, final OnItemClickListener listener) {
            textMateria.setText(aula.getMateria());
            textProfessor.setText(aula.getProfessor());
            textDiaSemana.setText(aula.getDiaSemana());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(aula);
                }
            });

            btnEditar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onEditarClick(aula);
                }
            });

            btnEntrar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, DetalhesAulaActivity.class);
                    intent.putExtra("AULA_ID", aula.getId());
                    context.startActivity(intent);
                }
            });
        }
    }
}
