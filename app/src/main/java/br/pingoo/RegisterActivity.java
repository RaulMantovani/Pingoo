package br.pingoo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONObject;
import org.json.JSONException;

public class RegisterActivity extends AppCompatActivity {

    private static final String REGISTER_URL = "http://192.168.1.254:8080/registrar";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        EditText email = findViewById(R.id.email);
        EditText username = findViewById(R.id.username);
        EditText password = findViewById(R.id.password);
        Button registerButton = findViewById(R.id.registerButton);

        registerButton.setOnClickListener(v -> {
            String emailText = email.getText().toString().trim();
            String userText = username.getText().toString().trim();
            String passText = password.getText().toString().trim();

            if (emailText.isEmpty() || userText.isEmpty() || passText.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                JSONObject json = new JSONObject();
                json.put("email", emailText);
                json.put("usuario", userText);
                json.put("senha", passText);

                JsonObjectRequest request = new JsonObjectRequest(
                        Request.Method.POST,
                        REGISTER_URL,
                        json,
                        response -> {
                            try {
                                String msg = response.getString("mensagem");
                                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

                                if (msg.equalsIgnoreCase("Usuário registrado com sucesso.")) {
                                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                    finish(); // fecha a tela de registro
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(this, "Erro na resposta do servidor", Toast.LENGTH_SHORT).show();
                            }
                        },
                        error -> {
                            String msg = "Erro ao conectar";

                            if (error.networkResponse != null) {
                                msg += " - Código HTTP: " + error.networkResponse.statusCode;
                                Log.e("VolleyError", "HTTP Status: " + error.networkResponse.statusCode);
                                Log.e("VolleyError", "Response: " + new String(error.networkResponse.data));
                            } else if (error.getCause() != null) {
                                msg += " - Causa: " + error.getCause().getMessage();
                                Log.e("VolleyError", "Cause: ", error.getCause());
                            }

                            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                        }
                );

                Volley.newRequestQueue(this).add(request);

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(this, "Erro ao criar JSON", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
