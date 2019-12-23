package fr.millenium_blitz.projectpicars.ui;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;
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

    @BindView(R.id.webView)
    WebView webView;
    @BindView(R.id.btnDebug)
    BootstrapButton btnDebug;

    @BindView(R.id.joystickPuissance)
    JoystickView joystickPowerView;
    @BindView(R.id.joystickDirection)
    JoystickView joystickDirectionView;

    @BindView(R.id.txtBattery)
    TextView txtBattery;

    private RequestQueue queue;
    private String ipAddress;
    private static Toast toast;

    private boolean debugMode = false;
    private CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Init
        super.onCreate(savedInstanceState);
        TypefaceProvider.registerDefaultIconSets();
        setContentView(R.layout.activity_joystick);
        ButterKnife.bind(this);
        getWindow().getDecorView().setSystemUiVisibility(SYSTEM_UI_FLAG_IMMERSIVE_STICKY | SYSTEM_UI_FLAG_FULLSCREEN | SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        //Toggle debug mode
        btnDebug.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                debugMode = !debugMode;
                if (debugMode) {
                    txtBattery.setVisibility(View.VISIBLE);
                } else {
                    txtBattery.setVisibility(View.INVISIBLE);
                }
            }
        });

        //Get select ipAddress toast
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            ipAddress = extras.getString("IP");

            if (!"".equals(ipAddress)) {

                webviewInit();

                timer = new CountDownTimer(360000, 2000) {

                    @Override
                    public void onTick(long millisUntilFinished) {
                        getBatteryVoltage();
                    }

                    @Override
                    public void onFinish() {
                        timer.start();

                    }
                }.start();


                queue = Volley.newRequestQueue(getApplicationContext());

                joystickPowerView.setOnJoystickMoveListener(new JoystickView.OnJoystickMoveListener() {

                    @Override
                    public void onValueChanged(int angle, int power, int direction) {

                        String request = "http://" + ipAddress + "/";
                        if (direction > 4) {
                            request += "power/-" + power;
                        } else {
                            request += "power/" + power;
                        }

                        StringRequest stringRequest = new StringRequest(Request.Method.GET, request, null,
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        if (toast != null)
                                            toast.cancel();
                                        toast = Toast.makeText(getBaseContext(), error.getLocalizedMessage(), Toast.LENGTH_SHORT);
                                        toast.show();
                                    }
                                });

                        queue.add(stringRequest);
                    }
                });

                joystickDirectionView.setOnJoystickMoveListener(new JoystickView.OnJoystickMoveListener() {

                    @Override
                    public void onValueChanged(int angle, int power, int direction) {

                        String request = "http://" + ipAddress + "/";

                        if (angle > 0) {
                            request += "direction/Droite";
                        } else if (angle < 0) {
                            request += "direction/Gauche";
                        } else {
                            request += "direction/Aucune";
                        }

                        StringRequest stringRequest = new StringRequest(Request.Method.GET, request, null,
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        if (toast != null)
                                            toast.cancel();
                                        toast = Toast.makeText(getBaseContext(), "Le serveur ne répond pas", Toast.LENGTH_SHORT);
                                        toast.show();
                                    }
                                });

                        queue.add(stringRequest);
                    }
                });
            }
        }


    }

    private void webviewInit() {

        Point size = new Point();
        this.getWindowManager().getDefaultDisplay().getSize(size);
        int width = size.x;
        int height = size.y;

        webView.setVerticalScrollBarEnabled(false);
        webView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        String htmlWebcam = "<body style='margin:0;padding:0;'><img src=\"http:" + ipAddress + ":8081/?action=stream\"  height='" + height + "' width='" + width + "'></body></html>";
        webView.loadData(htmlWebcam, "text/html", null);
    }

    private void getBatteryVoltage() {

        if (debugMode) {
            String request = "http://" + ipAddress + "/";
            request += "batteryVoltage";

            StringRequest stringRequest = new StringRequest(Request.Method.GET, request,

                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            txtBattery.setText(response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if (toast != null)
                                toast.cancel();
                            toast = Toast.makeText(getBaseContext(), "Impossible de lire le voltage", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    });

            queue.add(stringRequest);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        timer.cancel();
    }
}
