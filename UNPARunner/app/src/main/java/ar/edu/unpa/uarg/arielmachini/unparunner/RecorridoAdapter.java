package ar.edu.unpa.uarg.arielmachini.unparunner;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecorridoAdapter extends RecyclerView.Adapter<RecorridoAdapter.RecorridoViewHolder> {

    private final Context contexto;
    private final Cursor cursor;

    public RecorridoAdapter(Context contexto, Cursor cursor) {
        this.contexto = contexto;
        this.cursor = cursor;
    }

    @NonNull
    @Override
    public RecorridoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(this.contexto);
        View view = layoutInflater.inflate(R.layout.item_recorridos, parent, false);

        return new RecorridoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecorridoViewHolder holder, int position) {
        if (!this.cursor.moveToPosition(position)) {
            return;
        }

        int idRecorrido = this.cursor.getInt(0);

        holder.setFechaRecorrido(this.cursor.getString(1));
        holder.setIDRecorrido(idRecorrido);
        holder.crearOnClickListenerBotonDetalles(this.contexto, idRecorrido);
    }

    @Override
    public int getItemCount() {
        return this.cursor.getCount();
    }

    public class RecorridoViewHolder extends RecyclerView.ViewHolder {

        private final Button buttonVerDetallesRecorrido;
        private final TextView textViewTituloRecorrido;
        private final TextView textViewFechaRecorrido;

        public RecorridoViewHolder(@NonNull View itemView) {
            super(itemView);

            this.buttonVerDetallesRecorrido = itemView.findViewById(R.id.itemBotonDetalles);
            this.textViewTituloRecorrido = itemView.findViewById(R.id.itemTitulo);
            this.textViewFechaRecorrido = itemView.findViewById(R.id.itemFecha);
        }

        public void crearOnClickListenerBotonDetalles(Context contexto, int idRecorrido) {
            this.buttonVerDetallesRecorrido.setOnClickListener(view -> {
                android.content.Intent intent = new android.content.Intent(contexto, VerRecorridoActivity.class);

                intent.putExtra("idRecorrido", idRecorrido);
                contexto.startActivity(intent);
            });
        }

        public void setFechaRecorrido(String fechaRecorrido) {
            this.textViewFechaRecorrido.setText("Registrado el " + fechaRecorrido);
        }

        public void setIDRecorrido(int idRecorrido) {
            this.textViewTituloRecorrido.setText("Recorrido #" + idRecorrido);
        }

    }

}