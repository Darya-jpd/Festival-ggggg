package com.example.festivalapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class DetailsActivity extends AppCompatActivity {

    private SharedPreferences sharedPref;
    private String title;

    // Переменные для слайдера картинок
    private ViewPager2 viewPager;
    private TextView tvIndicator;
    private final Handler sliderHandler = new Handler(Looper.getMainLooper());
    private int[] festivalImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        // ШАГ 1. ПЕРВЫМ ДЕЛОМ ПОЛУЧАЕМ ДАННЫЕ ИЗ ИНТЕНТА
        title = getIntent().getStringExtra("FEST_TITLE");
        String desc = getIntent().getStringExtra("FEST_DESC");
        String date = getIntent().getStringExtra("FEST_DATE");
        String geoUrl = getIntent().getStringExtra("FEST_GEO");
        String userName = sharedPref.getString("USER_NAME", "Гость");

        // ШАГ 2. НАХОДИМ ВСЕ ЭЛЕМЕНТЫ ИНТЕРФЕЙСА
        TextView tvTitle = findViewById(R.id.tvDetailTitle);
        TextView tvDesc = findViewById(R.id.tvDetailDesc);
        Button btnMap = findViewById(R.id.btnMap);
        Button btnBack = findViewById(R.id.btnBack);

        LinearLayout layoutInfo = findViewById(R.id.layoutInfo);
        LinearLayout layoutTicket = findViewById(R.id.layoutTicket);
        ImageView ivTicketQr = findViewById(R.id.ivTicketQr);
        TextView tvTicketDate = findViewById(R.id.tvTicketDate);

        viewPager = findViewById(R.id.viewPagerImages);
        tvIndicator = findViewById(R.id.tvImageIndicator);
        ImageButton btnLeftArrow = findViewById(R.id.btnLeftArrow);
        ImageButton btnRightArrow = findViewById(R.id.btnRightArrow);

        Button btnRegisterFest = findViewById(R.id.btnRegisterFest);
        Button btnUnregisterFest = findViewById(R.id.btnUnregisterFest);

        // Заполняем базовые тексты
        if (tvTitle != null) tvTitle.setText(title);
        if (tvDesc != null) tvDesc.setText(desc);
        if (tvTicketDate != null && date != null) tvTicketDate.setText("Дата: " + date);

        // ШАГ 3. ЛОГИКА СЛУШАТЕЛЕЙ СТРЕЛОК СЛАЙДЕРА
        if (btnLeftArrow != null) {
            btnLeftArrow.setOnClickListener(v -> {
                if (viewPager != null && festivalImages != null) {
                    int currentItem = viewPager.getCurrentItem();
                    if (currentItem > 0) {
                        viewPager.setCurrentItem(currentItem - 1, true);
                    } else {
                        viewPager.setCurrentItem(festivalImages.length - 1, true);
                    }
                }
            });
        }

        if (btnRightArrow != null) {
            btnRightArrow.setOnClickListener(v -> {
                if (viewPager != null && festivalImages != null) {
                    int currentItem = viewPager.getCurrentItem();
                    if (currentItem < festivalImages.length - 1) {
                        viewPager.setCurrentItem(currentItem + 1, true);
                    } else {
                        viewPager.setCurrentItem(0, true);
                    }
                }
            });
        }

        // ШАГ 4. НАСТРОЙКА КАРТИНЕК ГАЛЕРЕИ
        if (title != null) {
            if (title.contains("Стереолето")) {
                festivalImages = new int[]{R.drawable.stereoleto1, R.drawable.stereoleto2, R.drawable.stereoleto3};
            } else if (title.contains("VK Fest") || title.contains("ВК")) {
                festivalImages = new int[]{R.drawable.vk1, R.drawable.vk2, R.drawable.vk3};
            } else if (title.contains("Небо")) {
                festivalImages = new int[]{R.drawable.nebo1, R.drawable.nebo2, R.drawable.nebo3, R.drawable.nebo4};
            } else {
                festivalImages = new int[]{R.drawable.myata1, R.drawable.myata2, R.drawable.myata3};
            }
        } else {
            festivalImages = new int[]{R.drawable.myata1};
        }

        if (viewPager != null && tvIndicator != null) {
            ImageSliderAdapter adapter = new ImageSliderAdapter(festivalImages);
            viewPager.setAdapter(adapter);

            viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    tvIndicator.setText((position + 1) + " из " + festivalImages.length);
                    sliderHandler.removeCallbacks(sliderRunnable);
                    sliderHandler.postDelayed(sliderRunnable, 3000);
                }
            });
        }

        // ШАГ 5. ЕДИНАЯ ПРОВЕРКА СТАТУСА РЕГИСТРАЦИИ
        boolean isRegistered = sharedPref.getBoolean("REG_" + title, false);

        if (isRegistered) {
            // Пользователь зарегистрирован -> Прячем инфо, показываем билет и кнопку ОТМЕНЫ
            if (layoutInfo != null) layoutInfo.setVisibility(View.GONE);
            if (layoutTicket != null) layoutTicket.setVisibility(View.VISIBLE);

            if (btnRegisterFest != null) btnRegisterFest.setVisibility(View.GONE);
            if (btnUnregisterFest != null) btnUnregisterFest.setVisibility(View.VISIBLE);

            if (ivTicketQr != null) {
                String ticketData = "TICKET:" + title + "|" + userName + "|" + date;
                Bitmap qrBitmap = generateQRCode(ticketData);
                if (qrBitmap != null) ivTicketQr.setImageBitmap(qrBitmap);
            }
        } else {
            // Пользователь НЕ зарегистрирован
            if (layoutInfo != null) layoutInfo.setVisibility(View.VISIBLE);
            if (layoutTicket != null) layoutTicket.setVisibility(View.GONE);

            if (btnRegisterFest != null) btnRegisterFest.setVisibility(View.VISIBLE);
            if (btnUnregisterFest != null) btnUnregisterFest.setVisibility(View.GONE);
        }

        // ШАГ 6. ОБРАБОТКА НАЖАТИЯ "ЗАРЕГИСТРИРОВАТЬСЯ"
        if (btnRegisterFest != null) {
            btnRegisterFest.setOnClickListener(v -> {
                sharedPref.edit().putBoolean("REG_" + title, true).apply();

                // Меняем экраны местами на лету
                if (layoutInfo != null) layoutInfo.setVisibility(View.GONE);
                if (layoutTicket != null) layoutTicket.setVisibility(View.VISIBLE);

                btnRegisterFest.setVisibility(View.GONE);
                if (btnUnregisterFest != null) btnUnregisterFest.setVisibility(View.VISIBLE);

                if (ivTicketQr != null) {
                    String ticketData = "TICKET:" + title + "|" + userName + "|" + date;
                    Bitmap qrBitmap = generateQRCode(ticketData);
                    if (qrBitmap != null) ivTicketQr.setImageBitmap(qrBitmap);
                }

                Toast.makeText(DetailsActivity.this, "Вы успешно зарегистрировались!", Toast.LENGTH_SHORT).show();
            });
        }

        // ШАГ 7. ОБРАБОТКА НАЖАТИЯ "ОТМЕНИТЬ РЕГИСТРАЦИЮ"
        if (btnUnregisterFest != null) {
            btnUnregisterFest.setOnClickListener(v -> {
                if (title != null) {
                    // Сохраняем отмену
                    sharedPref.edit().putBoolean("REG_" + title, false).apply();

                    Toast.makeText(DetailsActivity.this, "Регистрация отменена", Toast.LENGTH_SHORT).show();

                    // Закрываем текущую карточку и возвращаемся на главную
                    finish();
                }
            });
        }

        // Логика кнопки "Посмотреть на карте"
        if (btnMap != null && geoUrl != null) {
            btnMap.setOnClickListener(v -> {
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUrl));
                startActivity(mapIntent);
            });
        }

        // Логика кнопки "Назад"
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
    }

    // Метод генерации QR-кода
    private Bitmap generateQRCode(String text) {
        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, 500, 500);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? android.graphics.Color.BLACK : android.graphics.Color.WHITE);
                }
            }
            return bmp;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Задача автоматического перелистывания картинок
    private final Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            if (viewPager != null && festivalImages != null && festivalImages.length > 1) {
                int currentPosition = viewPager.getCurrentItem();
                int nextPosition = currentPosition + 1;

                if (nextPosition >= festivalImages.length) {
                    nextPosition = 0;
                }
                viewPager.setCurrentItem(nextPosition, true);
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (festivalImages != null && festivalImages.length > 1) {
            sliderHandler.postDelayed(sliderRunnable, 3000);
        }
    }
}