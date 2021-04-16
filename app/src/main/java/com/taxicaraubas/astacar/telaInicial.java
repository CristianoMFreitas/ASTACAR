package com.taxicaraubas.astacar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.taxicaraubas.astacar.R;
import com.taxicaraubas.astacar.config.ConfiguracaoFirebase;
import com.google.firebase.auth.FirebaseAuth;

public class telaInicial extends AppCompatActivity {

    private FirebaseAuth autenticacao;

    @Override
    public void onBackPressed() {
        this.moveTaskToBack(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_inicial);


    }

    //menus superior sair
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return super.onCreateOptionsMenu(menu);
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
            case R.id.menuConfiguracoes:
                startActivity(new Intent(this, configuracoes.class));
        }
        return super.onOptionsItemSelected(item);
    }

    public void viajar (View view){

        Intent intent = new Intent(getApplicationContext(), pedirCarro.class );
        startActivity(intent);
        finish();

    }

    public void encomenda (View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Confirme seu endereço!")
                .setMessage("Você gostaria de enviar sua encomenda para:")
                .setPositiveButton("MOSSORÓ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(getApplicationContext(), listaMotorista.class  );
                            intent.putExtra("destinoOp" , "Mossoró");
                            intent.putExtra("tela" , true);
                       startActivity(intent);
                       finish();

                    }
                }).setNegativeButton("CARAÚBAS", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(), listaMotorista.class );
                            intent.putExtra("destinoOp" , "Caraúbas");
                            intent.putExtra("tela" , true);
                        startActivity(intent);
                        finish();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
