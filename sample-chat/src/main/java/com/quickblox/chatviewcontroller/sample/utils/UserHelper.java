package com.quickblox.chatviewcontroller.sample.utils;


import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.quickblox.content.QBContent;
import com.quickblox.content.model.QBFile;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBProgressCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.io.IOUtils;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class UserHelper {
    private static final String TAG = UserHelper.class.getSimpleName();

    private final String fileNameOne = "woman";
    private final String fileNameTwo = "poker_face";

    public static final String PREFIX = "tempFile";
    public static final String SUFFIX = ".jpg";


    //    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static File createFileFromIStream(InputStream in) throws IOException {


        final File tempFile = File.createTempFile(PREFIX, SUFFIX);
        tempFile.deleteOnExit();

        FileOutputStream out = new FileOutputStream(tempFile);
        IOUtils.copy(in, out);

        return tempFile;
    }


    public void createFileFromRaw(Activity activity) throws IOException {

        InputStream stream = activity.getResources().openRawResource(
                activity.getResources().getIdentifier(fileNameTwo,
                        "raw", activity.getPackageName()));

        File avatar = createFileFromIStream(stream);

        if (avatar.exists()) {
            Log.e(TAG, "Valid file");
            updateProfileAvatar(avatar);
        } else {
            Log.e(TAG, "InValid file");
        }
    }

    public void updateProfileAvatar(File avatar) {

        Boolean fileIsPublic = true;

        QBContent.uploadFileTask(avatar, fileIsPublic, null, new QBProgressCallback() {

            @Override
            public void onProgressUpdate(int progress) {
                Log.d(TAG, "onProgressUpdate- " + progress);
            }
        }).performAsync(new QBEntityCallback<QBFile>() {
            @Override
            public void onSuccess(QBFile qbFile, Bundle params) {

                int uploadedFileID = qbFile.getId();

                // Connect image to user
                QBUser user = new QBUser();
                user.setId(Consts.userOneID);
                user.setFileId(uploadedFileID);

                QBUsers.updateUser(user).performAsync(new QBEntityCallback<QBUser>() {
                    @Override
                    public void onSuccess(QBUser user, Bundle args) {
                        Log.d(TAG, "updateUser onSuccess " + user);
                    }

                    @Override
                    public void onError(QBResponseException errors) {
                        Log.d(TAG, "updateUser onError " + errors.getMessage());
                    }
                });
            }

            @Override
            public void onError(QBResponseException errors) {
                Log.d(TAG, "uploadFileTask onError " + errors.getMessage());
            }
        });
    }

    public static void getAvatar(QBUser user) {

        int userProfilePictureID = user.getFileId();

        QBContent.downloadFileById(userProfilePictureID, new QBProgressCallback() {
            @Override
            public void onProgressUpdate(int progress) {
                Log.d(TAG, "downloadFileById onProgressUpdate- " + progress);
            }
        }).performAsync(new QBEntityCallback<InputStream>() {
            @Override
            public void onSuccess(InputStream inputStream, Bundle params) {

            }

            @Override
            public void onError(QBResponseException errors) {

            }
        });
    }

}
