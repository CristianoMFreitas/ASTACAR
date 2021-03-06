package com.taxicaraubas.astacar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.taxicaraubas.astacar.R;
import com.taxicaraubas.astacar.aplicacoes.SelecionarHoraMinuto;
import com.taxicaraubas.astacar.config.ConfiguracaoFirebase;
import com.taxicaraubas.astacar.model.UsuarioMotorista;
import com.google.firebase.auth.FirebaseAuth;

public class motorista extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

    private SeekBar sbPassageiro;
    private String  quatPassageiro = "";
    private TextView textPassageiro;
    private FirebaseAuth autenticacao;
    private EditText hora;
    private RadioButton mossoro, caraubas;
    private UsuarioMotorista motorista = new UsuarioMotorista();
    private String destino = "";
    public static boolean administrador;

    @Override
    public void onBackPressed() {
        this.moveTaskToBack(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motorista);
        setTitle("Iniciar viajem");

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        sbPassageiro = findViewById(R.id.sbPassageiro);
        textPassageiro = findViewById(R.id.textPassageiro);
        hora = findViewById(R.id.editTextHoraSaidaMotorista);
        mossoro = findViewById(R.id.destinoMossoro);
        caraubas = findViewById(R.id.destinoCaraubas);

        motorista.motoristaOffLine();

        hora.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                DialogFragment pegarHoraMin = new SelecionarHoraMinuto();
                pegarHoraMin.show(getSupportFragmentManager(), "HORARIO DE SA??DA");

            }
        });

        hora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment pegarHoraMin = new SelecionarHoraMinuto();
                pegarHoraMin.show(getSupportFragmentManager(), "HORARIO DE SA??DA");
            }
        });

        sbPassageiro.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textPassageiro.setText("VAGAS: " + sbPassageiro.getProgress());
                quatPassageiro = Integer.toString(sbPassageiro.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if (minute < 10) {
            hora.setText(hourOfDay + ":0" + minute);
        }else {
            hora.setText(hourOfDay + ":" + minute);
        }
    }

    //menus superior sair
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        Log.i("administrador" , String.valueOf(administrador));
        if (administrador){
            getMenuInflater().inflate(R.menu.menu_admin, menu);
            return super.onCreateOptionsMenu(menu);
        }else {
            getMenuInflater().inflate(R.menu.menu_principal, menu);
            return super.onCreateOptionsMenu(menu);
        }

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuSair:
                autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
                autenticacao.signOut();
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;
            case R.id.menuAdmin:
                startActivity(new Intent(this, gerenciarMotorista.class));

                break;
            case R.id.menuConfiguracoes:
                startActivity(new Intent(this, configuracoesMotorista.class));
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void botaoFicarDisponivel (View view){
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        if (mossoro.isChecked()){
            destino = "Mossor??";

        } else if (caraubas.isChecked()){
            destino = "Cara??bas";

        }


        motorista.setIdMotorista(autenticacao.getUid());

        if (!destino.equals("")){
                if (!quatPassageiro.equals("")){
                    int verificaQuant = Integer.parseInt(quatPassageiro);
                    if (!(verificaQuant == 0)){
                        StringBuilder menssagem = new StringBuilder();
                        menssagem.append("Estou indo para " + destino);

                        if (!hora.getText().toString().isEmpty()){
                            menssagem.append("\nSaindo ??s : "+ hora.getText().toString());
                        }else {
                            menssagem.append("\nSaindo ??s : n??o informado.");
                        }

                        menssagem.append("\n ");
                        menssagem.append("\nVoc?? tem " + quatPassageiro + " VAGAS no seu carro?");

                        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                                .setTitle("CONFIRMAR INFORMA????ES")
                                .setMessage(menssagem)
                                .setPositiveButton("SIM", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        int numPassageiros = Integer.parseInt(quatPassageiro);

                                        if (!hora.getText().toString().isEmpty()){
                                            motorista.setHoraSaida(hora.getText().toString());
                                        }else {
                                            motorista.setHoraSaida("N??o informado");
                                        }

                                        motorista.setStatus(UsuarioMotorista.STATUS_DISPONIVEL);
                                        motorista.setDestino(destino);
                                        motorista.setVagas(numPassageiros);
                                        motorista.setStatusEncomenda(UsuarioMotorista.STATUS_DISPONIVEL);

                                        motorista.alterarStatus();

                                        Intent intent = new Intent(getApplicationContext(), lotacaoCarro.class);
                                        intent.putExtra("enviarDestino", destino);
                                        intent.putExtra("quantidadePassageiro", numPassageiros);
                                        intent.putExtra("hora", hora.getText().toString());
                                        startActivity(intent);
                                        finish();


                                    }
                                }).setNegativeButton("N??O", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }else {
                        Toast.makeText(getApplicationContext(),
                                "Voc?? precisa ter pelo menos UMA vaga \npara ficar dispon??vel!" ,
                                Toast.LENGTH_LONG).show();
                    }

                }else {
                    Toast.makeText(getApplicationContext(),
                            "voc?? precisa informar \nquantos passageiros voc?? j?? tem." ,
                            Toast.LENGTH_LONG).show();
                }

            }else {
                Toast.makeText(getApplicationContext(),
                        "voc?? precisa selecionar o DESTINO." ,
                        Toast.LENGTH_LONG).show();
            }

    }

}

