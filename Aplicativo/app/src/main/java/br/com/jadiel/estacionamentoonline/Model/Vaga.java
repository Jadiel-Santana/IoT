package br.com.jadiel.estacionamentoonline.Model;

public class Vaga
{
    private int id;
    private String status;
    private Motorista motorista;

    public Vaga() { }

    public Vaga(int id, String status, Motorista motorista)
    {
        this.id = id;
        this.status = status;
        this.motorista = motorista;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public Motorista getMotorista()
    {
        return motorista;
    }

    public void setMotorista(Motorista motorista)
    {
        this.motorista = motorista;
    }
}