package fr.millenium_blitz.projectpicars.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.io.Serializable;
import java.util.List;

import fr.millenium_blitz.projectpicars.R;
import fr.millenium_blitz.projectpicars.util.sql.Connexion;
import fr.millenium_blitz.projectpicars.util.sql.ConnexionDAO;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        LoadConnections myTask = new LoadConnections();
        myTask.execute();
    }

    protected class LoadConnections extends AsyncTask<Void, Void, List<Connexion>>
    {
        @Override
        protected List<Connexion> doInBackground(Void... params) {
            ConnexionDAO datasource = new ConnexionDAO(getApplicationContext());
            datasource.open();
            List<Connexion> connexions = datasource.getAllConnexions();
            datasource.close();
            return connexions;
        }

        @Override
        protected void onPostExecute(List<Connexion> connexions) {
            super.onPostExecute(connexions);
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("Connexions", (Serializable) connexions);
            startActivity(intent);
            finish();
        }
    }
}
