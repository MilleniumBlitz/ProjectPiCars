package fr.millenium_blitz.projectpicars.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.TypefaceProvider;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.millenium_blitz.projectpicars.R;
import fr.millenium_blitz.projectpicars.util.IPAddressValidator;
import fr.millenium_blitz.projectpicars.util.sql.Connexion;
import fr.millenium_blitz.projectpicars.util.sql.ConnexionDAO;

public class MainActivity extends AppCompatActivity {

    private RequestQueue queue;
    private ConnexionDAO datasource;
    private View parentLayout;

    @BindView(R.id.btnGo) FloatingActionButton btnGo;
    @BindView(R.id.btnAdd) Button btnAdd;
    @BindView(R.id.btnDel) BootstrapButton btnDel;

    @BindView(R.id.btnTest) Button btnTest;

    @BindView(R.id.spinnerConnections) Spinner spinner;
    @BindView(R.id.lblConnections) TextView lblConnexions;
    @BindView(R.id.editTextIP) EditText txtIpAjout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        TypefaceProvider.registerDefaultIconSets();
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        parentLayout = findViewById(android.R.id.content);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        List<Connexion> connexions = (List<Connexion>) intent.getSerializableExtra("Connexions");

        //Initialisation
        init();
        loadSpinner(connexions);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    private void init() {

        //Connections'DataSource
        datasource = new ConnexionDAO(this);
        datasource.open();

        queue = Volley.newRequestQueue(getApplicationContext());
    }

    /**
     * Chargement de la liste des connexions
     * Load connection's list
     *
     * @param connectionsList Liste des connexions/Connection list
     */
    private void loadSpinner(List<Connexion> connectionsList) {

        if (connectionsList.size() > 0) {
            ArrayAdapter<Connexion> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, connectionsList);
            spinner.setAdapter(adapter);
            spinner.setVisibility(View.VISIBLE);
            btnGo.setVisibility(View.VISIBLE);
            btnDel.setVisibility(View.VISIBLE);
            lblConnexions.setVisibility(View.GONE);
        } else {
            lblConnexions.setText(R.string.no_connections);
            lblConnexions.setVisibility(View.VISIBLE);
            spinner.setAdapter(null);
            spinner.setVisibility(View.GONE);
            btnGo.setVisibility(View.GONE);
            btnDel.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.btnGo)
    public void connect() {

        final String connectionAddress = spinner.getSelectedItem().toString();
        String url = "http://" + connectionAddress + "/alive";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("alive")) {
                            Intent intent = new Intent(getApplicationContext(), JoystickActivity.class);
                            intent.putExtra("IP", connectionAddress);
                            startActivity(intent);

                        } else {
                            Snackbar.make(parentLayout, "Serveur indisponible, connexion impossible", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Snackbar.make(parentLayout, "Serveur indisponible, connexion impossible", Snackbar.LENGTH_SHORT).show();

                    }
                });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);
    }

    @OnClick(R.id.btnDel)
    public void deleteConnection() {
        if (spinner.getSelectedItem() != null) {
            Connexion uneConnexion = (Connexion) spinner.getSelectedItem();
            datasource.deleteConnexion(uneConnexion);
            loadSpinner(datasource.getAllConnexions());
        }
    }

    @OnClick(R.id.btnAdd)
    public void addConnection() {
        if (IPAddressValidator.validate(txtIpAjout.getText().toString())) {
            datasource.createConnexion(txtIpAjout.getText().toString());
            loadSpinner(datasource.getAllConnexions());
            txtIpAjout.getText().clear();
        } else {
            Snackbar.make(parentLayout, "Veuillez rentrer une adresse valide", Snackbar.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.btnTest)
    public void testMode()
    {
        Intent intent = new Intent(getApplicationContext(), JoystickActivity.class);
        intent.putExtra("IP", "");
        startActivity(intent);
    }




}
