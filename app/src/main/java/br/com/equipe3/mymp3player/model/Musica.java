package br.com.equipe3.mymp3player.model;

/**
 * Created by fernando on 14/12/16.
 */

public class Musica {
    private long id;
    private String nomeMusica;
    private String nomeArtista;

    public Musica(long id, String nomeMusica, String nomeArtista){
        this.id = id;
        this.nomeMusica = nomeMusica;
        this.nomeArtista = nomeArtista;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNomeMusica() {
        return nomeMusica;
    }

    public void setNomeMusica(String nomeMusica) {
        this.nomeMusica = nomeMusica;
    }

    public String getNomeArtista() {
        return nomeArtista;
    }

    public void setNomeArtista(String nomeArtista) {
        this.nomeArtista = nomeArtista;
    }

}
