package com.example.tasktide;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tasktide.DAO.DAO;
import com.example.tasktide.DAO.limparTabelas;
import com.example.tasktide.Objetos.Evento;

import java.util.List;

public class MeusEventosCriador extends AppCompatActivity {

    private DAO dao;
    private LinearLayout eventosContainer;
    private ImageButton imgBtnDeletar;
    private ImageView imgBannerEvento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_meus_eventos_criador);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Button btnParticipante = findViewById(R.id.btnParticipante);
        eventosContainer = findViewById(R.id.eventosContainer); // Container onde os eventos serão adicionados

        dao = new DAO(this);
        loadEventos();

        //limparTabelas dataCleaner = new limparTabelas(this);
        //dataCleaner.clearAllData();

        ViewGroup container = findViewById(R.id.eventosContainer);

        // Encontre o ImageView imgBannerEvento dentro do layout inflado mostrar_evento
        imgBannerEvento = container.findViewById(R.id.imgBannerEvento);
    }

    private void loadEventos() {
        List<Evento> eventos = dao.getAllEventos();  // Recupera a lista de eventos do banco de dados
        LayoutInflater inflater = LayoutInflater.from(this);

        for (Evento evento : eventos) {
            // Infla a view para o evento
            View eventoView = inflater.inflate(R.layout.mostrar_evento, eventosContainer, false);

            // Encontra os componentes da view
            ImageView imgBannerEvento = eventoView.findViewById(R.id.imgBannerEvento);
            ImageButton imgBtnDeletar = eventoView.findViewById(R.id.imgBtnDeletar);
            ImageButton imgbuttonVisaoGeral = eventoView.findViewById(R.id.imgbuttonVisaoGeral);
            TextView nomeEvento = eventoView.findViewById(R.id.nomeEvento);

            // Preenche os dados do evento
            nomeEvento.setText(evento.getNomeEvento());

            // Adiciona a view do evento ao container
            eventosContainer.addView(eventoView);

            imgBtnDeletar.setOnClickListener(v -> {
                // Lógica para deletar o evento
                showDeleteConfirmationDialog(evento.getId(), eventoView);
            });

            // Em MeusEventosCriador, ao clicar no botão de visão geral
            imgbuttonVisaoGeral.setOnClickListener(v -> {
                SharedPreferences prefs = getSharedPreferences("EventPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putLong("EVENTO_ID", evento.getId()); // Armazena o ID do evento
                editor.apply();

                Intent intent = new Intent(MeusEventosCriador.this, VisaoGeral.class);
                startActivity(intent);
            });

        }
    }

    private void showDeleteConfirmationDialog(long eventoId, View eventoView) {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar Exclusão")
                .setMessage("Você realmente deseja excluir este evento?")
                .setPositiveButton("Sim", (dialog, which) -> {
                    deleteEvento(eventoId, eventoView);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void deleteEvento(long eventoId, View eventoView) {
        dao.deleteEvento(eventoId);
        eventosContainer.removeView(eventoView);
        Toast.makeText(MeusEventosCriador.this, "Evento deletado com sucesso.", Toast.LENGTH_SHORT).show();
    }

    public void IrCriarEvento(View view) {
        Intent in = new Intent(MeusEventosCriador.this, CriarEvento.class);
        startActivity(in);
    }

    public void IrTelaParticipante(View view) {
        Intent in = new Intent(MeusEventosCriador.this, MeusEventosParticipante.class);
        startActivity(in);
    }

}
