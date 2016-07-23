package com.gyh.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private int mTotalProgress;
    private int mCurrentProgress;
    private RoundProgressBar rpBar01, rpBar02 ,rpBar03, rpBar04;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initVariable();
        viewInit();
        new Thread(new ProgressRunable()).start();
    }

    private void initVariable() {
        mTotalProgress = 0;
        mCurrentProgress = 10;
    }

    private void viewInit() {
//        mTasksView = (TasksCompletedView) findViewById(R.id.tasks_view);

        rpBar01 = (RoundProgressBar) findViewById(R.id.roundProgressBar01_id);
//        rpBar02 = (RoundProgressBar) findViewById(R.id.roundProgressBar02_id);
//        rpBar03 = (RoundProgressBar) findViewById(R.id.roundProgressBar03_id);
//        rpBar04 = (RoundProgressBar) findViewById(R.id.roundProgressBar04_id);
        rpBar01.setMax(10);
    }

    class ProgressRunable implements Runnable {

        @Override
        public void run() {
            while (mCurrentProgress > mTotalProgress) {
                    --mCurrentProgress;

//                mTasksView.setProgress(mCurrentProgress);

                rpBar01.setProgress(mCurrentProgress);
//                rpBar02.setProgress(mCurrentProgress);
//                rpBar03.setProgress(mCurrentProgress);
//                rpBar04.setProgress(mCurrentProgress);

                try {
                    Thread.sleep(1000);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            MainActivity.this.finish();
        }

    }
}
