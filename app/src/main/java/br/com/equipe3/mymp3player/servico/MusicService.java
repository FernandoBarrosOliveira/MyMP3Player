package br.com.equipe3.mymp3player.servico;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentUris;
import android.content.Context;
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

import br.com.equipe3.mymp3player.R;
import br.com.equipe3.mymp3player.activity.MainActivity;
import br.com.equipe3.mymp3player.controller.MusicaController;
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
    private String tituloMusica = "";
    private static final  int NOTIFY_ID = 1;
    private MusicaController musicaController;

    public void setMusicaController(MusicaController musicaController) {
        this.musicaController = musicaController;
    }

    public void onCreate(){
        posicaoMusica = 0;
        mediaPlayer = new MediaPlayer();
        iniciarMusicPlayer();
        super.onCreate();
    }

    @Override
    public void onDestroy(){
        stopForeground(true);
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
        if (mediaPlayer.getCurrentPosition() >0){
            mediaPlayer.reset();
            playNext();
        }
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        mediaPlayer.release();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
        musicaController.show(0);

        Intent notIntent = new Intent(this, MainActivity.class);
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentIntent(pendingIntent);
        builder.setTicker(tituloMusica);
        builder.setOngoing(true);
        builder.setContentTitle("Tocando");
        builder.setContentText(tituloMusica);


        Notification not = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            not = builder.build();
        }
        startForeground(NOTIFY_ID, not);

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
        tituloMusica=musica.getNomeMusica();
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

    public int getPosn(){
        return mediaPlayer.getCurrentPosition();
    }

    public int getDuracao(){
        return mediaPlayer.getDuration();
    }

    public boolean isPng(){
        return mediaPlayer.isPlaying();
    }

    public void pausePlayer(){
        mediaPlayer.pause();
    }

    public void seek(int posicao){
        mediaPlayer.seekTo(posicao);
    }

    public void go (){
        mediaPlayer.start();
    }

    public void playPrev(){
        posicaoMusica --;
        if(posicaoMusica < 0){
            posicaoMusica = listaMusica.size()-1;
        }
        tocaMusica();

    }

    public void playNext(){
        posicaoMusica++;
        if (posicaoMusica >= listaMusica.size()){
            posicaoMusica = 0;
        }
        tocaMusica();
    }

}
