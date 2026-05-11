package com.example.betlink.host;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.betlink.R;
import com.example.betlink.databinding.ActivityAddListingBinding;

public class AddListingActivity extends AppCompatActivity {

    private ActivityAddListingBinding binding;
    private AddListingViewModel viewModel;
    private Uri selectedImageUri = null;

    private final ActivityResultLauncher<String> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    binding.imagePreview.setImageURI(uri);
                    binding.imagePreview.setVisibility(View.VISIBLE);
                    binding.layoutAddPhotoHint.setVisibility(View.GONE);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddListingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(AddListingViewModel.class);

        setupSpinners();
        observeViewModel();

        binding.cardAddPhoto.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));

        binding.buttonContinueToPreview.setOnClickListener(v -> {
            if (validateInput()) {
                publishListing();
            }
        });
    }

    private void observeViewModel() {
        viewModel.getLoading().observe(this, isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.buttonContinueToPreview.setEnabled(!isLoading);
        });

        viewModel.getError().observe(this, errorMsg -> {
            if (errorMsg != null) {
                Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
            }
        });

        viewModel.getSuccess().observe(this, isSuccess -> {
            if (isSuccess) {
                Toast.makeText(this, R.string.publish_success, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void publishListing() {
        String title = binding.editPropertyTitle.getText().toString().trim();
        String city = binding.editCity.getText().toString().trim();
        String landmark = binding.editLandmark.getText().toString().trim();
        int price = Integer.parseInt(binding.editPrice.getText().toString().trim());
        String description = binding.editDescription.getText().toString().trim();
        String roomType = binding.spinnerRoomType.getSelectedItem().toString();
        
        // Amenities and Cancellation Rule (can be expanded later with UI)
        String amenities = "Standard Amenities";
        String cancellationRule = "Flexible";

        viewModel.publishListing(this, title, city, landmark, price, amenities, 
                description, cancellationRule, roomType, selectedImageUri);
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
        try {
            int price = Integer.parseInt(binding.editPrice.getText().toString().trim());
            if (price <= 0) {
                Toast.makeText(this, "Price must be greater than 0 ETB", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Enter a valid price in ETB", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.editLandmark.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please add a nearby landmark", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.editDescription.getText().toString().trim().length() < 20) {
            Toast.makeText(this, "Add a short description for travelers", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
