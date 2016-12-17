package br.com.equipe3.mymp3player.activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContentResolverCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import br.com.equipe3.mymp3player.R;
import br.com.equipe3.mymp3player.adpter.ListaMusicaAdpter;
import br.com.equipe3.mymp3player.interfaces.OnMusicaClickListener;
import br.com.equipe3.mymp3player.model.Musica;
import br.com.equipe3.mymp3player.servico.MusicService;

public class MainActivity extends AppCompatActivity implements OnMusicaClickListener {
    private RecyclerView recyclerViewMusicas;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123; // colocar em um resource de constant
    private MusicService musicService;
    private List<Musica> listaMusica;
    private Intent playIntent;
    private boolean musicBound = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerViewMusicas = (RecyclerView) findViewById(R.id.recycler);

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            iniciaRecycleMusica();
        }
    }


    private List<Musica> AlimentaListaMusica() {
        List<Musica> listaMusica = new ArrayList<>();
        ContentResolver musicaResolver = getContentResolver();
        Uri musicaUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = musicaResolver.query(musicaUri, null, null, null, null);

        if (cursor !=null && cursor.moveToFirst())
        {
            int ColunaNomeMusica = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int ColunaIdMusica = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int ColunaNomeArtista = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);

            while (cursor.moveToNext()){
                listaMusica.add(new Musica(
                       cursor.getLong(ColunaIdMusica),
                       cursor.getString(ColunaNomeMusica),
                       cursor.getString(ColunaNomeArtista)));

            }

        }

        return listaMusica;
    }

    private void iniciaRecycleMusica(){
        this.listaMusica  = AlimentaListaMusica();
        ListaMusicaAdpter listaMusicaAdpter = new ListaMusicaAdpter(listaMusica);
        recyclerViewMusicas.setAdapter(listaMusicaAdpter);
        recyclerViewMusicas.setLayoutManager(new LinearLayoutManager(this));
        listaMusicaAdpter.setMusicaClickListener(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    iniciaRecycleMusica();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MusicService.MusicaBinder musicaBinder = (MusicService.MusicaBinder)iBinder;
            musicService = musicaBinder.getService();
            musicService.setListaMusica(listaMusica);
            musicBound = true;

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            musicBound = false;

        }
    };

    protected void onStart(){
        super.onStart();
        if (playIntent == null){
            playIntent = new Intent(this , MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }


    @Override
    public void OnMusicaClick(View view, int position) {

        startService(playIntent);
        musicService.setMusica(position);
        musicService.tocaMusica();


    }
}
