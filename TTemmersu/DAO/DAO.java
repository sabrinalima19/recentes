package com.example.tasktide.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.tasktide.Objetos.Evento;
import com.example.tasktide.Objetos.Informacoes;
import com.example.tasktide.Objetos.Participantes;

import java.util.ArrayList;
import java.util.List;

public class DAO extends SQLiteOpenHelper {

    private static final String TAG = "DAO";

    private static final String NOME_BANCO = "tasktide_db";
    private static final int VERSAO_BANCO = 5;

    public  static final String TABELA_EVENTO = "evento";
    public  static final String TABELA_INFORMACOES = "informacoes";
    public static final String TABELA_PARTICIPANTES = "participantes";


    public DAO(Context context) {
        super(context, NOME_BANCO, null, VERSAO_BANCO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlEvento = "CREATE TABLE " + TABELA_EVENTO + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nomeEvento TEXT," +
                "tipoEvento TEXT," +
                "horasComplementares TEXT," +
                "modalidade TEXT)";
        db.execSQL(sqlEvento);
        Log.i(TAG, "Tabela evento criada com sucesso. Local: " + db.getPath());

        createInformacoesTable(db);
        createParticipantesTable(db);


    }
    private void createInformacoesTable(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABELA_INFORMACOES + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "id_evento INTEGER," +
                "dataPrevis TEXT," +
                "dataFim TEXT," +
                "horarioInicio TEXT," +
                "horarioTermino TEXT," +
                "prazo TEXT," +
                "local TEXT," +
                "valorEvento double," +
                "Pago TEXT," + // Sim ou Não
                "FOREIGN KEY (id_evento) REFERENCES " + TABELA_EVENTO + "(id))";
        db.execSQL(sql);
        Log.i(TAG, "Tabela informacoes criada com sucesso.");
    }

    private void createParticipantesTable(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABELA_PARTICIPANTES + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "id_evento INTEGER," +
                "quantParticipantes TEXT," +
                "FOREIGN KEY (id_evento) REFERENCES " + TABELA_EVENTO + "(id))";
        db.execSQL(sql);
        Log.i(TAG, "Tabela participantes criada com sucesso.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABELA_PARTICIPANTES);
            db.execSQL("DROP TABLE IF EXISTS " + TABELA_INFORMACOES);
            db.execSQL("DROP TABLE IF EXISTS " + TABELA_EVENTO);
            onCreate(db);
            Log.i(TAG, "Tabelas atualizadas para a nova versão do banco de dados.");
        }
    }

    public long inserirEvento(Evento evento) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nomeEvento", evento.getNomeEvento());
        values.put("tipoEvento", evento.getTipoEvento());
        values.put("horasComplementares", evento.getHorasComplementares());
        values.put("modalidade", evento.getModalidade());

        long id = db.insert(TABELA_EVENTO, null, values);
        db.close();
        if (id != -1) {
            Log.i(TAG, "Evento inserido com sucesso. ID: " + id);
        } else {
            Log.e(TAG, "Erro ao inserir evento.");
        }
        return id;
    }

    public long inserirInformacoes(Informacoes informacoes, long idEvento) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id_evento", idEvento);
        values.put("dataPrevis", informacoes.getDataPrevis());
        values.put("dataFim", informacoes.getDataFim());
        values.put("horarioInicio", informacoes.getHorarioInicio());
        values.put("horarioTermino", informacoes.getHorarioFim());
        values.put("prazo", informacoes.getPrazo());
        values.put("local", informacoes.getLocal());
        values.put("valorEvento", informacoes.getValorEvento());
        values.put("Pago", informacoes.getPago());

        long id = db.insert(TABELA_INFORMACOES, null, values);

        if (id == -1) {
            Log.e(TAG, "Erro ao inserir informações no banco de dados.");
        } else {
            Log.i(TAG, "Informações inseridas com sucesso. ID: " + id);
        }

        db.close();
        return id;
    }


    public long inserirParticipantes(Participantes participantes, long id_evento) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("quantParticipantes", participantes.getQuantParticipantes());
        values.put("id_evento", id_evento);  // Associar ao evento específico

        long id = db.insert(TABELA_PARTICIPANTES, null, values);
        db.close();
        if (id != -1) {
            Log.i(TAG, "Participantes inseridos com sucesso. ID: " + id);
        } else {
            Log.e(TAG, "Erro ao inserir participantes.");
        }
        return id;
    }

    //fases de teste, yupi
    //novo
    public Evento getEventoById(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Evento evento = null;
        Cursor cursor = db.query(TABELA_EVENTO,
                new String[]{"nomeEvento", "tipoEvento", "horasComplementares"},
                "id = ?",
                new String[]{String.valueOf(id)},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            evento = new Evento();
            evento.setNomeEvento(cursor.getString(cursor.getColumnIndexOrThrow("nomeEvento")));
            evento.setTipoEvento(cursor.getString(cursor.getColumnIndexOrThrow("tipoEvento")));
            evento.setHorasComplementares(cursor.getString(cursor.getColumnIndexOrThrow("horasComplementares")));
            cursor.close();
        }
        db.close();
        return evento;
    }

    //novo
    //sem funcionamento(por enquanto)
    public void LimparTabelas() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABELA_PARTICIPANTES, null, null);
        db.delete(TABELA_INFORMACOES, null, null);
        db.delete(TABELA_EVENTO, null, null);
        Log.i(TAG, "Todas as tabelas foram limpas.");
        db.close();
    }

    public List<Evento> getAllEventos() {
        List<Evento> eventos = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABELA_EVENTO, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Evento evento = new Evento();
                evento.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                evento.setNomeEvento(cursor.getString(cursor.getColumnIndexOrThrow("nomeEvento")));
                evento.setTipoEvento(cursor.getString(cursor.getColumnIndexOrThrow("tipoEvento")));
                evento.setHorasComplementares(cursor.getString(cursor.getColumnIndexOrThrow("horasComplementares")));
                evento.setModalidade(cursor.getString(cursor.getColumnIndexOrThrow("modalidade")));
                eventos.add(evento);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return eventos;


    }

    public void deleteEvento(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABELA_EVENTO, "id = ?", new String[]{String.valueOf(id)});
        db.close();
    }


}