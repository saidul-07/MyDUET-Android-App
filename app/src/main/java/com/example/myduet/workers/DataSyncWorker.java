package com.example.myduet.workers;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.example.myduet.utils.DataUpdateManager;
import java.util.concurrent.CountDownLatch;

public class DataSyncWorker extends Worker {

    public DataSyncWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        CountDownLatch latch = new CountDownLatch(1);
        final boolean[] success = {false};

        DataUpdateManager.getInstance(getApplicationContext()).checkForUpdates(new DataUpdateManager.UpdateCallback() {
            @Override
            public void onProgress(String status) {
                // Background update manager logging
            }

            @Override
            public void onSuccess(boolean updated) {
                success[0] = true;
                latch.countDown();
            }

            @Override
            public void onFailure(String error) {
                success[0] = false;
                latch.countDown();
            }
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            return Result.retry();
        }

        if (success[0]) {
            return Result.success();
        } else {
            return Result.retry();
        }
    }
}
