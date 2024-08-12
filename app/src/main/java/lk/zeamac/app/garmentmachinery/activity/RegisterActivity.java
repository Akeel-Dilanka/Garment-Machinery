package lk.zeamac.app.garmentmachinery.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lk.zeamac.app.garmentmachinery.R;
import lk.zeamac.app.garmentmachinery.entity.UserEntity;

public class RegisterActivity extends AppCompatActivity {

    ImageView image;
    TextView logo, comp;
    TextInputLayout email, password;


    private FirebaseFirestore fireStore;
    private FirebaseStorage storage;
    String userId;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_GarmentMachinery_FullScreen);
        setContentView(R.layout.activity_register);


        firebaseAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();


        EditText nameEditText = findViewById(R.id.editTextTextUserName);
        EditText emailEditText = findViewById(R.id.editTextTextEmailAddress);
        EditText passwordEditText = findViewById(R.id.editTextTextPassword);
        EditText mobileEditText = findViewById(R.id.editTextTextContactNo);

        findViewById(R.id.reg_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String name = nameEditText.getText().toString();
                String mobile = mobileEditText.getText().toString();
                String id = UUID.randomUUID().toString();

                Pattern patternPhone = Pattern.compile("^((94|0)(7|8|9)(\\d{8}))$");
                Matcher matcherPhone = patternPhone.matcher(mobile);

                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(RegisterActivity.this, "Please Enter Username", Toast.LENGTH_LONG).show();
                }else if (TextUtils.isEmpty(email)) {
                    Toast.makeText(RegisterActivity.this, "Please Enter Your Email", Toast.LENGTH_LONG).show();
                }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(RegisterActivity.this, "Invalid Email", Toast.LENGTH_LONG).show();
                } else if (TextUtils.isEmpty(mobile)) {
                    Toast.makeText(RegisterActivity.this, "Please Enter Your Contact No", Toast.LENGTH_LONG).show();
                } else if (!matcherPhone.matches()) {
                    Toast.makeText(RegisterActivity.this, "Invalid Contact No", Toast.LENGTH_LONG).show();
                } else if (TextUtils.isEmpty(password)) {
                    Toast.makeText(RegisterActivity.this, "Please Enter Password", Toast.LENGTH_LONG).show();
                } else {

                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                user.sendEmailVerification();
                                userId = firebaseAuth.getCurrentUser().getUid();

                                DocumentReference documentReference = fireStore.collection("Users").document(userId);
                                UserEntity userEntity = new UserEntity(id,null,name,null,email,"user",mobile);
                                documentReference.set(userEntity).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                    }
                                });

                                Toast.makeText(RegisterActivity.this, "Please check your email & verify your email", Toast.LENGTH_LONG).show();

                            } else {
                                Toast.makeText(RegisterActivity.this, "Registration failed", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }

            }
        });


        image = findViewById(R.id.logoId);
        logo = findViewById(R.id.logo_name);
        comp = findViewById(R.id.sub_logo_name);
        email = findViewById(R.id.reg_email);
        password = findViewById(R.id.reg_password);
        Button loginBtn = findViewById(R.id.reg_btn);

        Button callSignIn = findViewById(R.id.goToSignIn);
        callSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);

                Pair[] pairs = new Pair[7];
                pairs[0] = new Pair<View, String>(image, "logo_image");
                pairs[1] = new Pair<View, String>(logo, "logo_text");
                pairs[2] = new Pair<View, String>(comp, "logo_sub_text");
                pairs[3] = new Pair<View, String>(email, "user_email");
                pairs[4] = new Pair<View, String>(password, "user_password");
                pairs[5] = new Pair<View, String>(loginBtn, "button_trans");
                pairs[6] = new Pair<View, String>(callSignIn, "signIn_signUp_trans");

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(RegisterActivity.this, pairs);
                    startActivity(intent, activityOptions.toBundle());
                } else {
                    startActivity(intent);
                }

            }
        });

    }
}