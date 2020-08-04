package com.example.user.interactive_learning_tehnology_app.Main;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.user.interactive_learning_tehnology_app.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    GoogleSignInOptions signInOptions;
    GoogleSignInClient client;
    DriveServiceHelper driveServiceHelper;
    Button btnUpload;
    Button btnChooseFile;
    private static final int READ_REQUEST_CODE = 42;
    private List<String> filepa = new ArrayList<>();
    private List<String> fileName = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnUpload= findViewById(R.id.btnUpload);
        btnUpload.setOnClickListener(this);
        btnChooseFile =findViewById(R.id.btnChooseFile);
        btnChooseFile.setOnClickListener(this);
        requestSignIn();
    }

    public void requestSignIn(){
        signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(new Scope(DriveScopes.DRIVE_FILE),
                        new Scope(DriveScopes.DRIVE_APPDATA),
                        new Scope(DriveScopes.DRIVE))
                .build();

        client = GoogleSignIn.getClient(this,signInOptions);
//        String account =signInOptions.getAccount().toString();
        String account =client.toString();
        Log.e("account", account );
        startActivityForResult(client.getSignInIntent(),400);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK  && requestCode == 400) {
            String account = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);

            Log.e("resultCode","resultCode="+resultCode);
            Log.e("requestCode","requestCode="+requestCode);
            Log.e("account", ""+account );
            handleSignInIntent(data);
        }

        if (requestCode == READ_REQUEST_CODE && resultCode == RESULT_OK){
            Uri selectedCsv =null;
            ClipData clipData = null;

            if (resultCode == Activity.RESULT_OK && data != null){
                selectedCsv = data.getData();
                if (clipData != null){
                    catchFileInApp(selectedCsv,clipData);
                }
                else if (Build.VERSION.SDK_INT>=16 && clipData== null){
                    clipData = data.getClipData();
                    catchFileInApp(selectedCsv,clipData);
                }
            }
        }

    }

    private void handleSignInIntent(Intent data) {
        Log.d("data", "data:"+data);
        GoogleSignIn.getSignedInAccountFromIntent(data)
                .addOnSuccessListener(googleSignInAccount -> {
                    GoogleAccountCredential credential = GoogleAccountCredential
//                            .usingOAuth2(MainActivity.this, DriveScopes.a);

                            .usingOAuth2(this, Collections.singleton(DriveScopes.DRIVE_FILE));

                    credential.setSelectedAccount(googleSignInAccount.getAccount());

                    String account = credential.getSelectedAccountName();
                    Log.e("account", "account =" + account);

//                    if (account.equals(mail)){
//                        Log.e("success","success");
//                        drive(credential);
//                    }else {
//                        client.signOut();
//                        credential = null;
//                        requestSignIn();
//                    }

                    drive(credential);
                })
                .addOnFailureListener(e -> e.printStackTrace());
    }

    private void drive(GoogleAccountCredential credential) {
        Drive googleDriveService =
                new Drive.Builder(
                        AndroidHttp.newCompatibleTransport(),
                        new GsonFactory(),
                        credential)
                        .setApplicationName("AppName")
                        .build();

        driveServiceHelper = new DriveServiceHelper(googleDriveService);
        Log.e("aaa", "handleSignInIntent: " +driveServiceHelper.toString() );

//        mnewDrive = new CompaeisonData(googleDriveService);
//        createFolder = new CreateFolder(googleDriveService);
//        searchFIle = new SearchFIle(googleDriveService);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnUpload:
                UploadFile();
                break;
            case R.id.btnChooseFile:
                chooseFile();
                break;
        }
    }

    private void UploadFile() {
//        String Path ="/storage/emulated/0/使用者帳號管理 - 使用者帳號管理.csv";
//        String Path = "/storage/emulated/0/使用.csv";
//        String Name = "使用者帳號管理 - 使用者帳號管理.csv";
//        String Name = "使用.csv";
//        driveServiceHelper.createFile(Path,Name);

        int j = filepa.size();
        if (j == 0){
            Toast.makeText(this, "請選取資料", Toast.LENGTH_SHORT).show();
        }else{
            for (int i = 0; i < j; i++) {
//        driveServiceHelper.fileName(fileName.get(i));
                driveServiceHelper.createFile(filepa.get(i), fileName.get(i))
                        .addOnSuccessListener(new OnSuccessListener<String>() {
                            @Override
                            public void onSuccess(String s) {
//                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Uploaded successfully", Toast.LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
//                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Check your google api key", Toast.LENGTH_LONG).show();
                        Log.d("aaa", "" + e);
                    }
                });
            }
        }
        filepa.clear();
        fileName.clear();

    }

    private void chooseFile(){
//            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//            intent.addCategory(Intent.CATEGORY_OPENABLE);
////            intent.setType("image/*");
//            startActivityForResult(intent, READ_REQUEST_CODE);
        String mimeType = "text/comma-separated-values";
//        String mimeType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

//        String mimeType = "image/jpeg";
        PackageManager packageManager = MainActivity.this.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(mimeType);
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (list.size() > 0) {
            Intent picker = new Intent(Intent.ACTION_GET_CONTENT);
            picker.setType(mimeType);
            picker.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
//            picker.addCategory(Intent.CATEGORY_OPENABLE);
            picker.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            Intent destIntent = Intent.createChooser(picker, "選取csv檔案");
            startActivityForResult(destIntent, READ_REQUEST_CODE);

        } else {
            Log.d("error", "無可用的Activity");
        }

    }

    private void catchFileInApp(Uri selectedCsv, ClipData clipData){
        if (selectedCsv != null){
            String path = FileUtil.getFileAbsolutePath(this,selectedCsv);
//            Log.d("path1",""+path);
//            filePath = path;
            filepa.add(path);
            String name = FileUtil.fileName(path);
            fileName.add(name);
            Log.d("asd",fileName.get(0));


            //設定檔案名稱
        }else  if (clipData != null){
            int count = clipData.getItemCount();
            if (count > 0){
                Uri[] uris = new Uri[count];
                String[] paths = new String[count];
                String[] names = new String[count];
                for (int i=0;i<count;i++){
                    uris[i] = clipData.getItemAt(i).getUri();
                    paths[i] = FileUtil.getFileAbsolutePath(this,uris[i]);
                    names[i] = FileUtil.fileName(paths[i]);
                    Log.d("paee",paths[i]);
                    filepa.add(paths[i]);
                    fileName.add(names[i]);
                }
                Log.d("paee",fileName.get(1));
                Log.d("paee",fileName.get(0));
            }
        }
    }
}
