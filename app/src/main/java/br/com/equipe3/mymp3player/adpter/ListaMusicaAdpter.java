package br.com.equipe3.mymp3player.adpter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import br.com.equipe3.mymp3player.R;
import br.com.equipe3.mymp3player.interfaces.OnMusicaClickListener;
import br.com.equipe3.mymp3player.model.Musica;

import static android.widget.Toast.makeText;

/**
 * Created by fernando on 14/12/16.
 */

public class ListaMusicaAdpter extends RecyclerView.Adapter<ListaMusicaAdpter.ViewHolder> {
    private List<Musica> listaMusica ;
    private OnMusicaClickListener musicaClickListener;
    private int posicaoCorrente;

    public ListaMusicaAdpter(List listaMusica){
        this.listaMusica = listaMusica;
    }

    public int getPosicaoCorrente() {
        return posicaoCorrente;
    }

    public void setPosicaoCorrente(int posicaoCorrente) {
        this.posicaoCorrente = posicaoCorrente;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_musica, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Musica musica = listaMusica.get(position);
        holder.txtMusica.setText(musica.getNomeMusica());
        holder.txtArtista.setText(musica.getNomeArtista());

        if (position != posicaoCorrente){
            holder.txtMusica.setTextColor(Color.BLACK);
            holder.txtArtista.setTextColor(Color.BLACK);

        }else{
            holder.txtMusica.setTextColor(Color.RED);
            holder.txtArtista.setTextColor(Color.RED);
        }


    }

    @Override
    public int getItemCount() {
        return listaMusica.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView txtMusica, txtArtista;
        private SparseBooleanArray selectedItems = new SparseBooleanArray();


        public ViewHolder(View itemView) {
            super(itemView);
            txtMusica = (TextView) itemView.findViewById(R.id.item_musica_txt_titulo);
            txtArtista = (TextView) itemView.findViewById(R.id.item_musica_txt_artista);
            itemView.setTag(itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (musicaClickListener != null)
                musicaClickListener.OnMusicaClick(view, getAdapterPosition());
            setPosicaoCorrente(getAdapterPosition());
            notifyDataSetChanged();
        }

        public void SelectItem(View view){

            if (posicaoCorrente != getAdapterPosition()){
                view.setSelected(false);
            }else{
                view.setSelected(false);
            }

        }
    }


    public void setMusicaClickListener(OnMusicaClickListener musicaClickListener) {
        this.musicaClickListener = musicaClickListener;
    }
}
