package lk.zeamac.app.garmentmachinery.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import lk.zeamac.app.garmentmachinery.R;
import lk.zeamac.app.garmentmachinery.activity.LoginActivity;
import lk.zeamac.app.garmentmachinery.activity.MainActivity;
import lk.zeamac.app.garmentmachinery.activity.OrderDetailsActivity;
import lk.zeamac.app.garmentmachinery.activity.ProfileInformationActivity;


public class AccountFragment extends Fragment {

    GoogleSignInClient googleClient;
    GoogleSignInOptions googleOptions;
    AlertDialog.Builder builder;
    private FirebaseFirestore fireStore;
    private FirebaseAuth firebaseAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View fragment, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(fragment, savedInstanceState);

        googleOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleClient = GoogleSignIn.getClient(getActivity(), googleOptions);

        fireStore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        builder = new AlertDialog.Builder(getContext());

        fragment.findViewById(R.id.deleteAccountBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                builder.setTitle("Alert").setMessage("Do you want to delete Your Account?").setCancelable(true).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                        fireStore.collection("Users").document(userId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                if (user != null) {
                                    user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {

                                                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(LoginActivity.PREFS_NAME, 0);
                                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                                editor.putBoolean("hasLoggedIn", false);
                                                editor.commit();

                                                startActivity(new Intent(getActivity(), LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));

                                                Toast.makeText(getActivity(), "Account deleted from FireStore & Authentication", Toast.LENGTH_LONG).show();
                                            } else {
                                                Toast.makeText(getActivity(), "Error deleting account from Authentication", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(), "Error deleting account from FireStore", Toast.LENGTH_LONG).show();
                            }
                        });


                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();


            }
        });


        fragment.findViewById(R.id.accountLogoutBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        FirebaseAuth.getInstance().signOut();

                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(LoginActivity.PREFS_NAME, 0);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("hasLoggedIn", false);
                        editor.commit();

                        startActivity(new Intent(getActivity(), LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));

                    }
                });
            }
        });

        fragment.findViewById(R.id.whatsapp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String phoneNumber = "+94716131046";
                    String message = "Hello Garment Machinery Store!";
                    String encodedMessage = URLEncoder.encode(message, "UTF-8");
                    Uri uri = Uri.parse("https://api.whatsapp.com/send?phone=" + phoneNumber + "&text=" + encodedMessage);
                    Intent intentWhatsapp = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intentWhatsapp);

                } catch (UnsupportedEncodingException e) {
                    Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                } catch (android.content.ActivityNotFoundException e) {
                    gotoUrl("https://www.whatsapp.com/");
                }
            }

            private void gotoUrl(String s) {
                Uri uri = Uri.parse(s);
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
            }
        });


        fragment.findViewById(R.id.orderDetailsBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentOrderDetailsBtn = new Intent(getActivity(), OrderDetailsActivity.class);
                startActivity(intentOrderDetailsBtn);
            }
        });

        fragment.findViewById(R.id.imageViewProfileInformation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentProfileInformation = new Intent(getActivity(), ProfileInformationActivity.class);
                startActivity(intentProfileInformation);
            }
        });

    }
}