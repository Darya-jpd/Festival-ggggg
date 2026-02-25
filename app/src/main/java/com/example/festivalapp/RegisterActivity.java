package com.example.festivalapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Находим все поля
        EditText etName = findViewById(R.id.etName);
        EditText etEmail = findViewById(R.id.etEmail);
        EditText etPassword = findViewById(R.id.etPassword);
        Button btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            // 1. Проверка на пустоту
            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Заполните все поля!", Toast.LENGTH_SHORT).show();
                return;
            }

            // 2. Проверка почты (чтоб на конце gmail.com)
            if (!email.endsWith("@gmail.com")) {
                Toast.makeText(this, "Почта должна быть @gmail.com", Toast.LENGTH_SHORT).show();
                return;
            }

            // 3. Проверка пароля (не менее 6 символов)
            if (password.length() < 6) {
                Toast.makeText(this, "Пароль слишком короткий (мин. 6 симв.)", Toast.LENGTH_SHORT).show();
                return;
            }

            // Открываем SharedPreferences для записи
            SharedPreferences sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();

            // Сначала СКЛАДЫВАЕМ все данные в буфер
            editor.putString("USER_NAME", name);
            editor.putString("USER_EMAIL", email);
            editor.putBoolean("IS_LOGGED_IN", true);

            // Теперь ОДИН РАЗ сохраняем на диск и проверяем успех
            if (editor.commit()) {
                Toast.makeText(this, "Добро пожаловать, " + name + "!", Toast.LENGTH_SHORT).show();

                // Переходим на главный экран
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                // Если память устройства заблокирована или произошел сбой
                Toast.makeText(this, "Критическая ошибка памяти! Не удалось сохранить профиль.", Toast.LENGTH_LONG).show();
            }
        });
    }
}