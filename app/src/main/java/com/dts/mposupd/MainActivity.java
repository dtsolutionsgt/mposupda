package com.dts.mposupd;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.Settings;
import android.view.Gravity;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

public class MainActivity extends Activity {

    private FirebaseStorage storage;
    private StorageReference storageReference, apkref;

    private extWaitDlg waitdlg;

    private PackageInstaller packageInstaller;

    private Uri localfile,fileUri;
    private int callback=0;
    private String fname;

    private String packagename="com.dtsgt.mpos";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());

            storage = FirebaseStorage.getInstance();
            storageReference = storage.getReference();

            waitdlg= new extWaitDlg();

            callback=0;

            Handler mtimer = new Handler();
            Runnable mrunner=new Runnable() {
                @Override
                public void run() {
                    grantPermissions();
                }
            };
            mtimer.postDelayed(mrunner,50);

        } catch (Exception e) {
            toastlong(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }

    }

    //region Main

    private void startApplication() {
        try {
            File directory = getExternalMediaDirs()[0];
            directory.mkdirs();

            fname="mpos.apk";
            downloadVersion();
        } catch (Exception e) {}
    }

    private void downloadVersion() {
        String fbname,fname;
        File file;

        try {
            fname=getStorage()+"/mpos.apk";
            fbname="mpos.apk";

            apkref = storageReference.child(fbname);
            file=new File(fname);
            Uri localfile = Uri.fromFile(file);

            waitdlg.buildDialog(this,"Descargando MPos.apk . . .","Ocultar");
            waitdlg.setWidth(800);
            waitdlg.show();

            apkref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    String ss=uri.toString();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });

            apkref.getFile(localfile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    try {
                        waitdlg.wdhandle.dismiss();
                        if (file.exists()) {
                            updateFile();
                        } else {
                            toastlong("No se logró descargar mpos.apk.");
                        }
                    } catch (Exception e) {}
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    try {
                        waitdlg.wdhandle.dismiss();
                    } catch (Exception e) {}
                    toastlong("Download error : \n"+exception.getMessage());finish();
                }
            });
        } catch (Exception e) {
            toastlong(new Object(){}.getClass().getEnclosingMethod().getName()+" . "+e.getMessage());
        }
    }

    private void updateFile() {
        try {
            String ffname=getStorage()+"/mpos.apk";
            File fapk = new File(ffname);

            if (!getPackageManager().canRequestPackageInstalls()) {
                startActivityForResult(new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).setData(Uri.parse(String.format("package:%s", getPackageName()))), 1234);
            } else {
                Uri uri = FileProvider.getUriForFile(this,getPackageName()+".provider",fapk);

                Intent install = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                install.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                install.setDataAndType(uri, getMimeType(uri));
                startActivity(install);
            }

            finish();
        } catch (Exception e) {
            String ss=e.getMessage();
            toastlong("Mpos actualizador \n"+ss);finish();
        }
    }

    //endregion

    //region Aux

    private void grantPermissions() {
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    startApplication();
                } else {
                    //ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    startApplication();
                }
            } else {
                startApplication();
            }
        } catch (Exception e) {}
    }

    public String getStorage() {
        String sd= getApplicationContext().getExternalFilesDir("").getAbsolutePath();

        try {
            File directory = new File(sd);
            directory.mkdirs();
        } catch (Exception e) {
            String ss=e.getMessage();
        }

        return sd;
    }

    protected void toastlong(String msg) {
        Toast toast= Toast.makeText(getApplicationContext(),msg, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public String getMimeType(Uri uri) {
        String mimeType = null;
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            ContentResolver cr = this.getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.toLowerCase());
        }


        return mimeType;
    }

    //endregion

    //region Activity Events

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        try {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ) {
                startApplication();
            } else {
                Toast.makeText(this, "Permission not granted.", Toast.LENGTH_LONG).show();
                super.finish();
            }
        } catch (Exception e){}
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (callback==1) {
            callback=0;
            downloadVersion();return;
        }
    }

    //endregion


}