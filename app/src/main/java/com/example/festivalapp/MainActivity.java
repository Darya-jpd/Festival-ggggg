package com.example.festivalapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.cardview.widget.CardView;
import android.widget.Toast;
import android.content.Intent;
import android.widget.LinearLayout;
import android.content.SharedPreferences;
import android.widget.TextView;
import android.app.AlertDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Сначала ПРОВЕРЯЕМ вход (без setContentView)
        SharedPreferences sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPref.getBoolean("IS_LOGGED_IN", false);

        if (!isLoggedIn) {
            // Если входа не было — уходим на регистрацию
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // 2. Если мы здесь, значит вход выполнен — ставим макет
        setContentView(R.layout.activity_main);

        // 3. ТЕПЕРЬ можно искать элементы и вешать клики
        FloatingActionButton fabAdd = findViewById(R.id.fabAddFestival);
        if (fabAdd != null) {
            fabAdd.setOnClickListener(v -> showAddFestivalDialog());
        }

        // 3. Выводим имя пользователя в заголовок
        String savedName = sharedPref.getString("USER_NAME", "Гость");
        TextView tvHeader = findViewById(R.id.tvHeader);

        if (tvHeader != null) {
            tvHeader.setText("Привет, " + savedName + "!");
        }



        // 1. Поиск и Темы
        EditText etSearch = findViewById(R.id.etSearch);
        View btnTheme = findViewById(R.id.btnTheme);
        // Находим кнопку профиля
        android.widget.ImageButton btnOpenProfile = findViewById(R.id.btnOpenProfile);

// Вешаем клик
        if (btnOpenProfile != null) {
            btnOpenProfile.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
            });
        }
        if (btnTheme != null) {
            btnTheme.setOnClickListener(v -> {
                if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }
            });
        }

        Button btnSort = findViewById(R.id.btnSort);
        final LinearLayout listContainer = findViewById(R.id.listContainer);

        if (btnSort != null && listContainer != null) {
            btnSort.setOnClickListener(v -> {
                View cardNebo = findViewById(R.id.cardNebo);
                if (cardNebo != null) {
                    // Исправляем ошибки компиляции
                    listContainer.removeView(cardNebo);
                    listContainer.addView(cardNebo, 3); // Вставляем после заголовков
                    Toast.makeText(this, "Сортировка по дате применена", Toast.LENGTH_SHORT).show();
                }
            });
        }



        // 2. Карточки
        final CardView cardStereoleto = findViewById(R.id.cardStereoleto);
        final CardView cardNebo = findViewById(R.id.cardNebo);

        // 3. Картинки
        ImageView ivStereo = findViewById(R.id.ivStereo);
        ImageView ivNebo = findViewById(R.id.imageNebo);
        CardView cardVkFest = findViewById(R.id.cardVkFest);
        CardView cardMint = findViewById(R.id.cardMint);
        ImageView ivVkFest = findViewById(R.id.ivVkFest);
        ImageView ivMint = findViewById(R.id.ivMint);

        if (ivStereo != null) ivStereo.setImageResource(R.drawable.fest_stereo);
        if (ivNebo != null) ivNebo.setImageResource(R.drawable.fest_nebo);
        if (ivVkFest != null) ivVkFest.setImageResource(R.drawable.fest_vk);
        if (ivMint != null) ivMint.setImageResource(R.drawable.fest_mint);


        // 4. Настройка кликов с передачей ИНФОРМАЦИИ
        setupCard(cardStereoleto, "Стереолето",
                "Старейший музыкальный фестиваль. 3 сцены и море драйва.", "16 Июля 2026", "https://www.google.com/maps/place/%D0%A1%D0%B5%D0%B2%D0%BA%D0%B0%D0%B1%D0%B5%D0%BB%D1%8C+%D0%9F%D0%BE%D1%80%D1%82+%2F+Sevkabel+Port/@59.9243343,30.2384753,17z/data=!3m1!4b1!4m6!3m5!1s0x469630ceca4c4d4f:0x44047906277f0544!8m2!3d59.9243343!4d30.2410502!16s%2Fg%2F11fxdw_6l0?entry=ttu&g_ep=EgoyMDI2MDYwMy4xIKXMDSoASAFQAw%3D%3D");

        setupCard(cardNebo, "Небо",
                "Этно-фестиваль под открытым небом. Полеты на шарах.", "22 Августа 2026", "https://www.google.com/maps/place/%D1%83%D0%BB.+%D0%9A%D1%80%D1%8B%D0%BC%D1%81%D0%BA%D0%B8%D0%B9+%D0%92%D0%B0%D0%BB,+9+Parking/@55.732073,37.6031229,18.75z/data=!4m6!3m5!1s0x46b54b085efeb4ef:0x4e4063d23857db49!8m2!3d55.7321112!4d37.6037376!16s%2Fg%2F11jz5g8h_5?entry=ttu&g_ep=EgoyMDI2MDYwMy4xIKXMDSoASAFQAw%3D%3D");
        setupCard(cardVkFest, "VK Fest",
                "Главный open-air страны. Встречи с блогерами и топовые артисты.", "5-6 Июля 2026", "https://www.google.com/maps/place/%D0%92%D1%8B%D1%81%D1%82%D0%B0%D0%B2%D0%BA%D0%B0+%D0%B4%D0%BE%D1%81%D1%82%D0%B8%D0%B6%D0%B5%D0%BD%D0%B8%D0%B9+%D0%BD%D0%B0%D1%80%D0%BE%D0%B4%D0%BD%D0%BE%D0%B3%D0%BE+%D1%85%D0%BE%D0%B7%D1%8F%D0%B9%D1%81%D1%82%D0%B2%D0%B0+(%D0%92%D0%94%D0%9D%D0%A5)/@55.831,37.627225,17z/data=!3m1!4b1!4m6!3m5!1s0x46b536676b2e6f75:0x91bd12197c6a68a1!8m2!3d55.831!4d37.6297999!16zL20vMDdoc2s4?entry=ttu&g_ep=EgoyMDI2MDYwMy4xIKXMDSoASAFQAw%3D%3D");

        setupCard(cardMint, "Дикая Мята",
                "Три дня музыки и любви. Крупнейший независимый фестиваль.", "13-15 Июня 2026",  "https://www.google.com/maps/place/%D0%A4%D0%B5%D1%81%D1%82%D0%B8%D0%B2%D0%B0%D0%BB%D1%8C+%C2%AB%D0%94%D0%B8%D0%BA%D0%B0%D1%8F+%D0%BC%D1%8F%D1%82%D0%B0%C2%BB/@54.5691641,37.1507783,17.25z/data=!4m15!1m8!3m7!1s0x4134fbb6b492ef63:0xac5046ca17866aa2!2z0JHRg9C90YvRgNC10LLQviwg0KLRg9C70YzRgdC60LDRjyDQvtCx0LsuLCDQoNC-0YHRgdC40Y8!3b1!8m2!3d54.5692857!4d37.153356!16s%2Fg%2F1hc0h76hw!3m5!1s0x4134fb1876b6ffaf:0x7a04820658674a2e!8m2!3d54.5686149!4d37.1509133!16s%2Fg%2F11hz1hr2f5?entry=ttu&g_ep=EgoyMDI2MDYwMy4xIKXMDSoASAFQAw%3D%3D");




        // 5. Умный поиск (фильтрация с первой буквы)
        if (etSearch != null) {
            etSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // Приводим ввод к нижнему регистру и убираем пробелы
                    String query = s.toString().toLowerCase().trim();

                    // Если поиск пустой — показываем всё. Если нет — проверяем наличие букв в названии.
                    if (cardStereoleto != null) {
                        boolean match = query.isEmpty() || "стереолето музыкальный фестиваль".contains(query);
                        cardStereoleto.setVisibility(match ? View.VISIBLE : View.GONE);
                    }

                    if (cardNebo != null) {
                        boolean match = query.isEmpty() || "небо этно-фестиваль".contains(query);
                        cardNebo.setVisibility(match ? View.VISIBLE : View.GONE);
                    }

                    if (cardVkFest != null) {
                        boolean match = query.isEmpty() || "vk fest вк фест".contains(query);
                        cardVkFest.setVisibility(match ? View.VISIBLE : View.GONE);
                    }

                    if (cardMint != null) {
                        boolean match = query.isEmpty() || "дикая мята".contains(query);
                        cardMint.setVisibility(match ? View.VISIBLE : View.GONE);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }
    }

    // Универсальный метод для настройки карточек
    private void setupCard(final CardView card, final String title, final String desc, final String date, final String geoUrl) {
        if (card != null) {
            card.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                intent.putExtra("FEST_TITLE", title);
                intent.putExtra("FEST_DESC", desc);
                intent.putExtra("FEST_DATE", date);
                intent.putExtra("FEST_GEO", geoUrl);
                startActivity(intent);
            });

            // Долгий клик — ОТМЕНА регистрации прямо с главного экрана (Бонус для препода!)
            card.setOnLongClickListener(v -> {
                SharedPreferences sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                boolean isReg = sharedPref.getBoolean("REG_" + title, false);

                if (isReg) {
                    sharedPref.edit().putBoolean("REG_" + title, false).apply();
                    Toast.makeText(MainActivity.this, "Регистрация на " + title + " отменена!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Вы еще не зарегистрированы на этот фест", Toast.LENGTH_SHORT).show();
                }
                return true;
            });
        }
    }

    private void showAddFestivalDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Добавить фестиваль");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        // Объявляем inputTitle и inputDate правильно!
        final EditText inputTitle = new EditText(this);
        inputTitle.setHint("Название фестиваля");
        layout.addView(inputTitle);

        final EditText inputDate = new EditText(this);
        inputDate.setHint("Дата (например, 20 сентября)");
        layout.addView(inputDate);

        builder.setView(layout);

        builder.setPositiveButton("Добавить", (dialog, which) -> {
            String title = inputTitle.getText().toString();
            String date = inputDate.getText().toString();
            if (!title.isEmpty() && !date.isEmpty()) {
                addNewFestivalCard(title, date);
            }
        });
        builder.setNegativeButton("Отмена", null);
        builder.show();
    }

    private void addNewFestivalCard(String title, String date) {
        // Находим наш контейнер (проверь, чтобы ID в XML был таким же!)
        LinearLayout listContainer = findViewById(R.id.listContainer);
        if (listContainer == null) return;

        // Создаем новую карточку
        androidx.cardview.widget.CardView card = new androidx.cardview.widget.CardView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 40);
        card.setLayoutParams(params);
        card.setRadius(35); // Закругляем углы

        LinearLayout innerLayout = new LinearLayout(this);
        innerLayout.setOrientation(LinearLayout.VERTICAL);
        innerLayout.setPadding(30, 30, 30, 30);

        // Добавляем дату (серым цветом)
        TextView tvDate = new TextView(this);
        tvDate.setText(date);
        tvDate.setTextColor(android.graphics.Color.GRAY);
        innerLayout.addView(tvDate);

        // Добавляем название (жирным)
        TextView tvTitle = new TextView(this);
        tvTitle.setText(title);
        tvTitle.setTextSize(18);
        tvTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        innerLayout.addView(tvTitle);

        card.addView(innerLayout);
        listContainer.addView(card);

        // Делаем так, чтобы новая карточка тоже открывала билет
        setupCard(card, title, "Описание будет добавлено позже", date, "https://maps.google.com/?q=" + title);
    }


}