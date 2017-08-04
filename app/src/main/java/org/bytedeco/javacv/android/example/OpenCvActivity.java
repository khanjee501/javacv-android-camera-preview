package org.bytedeco.javacv.android.example;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;

import org.bytedeco.javacv.android.example.utils.StorageHelper;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.Point;
import org.bytedeco.javacpp.opencv_core.RectVector;
import org.bytedeco.javacpp.opencv_core.Size;

import static org.bytedeco.javacpp.opencv_core.LINE_8;
import static org.bytedeco.javacpp.opencv_core.Mat;
import static org.bytedeco.javacpp.opencv_imgproc.CV_BGR2GRAY;
import static org.bytedeco.javacpp.opencv_imgproc.cvtColor;
import static org.bytedeco.javacpp.opencv_imgproc.rectangle;
import static org.bytedeco.javacpp.opencv_objdetect.CascadeClassifier;

/**
 * Created by hunghd on 4/10/17.
 */

public class OpenCvActivity extends Activity implements CvCameraPreview.CvCameraViewListener {

    private CascadeClassifier detector;
    private int rectangSize = 0;
    private CvCameraPreview cameraView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_opencv);

        cameraView = (CvCameraPreview) findViewById(R.id.camera);
        cameraView.setCvCameraViewListener(this);

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                detector = StorageHelper.loadClassifierCascade(OpenCvActivity.this, R.raw.frontalface);
                return null;
            }
        }.execute();
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        rectangSize = (int) (width * 0.32);
    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(Mat rgbaMat) {
        if (detector != null) {
            Mat grayMat = new Mat(rgbaMat.rows(), rgbaMat.cols());

            cvtColor(rgbaMat, grayMat, CV_BGR2GRAY);

            RectVector vector = new RectVector();
            detector.detectMultiScale(grayMat, vector, 1.25, 3, 1,
                    new Size(rectangSize, rectangSize),
                    new Size(4 * rectangSize, 4 * rectangSize));
            if (vector.size() == 1) {
                int x = vector.get(0).x();
                int y = vector.get(0).y();
                int w = vector.get(0).width();
                int h = vector.get(0).height();
                rectangle(rgbaMat, new Point(x, y), new Point(x + w, y + h), opencv_core.Scalar.GREEN, 2, LINE_8, 0);
            }

            grayMat.release();
        }

        return rgbaMat;
    }
}
