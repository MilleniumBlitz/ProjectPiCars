package fr.millenium_blitz.projectpicars.ui;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.TypefaceProvider;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.millenium_blitz.projectpicars.R;
import fr.millenium_blitz.projectpicars.view.JoystickView;

import static android.view.View.SYSTEM_UI_FLAG_FULLSCREEN;
import static android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
import static android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

public class JoystickActivity extends Activity {

    @BindView(R.id.webView) WebView webView;
    @BindView(R.id.btnDebug) BootstrapButton btnDebug;
    @BindView(R.id.btnRefresh) BootstrapButton btnRefresh;

    @BindView(R.id.joystickPuissance) JoystickView joystickPowerView;
    @BindView(R.id.joystickDirection) JoystickView joystickDirectionView;

    private RequestQueue queue;
    private String ip;
    private static Toast address;

    private boolean debugMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        TypefaceProvider.registerDefaultIconSets();
        setContentView(R.layout.activity_joystick);

        ButterKnife.bind(this);
        getWindow().getDecorView().setSystemUiVisibility(SYSTEM_UI_FLAG_IMMERSIVE_STICKY | SYSTEM_UI_FLAG_FULLSCREEN | SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        final String htmlWebcam;
        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            String IPADRESS = extras.getString("IP");

            if (!"".equals(IPADRESS))
            {
                ip = "http://" + IPADRESS + "/";

                Point size = new Point();
                this.getWindowManager().getDefaultDisplay().getSize(size);
                int width = size.x;
                int height = size.y;


                webView.setVerticalScrollBarEnabled(false);
                webView.setOverScrollMode(View.OVER_SCROLL_NEVER);
                htmlWebcam = "<body style='margin:0;padding:0;'><img src=\"http:" + IPADRESS + ":8081/?action=stream\"  height='" + height + "' width='" + width + "'></body></html>";
                webView.loadData(htmlWebcam, "text/html", null);

                queue = Volley.newRequestQueue(getApplicationContext());

                btnRefresh.setVisibility(View.GONE);

                btnDebug.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (!debugMode) {
                            debugMode = true;
                            btnRefresh.setVisibility(View.VISIBLE);
                            btnDebug.setText("Webcam");
                            webView.loadUrl(ip);
                            Log.e(ip, "");
                            joystickPowerView.setVisibility(View.GONE);
                            joystickDirectionView.setVisibility(View.GONE);
                        } else {
                            debugMode = false;
                            btnRefresh.setVisibility(View.GONE);
                            btnDebug.setText("Debug");
                            webView.loadData(htmlWebcam, "text/html", null);
                            joystickPowerView.setVisibility(View.VISIBLE);
                            joystickDirectionView.setVisibility(View.VISIBLE);
                        }
                    }
                });

                btnRefresh.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        webView.reload();
                    }
                });

                joystickPowerView.setOnJoystickMoveListener(new JoystickView.OnJoystickMoveListener() {

                    @Override
                    public void onValueChanged(int angle, int power, int direction) {

                        String url;
                        if (direction > 4) {
                            url = ip + "power/-" + power;
                        } else {
                            url = ip + "power/" + power;
                        }


                        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,

                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        if (address != null)
                                            address.cancel();
                                        address = Toast.makeText(getBaseContext(), "Le serveur ne répond pas", Toast.LENGTH_SHORT);
                                        address.show();
                                    }
                                });

                        queue.add(stringRequest);
                    }
                });

                joystickDirectionView.setOnJoystickMoveListener(new JoystickView.OnJoystickMoveListener() {

                    @Override
                    public void onValueChanged(int angle, int power, int direction) {
                        String url;

                        if (angle > 0) {
                            url = ip + "direction/Droite";
                        } else if (angle < 0) {
                            url = ip + "direction/Gauche";
                        } else {
                            url = ip + "direction/Aucune";
                        }

                        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,

                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        if (address != null)
                                            address.cancel();
                                        address = Toast.makeText(getBaseContext(), "Le serveur ne répond pas", Toast.LENGTH_SHORT);
                                        address.show();
                                    }
                                });

                        queue.add(stringRequest);
                    }
                });
            }
        }



    }
}
