package com.example.betlink.host;
import com.example.betlink.R;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.betlink.databinding.ActivityAddListingBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class AddListingActivity extends AppCompatActivity {

    private ActivityAddListingBinding binding;
    private String savedImagePath = null;

    private final ActivityResultLauncher<String> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    saveImageToInternal(uri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddListingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupSpinners();

        binding.cardAddPhoto.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));

        binding.buttonContinueToPreview.setOnClickListener(v -> {
            if (validateInput()) {
                Intent intent = new Intent(this, ListingPreviewActivity.class);
                intent.putExtra("title", binding.editPropertyTitle.getText().toString());
                intent.putExtra("city", binding.editCity.getText().toString());
                intent.putExtra("landmark", binding.editLandmark.getText().toString());
                intent.putExtra("price", binding.editPrice.getText().toString());
                intent.putExtra("description", binding.editDescription.getText().toString());
                intent.putExtra("propertyType", binding.spinnerPropertyType.getSelectedItem().toString());
                intent.putExtra("roomType", binding.spinnerRoomType.getSelectedItem().toString());
                intent.putExtra("imageUri", savedImagePath);
                startActivity(intent);
            }
        });
    }

    private void saveImageToInternal(Uri uri) {
        try {
            InputStream in = getContentResolver().openInputStream(uri);
            if (in == null) return;
            
            File file = new File(getFilesDir(), "prop_" + UUID.randomUUID() + ".jpg");
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();
            
            savedImagePath = file.getAbsolutePath();
            binding.imagePreview.setImageURI(Uri.fromFile(file));
            binding.imagePreview.setVisibility(View.VISIBLE);
            binding.layoutAddPhotoHint.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupSpinners() {
        String[] propertyTypes = {"Apartment", "House", "Guesthouse", "Studio"};
        ArrayAdapter<String> propAdapter = new ArrayAdapter<>(this, R.layout.spinner_item_betlink, propertyTypes);
        propAdapter.setDropDownViewResource(R.layout.spinner_item_betlink);
        binding.spinnerPropertyType.setAdapter(propAdapter);

        String[] roomTypes = {"Single Room", "Entire Place", "Shared Room", "Studio"};
        ArrayAdapter<String> roomAdapter = new ArrayAdapter<>(this, R.layout.spinner_item_betlink, roomTypes);
        roomAdapter.setDropDownViewResource(R.layout.spinner_item_betlink);
        binding.spinnerRoomType.setAdapter(roomAdapter);
    }

    private boolean validateInput() {
        if (binding.editPropertyTitle.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter a property title", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.editCity.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter a city", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.editPrice.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter a price", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
