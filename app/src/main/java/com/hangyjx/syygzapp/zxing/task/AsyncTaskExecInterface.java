package com.hangyjx.syygzapp.zxing.task;

import android.os.AsyncTask;

public interface AsyncTaskExecInterface {

  <T> void execute(AsyncTask<T, ?, ?> task, T... args);

}
