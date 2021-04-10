package fr.millenium_blitz.projectpicars.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.View;
import android.widget.EditText;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.beardedhen.androidbootstrap.TypefaceProvider;

import fr.millenium_blitz.projectpicars.R;
import fr.millenium_blitz.projectpicars.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private RequestQueue queue;

    private View parentLayout;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        TypefaceProvider.registerDefaultIconSets();
        setContentView(R.layout.activity_main);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        parentLayout = binding.getRoot();
        setContentView(parentLayout);

        binding.btnConnect.setOnClickListener(v -> connect());
        binding.btnTest.setOnClickListener(v -> testMode());


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Récupère la dernière adresse utilisée
        Intent intent = getIntent();
        String lastUsedAddress = intent.getStringExtra("lastUsedAddress");
        EditText txtIP = binding.editTextIP;
        txtIP.setText(lastUsedAddress);

        queue = Volley.newRequestQueue(getApplicationContext());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    public void connect() {

        EditText txtIP = binding.editTextIP;
        String ipAddress = txtIP.getText().toString();

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

    public void testMode()
    {
        Intent intent = new Intent(getApplicationContext(), JoystickActivity.class);
        intent.putExtra("IP", "");
        startActivity(intent);
    }
}
