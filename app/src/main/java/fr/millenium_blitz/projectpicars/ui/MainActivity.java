package fr.millenium_blitz.projectpicars.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.beardedhen.androidbootstrap.TypefaceProvider;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.millenium_blitz.projectpicars.R;

public class MainActivity extends AppCompatActivity {

    private RequestQueue queue;

    @BindView(android.R.id.content) View parentLayout;

    @BindView(R.id.btnConnect) Button btnConnect;

    @BindView(R.id.btnTest) Button btnTest;

    @BindView(R.id.editTextIP) EditText txtIpAjout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        TypefaceProvider.registerDefaultIconSets();
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Récupère la dernière adresse utilisée
        Intent intent = getIntent();
        String lastUsedAddress = intent.getStringExtra("lastUsedAddress");
        txtIpAjout.setText(lastUsedAddress);

        queue = Volley.newRequestQueue(getApplicationContext());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @OnClick(R.id.btnConnect)
    public void connect() {

        String ipAddress = txtIpAjout.getText().toString();

        //Si l'adresse rentré est valide, sauvegarde de l'adresse
        SharedPreferences settings = getApplicationContext().getSharedPreferences("mySettings", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("lastUsedAddress", ipAddress);
        editor.apply();

        final String connectionAddress = ipAddress;
        String url = "http://" + connectionAddress;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,

                response -> {
                    if (response.equals("alive")) {
                        Intent intent = new Intent(getApplicationContext(), JoystickActivity.class);
                        intent.putExtra("IP", connectionAddress);
                        startActivity(intent);

                    } else {
                        Snackbar.make(parentLayout, "Serveur indisponible, connexion impossible", Snackbar.LENGTH_SHORT).show();
                    }
                },
                error ->

                    Snackbar.make(parentLayout, "Erreur lors de la connection", Snackbar.LENGTH_SHORT).show()
        );

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);
    }

    @OnClick(R.id.btnTest)
    public void testMode()
    {
        Intent intent = new Intent(getApplicationContext(), JoystickActivity.class);
        intent.putExtra("IP", "");
        startActivity(intent);
    }
}
