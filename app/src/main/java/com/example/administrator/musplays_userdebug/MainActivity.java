package com.example.administrator.musplays_userdebug;

import android.Manifest;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.constraintlayout.widget.Constraints;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.security.Permission;
import java.util.ArrayList;

//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.karan.churi.PermissionManager.PermissionManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ListView lv;
    //String[] items;

    ArrayAdapter<String> adp;

    public static ArrayList<Audio> mysongs = new ArrayList<Audio>();

    public static Audio selectedAudio;


   /* PermissionManager permissionManager;*/



    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        /*ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });*/
        if(Build.VERSION.SDK_INT>=33){
            //API 33 Requires Read Media
            checkPermission(Manifest.permission.READ_MEDIA_AUDIO, 102);
        }else{
            //API 32 and below must read storage
            checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, 101);
        }
        //checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, 101);
        //checkPermission(Manifest.permission.READ_MEDIA_AUDIO, 102);

        //Read Media Store attr https://developer.android.com/training/data-storage/shared/media?hl=zh-tw#java
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION
        };

        lv = findViewById(R.id.lvplaylist);


        /*final ArrayList<File> mysongs = findsongs(Environment.getExternalStorageDirectory());

        items = new String[mysongs.size()];
        //permission
        permissionManager = new PermissionManager() {};
        permissionManager.checkAndRequestPermissions(this);*/

        Uri contentUri;
        String name;
        int duration;
        String data;
        String title;
        //Get Music Data, using (https://developer.android.com/training/data-storage/shared/media?hl=zh-tw#java and Grok3)
        try (Cursor cursor = getApplicationContext().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection, null, null)) {

            int idColum = ((Cursor) cursor).getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
            int nameColum = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
            int durationColum = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);
            int dataColum = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
            int titlecol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);

            while (cursor.moveToNext()) {

                long id = cursor.getLong(idColum);
                name = cursor.getString(nameColum);
                duration = cursor.getInt(durationColum);
                data = cursor.getString(dataColum);
                title = cursor.getString(titlecol);

                contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);

                mysongs.add(new Audio(contentUri, name, duration, data, title));
            }


        }

        //List songs in List View
        List<String> songNames = new ArrayList<>();
        for (Audio song : mysongs) {
            //songNames.add(song.getName().replace(".mp3", "").replace(".flac", "").replace(".wav",""));
            songNames.add(song.geStitle());
        }

        // Initialize the adapter ()
        adp = new ArrayAdapter<>(this, R.layout.song_layout, R.id.textView, songNames);
        lv.setAdapter(adp); // Set the adapter to the ListView

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedAudio = mysongs.get(position);
                Log.d("Test", selectedAudio.getName());
                //startActivity(new Intent(getApplicationContext(), Player.class).putExtra("pos", position).putExtra("songlist", new Audio(finalContentUri, finalName, finalDuration, finalData, finalTitle)));
                Intent intent = new Intent(getApplicationContext(), Player.class);
                intent.putExtra("pos", position);
                intent.putExtra("songlist", selectedAudio); // Pass the selected audio object
                intent.putExtra("song_name", selectedAudio.geStitle());
                //intent.putParcelableArrayListExtra("song_array", mysongs);
                if(Player.exp != null){
                    Player.exp.stop();
                    Player.exp.release();
                    Player.mediaSession.release();
                }

                startActivity(intent);

            }
        });

    }
        /*for (int i=0; i<mysongs.size();i++){

            //toast(mysongs.get(i).getName().toString());
            items[i] = mysongs.get(i).getName().replace(".mp3","").replace(".flac","");

        }*/

        //bug


// Source: https://www.youtube.com/watch?v=OJpceQqXIjY and https://developer.android.com/training/permissions/requesting?hl=zh-tw
    public void checkPermission(String permission,int requestCode){
        Log.d("Permiss","True");
        if(ActivityCompat.checkSelfPermission(this,permission)== PackageManager.PERMISSION_GRANTED){
            //Toast.makeText(this,"Pass",Toast.LENGTH_LONG).show();
        }else if(ActivityCompat.shouldShowRequestPermissionRationale(this,permission)){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("need permission").setTitle("need permission").setCancelable(false).setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission},requestCode);
                    dialogInterface.dismiss();
                }
            }).setNegativeButton("cancel",(dialogInterface, which) -> dialogInterface.dismiss());
            builder.show();
        }else
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission},requestCode);
    }

    /*public void checkPermission(String permission,int requestCode){
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission},requestCode);
    }*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //Old Method
        /*permissionManager.checkResult(requestCode,permissions,grantResults);
        ArrayList<String> granted =permissionManager.getStatus().get(0).granted;
        ArrayList<String> denied =permissionManager.getStatus().get(0).denied;*/
        if(requestCode==101||requestCode==102){
            if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"Granted, Please restart the app",Toast.LENGTH_LONG).show();
            }
            else if(!ActivityCompat.shouldShowRequestPermissionRationale(this, Arrays.toString(permissions))){
                AlertDialog.Builder builder=new AlertDialog.Builder(this);
                builder.setMessage("unavailable").setTitle("Permission Required").setCancelable(false)
                        .setNegativeButton("Cancel",(dialogInterface, which) -> dialogInterface.dismiss())
                        .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Open settings
                                Intent intent=new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",getPackageName(),null);
                                intent.setData(uri);
                                startActivity(intent);

                                dialogInterface.dismiss();
                            }
                        });
                builder.show();
            }
        }
    }




    /*public ArrayList<File> findsongs(File root){
        ArrayList<File> al = new ArrayList<File>();
        File[] files = root.listFiles();
        for (File singleFile : files){
            if (singleFile.isDirectory() && !singleFile.isHidden()){
                al.addAll(findsongs(singleFile));
            }
            else{
                if (singleFile.getName().endsWith(".mp3")||singleFile.getName().endsWith(".flac")){
                    al.add(singleFile);
                }
            }
        }
        return al;
    }*/

    public void toast(String text){
        Toast.makeText(getApplicationContext(),text,Toast.LENGTH_SHORT ).show();
    }

}

