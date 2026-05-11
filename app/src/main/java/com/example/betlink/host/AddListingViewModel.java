package com.example.betlink.host;

import android.content.Context;
import android.net.Uri;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.betlink.data.Listing;
import com.example.betlink.data.ListingRepository;
import com.example.betlink.data.SessionManager;

import java.util.Collections;

public class AddListingViewModel extends ViewModel {

    private final ListingRepository repository;
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> success = new MutableLiveData<>(false);

    public AddListingViewModel() {
        this.repository = ListingRepository.getInstance();
    }

    public LiveData<Boolean> getLoading() { return loading; }
    public LiveData<String> getError() { return error; }
    public LiveData<Boolean> getSuccess() { return success; }

    public void publishListing(Context context, String title, String city, String landmark, 
                               int price, String amenities, String description, 
                               String cancellationRule, String roomType, Uri imageUri) {
        
        loading.setValue(true);
        String hostId = SessionManager.getInstance().getUserId();

        if (imageUri != null) {
            // Step 1: Upload image first
            String fileName = "listing_" + System.currentTimeMillis() + ".jpg";
            repository.uploadImage(context, imageUri, fileName, new ListingRepository.ListingCallback<String>() {
                @Override
                public void onSuccess(String imageUrl) {
                    // Step 2: Create listing with the uploaded image URL
                    createListingInternal(hostId, title, city, landmark, price, amenities, 
                            description, cancellationRule, roomType, imageUrl);
                }

                @Override
                public void onError(String message) {
                    loading.setValue(false);
                    error.setValue("Image upload failed: " + message);
                }
            });
        } else {
            // No image, create directly
            createListingInternal(hostId, title, city, landmark, price, amenities, 
                    description, cancellationRule, roomType, null);
        }
    }

    private void createListingInternal(String hostId, String title, String city, String landmark,
                                      int price, String amenities, String description,
                                      String cancellationRule, String roomType, String imageUrl) {
        
        Listing listing = new Listing(
            hostId, title, city, landmark, price, amenities, 
            description, cancellationRule, roomType, 
            imageUrl != null ? Collections.singletonList(imageUrl) : Collections.emptyList()
        );

        repository.createListing(listing, new ListingRepository.ListingCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                loading.setValue(false);
                success.setValue(true);
            }

            @Override
            public void onError(String message) {
                loading.setValue(false);
                error.setValue("Failed to create listing: " + message);
            }
        });
    }
}
