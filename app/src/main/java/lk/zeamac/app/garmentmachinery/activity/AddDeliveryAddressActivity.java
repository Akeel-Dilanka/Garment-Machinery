package lk.zeamac.app.garmentmachinery.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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

import lk.zeamac.app.garmentmachinery.R;
import lk.zeamac.app.garmentmachinery.entity.userDeliveryAddressEntity;

public class AddDeliveryAddressActivity extends AppCompatActivity {

    private FirebaseFirestore fireStore;

    private FirebaseAuth firebaseAuth;
    private String selectedCountry;
    private String selectedCity;
    EditText editTextId, editTextSuburb, editTextStreetName, editTextHouseNumber, editTextLabel, editTextDeliveryInstructions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_delivery_address);

        firebaseAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();

        editTextSuburb = findViewById(R.id.suburbId);
        editTextStreetName = findViewById(R.id.streetNameId);
        editTextHouseNumber = findViewById(R.id.houseNoId);
        editTextLabel = findViewById(R.id.textLabelId);
        editTextDeliveryInstructions = findViewById(R.id.deliveryIndId);

        findViewById(R.id.addAddressBackBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentAddressBtn = new Intent(AddDeliveryAddressActivity.this,OrderInfoActivity.class);
                startActivity(intentAddressBtn);
            }
        });

        findViewById(R.id.newAddressSaveBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentNewAddressSaveBtn = new Intent(AddDeliveryAddressActivity.this,OrderInfoActivity.class);
                startActivity(intentNewAddressSaveBtn);
            }
        });


        Spinner countryspinner = findViewById(R.id.countrySpinner);
        String[] itemsCountry = {"Select Country","Sri Lanka"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, itemsCountry);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countryspinner.setAdapter(adapter);
        countryspinner.setAdapter(adapter);
        countryspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCountry = adapter.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Spinner citySpinner = findViewById(R.id.citySpinner);
        String[] itemsCity = {"Select City","Kurunegala","Colombo","Anuradhapura","Ampara","Badulla","Batticaloa","Galle","Gampaha","Hambantota","Jaffna","Kalutara","Kandy","Kegalle","Kilinochchi","Mannar","Matale","Matara","Monaragala","Mullaitivu","Nuwara Eliya","Polonnaruwa","Puttalam","Ratnapura","Trincomalee","Vavuniya"};
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, itemsCity);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(adapter1);
        citySpinner.setAdapter(adapter1);
        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCity = adapter1.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //AddProduct
        findViewById(R.id.newAddressSaveBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String suburb = editTextSuburb.getText().toString();
                String streetName = editTextStreetName.getText().toString();
                String houseNumber = editTextHouseNumber.getText().toString();
                String textLabel = editTextLabel.getText().toString();
                String deliveryInstructions = editTextDeliveryInstructions.getText().toString();

                if (selectedCountry.equals("Select")) {
                    Toast.makeText(AddDeliveryAddressActivity.this, "Please Select Country", Toast.LENGTH_LONG).show();
                }else if (selectedCity.equals("Select")) {
                    Toast.makeText(AddDeliveryAddressActivity.this, "Please Select City", Toast.LENGTH_LONG).show();
                }  else if (suburb.isEmpty()) {
                    Toast.makeText(AddDeliveryAddressActivity.this, "Please Enter Suburb", Toast.LENGTH_LONG).show();
                }  else if (streetName.isEmpty()) {
                    Toast.makeText(AddDeliveryAddressActivity.this, "Please Enter Street Name", Toast.LENGTH_LONG).show();
                } else if (houseNumber.isEmpty()) {
                    Toast.makeText(AddDeliveryAddressActivity.this, "Please Enter House Number", Toast.LENGTH_LONG).show();
                } else if (textLabel.isEmpty()) {
                    Toast.makeText(AddDeliveryAddressActivity.this, "Please Enter Text Label", Toast.LENGTH_LONG).show();
                }else if (deliveryInstructions.isEmpty()) {
                    Toast.makeText(AddDeliveryAddressActivity.this, "Please Enter Delivery Instructions", Toast.LENGTH_LONG).show();
                } else {

                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    String Id = user.getUid();
                    DocumentReference reference =    fireStore.collection("UsersAddress").document(Id);
                    userDeliveryAddressEntity address = new userDeliveryAddressEntity(Id,selectedCountry,selectedCity,suburb,streetName,houseNumber,textLabel,deliveryInstructions,null,null);
                    reference.set(address).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(AddDeliveryAddressActivity.this,"Data Add Successful",Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(AddDeliveryAddressActivity.this, DeliveryLocationActivity.class);
                            startActivity(intent);
                        }

                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(AddDeliveryAddressActivity.this,"Failed",Toast.LENGTH_LONG).show();

                        }
                    });

                }
            }

        });

        //Show Current Address
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String Id = user.getUid();
        CheckBox showAddress = findViewById(R.id.oldAddress);

        showAddress.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean checked) {
                if (checked) {

                    fireStore.collection("UsersAddress").document(Id).get().addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            DocumentSnapshot snapshot = task.getResult();

                            if(snapshot.exists()){
                                userDeliveryAddressEntity address = snapshot.toObject(userDeliveryAddressEntity.class);
                                if(address != null){
                                    countryspinner.setSelection(getIndex(countryspinner,address.getCountry()));
                                    citySpinner.setSelection(getIndex(citySpinner,address.getCity()));
                                    editTextSuburb.setText(address.getSuburb());
                                    editTextStreetName.setText(address.getStreetName());
                                    editTextHouseNumber.setText(address.getHouseNumber());
                                    editTextLabel.setText(address.getLabel());
                                    editTextDeliveryInstructions.setText(address.getDeliveryInstructions());
                                    Toast.makeText(AddDeliveryAddressActivity.this, "Get previous address", Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    });
                } else {
                    countryspinner.setSelection(0);
                    citySpinner.setSelection(0);
                    editTextSuburb.setText("");
                    editTextStreetName.setText("");
                    editTextHouseNumber.setText("");
                    editTextLabel.setText("");
                    editTextDeliveryInstructions.setText("");
                    Toast.makeText(AddDeliveryAddressActivity.this, "No saved address", Toast.LENGTH_LONG).show();
                }
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