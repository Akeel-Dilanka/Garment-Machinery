package lk.zeamac.app.garmentmachinery.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import lk.zeamac.app.garmentmachinery.R;

public class AboutUsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);


        findViewById(R.id.aboutUsBackBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentAboutUsBack = new Intent(AboutUsActivity.this, MainActivity.class);
                startActivity(intentAboutUsBack);
            }
        });


        findViewById(R.id.callMe).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent callIntent = new Intent("android.intent.action.DIAL");
                callIntent.setData(Uri.parse("tel:+94 716131046"));
                startActivity(callIntent);

            }
        });

        findViewById(R.id.sendEmail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                String uriText = "mailto:" + Uri.encode("smartwearexports@gmail.com") + "?subject=" +
                        Uri.encode("akeeldfernando@gmail.com ") + "&body=" + Uri.encode("Hello Garment Machinery Store!");

                Uri uri = Uri.parse(uriText);
                intent.setData(uri);
                startActivity(Intent.createChooser(intent, "Send Email"));
            }
        });

        findViewById(R.id.sendYourMessage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent msgIntent = new Intent(Intent.ACTION_SENDTO);
                msgIntent.setType("text/plain");
                msgIntent.setData(Uri.parse("smsto:+94 716131046"));
                msgIntent.putExtra(Intent.EXTRA_TEXT, "Hello Garment Machinery Store!");
                startActivity(msgIntent);

            }
        });


        findViewById(R.id.aboutUsFacebook).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoUrl("https://www.facebook.com/");
            }

            private void gotoUrl(String s) {
                Uri uri = Uri.parse(s);
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
            }
        });

        findViewById(R.id.twitter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoUrl("https://www.twitter.com/");
            }

            private void gotoUrl(String s) {
                Uri uri = Uri.parse(s);
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
            }
        });

        findViewById(R.id.youtube).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoUrl("https://www.youtube.com/");
            }

            private void gotoUrl(String s) {
                Uri uri = Uri.parse(s);
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
            }
        });
        findViewById(R.id.google).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoUrl("https://www.srilanka-places.com/places/smart-wear-exports-pvt-ltd-metiyagane");
            }

            private void gotoUrl(String s) {
                Uri uri = Uri.parse(s);
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
            }
        });

    }
}