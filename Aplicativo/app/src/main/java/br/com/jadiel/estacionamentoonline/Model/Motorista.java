package br.com.jadiel.estacionamentoonline.Model;

public class Motorista
{
    private String nome;

    public Motorista() { }

    public Motorista(String nome)
    {
        this.nome = nome;
    }

    public String getNome()
    {
        return nome;
    }

    public void setNome(String nome)
    {
        this.nome = nome;
    }
}