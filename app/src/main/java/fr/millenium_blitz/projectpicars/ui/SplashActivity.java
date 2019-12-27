package fr.millenium_blitz.projectpicars.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import fr.millenium_blitz.projectpicars.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        String preferencesFileName = "mySettings";

        // Get from the SharedPreferences
        SharedPreferences settings = getApplicationContext().getSharedPreferences(preferencesFileName, MODE_PRIVATE);

        String lastUsedAddressSetting = "lastUsedAddress";

        String lastUsedAddress = settings.getString(lastUsedAddressSetting, "");


        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra(lastUsedAddressSetting, lastUsedAddress);
        startActivity(intent);
    }
}
