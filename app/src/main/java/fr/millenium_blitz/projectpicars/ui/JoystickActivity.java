package fr.millenium_blitz.projectpicars.ui;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.webkit.WebView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.beardedhen.androidbootstrap.TypefaceProvider;

import java.util.concurrent.atomic.AtomicReference;

import fr.millenium_blitz.projectpicars.R;
import fr.millenium_blitz.projectpicars.databinding.ActivityJoystickBinding;

import static android.view.View.SYSTEM_UI_FLAG_FULLSCREEN;
import static android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
import static android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

public class JoystickActivity extends Activity {

    private static final String failsafe_request_url = "failsafe";
    private static final String batteryVoltage_request_url = "batteryVoltage";
    private static final String power_request_url = "power";
    private static final String direction_droite_request_url = "direction/Droite";
    private static final String direction_gauche_request_url = "direction/Gauche";
    private static final String direction_aucune_request_url = "direction/Aucune";

    private ActivityJoystickBinding binding;

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

        binding = ActivityJoystickBinding.inflate(getLayoutInflater());
        View parentLayout = binding.getRoot();
        setContentView(parentLayout);

        WindowInsetsController controller = getWindow().getInsetsController();
        if(controller != null) {
            controller.hide(WindowInsets.Type.navigationBars());
            controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        }

        debugSwitchInit();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            ipAddress = extras.getString("IP");

            if (!"".equals(ipAddress)) {

                queue = Volley.newRequestQueue(getApplicationContext());

                timerInit();
                initPowerJoystick();
                initSteeringJoystick();

                if (binding.webView.getVisibility() == View.VISIBLE)
                    webviewInit();

            }
        }
    }

    /**
     * Initialization of the debug button
     */
    private void debugSwitchInit() {

        binding.btnDebug.setOnClickListener(view -> {
            debugMode = !debugMode;

            // Switch visibility on/off for debug elements
            int visibility = debugMode ? View.VISIBLE : View.INVISIBLE;

            binding.txtBattery.setVisibility(visibility);
            binding.txtPowerJoyAngleDebug.setVisibility(visibility);
            binding.txtPowerJoyPowerDebug.setVisibility(visibility);
            binding.txtPowerJoyDirectionDebug.setVisibility(visibility);
            binding.txtDirectionJoyAngleDebug.setVisibility(visibility);
            binding.txtDirectionJoyPowerDebug.setVisibility(visibility);
            binding.txtDirectionJoyDirectionDebug.setVisibility(visibility);
        });
    }


    /**
     * Initialization of the power joystick (left side)
     */
    private void initPowerJoystick() {

        // When the left joystick is used (Power of the car)
        binding.joystickPuissance.setOnJoystickMoveListener((angle, power, direction) -> {

            String request;
            if (direction > 4) {
                request = power_request_url + "/-" + power;
            } else {
                request = power_request_url + "/" + power;
            }

            sendRequest(request);

            // DEBUG INFO
            binding.txtPowerJoyAngleDebug.setText(String.valueOf(angle));
            binding.txtPowerJoyPowerDebug.setText(String.valueOf(power));
            binding.txtPowerJoyDirectionDebug.setText(String.valueOf(direction));

        });
    }

    /**
     * Initialization of the steering joystick (right side)
     */
    private void initSteeringJoystick() {

        // When the right joystick is used (Direction of the car)
        binding.joystickDirection.setOnJoystickMoveListener((int angle, int power, int direction) -> {

            String request;

            if (angle > 0) {
                request = direction_droite_request_url;
            } else if (angle < 0) {
                request = direction_gauche_request_url;
            } else {
                request = direction_aucune_request_url;
            }

            sendRequest(request);

            // DEBUG INFO
            binding.txtDirectionJoyAngleDebug.setText(String.valueOf(angle));
            binding.txtDirectionJoyPowerDebug.setText(String.valueOf(power));
            binding.txtDirectionJoyDirectionDebug.setText(String.valueOf(direction));
        });
    }

    /**
     * Initialize the timer
     * The Timer run every 2 sec
     */
    private void timerInit() {
        timer = new CountDownTimer(360000, 2000) {

            @Override
            public void onTick(long millisUntilFinished) {
                getBatteryVoltage();
                sendFailSafeRequest();
            }

            @Override
            public void onFinish() {
                timer.start();
            }
        }.start();
    }

    /**
     * Send a request to the car
     * @param request The request to send to the car
     * @return The response of the request
     */
    private String sendRequest(String request) {

        AtomicReference<String> request_response = new AtomicReference<>();

        String final_request = "http://" + ipAddress + "/" + request;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, final_request,
                response -> {

                    // If there is a response
                    request_response.set(response);
                    binding.connectionRect.setBackgroundColor(Color.rgb(0,255,0));},

                error -> {

                    // Error of the request
                    binding.connectionRect.setBackgroundColor(Color.rgb(255,0,0));

                    if (debugMode) {
                        if (toast != null)
                            toast.cancel();
                        toast = Toast.makeText(getBaseContext(), error.getLocalizedMessage(), Toast.LENGTH_SHORT);
                        toast.show();
                    }

                });

        queue.add(stringRequest);
        return request_response.get();
    }

    /**
     * Initialize the web view to display the camera feedback
     */
    private void webviewInit() {

        Point size = new Point();
        int width = size.x;
        int height = size.y;
        WebView webView = binding.webView;
        webView.setVerticalScrollBarEnabled(false);
        webView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        String htmlWebcam = "<body style='margin:0;padding:0;'><img src=\"http:" + ipAddress + ":8081/?action=stream\"  height='" + height + "' width='" + width + "'></body></html>";
        webView.loadData(htmlWebcam, "text/html", null);
    }

    /**
     * Get the battery voltage and display it on the screen
     */
    private void getBatteryVoltage() {

        if (debugMode) {
            String batteryVoltage = sendRequest(batteryVoltage_request_url);
            if (batteryVoltage != null) {
                binding.txtBattery.setText(batteryVoltage);
            }
        }
    }

    /**
     * Send the fail safe request
     */
    private void sendFailSafeRequest() {

        // Send the fail safe request
        sendRequest(failsafe_request_url);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (timer != null)
            timer.cancel();
    }
}
