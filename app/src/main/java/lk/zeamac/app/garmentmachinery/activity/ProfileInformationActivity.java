package lk.zeamac.app.garmentmachinery.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import lk.zeamac.app.garmentmachinery.R;
import lk.zeamac.app.garmentmachinery.entity.UserEntity;
import lk.zeamac.app.garmentmachinery.fragment.AccountFragment;

public class ProfileInformationActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private EditText nameEditText, fullNameEditText, emailEditText, contactNoEditText;
    private Spinner nameTitleSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_information);

        findViewById(R.id.profilesBackBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileInformationActivity.this,MainActivity.class));
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        nameEditText = findViewById(R.id.userName);
        fullNameEditText = findViewById(R.id.fullName);
        emailEditText = findViewById(R.id.email);
        contactNoEditText = findViewById(R.id.contact);
        nameTitleSpinner = findViewById(R.id.nameTitleSpinner);

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            firestore.collection("Users").document(currentUser.getUid()).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        UserEntity userEntity = document.toObject(UserEntity.class);
                        if (userEntity != null) {
                            nameTitleSpinner.setSelection(getIndex(nameTitleSpinner, userEntity.getTitle()));
                            nameEditText.setText(userEntity.getName());
                            fullNameEditText.setText(userEntity.getFullName());
                            emailEditText.setText(userEntity.getEmail());
                            contactNoEditText.setText(userEntity.getMobile());
                        }
                    } else {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) {
                            nameEditText.setText(user.getDisplayName());
                            emailEditText.setText(user.getEmail());
                        }
                    }
                }
            });
        }

        Spinner nameTitleSpinner = findViewById(R.id.nameTitleSpinner);
        String[] itemsNameTitle = {"Select Name Title", "Mr. ", "Mrs. ", "Miss. "};
        ArrayAdapter<String> adapterNameTitle = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, itemsNameTitle);
        adapterNameTitle.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        nameTitleSpinner.setAdapter(adapterNameTitle);

        nameEditText.setEnabled(false);
        fullNameEditText.setEnabled(false);
        emailEditText.setEnabled(false);
        contactNoEditText.setEnabled(false);
        nameTitleSpinner.setEnabled(false);

        findViewById(R.id.editDetailsBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameEditText.setEnabled(true);
                fullNameEditText.setEnabled(true);
                emailEditText.setEnabled(true);
                contactNoEditText.setEnabled(true);
                nameTitleSpinner.setEnabled(true);

            }
        });


        findViewById(R.id.saveDetailsBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = nameEditText.getText().toString();
                String newFullName = fullNameEditText.getText().toString();
                String newEmail = emailEditText.getText().toString();
                String newContactNo = contactNoEditText.getText().toString();
                String newNameTitle = nameTitleSpinner.getSelectedItem().toString();

                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                if (currentUser != null) {
                    DocumentReference userDocument = firestore.collection("Users").document(currentUser.getUid());

                    userDocument.get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Map<String, Object> updates = new HashMap<>();
                                updates.put("name", newName);
                                updates.put("fullName", newFullName);
                                updates.put("email", newEmail);
                                updates.put("mobile", newContactNo);
                                updates.put("title", newNameTitle);

                                userDocument.update(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(ProfileInformationActivity.this, "Details updated successfully", Toast.LENGTH_LONG).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(ProfileInformationActivity.this, "Failed to update details", Toast.LENGTH_LONG).show();
                                    }
                                });
                            } else {

                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                String userId = user.getUid();
                                if (user != null) {
                                    DocumentReference documentReference = firestore.collection("Users").document(userId);
                                    UserEntity userEntity = new UserEntity(userId, newNameTitle, newName, newFullName, newEmail, "user", newContactNo);
                                    documentReference.set(userEntity).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(ProfileInformationActivity.this, "Details updated successfully", Toast.LENGTH_LONG).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(ProfileInformationActivity.this, "Failed to update details", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }
                        }
                    });
                }
                nameEditText.setEnabled(false);
                fullNameEditText.setEnabled(false);
                emailEditText.setEnabled(false);
                contactNoEditText.setEnabled(false);
                nameTitleSpinner.setEnabled(false);
            }
        });


    }

    private int getIndex(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(value)) {
                return i;
            }
        }
        return 0;
    }


}