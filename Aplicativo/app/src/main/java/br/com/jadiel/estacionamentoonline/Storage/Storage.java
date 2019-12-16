package br.com.jadiel.estacionamentoonline.Storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import br.com.jadiel.estacionamentoonline.Model.Motorista;
import br.com.jadiel.estacionamentoonline.View.MainActivity;

public class Storage
{
    private Context context;

    public Storage(Context context)
    {
        this.context = context;
    }

    public void salvarStatusVaga(String status)
    {
        SharedPreferences prefs = this.context.getSharedPreferences("preferencias.Vagas", Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();
        ed.putString("status", status);
        ed.apply();
        Log.i("SALVAR => ", status);
    }

    public String buscarStatusVaga()
    {
        SharedPreferences prefs = this.context.getSharedPreferences("preferencias.Vagas", Context.MODE_PRIVATE);
        String status = prefs.getString("status", MainActivity.LIVRE);
        if(status.equalsIgnoreCase(MainActivity.LIVRE))
        {
            return MainActivity.LIVRE;
        }
        else if(status.equalsIgnoreCase(MainActivity.RESERVADA))
        {
            return MainActivity.RESERVADA;
        }
        else if(status.equalsIgnoreCase(MainActivity.OCUPADA))
        {
            return MainActivity.OCUPADA;
        }
        return "";
    }

    public void salvarMotorista(String motorista)
    {
        SharedPreferences prefs = this.context.getSharedPreferences("preferencias.Motorista", Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();
        ed.putString("motorista", motorista);
        ed.apply();
        Log.i("SALVAR => ", motorista);
    }

    public Motorista buscarMotorista()
    {
        SharedPreferences prefs = this.context.getSharedPreferences("preferencias.Motorista", Context.MODE_PRIVATE);
        String motorista = prefs.getString("motorista", "");
        if(motorista.isEmpty() || motorista.equalsIgnoreCase("") || motorista == null)
        {
            return new Motorista("");
        }
        return new Motorista("Jadiel Santana");
    }
}