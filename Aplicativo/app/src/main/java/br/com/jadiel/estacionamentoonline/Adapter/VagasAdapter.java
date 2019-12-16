package br.com.jadiel.estacionamentoonline.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import br.com.jadiel.estacionamentoonline.View.MainActivity;
import br.com.jadiel.estacionamentoonline.Model.Vaga;
import br.com.jadiel.estacionamentoonline.R;


public class VagasAdapter extends RecyclerView.Adapter<VagasAdapter.RecyclerViewHolder>
{
    private List<Vaga> lista;
    private LayoutInflater layoutInflater;
    private Context context;

    public VagasAdapter(Context c, List<Vaga> l)
    {
        this.context = c;
        this.lista = l;
        layoutInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = layoutInflater.inflate(R.layout.itens_vagas, parent, false);
        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view);
        return recyclerViewHolder;
    }

    @SuppressLint("ResourceType")
    public void onBindViewHolder(RecyclerViewHolder holder, int position)
    {
        holder.txtNumeroVaga.setText("Vaga: "+lista.get(position).getId());
        holder.txtMotorista.setText(lista.get(position).getMotorista().getNome());

        if(lista.get(position).getStatus().equalsIgnoreCase(MainActivity.LIVRE))
        {
            holder.txtStatus.setTextColor(context.getResources().getColor(R.color.colorGreen));
        }
        else if(lista.get(position).getStatus().equalsIgnoreCase(MainActivity.RESERVADA))
        {
            holder.txtStatus.setTextColor(context.getResources().getColor(R.color.colorYellow));
        }
        else if(lista.get(position).getStatus().equalsIgnoreCase(MainActivity.OCUPADA))
        {
            holder.txtStatus.setTextColor(context.getResources().getColor(R.color.colorRed));
        }
        holder.txtStatus.setText(lista.get(position).getStatus());
    }

    public int getItemCount()
    {
        return lista.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder
    {
        private TextView txtNumeroVaga;
        private TextView txtMotorista;
        private TextView txtStatus;

        public RecyclerViewHolder(View itemView)
        {
            super(itemView);
            txtNumeroVaga = itemView.findViewById(R.id.txt_numero_vaga);
            txtMotorista = itemView.findViewById(R.id.txt_motorista);
            txtStatus = itemView.findViewById(R.id.txt_status);
        }
    }
}