package com.example.festivalapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class ProfileActivity extends AppCompatActivity {

    private ImageView ivProfileAvatar;
    private ImageView ivProfileQr;
    private SharedPreferences sharedPref;

    private final ActivityResultLauncher<String> mGetContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null && ivProfileAvatar != null) {
                    ivProfileAvatar.setImageURI(uri);

                    // Сохраняем путь к картинке, чтобы она не пропадала
                    sharedPref.edit().putString("USER_AVATAR_URI", uri.toString()).apply();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        // 1. Инициализация элементов
        TextView tvName = findViewById(R.id.tvProfileName);
        TextView tvEmail = findViewById(R.id.tvProfileEmail);
        Button btnLogout = findViewById(R.id.btnLogout);
        Button btnBack = findViewById(R.id.btnBack);
        ivProfileAvatar = findViewById(R.id.ivProfileAvatar);
        ivProfileQr = findViewById(R.id.ivProfileQr);

        // 2. Восстановление данных
        String savedName = sharedPref.getString("USER_NAME", "Гость");
        String savedEmail = sharedPref.getString("USER_EMAIL", "Почта не указана");
        String savedAvatarUri = sharedPref.getString("USER_AVATAR_URI", null);

        if (tvName != null) tvName.setText(savedName);
        if (tvEmail != null) tvEmail.setText(savedEmail);

        // Восстанавливаем аватарку, если она была выбрана ранее
        if (savedAvatarUri != null && ivProfileAvatar != null) {
            ivProfileAvatar.setImageURI(Uri.parse(savedAvatarUri));
        }

        // 3. Выбор фото по клику
        if (ivProfileAvatar != null) {
            ivProfileAvatar.setOnClickListener(v -> mGetContent.launch("image/*"));
        }

        // 4. ГЕНЕРАЦИЯ QR-КОДА
        if (ivProfileQr != null) {
            // Зашиваем в QR уникальную строку пользователя
            String qrData = "FestivalGoer:" + savedName + "|" + savedEmail;
            Bitmap qrBitmap = generateQRCode(qrData);
            if (qrBitmap != null) {
                ivProfileQr.setImageBitmap(qrBitmap);
            }
        }

        // 5. Кнопки навигации
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("IS_LOGGED_IN", false);
                editor.commit();

                Intent intent = new Intent(ProfileActivity.this, RegisterActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        }
    }

    // Вспомогательный метод для создания QR-кода
    private Bitmap generateQRCode(String text) {
        QRCodeWriter writer = new QRCodeWriter();
        try {
            // Создаем матрицу QR-кода размером 500x500 пикселей
            BitMatrix bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, 500, 500);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    // Если точка в матрице true — красим в черный, иначе в белый
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? android.graphics.Color.BLACK : android.graphics.Color.WHITE);
                }
            }
            return bmp;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }
}