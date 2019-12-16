package br.com.jadiel.estacionamentoonline.View;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.List;

import br.com.jadiel.estacionamentoonline.Adapter.VagasAdapter;
import br.com.jadiel.estacionamentoonline.Interfaces.RecyclerViewOnClickListenerHack;
import br.com.jadiel.estacionamentoonline.Model.Motorista;
import br.com.jadiel.estacionamentoonline.Model.Vaga;
import br.com.jadiel.estacionamentoonline.R;
import br.com.jadiel.estacionamentoonline.Storage.Storage;
import me.drakeet.materialdialog.MaterialDialog;

public class MainActivity extends AppCompatActivity implements RecyclerViewOnClickListenerHack
{
    public static final String NOTIFICATION_CHANNEL_ID = "channel_id";
    public static final String CHANNEL_NAME = "Notification Channel";
    public static final int NOTIFICATION_ID = 101;
    private int importance = NotificationManager.IMPORTANCE_DEFAULT;
    private MaterialDialog materialDialog;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<Vaga> lista = new ArrayList<>();
    private VagasAdapter vagasAdapter;
    private Vaga vaga = new Vaga();
    private Motorista motorista = new Motorista();
    private Storage storage;
    private String status = "";
    private int pos = 0;

    public static final String LIVRE = "Livre";
    public static final String RESERVADA = "Reservada";
    public static final String OCUPADA = "Ocupada";

    /* SERVIDOR MQTT */
    private static String MQTTHOST = "tcp://tailor.cloudmqtt.com:16607";
    private static String USUARIO = "fyvugjqh";
    private static String SENHA = "6NVSxJiSFcDY";
    private static String TOPICO = "LED";
    private static String TOPICO_SENSOR = "SENSOR";
    private String mensagem[] = {};
    private MqttAndroidClient client;

    @SuppressLint("ResourceType")
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        storage = new Storage(this);
        status = storage.buscarStatusVaga();
        motorista = storage.buscarMotorista();

        conectaServidor();

        lista = iniciarLista();
        recyclerView = findViewById(R.id.recycler_view);
        vagasAdapter = new VagasAdapter(this, lista);
        adapter = vagasAdapter;
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.addOnItemTouchListener(new RecyclerViewTouchListener(this, recyclerView, this));

