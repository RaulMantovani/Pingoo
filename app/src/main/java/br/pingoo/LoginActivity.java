package br.pingoo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONObject;
import org.json.JSONException;

public class LoginActivity extends AppCompatActivity {

    private static final String LOGIN_URL = IpConfig.API_IP + "/login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText username = findViewById(R.id.username);
        EditText password = findViewById(R.id.password);
        Button loginButton = findViewById(R.id.loginButton);
        Button registerButton = findViewById(R.id.registerButton);

        loginButton.setOnClickListener(v -> {
            String user = username.getText().toString();
            String pass = password.getText().toString();

            if (user.isEmpty() || pass.isBlank()) {
                Toast.makeText(this, "Insira usuario e senha", Toast.LENGTH_SHORT).show();
            }

            try {
                JSONObject dadosLogin = new JSONObject();
                dadosLogin.put("usuario", user);
                dadosLogin.put("senha", pass);

                JsonObjectRequest request = new JsonObjectRequest(
                        Request.Method.POST,
                        LOGIN_URL,
                        dadosLogin,
                        response -> {
                            try {
                                String mensagem = response.getString("mensagem");
                                if (mensagem.equalsIgnoreCase("Login bem-sucedido.")) {
                                    Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                } else {
                                    Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(this, "Erro no formato da resposta", Toast.LENGTH_SHORT).show();
                            }
                        },
                        error -> {
                            String msg = "Erro ao conectar";

                            if (error.networkResponse != null) {
                                msg += " - Código HTTP: " + error.networkResponse.statusCode;
                                Log.e("VolleyError", "HTTP Status: " + error.networkResponse.statusCode);
                                Log.e("VolleyError", "Response data: " + new String(error.networkResponse.data));
                            } else if (error.getCause() != null) {
                                msg += " - Causa: " + error.getCause().getMessage();
                                Log.e("VolleyError", "Cause: ", error.getCause());
                            } else if (error.getMessage() != null) {
                                msg += " - Mensagem: " + error.getMessage();
                                Log.e("VolleyError", "Message: " + error.getMessage());
                            } else {
                                Log.e("VolleyError", "Erro desconhecido", error);
                            }

                            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                        }
                );

                Volley.newRequestQueue(this).add(request);

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(this, "Erro ao preparar JSON", Toast.LENGTH_SHORT).show();
            }
        });

        registerButton.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
    }
}
