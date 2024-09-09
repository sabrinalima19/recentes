package com.example.tasktide.DAO;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class limparTabelas {

    private static final String TAG = "DataCleaner";
    private final DAO dbHelper;

    public limparTabelas(DAO dbHelper) {
        this.dbHelper = dbHelper;
    }

    // Construtor que inicializa dbHelper
    public limparTabelas(Context context) {
        dbHelper = new DAO(context);
    }

    // Método para limpar todos os dados
    public void clearAllData() {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            db.beginTransaction();

            // Deletar dados das tabelas
            db.delete(DAO.TABELA_EVENTO, null, null);
            db.delete(DAO.TABELA_INFORMACOES, null, null);
            db.delete(DAO.TABELA_PARTICIPANTES, null, null);

            // Resetar o contador de auto incremento
            db.execSQL("DELETE FROM sqlite_sequence WHERE name='" + DAO.TABELA_EVENTO + "'");
            db.execSQL("DELETE FROM sqlite_sequence WHERE name='" + DAO.TABELA_INFORMACOES + "'");
            db.execSQL("DELETE FROM sqlite_sequence WHERE name='" + DAO.TABELA_PARTICIPANTES + "'");

            db.setTransactionSuccessful();
            Log.i(TAG, "Todos os dados foram apagados com sucesso.");
        } catch (Exception e) {
            Log.e(TAG, "Erro ao apagar os dados: ", e);
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }
    }
}
