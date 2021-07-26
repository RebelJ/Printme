package com.example.printme.ui.register;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.printme.MainActivity;
import com.example.printme.R;
import com.example.printme.ui.app.AppConfig;
import com.example.printme.ui.app.AppController;
import com.example.printme.ui.helper.SQLiteHandler;
import com.example.printme.ui.helper.SessionManager;
import com.example.printme.ui.login.LoginActivity;
import com.example.printme.ui.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class RegisterActivity extends Activity {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private Button btnRegister;
    private Button btnLinkToLogin;
    private EditText inputFirstName;
    private EditText inputFullName;
    private EditText inputEmail;
    private EditText inputPassword;
    private EditText inputConfirmMdp;

    private EditText inputNumAddress;
    private EditText inputVoieAddress;
    private EditText inputCodePostal;
    private EditText inputCountry;
    private EditText inputRegion;
    private EditText inputBatiment;
    private EditText inputPhone;


    private Dialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    private static final int SOCKET_TIMEOUT_MS = 1000 * 10; //default is 2500

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        inputFirstName =  findViewById(R.id.familyName);
        inputFullName =  findViewById(R.id.name);
        inputEmail = findViewById(R.id.email);
        inputPassword = findViewById(R.id.password);
        inputConfirmMdp =  findViewById(R.id.passwordconfirm);
        inputNumAddress =  findViewById(R.id.addressNumber);
        inputVoieAddress = findViewById(R.id.addressHouse);
        inputCodePostal = findViewById(R.id.codePostal);
        inputCountry = findViewById(R.id.country);
        inputRegion = findViewById(R.id.addressRegion);
        inputBatiment = findViewById(R.id.addressBatiment);
        inputPhone = findViewById(R.id.numeroPhone);

        btnRegister =  findViewById(R.id.btnRegister);
        btnLinkToLogin = findViewById(R.id.btnLinkToLoginScreen);

        // Progress dialog
        pDialog = new Dialog(this);
        pDialog.setCancelable(false);

        // Session manager
        session = new SessionManager(getApplicationContext());

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(RegisterActivity.this,
                    MainActivity.class);
            startActivity(intent);
            finish();
        }

        // Register Button Click event
        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String firstName = inputFirstName.getText().toString().trim();
                String name = inputFullName.getText().toString().trim();
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                String confirmPassword = inputConfirmMdp.getText().toString().trim();

                String numAddress = inputNumAddress.getText().toString().trim();
                String voieAddress = inputVoieAddress.getText().toString().trim();
                String codePostal = inputCodePostal.getText().toString().trim();
                String country = inputCountry.getText().toString().trim();
                String region = inputRegion.getText().toString().trim();
                String batiment = inputBatiment.getText().toString().trim();
                String phone = inputPhone.getText().toString().trim();


                if (confirmPassword.equals(password) ){
                    if (!firstName.isEmpty() && !name.isEmpty() && !email.isEmpty() && !password.isEmpty() &&
                    !numAddress.isEmpty() && !voieAddress.isEmpty() && !codePostal.isEmpty() && !country.isEmpty()) {
                        registerUser(firstName, name, email, password, numAddress, voieAddress, codePostal, country, region, batiment, phone);
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Entrer tous les données obligatoires * ", Toast.LENGTH_LONG)
                                .show();
                    }
                }else {
                    Toast.makeText(getApplicationContext(),
                            "Les mots de passe ne sont pas les mêmes", Toast.LENGTH_LONG)
                            .show();
                }



            }
        });

        // Link to Login Screen
        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

    }

    /**
     * Function to store user in MySQL database will post params(tag, name,
     * email, password) to register url
     * */
    private void registerUser(final String firstName, final String name, final String email,
                              final String password, final String numAddress, final String voieAddress,
                              final String codePostal, final String country, final String region,final String batiment, final String phone) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

       // pDialog.setMessage("Registering ...");
        showDialog();

        StringRequest strReq = new StringRequest(Method.POST,
                AppConfig.URL_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response);
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in MySQL
                        // Now store the user in sqlite
                        String uid = jObj.getString("uid");

                        JSONObject user = jObj.getJSONObject("user");
                        String firstName = user.getString("firstName");
                        String name = user.getString("name");
                        String email = user.getString("email");
                        String numero = user.getString("num");
                        String address = user.getString("address");
                        String codePostal = user.getString("postal");
                      //  String country = user.getString("country");
                     //   String region = user.getString("region");
                       // String batiment = user.getString("bat");
                       // String phone = user.getString("tel");

                        String created_at = user
                                .getString("created_at");

                        // Inserting row in users table
                        db.addUser(firstName, name, email, uid, created_at, numero, address, codePostal, country, region, batiment, phone);


                        Toast.makeText(getApplicationContext(), "User successfully registered. Try login now!", Toast.LENGTH_LONG).show();

                        // Launch login activity
                        Intent intent = new Intent(
                                RegisterActivity.this,
                                LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<>();
                params.put("firstName", firstName);
                params.put("name", name);
                params.put("email", email);
                params.put("password", password);
                params.put("num", numAddress);
                params.put("address", voieAddress);
                params.put("postal", codePostal);
                params.put("city", country);
                params.put("country", country);
                params.put("region", region);
                params.put("bat", batiment);
                params.put("tel", phone);

                return params;
            }

        };
        strReq.setRetryPolicy(new DefaultRetryPolicy(
                SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}