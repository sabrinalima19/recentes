package com.example.tasktide;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tasktide.DAO.DAO;
import com.example.tasktide.Objetos.Evento;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class VisaoGeral extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PERMISSION_REQUEST_CODE = 2;
    private ImageView imgBanner;
    private TextView txtMostraNomeDoEvento;
    private TextView txtMostraLocalDoEvento;
    private TextView txtMostraDataDoEvento;
    private TextView txtMostraHoraDeInicioEvento;
    private TextView txtMostraHoraDeTerminoEvento;
    private TextView txtMostraTipoDoEvento;
    private TextView txtMostraHorasComplementaresEvento;
    private DAO dao;
    private long idEvento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visao_geral);

        imgBanner = findViewById(R.id.imgBanner);
        ImageView btnMudarBanner = findViewById(R.id.imgbtnMudarBanner);
        ImageButton imgbtnCriarCronograma = findViewById(R.id.imgbtnCriarCronograma);

        txtMostraLocalDoEvento = findViewById(R.id.txtMostraLocalDoEvento);
        txtMostraNomeDoEvento = findViewById(R.id.txtMostraNomeDoEvento);
        txtMostraDataDoEvento = findViewById(R.id.txtMostraDataDoEvento);
        txtMostraHoraDeInicioEvento = findViewById(R.id.txtMostraHoraDeInicioEvento);
        txtMostraHoraDeTerminoEvento = findViewById(R.id.txtMostraHoraDeTerminoEvento);
        txtMostraTipoDoEvento = findViewById(R.id.txtMostraTipoDoEvento);
        txtMostraHorasComplementaresEvento = findViewById(R.id.txtMostraHorasComplementaresEvento);

        SharedPreferences prefs = getSharedPreferences("EventPrefs", MODE_PRIVATE);
        idEvento = prefs.getLong("EVENTO_ID", -1);

        if (idEvento != -1) {
            // Inicialize o DAO
            dao = new DAO(this);

            // Preencher os campos com os dados do evento
            preencherCamposComDadosDoEvento(idEvento);

            // Limpar o ID do evento após o uso
            limparIdEvento();
        } else {
            // Caso o ID não seja válido, exiba uma mensagem de erro ou tome outra ação apropriada
            Toast.makeText(this, "Evento não encontrado", Toast.LENGTH_SHORT).show();
        }


        // Solicitar permissões se necessário
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }

        //btnMudarBanner.setOnClickListener(v -> openImageChooser());
        btnMudarBanner.setOnClickListener(v -> showImageSizeWarningDialog());

        txtMostraNomeDoEvento.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // Verifica se o botão Enter foi pressionado
                if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    // Desabilita o EditText
                    txtMostraNomeDoEvento.setEnabled(false);
                    // Esconde o teclado
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(txtMostraNomeDoEvento.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        ImageButton btnPopupMenu = findViewById(R.id.btnPopupMenu);

        btnPopupMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(VisaoGeral.this, v);
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int itemId = item.getItemId();
                        if (itemId == R.id.gerarRelatorio) {
                            mostrarDialogoConfirmacao();
                            return true;
                        } else if (itemId == R.id.SeInscrever) {
                            seInscrever();
                            return true;
                        } else if (itemId == R.id.voltar) {
                            // Lógica para voltar para a tela MeusEventosCriador
                            Intent intent = new Intent(VisaoGeral.this, MeusEventosCriador.class);
                            startActivity(intent);
                            return true;
                        }
                        return false;
                    }
                });

                popup.show();
            }
        });

        imgbtnCriarCronograma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Criar o diálogo
                AlertDialog.Builder builder = new AlertDialog.Builder(VisaoGeral.this);
                builder.setTitle("Criar Cronograma");
                builder.setMessage("Deseja realmente criar um cronograma?");

                builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(VisaoGeral.this, "Cronograma criado com sucesso!", Toast.LENGTH_SHORT).show();
                    }
                });

                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });


    }

    private void limparIdEvento() {
        SharedPreferences prefs = getSharedPreferences("EventPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("EVENTO_ID");
        editor.apply();
    }

    private void mostrarDialogoConfirmacao() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmação");
        builder.setMessage("Você realmente deseja gerar o relatório?");

        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                gerarRelatorio();
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void gerarRelatorio() {
        // Criação de um documento PDF
        PdfDocument document = new PdfDocument();

        // Definindo o tamanho da página
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();

        // Iniciando uma nova página
        PdfDocument.Page page = document.startPage(pageInfo);

        // Escrevendo no canvas da página
        Paint paint = new Paint();
        paint.setTextSize(16);
        page.getCanvas().drawText("Relatório de Exemplo", 80, 50, paint);

        // Finalizando a página
        document.finishPage(page);

        // Salvando o arquivo PDF no armazenamento
        File directory = getExternalFilesDir(null);
        if (directory != null && !directory.exists()) {
            boolean directoryCreated = directory.mkdirs();
            if (!directoryCreated) {
                Toast.makeText(this, "Erro ao criar diretório", Toast.LENGTH_SHORT).show();
                document.close();
                return;
            }
        }

        String fileName = "relatorio_" + System.currentTimeMillis() + ".pdf";
        File file = new File(directory, fileName);

        try (FileOutputStream fos = new FileOutputStream(file)) {
            document.writeTo(fos);
            Toast.makeText(this, "Relatório salvo em " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Log.e("VisaoGeral", "Erro ao salvar PDF: " + e.getMessage());
            Toast.makeText(this, "Erro ao gerar relatório", Toast.LENGTH_SHORT).show();
        } finally {
            document.close();
        }
    }
    private void seInscrever() {
        Toast.makeText(this, "Inscrição realizada com sucesso!", Toast.LENGTH_SHORT).show();
    }

    private void preencherCamposComDadosDoEvento(long idEvento) {
        // Recuperar informações do evento
        Evento evento = dao.getEventoById(idEvento);
        if (evento != null) {
            txtMostraNomeDoEvento.setText(evento.getNomeEvento());
            txtMostraTipoDoEvento.setText(evento.getTipoEvento());
            txtMostraHorasComplementaresEvento.setText(evento.getHorasComplementares());
        }

        // Recuperar informações adicionais
        Cursor cursorInformacoes = dao.getReadableDatabase().query(DAO.TABELA_INFORMACOES,
                new String[]{"dataPrevis", "horarioInicio", "horarioTermino", "local"},
                "id_evento = ?",
                new String[]{String.valueOf(idEvento)},
                null, null, null);

        if (cursorInformacoes != null && cursorInformacoes.moveToFirst()) {
            txtMostraDataDoEvento.setText(cursorInformacoes.getString(cursorInformacoes.getColumnIndexOrThrow("dataPrevis")));
            txtMostraHoraDeInicioEvento.setText(cursorInformacoes.getString(cursorInformacoes.getColumnIndexOrThrow("horarioInicio")));
            txtMostraHoraDeTerminoEvento.setText(cursorInformacoes.getString(cursorInformacoes.getColumnIndexOrThrow("horarioTermino")));
            txtMostraLocalDoEvento.setText(cursorInformacoes.getString(cursorInformacoes.getColumnIndexOrThrow("local")));
            cursorInformacoes.close();
        }
    }

    private void showImageSizeWarningDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Aviso de Imagem")
                .setMessage("A imagem deve ter preferencialmente 310 x 160 pixels. Deseja continuar?")
                .setPositiveButton("Continuar", (dialog, which) -> openImageChooser())
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                .show();
    }


    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imgBanner.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imgBanner.setImageBitmap(bitmap);
                saveSelectedImageUri(imageUri);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("VisaoGeral", "Erro ao carregar a imagem: " + e.getMessage());
            }
        }
    }

    private void saveSelectedImageUri(Uri imageUri) {
        getSharedPreferences("MyPrefs", MODE_PRIVATE)
                .edit()
                .putString("selected_image_uri", imageUri.toString())
                .apply();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permissão concedida
            } else {
                // Permissão negada
                Log.e("VisaoGeral", "Permissão para acessar armazenamento negada.");
            }
        }
    }

    public void telaAcessarCriarEvento(View view) {
        Intent in = new Intent(VisaoGeral.this, CriarEvento.class);
        startActivity(in);
    }

    //nao oficial
    public void VoltarAlternativo(View view) {
        Intent in = new Intent(VisaoGeral.this, MeusEventosCriador.class);
        startActivity(in);
    }

}