package com.example.camera2apisample;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import java.io.File;
import java.util.ArrayList;

public class CustomGalleryActivity extends AppCompatActivity {

    ArrayList<String> f = new ArrayList<>();
    File[] listFile;
    private String folderName = "MyPhotoDir";
    ViewPager mViewPager;
    ViewPagerAdapter mViewPagerAdapter;
    View currentView;
    int currentPosition;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        getFromSdcard();
        mViewPager = findViewById(R.id.viewPagerMain);
        mViewPagerAdapter = new ViewPagerAdapter(this, f);
        mViewPager.setAdapter(mViewPagerAdapter);
/*        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
               @Override
               public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                   //Log.e("CustomGalleryActivity", "onPageScrolled");
               }

               @Override
               public void onPageSelected(int position) {
                   Log.e("CustomGalleryActivity", "onPageSelected position=" + position);
                   currentPosition = position;
               }

               @Override
               public void onPageScrollStateChanged(int state) {
                   //Log.e("CustomGalleryActivity", "onPageScrollStateChanged");
               }
        });
*/
        findViewById(R.id.upload).setOnClickListener(v -> {
//            currentView = (View) mViewPager.getChildAt(currentPosition);
            currentView = (View) mViewPager.findViewWithTag("childView" + mViewPager.getCurrentItem());
            if (currentView == null) {
                Log.e("Click", "currentView is null");
                Log.e("Click", "child count is " + mViewPager.getChildCount());
            } else {
                ImageView iv = currentView.findViewById(R.id.imageViewMain);
                Bitmap bmp = ((BitmapDrawable)iv.getDrawable()).getBitmap();
//                new PostAsyncHttpRequest(this).execute(new PostAsyncHttpRequest.Param("https://0417worksmk.tech/", bmp));
//                new PostAsyncHttpRequest(this).execute(new PostAsyncHttpRequest.Param("https://0417worksmk.tech/webapp/cam2api/", bmp));
                new PostAsyncHttpRequest(this).execute(new PostAsyncHttpRequest.Param("https://0417worksmk.tech/webapp/cam2api/upload", bmp));
            }
        });
    }

    public void getFromSdcard() {
        File file = new File(getExternalFilesDir(folderName), "/");
        if (file.isDirectory()) {
            listFile = file.listFiles();
            for (int i = 0; i < listFile.length; i++) {
                f.add(listFile[i].getAbsolutePath());
            }
        }
    }

}
