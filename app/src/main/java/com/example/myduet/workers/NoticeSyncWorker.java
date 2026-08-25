package com.example.myduet.workers;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.example.myduet.repositories.NoticeRepository;

public class NoticeSyncWorker extends Worker {

    public NoticeSyncWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        NoticeRepository repository = new NoticeRepository(getApplicationContext());
        boolean success = repository.syncNoticesSync();
        if (success) {
            return Result.success();
        } else {
            return Result.retry();
        }
    }
}
