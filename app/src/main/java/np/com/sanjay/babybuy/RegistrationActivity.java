package np.com.sanjay.babybuy;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Locale;

import np.com.sanjay.babybuy.db.BabyBuyDatabase;
import np.com.sanjay.babybuy.db.user.User;
import np.com.sanjay.babybuy.db.user.UserDao;

public class RegistrationActivity extends AppCompatActivity {

    private ImageButton imageButtonBack;
    private TextInputEditText textInputEditTextEmail;
    private TextInputEditText textInputEditTextFullName;
    private TextInputEditText textInputEditTextAddress;
    private TextInputEditText textInputEditTextPassword;
    private TextInputEditText textInputEditTextConfirmPassword;
    private MaterialButton materialButtonRegister;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        imageButtonBack = findViewById(R.id.ib_back);
        textInputEditTextEmail = findViewById(R.id.tiet_registration_email);
        textInputEditTextFullName = findViewById(R.id.tiet_registration_full_name);
        textInputEditTextAddress = findViewById(R.id.tiet_registration_address);
        textInputEditTextPassword = findViewById(R.id.tiet_registration_password);
        textInputEditTextConfirmPassword = findViewById(R.id.tiet_registration_confirm_password);
        materialButtonRegister = findViewById(R.id.mb_register);
        progressDialog = new ProgressDialog(this);
        materialButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });

        imageButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startHomeScreenActivity();
            }
        });
    }

    @Override
    public void onBackPressed() {
        startHomeScreenActivity();
    }

    private void validateData() {
        String email = textInputEditTextEmail.getText().toString().trim();
        String fullName = textInputEditTextFullName.getText().toString().trim();
        String address = textInputEditTextAddress.getText().toString().trim();
        String password = textInputEditTextPassword.getText().toString().trim();
        String confirmPassword = textInputEditTextConfirmPassword.getText().toString().trim();

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            textInputEditTextEmail.setError("Please enter a valid email");
        } if (fullName.isEmpty()) {
            textInputEditTextFullName.setError("Please enter a valid name");
        } else if (address.isEmpty()) {
            textInputEditTextAddress.setError("Please enter a valid address");
        } else if (password.isEmpty()) {
            Toast.makeText(
                    RegistrationActivity.this,
                    "Please enter a password",
                    Toast.LENGTH_SHORT
            ).show();
        } else if (!password.equalsIgnoreCase(confirmPassword)) {
            Toast.makeText(
                    RegistrationActivity.this,
                    "Password didn't match",
                    Toast.LENGTH_SHORT
            ).show();
        } else {
            progressDialog.setMessage("Registering user...");
            progressDialog.hide();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    insertUserInDatabase(email, fullName, address, password);
                }
            }, 2000);
        }
    }

    private void insertUserInDatabase(
            String email,
            String fullName,
            String address,
            String password
    ) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    User user = new User();
                    user.email = email.toLowerCase(Locale.ROOT);
                    user.fullName = fullName;
                    user.address = address;
                    user.password = password;
                    BabyBuyDatabase babyBuyDatabase = BabyBuyDatabase
                            .getInstance(getApplicationContext());
                    UserDao userDao = babyBuyDatabase.getUserDao();
                    userDao.insertUser(user);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            notifyRegistrationSuccess();
                        }
                    });
                } catch (Exception exception) {
                    exception.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            notifyRegistrationFailure();
                        }
                    });
                }
            }
        }).start();
    }

    private void notifyRegistrationSuccess() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        Toast.makeText(
                RegistrationActivity.this,
                "Registration Success",
                Toast.LENGTH_LONG
        ).show();
        startLoginActivity();
    }

    private void notifyRegistrationFailure() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        Toast.makeText(
                RegistrationActivity.this,
                "Registration Failure. Please try again...",
                Toast.LENGTH_LONG
        ).show();
    }

    private void startLoginActivity() {
        Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void startHomeScreenActivity() {
        Intent intent = new Intent(RegistrationActivity.this, HomeScreenActivity.class);
        startActivity(intent);
        finish();
    }
}