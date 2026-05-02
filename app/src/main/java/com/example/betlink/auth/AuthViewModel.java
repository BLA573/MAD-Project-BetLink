package com.example.betlink.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.betlink.data.AuthResponse;
import com.example.betlink.data.UserRepository;

/**
 * Shared ViewModel for LoginActivity and SignUpActivity.
 * Exposes three LiveData streams:
 *   - isLoading  : show / hide progress spinner
 *   - authSuccess: non-null AuthResponse on success
 *   - errorMessage: human-readable error string on failure
 */
public class AuthViewModel extends ViewModel {

    private final UserRepository repository = new UserRepository();

    private final MutableLiveData<Boolean>      isLoading   = new MutableLiveData<>(false);
    private final MutableLiveData<AuthResponse> authSuccess = new MutableLiveData<>();
    private final MutableLiveData<String>       errorMessage = new MutableLiveData<>();

    // -------------------------------------------------------------------------
    // Exposed LiveData
    // -------------------------------------------------------------------------

    public LiveData<Boolean>      getIsLoading()    { return isLoading;    }
    public LiveData<AuthResponse> getAuthSuccess()  { return authSuccess;  }
    public LiveData<String>       getErrorMessage() { return errorMessage; }

    // -------------------------------------------------------------------------
    // Login
    // -------------------------------------------------------------------------

    public void login(String email, String password) {
        if (!validate(email, password)) return;

        isLoading.setValue(true);
        repository.login(email.trim(), password, new UserRepository.AuthCallback() {
            @Override
            public void onSuccess(AuthResponse response) {
                isLoading.setValue(false);
                authSuccess.setValue(response);
            }

            @Override
            public void onError(String message) {
                isLoading.setValue(false);
                errorMessage.setValue(message);
            }
        });
    }

    // -------------------------------------------------------------------------
    // Sign-up
    // -------------------------------------------------------------------------

    public void signUp(String email, String password,
                       String fullName, String role) {

        if (fullName == null || fullName.trim().isEmpty()) {
            errorMessage.setValue("Full name is required.");
            return;
        }
        if (!validate(email, password)) return;

        isLoading.setValue(true);
        repository.signUp(email.trim(), password, fullName.trim(), role,
            new UserRepository.AuthCallback() {
                @Override
                public void onSuccess(AuthResponse response) {
                    isLoading.setValue(false);
                    authSuccess.setValue(response);
                }

                @Override
                public void onError(String message) {
                    isLoading.setValue(false);
                    errorMessage.setValue(message);
                }
            });
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private boolean validate(String email, String password) {
        if (email == null || email.trim().isEmpty()) {
            errorMessage.setValue("Email is required.");
            return false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
            errorMessage.setValue("Enter a valid email address.");
            return false;
        }
        if (password == null || password.length() < 6) {
            errorMessage.setValue("Password must be at least 6 characters.");
            return false;
        }
        return true;
    }
}
