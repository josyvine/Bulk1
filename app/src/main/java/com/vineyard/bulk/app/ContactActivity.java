package com.vineyard.bulk.app;

import android.os.Bundle;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.vineyard.bulk.app.utils.NativeBridge;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;

public class ContactActivity extends AppCompatActivity {

    private EditText nameInput;
    private EditText emailInput;
    private EditText messageInput;
    private MaterialButton sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        View rootLayout = findViewById(R.id.contact_root_layout);
        if (rootLayout != null) {
            ViewCompat.setOnApplyWindowInsetsListener(rootLayout, (v, insets) -> {
                Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime());
                v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
                return insets;
            });
        }

        MaterialToolbar toolbar = findViewById(R.id.toolbar_contact);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        nameInput = findViewById(R.id.et_contact_name);
        emailInput = findViewById(R.id.et_contact_email);
        messageInput = findViewById(R.id.et_contact_message);
        sendButton = findViewById(R.id.btn_send_contact);

        sendButton.setOnClickListener(v -> sendForm());
    }

    private void sendForm() {
        String name = nameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String message = messageInput.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || message.isEmpty()) {
            Toast.makeText(this, R.string.contact_fill_all_fields, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, R.string.contact_invalid_email, Toast.LENGTH_SHORT).show();
            return;
        }

        sendButton.setEnabled(false);
        sendButton.setText(R.string.contact_sending);

        new Thread(() -> {
            boolean success = false;
            HttpURLConnection conn = null;
            try {
                URL url = new URL(NativeBridge.getContactEndpoint());
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; utf-8");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("contactName", name);
                jsonParam.put("contactEmail", email);
                jsonParam.put("contactMessage", message);

                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonParam.toString().getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                int responseCode = conn.getResponseCode();
                success = (responseCode >= 200 && responseCode < 300);
            } catch (Exception ignored) {
                success = false;
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }

            final boolean finalSuccess = success;
            runOnUiThread(() -> {
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                if (finalSuccess) {
                    Toast.makeText(ContactActivity.this, R.string.contact_message_sent, Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    sendButton.setEnabled(true);
                    sendButton.setText(R.string.contact_send_button);
                    Toast.makeText(ContactActivity.this, R.string.contact_send_failed, Toast.LENGTH_LONG).show();
                }
            });
        }).start();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}