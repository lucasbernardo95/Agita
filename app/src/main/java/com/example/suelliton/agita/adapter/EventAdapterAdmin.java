package com.example.suelliton.agita.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.suelliton.agita.R;
import com.example.suelliton.agita.activity.EventoActivity;
import com.example.suelliton.agita.model.Evento;
import com.example.suelliton.agita.utils.GPSTracker;
import com.example.suelliton.agita.utils.MyDialog;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.example.suelliton.agita.activity.AdminActivity.adapterAdmin;
import static com.example.suelliton.agita.activity.AdminActivity.listaEventos;
import static com.example.suelliton.agita.activity.EventoActivity.temporarioReference;
import static com.example.suelliton.agita.activity.SplashActivity.eventosReference;

public class EventAdapterAdmin extends RecyclerView.Adapter{

    private List<Evento> eventos; //eventos do banco
    private Context context;

    public EventAdapterAdmin(List<Evento> eventos, Context context) {
        this.eventos = eventos;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.admin_content_evento, parent, false);//filtro_eventos que tem os view a serem inflados

        AdminViewHolder holder = new AdminViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final AdminViewHolder myHolder = (AdminViewHolder) holder;
        final Evento escolhido = eventos.get(position);

        myHolder.nome.setText(escolhido.getNome());
        myHolder.local.setText(escolhido.getLocal());
        myHolder.estilo.setText(escolhido.getEstilo());
        myHolder.dono.setText(escolhido.getDono());

        //Comentei esse if pois ainda continuou dando erro quando não consegue carregar a imagem
        //if(escolhido.getUrlBanner() != null || !escolhido.getUrlBanner().equals("")) {
        try {
            Picasso.get().load(escolhido.getUrlBanner()).into(myHolder.imagem);
        } catch (RuntimeException erro) {
            erro.printStackTrace();
            Toast.makeText(context, "Erro ao tentar carregar a imagem do evento.",Toast.LENGTH_LONG).show();
        }
        //}
        //implementa o zoom na imagemDetalhe
        myHolder.imagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyDialog dialogo = new MyDialog(context);
                dialogo.criaDialogo(escolhido.getKey());
            }
        });

        //Botão para excluir o evento
        myHolder.exclui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertEventoDelet(context, escolhido, escolhido.getKey(), position);

            }
        });

        //ação do botão de aprovar o evento, pega o evento da tabela temporária, salva na tabela de eventos e apaga o temporário
        myHolder.aprova.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aletEventVerify(context, escolhido, escolhido.getKey(), position);
            }
        });

        //ação do botão info

        myHolder.bt_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context)
                        .setTitle("Informações do evento!")
                        .setMessage(
                                "Nome: " + escolhido.getNome() + "\n"
                                        +"Data: "+ escolhido.getData() + "\n"
                                        +"Hora: "+ escolhido.getHora() + "\n"
                                        +"Local: "+ escolhido.getLocal() + "\n"
                                        +"Valor: "+ escolhido.getValor() + "\n"
                                        +"Estilo: "+ escolhido.getEstilo() + "\n"
                                        +"Organização: "+ escolhido.getCasashow() + "\n"
                                        +"Bandas: "+ escolhido.getBandas() + "\n"
                                        +"Descrição: "+ escolhido.getDescricao() + "\n"

                        )
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).setIcon(R.drawable.ic_info)
                        .show();
            }
        });
        //fin do botão info

        //Ação do botão mapa
        myHolder.bt_mapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Location mlocation;
                GPSTracker gpsTracker;

                gpsTracker = new GPSTracker(context.getApplicationContext()); //intancia a classe do GPS para pegar minha localização
                mlocation = gpsTracker.getLocation();
                LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

                boolean isOnGps = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);

                if (!isOnGps) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    Toast.makeText(context, "Ative o GPS do dispositivo", Toast.LENGTH_SHORT).show();
                    context.startActivity(intent);
                }else{
                    try {
                        String uri = "http://maps.google.com/maps?saddr=" + mlocation.getLatitude() + "," + mlocation.getLongitude() + "&daddr=" + escolhido.getLatitude() + "," + escolhido.getLongitude();
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
                        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                        context.startActivity(intent);
                    } catch (RuntimeException erro) {
                        Toast.makeText(context, "Erro ao tentar encontrar o local do evento.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        //fim do botão mapa

    }

    @Override
    public int getItemCount() {
        return eventos == null ? 0 :  eventos.size();
    }

    public class AdminViewHolder extends RecyclerView.ViewHolder {

        public TextView nome, local, estilo, dono;
        public ImageView imagem;
        public ImageButton aprova, exclui, bt_info, bt_mapa;

        public AdminViewHolder(View itemView) {
            super(itemView);
            nome = (TextView) itemView.findViewById(R.id.nomeEventoAdmin);
            local = (TextView) itemView.findViewById(R.id.localEventoAdmin);
            estilo = (TextView) itemView.findViewById(R.id.estiloEventoAdmin);
            dono = (TextView) itemView.findViewById(R.id.donoEventoAdmin);
            imagem = (ImageView) itemView.findViewById(R.id.imageEventoAdmin);

            //permite dar um zoom na foto
//            PhotoViewAttacher photoZoom = new PhotoViewAttacher(imagemDetalhe);
//            photoZoom.update();

            aprova = (ImageButton) itemView.findViewById(R.id.botaoAprova);
            exclui = (ImageButton) itemView.findViewById(R.id.botaoExcluir);
            bt_info = (ImageButton) itemView.findViewById(R.id.botaoInfoAdmin);
            bt_mapa = (ImageButton) itemView.findViewById(R.id.botaoMapAdmin);

        }

    }

    private void alertEventoDelet(Context context, final Evento evento, final String key, final int position) {
        new AlertDialog.Builder(context)
                .setTitle("Deletando: "+evento.getNome())
                .setMessage("Tem certeza que deseja deletar esse evento?")
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        temporarioReference.child(key).removeValue();
                        deleteBannerEvent(key); //deleta também o banner
                        //remove o item da lista de eventos e atualiza o adapter
                        listaEventos.remove(evento);
                        adapterAdmin.notifyItemRemoved(position);
                    }
                })
                .setNegativeButton("Não", null)
                .show();
    }

    private void deleteBannerEvent(String key){
        StorageReference storage = FirebaseStorage.getInstance().getReference("eventos").child(key);
        storage.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context, "Exluído com sucesso!", Toast.LENGTH_SHORT).show();
            }
        });
    } //

    private void aletEventVerify(Context context, final Evento model, final  String key, final  int position) {
        new AlertDialog.Builder(context)
                .setTitle("Confirmação de validação")
                .setMessage("Tem certeza que deseja prosseguir?")
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        //seta como verificado
                        model.setVerificado(true);
                        //Salva o evento em outra tabela
                        eventosReference.child(model.getKey()).setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Query query = eventosReference.orderByChild("nome").startAt(model.getNome()).endAt(model.getNome()).limitToFirst(1);
                                query.addChildEventListener(new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                        //eventosReference.child(dataSnapshot.getRef().getKey()).child("key").setValue(dataSnapshot.getRef().getKey());
                                    }

                                    @Override
                                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                    }

                                    @Override
                                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                                    }

                                    @Override
                                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        });

                        //apaga o evento antigo
                        temporarioReference.child(key).removeValue();

                        listaEventos.remove(model);
                        adapterAdmin.notifyItemRemoved(position);

                    }

                })
                .setNegativeButton("Não", null)
                .show();
    }
}
