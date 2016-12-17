package br.com.equipe3.mymp3player.servico;

import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;

import br.com.equipe3.mymp3player.model.Musica;

/**
 * Created by fernando on 15/12/16.
 */

public class MusicService extends Service implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {
    private MediaPlayer mediaPlayer;
    private List<Musica> listaMusica;
    private int posicaoMusica;
    private final IBinder musicaBinder = new MusicaBinder();



    public void onCreate(){
        posicaoMusica = 0;
        mediaPlayer = new MediaPlayer();
        iniciarMusicPlayer();
    }

    public void iniciarMusicPlayer(){
        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return musicaBinder;
    }

    public boolean onUnbind(Intent intent){
        mediaPlayer.stop();
        mediaPlayer.release();
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();

    }

    public void setListaMusica(List<Musica> listaMusica) {
        this.listaMusica = listaMusica;
    }

    public class MusicaBinder extends Binder{
        public MusicService getService(){
            return MusicService.this;
        }
    }

    public  void tocaMusica(){
        mediaPlayer.reset();
        Musica musica = listaMusica.get(posicaoMusica);
        long musicaAtual = musica.getId();
        Uri trackUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                musicaAtual);

        try{
            mediaPlayer.setDataSource(getApplicationContext(), trackUri);
        }catch (Exception e){
            Log.e("SERVIÇO MUSICA", "Erro ao setar dataSource",e);
        }

        mediaPlayer.prepareAsync();
    }

    public void setMusica(int musicaIndice){
        posicaoMusica = musicaIndice;
    }

}