        try
        {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    client.setCallback(new MqttCallback() {
                        public void connectionLost(Throwable cause) {}
                        public void messageArrived(String topic, MqttMessage message)
                        {
                            String sensor = new String(message.getPayload());
                            vaga = lista.get(pos);
                            if(Integer.parseInt(sensor) > 100 && vaga.getId() == 1050 && vaga.getStatus().equalsIgnoreCase(MainActivity.OCUPADA))
                            {
                                Log.i("Notificação => ", "Seu veículo acaba de sair da vaga.");

                                runOnUiThread(new Runnable() {
                                    @RequiresApi(api = Build.VERSION_CODES.O)
                                    @Override
                                    public void run() {
                                        dispararNotificacao();
                                    }
                                });

                                vagaLivre();
                                storage.salvarStatusVaga(MainActivity.LIVRE);
                                vaga.setStatus(MainActivity.LIVRE);
                                storage.salvarMotorista("");
                                motorista = storage.buscarMotorista();
                                vaga.setMotorista(motorista);
                                vagasAdapter.notifyDataSetChanged();
                            }
                        }
                        public void deliveryComplete(IMqttDeliveryToken token) {}
                    });
                }
            }).start();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public List<Vaga> iniciarLista()
    {
        lista.add(new Vaga(1040, MainActivity.LIVRE, new Motorista("")));
        lista.add(new Vaga(1050, status, motorista));
        lista.add(new Vaga(1060, MainActivity.LIVRE, new Motorista("")));
        lista.add(new Vaga(1070, MainActivity.LIVRE, new Motorista("")));
        lista.add(new Vaga(1080, MainActivity.LIVRE, new Motorista("")));
        return lista;
    }

    private void conectaServidor()
    {
        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(this.getApplicationContext(), MQTTHOST, clientId);
        final MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(USUARIO);
        options.setPassword(SENHA.toCharArray());

        new Thread(new Runnable() {
            @Override
            public void run() {
                try
                {
                    IMqttToken token = client.connect(options);
                    token.setActionCallback(new IMqttActionListener()
                    {
                        public void onSuccess(IMqttToken asyncActionToken)
                        {
                            Toast.makeText(MainActivity.this,"Conectado",Toast.LENGTH_LONG).show();
                            sensorVaga();
                        }

                        public void onFailure(IMqttToken asyncActionToken, Throwable exception)
                        {
                            Toast.makeText(MainActivity.this,"Não conectado",Toast.LENGTH_LONG).show();
                        }
                    });
                }
                catch (MqttException e)
                {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void vagaLivre()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mensagem = new String[]{"L1", "D2", "D3"};
                try
                {
                    client.publish(TOPICO, mensagem[0].getBytes(),0,false);
                    client.publish(TOPICO, mensagem[1].getBytes(),0,false);
                    client.publish(TOPICO, mensagem[2].getBytes(),0,false);
                }
                catch (MqttException e)
                {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void reservarVaga()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mensagem = new String[]{"D1", "L2", "D3"};
                try
                {
                    client.publish(TOPICO, mensagem[0].getBytes(),0,false);
                    client.publish(TOPICO, mensagem[1].getBytes(),0,false);
                    client.publish(TOPICO, mensagem[2].getBytes(),0,false);
                }
                catch (MqttException e)
                {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void ocuparVaga()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mensagem = new String[]{"D1", "D2", "L3"};
                try
                {
                    client.publish(TOPICO, mensagem[0].getBytes(),0,false);
                    client.publish(TOPICO, mensagem[1].getBytes(),0,false);
                    client.publish(TOPICO, mensagem[2].getBytes(),0,false);
                }
                catch (MqttException e)
                {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void sensorVaga()
    {
        try
        {
            client.subscribe(MainActivity.TOPICO_SENSOR, 0);
        }
        catch (MqttException e)
        {
            e.printStackTrace();
        }
    }

    public void dispararNotificacao()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, CHANNEL_NAME, importance);
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);
            notificationChannel.setLightColor(Color.GREEN);
            notificationChannel.setVibrationPattern(new long[] {
                    500,
                    500,});
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        builder.setSmallIcon(R.drawable.notificacao);
        builder.setAutoCancel(true);
        builder.setColor(Color.GREEN);
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        builder.setContentTitle("Obrigado!");
        builder.setContentText("Seu veículo acabou de sair da Vaga.");

        Intent intent = new Intent(MainActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        Notification notification = builder.build();

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(NOTIFICATION_ID, notification);
    }

    public void onClickListener(View view, int position)
    {
        pos = position;
        vaga = lista.get(position);
        if(vaga.getId() == 1050)
        {
            if(vaga.getStatus().equalsIgnoreCase(MainActivity.LIVRE))
            {
                materialDialog = new MaterialDialog(this);
                materialDialog.setTitle("Aviso..")
                        .setMessage("Deseja Reservar esta Vaga?")
                        .setPositiveButton("SIM", new View.OnClickListener()
                        {
                            public void onClick(View v)
                            {
                                reservarVaga();
                                storage.salvarStatusVaga(MainActivity.RESERVADA);
                                vaga.setStatus(MainActivity.RESERVADA);
                                storage.salvarMotorista("Jadiel Santana");
                                motorista = storage.buscarMotorista();
                                vaga.setMotorista(motorista);
                                vagasAdapter.notifyDataSetChanged();
                                materialDialog.dismiss();
                            }
                        })
                        .setNegativeButton("NÃO", new View.OnClickListener()
                        {
                            public void onClick(View v)
                            {
                                materialDialog.dismiss();
                            }
                        });
                materialDialog.show();
            }
            else if(vaga.getStatus().equalsIgnoreCase(MainActivity.RESERVADA))
            {
                materialDialog = new MaterialDialog(this);
                materialDialog.setTitle("Aviso...")
                        .setMessage("Você chegou na Vaga Reservada?")
                        .setPositiveButton("SIM", new View.OnClickListener()
                        {
                            public void onClick(View v)
                            {
                                ocuparVaga();
                                storage.salvarStatusVaga(MainActivity.OCUPADA);
                                vaga.setStatus(MainActivity.OCUPADA);
                                storage.salvarMotorista("Jadiel Santana");
                                motorista = storage.buscarMotorista();
                                vaga.setMotorista(motorista);
                                vagasAdapter.notifyDataSetChanged();
                                materialDialog.dismiss();
                            }
                        })
                        .setNegativeButton("NÃO", new View.OnClickListener()
                        {
                            public void onClick(View v) {
                                materialDialog.dismiss();
                            }
                        });
                materialDialog.show();
            }
            else if(vaga.getStatus().equalsIgnoreCase(MainActivity.OCUPADA))
            {
                materialDialog = new MaterialDialog(this);
                materialDialog.setTitle("Aviso...")
                        .setMessage("Você está saindo Vaga?")
                        .setPositiveButton("SIM", new View.OnClickListener()
                        {
                            public void onClick(View v)
                            {
                                vagaLivre();
                                storage.salvarStatusVaga(MainActivity.LIVRE);
                                vaga.setStatus(MainActivity.LIVRE);
                                storage.salvarMotorista("");
                                motorista = storage.buscarMotorista();
                                vaga.setMotorista(motorista);
                                vagasAdapter.notifyDataSetChanged();
                                materialDialog.dismiss();
                            }
                        })
                        .setNegativeButton("NÃO", new View.OnClickListener()
                        {
                            public void onClick(View v) {
                                materialDialog.dismiss();
                            }
                        });
                materialDialog.show();
            }
        }
        else
        {
            Toast.makeText(MainActivity.this, "Você já possui uma reserva em aberto.", Toast.LENGTH_SHORT).show();
        }
    }

    public void onLongPressClickListener(View view, int position)
    {
        vaga = lista.get(position);
        if(vaga.getStatus().equalsIgnoreCase(MainActivity.RESERVADA))
        {
            materialDialog = new MaterialDialog(this);
            materialDialog.setTitle("Aviso..")
                    .setMessage("Deseja cancelar sua reserva?")
                    .setPositiveButton("SIM", new View.OnClickListener()
                    {
                        public void onClick(View v)
                        {
                            vagaLivre();
                            storage.salvarStatusVaga(MainActivity.LIVRE);
                            vaga.setStatus(MainActivity.LIVRE);
                            storage.salvarMotorista("");
                            motorista = storage.buscarMotorista();
                            vaga.setMotorista(motorista);
                            vagasAdapter.notifyDataSetChanged();
                            materialDialog.dismiss();
                        }
                    })
                    .setNegativeButton("NÃO", new View.OnClickListener()
                    {
                        public void onClick(View v) {
                            materialDialog.dismiss();
                        }
                    });
            materialDialog.show();
        }
    }

    private static class RecyclerViewTouchListener implements RecyclerView.OnItemTouchListener
    {
        private Context context;
        private GestureDetector gestureDetector;
        private RecyclerViewOnClickListenerHack recyclerViewOnClickListenerHack;

        public RecyclerViewTouchListener(Context c, final RecyclerView rv, final RecyclerViewOnClickListenerHack hack)
        {
            this.context = c;
            this.recyclerViewOnClickListenerHack = hack;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener()
            {

                public void onLongPress(MotionEvent motionEvent)
                {
                    super.onLongPress(motionEvent);
                    View cv = rv.findChildViewUnder(motionEvent.getX(), motionEvent.getY());

                    if(cv != null && recyclerViewOnClickListenerHack != null)
                    {
                        recyclerViewOnClickListenerHack.onLongPressClickListener(cv, rv.getChildAdapterPosition(cv));
                    }
                }

                public boolean onSingleTapUp(MotionEvent motionEvent)
                {
                    View cv = rv.findChildViewUnder(motionEvent.getX(), motionEvent.getY());

                    boolean callContextMenuStatus = false;
                    if( cv instanceof CardView)
                    {
                        float x = ((RelativeLayout) ((CardView) cv).getChildAt(0)).getChildAt(4).getX();
                        float w = ((RelativeLayout) ((CardView) cv).getChildAt(0)).getChildAt(4).getWidth();
                        float y;// = ((RelativeLayout) ((CardView) cv).getChildAt(0)).getChildAt(3).getY();
                        float h = ((RelativeLayout) ((CardView) cv).getChildAt(0)).getChildAt(4).getHeight();

                        Rect rect = new Rect();
                        ((RelativeLayout) ((CardView) cv).getChildAt(0)).getChildAt(4).getGlobalVisibleRect(rect);
                        y = rect.top;

                        if( motionEvent.getX() >= x && motionEvent.getX() <= w + x && motionEvent.getRawY() >= y && motionEvent.getRawY() <= h + y )
                        {
                            callContextMenuStatus = true;
                        }
                    }

                    if(cv != null && recyclerViewOnClickListenerHack != null && !callContextMenuStatus)
                    {
                        recyclerViewOnClickListenerHack.onClickListener(cv, rv.getChildAdapterPosition(cv));
                    }
                    return true;
                }
            });
        }

        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e)
        {
            gestureDetector.onTouchEvent(e);
            return false;
        }

        public void onTouchEvent(RecyclerView rv, MotionEvent e) { }

        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) { }
    }
}