package com.example.logicandsolutions;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.logicandsolutions.DeveloperInfo;
import com.example.logicandsolutions.DonationBox;
import com.example.logicandsolutions.Homeactivity;
import com.example.logicandsolutions.Loginactivity;
import com.example.logicandsolutions.Profileactivity;
import com.example.logicandsolutions.R;
import com.google.firebase.auth.FirebaseAuth;

public class GalleryActivity extends AppCompatActivity {

    GridView galleryGrid;
    ImageView homeimg, profile, developer, donation, logouticon;

    int[] images = {
            R.drawable.galleryphoto1,
            R.drawable.galleryphoto2,
            R.drawable.galleryphoto3,
            R.drawable.galleryphoto4,
            R.drawable.galleryphoto5,
            R.drawable.galleryphoto6,
            R.drawable.galleryphoto7,
            R.drawable.galleryphoto8,
            R.drawable.galleryphoto9,
            R.drawable.galleryphoto10,
            R.drawable.galleryphoto11,
            R.drawable.galleryphoto12,
            R.drawable.galleryphoto13,
            R.drawable.galleryphoto14,
            R.drawable.galleryphoto15,
            R.drawable.galleryphoto16
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        galleryGrid = findViewById(R.id.galleryGrid);
        galleryGrid.setAdapter(new GalleryAdapter());

        galleryGrid.setOnItemClickListener((parent, view, position, id) ->
                showFullImageDialog(images[position])
        );

        // 🔻 Footer nav setup
        homeimg = findViewById(R.id.homeimg);
        profile = findViewById(R.id.profile);
        developer = findViewById(R.id.developer);
        donation = findViewById(R.id.donationbtnnav);
        logouticon = findViewById(R.id.logout);

        homeimg.setOnClickListener(v -> navigateTo(Homeactivity.class));
        profile.setOnClickListener(v -> navigateTo(Profileactivity.class));
        developer.setOnClickListener(v -> navigateTo(DeveloperInfo.class));
        donation.setOnClickListener(v -> navigateTo(DonationBox.class));
        logouticon.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent i = new Intent(GalleryActivity.this, Loginactivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            finish();
        });
    }

    private void navigateTo(Class<?> activityClass) {
        Intent i = new Intent(GalleryActivity.this, activityClass);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        finish();
    }

    private void showFullImageDialog(int imageResId) {
        Dialog dialog = new Dialog(GalleryActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ImageView fullImage = dialog.findViewById(R.id.fullImageView);
        fullImage.setImageResource(imageResId);
        dialog.show();
    }

    class GalleryAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return images.length;
        }

        @Override
        public Object getItem(int position) {
            return images[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.gallery_item, parent, false);
            }
            ImageView img = convertView.findViewById(R.id.galleryImage);
            img.setImageResource(images[position]);
            return convertView;
        }
    }
}
