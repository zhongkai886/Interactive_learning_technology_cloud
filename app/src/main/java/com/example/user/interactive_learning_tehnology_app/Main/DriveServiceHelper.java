package com.example.user.interactive_learning_tehnology_app.Main;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DriveServiceHelper {
    private final Executor mExecutor = Executors.newSingleThreadExecutor();
    //    private Drive mDriveService;
    protected com.google.api.services.drive.Drive mDriveService;
//    private String name;

    public DriveServiceHelper(Drive driveService) {
        this.mDriveService = driveService;
    }

    public Task<String> createFile(String filePath, String name){
        return Tasks.call(mExecutor,() ->{
            Log.e("file",filePath+","+name );
            File fileMetaData = new File();
            fileMetaData.setName(name);

//            fileMetaData.setParents((Collections.singletonList(folderId)));

            java.io.File file = new java.io.File(filePath);

            FileContent mediaContent = new FileContent("application/octet-stream",file);

            File myFile = null;
            try{
                myFile = mDriveService.files().create(fileMetaData,mediaContent)
                        .setFields("id")
                        .execute();

                Log.e("File" ,"Flie ID = " +myFile.getId());
            }catch (Exception e){
                e.printStackTrace();
                Log.e("error",e.getMessage() );
            }

            if (myFile == null){
                throw new IOException("Null result when request file creation");
            }

            return myFile.getId();

        });
    }
}
