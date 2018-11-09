package com.romansg.yaft;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.widget.ShareDialog;

public class MainActivity extends AppCompatActivity {
    CallbackManager callbackManager;
    ProfileTracker profileTracker;
    ShareDialog shareDialog;
    LoginButton loginButton;
    TextView tvAccessToken;
    TextView tvUserId;
    Button btnCompartirImagen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Se encarga de las respuestas del inicio de sesión
        callbackManager = CallbackManager.Factory.create();

        loginButton = findViewById(R.id.login_button);
        tvAccessToken = findViewById(R.id.tvAccessToken);
        tvUserId = findViewById(R.id.tvUserId);
        btnCompartirImagen = findViewById(R.id.btnCompartirImagen);

        loginButton.setText("Continuar con Facebook");
        loginButton.setPadding(16, 16, 16, 16);
        loginButton.setElevation(8);
        loginButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        loginButton.setReadPermissions("email");

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                tvAccessToken.setText(loginResult.getAccessToken().getToken());
                tvUserId.setText(loginResult.getAccessToken().getUserId());

                String nombreCompleto = Profile.getCurrentProfile().getFirstName() + " " + Profile.getCurrentProfile().getLastName();
                Toast.makeText(MainActivity.this, "Bienvenido(a) " + nombreCompleto, Toast.LENGTH_SHORT).show();

                btnCompartirImagen.setEnabled(true);
            }

            @Override
            public void onCancel() {
                tvUserId.setText("Cancelaste el inicio");
            }

            @Override
            public void onError(FacebookException exception) {
                tvAccessToken.setText("Ocurrió un error al intentar registrarse");
            }
        });
        
        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                if (currentProfile == null) {
                    tvAccessToken.setText("");
                    tvUserId.setText("");
                    btnCompartirImagen.setEnabled(false);
                }
            }
        };

        btnCompartirImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CompartirActivity.class);
                startActivity(intent);
            }
        });

        if (sesionIniciada()) {
            btnCompartirImagen.setEnabled(true);

            tvAccessToken.setText(AccessToken.getCurrentAccessToken().getToken().toString());
            tvUserId.setText(Profile.getCurrentProfile().getName());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private boolean sesionIniciada() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null && !accessToken.isExpired();
    }
}
