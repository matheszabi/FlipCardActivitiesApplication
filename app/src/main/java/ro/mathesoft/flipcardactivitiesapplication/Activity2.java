package ro.mathesoft.flipcardactivitiesapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;

public class Activity2 extends Activity implements IAnimationConstant {

    public static final int Acitvity1RequestCode = 123456;

    public static final String BUNDLE_ROTATE_IN = "BUNDLE_ROTATE_IN";
    private boolean shouldRotateIn = false;
    private Point screenSize = new Point();
    private int centerX, centerY;
    private Camera camera;
    private View viewRoot;

    private MyApplication myApplication;
    private FlipAnimationIn flipAnimationIn = new FlipAnimationIn();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity2);

        myApplication = (MyApplication) getApplication();

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if (extras.containsKey(BUNDLE_ROTATE_IN)) {
            shouldRotateIn = extras.getBoolean(BUNDLE_ROTATE_IN);
            if (shouldRotateIn) {
                flipAnimationIn.setInterpolator(new LinearInterpolator());
                flipAnimationIn.setFillAfter(true);
                flipAnimationIn.setDuration(ANIMATION_DURATION);
                flipAnimationIn.setAnimationListener(new AnimationFlipInListener() );

                Display display = getWindowManager().getDefaultDisplay();
                display.getSize(screenSize);
                centerX = screenSize.x / 2; // in pixels;
                centerY = screenSize.y / 2; // in pixels;
            }
        }
    }

    @Override
    protected void onResume() {

        if (shouldRotateIn) {// animateIn this activity
            viewRoot = findViewById(android.R.id.content);
            viewRoot.clearAnimation();// Cancels any animations for this view.
            viewRoot.startAnimation(flipAnimationIn);// start our anim
        }

        super.onResume();
    }

    private class AnimationFlipInListener implements Animation.AnimationListener
    {

        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            //clear, start whatever you want
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }
    }


    private class FlipAnimationIn extends Animation {
        private float fromDegree = 90.0f;
        //private float toDegree = 0.0f;

        @Override
        public void initialize(int width, int height, int parentWidth, int parentHeight) {
            super.initialize(width, height, parentWidth, parentHeight);
            camera = new Camera();
        }

        // @param interpolatedTime The value of the normalized time (0.0 to 1.0)
        // @param t The Transformation object to fill in with the current transforms.
        protected void applyTransformation(float interpolatedTime, Transformation t) {

            float degrees = fromDegree * (1.0f - interpolatedTime);
            float rad = (float) (degrees * Math.PI / 180.0f);
            //

            Matrix matrix = t.getMatrix();
            camera.save();

            float dz = (float) (Math.sin(rad) * centerX);
            camera.translate(0, 0, dz);
            camera.rotateY(degrees);

            camera.getMatrix(matrix);
            camera.restore();

            matrix.preTranslate(-centerX, -centerY);// M' = M * T(dx, dy)
            matrix.postTranslate(centerX, centerY); // M' = T(dx, dy) * M
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        btFlip2Clicked(null);
    }

    // it is bind at xml
    public void btFlip2Clicked(View v) {
        //Toast.makeText(this, "Flip2", Toast.LENGTH_SHORT).show();

        viewRoot = findViewById(android.R.id.content);
        viewRoot.clearAnimation();// Cancels any animations for this view.

        FlipAnimationOut flipAnimationOut = new FlipAnimationOut();
        flipAnimationOut.setAnimationListener(new FlipAnimationOutListener());
        flipAnimationOut.setInterpolator(new LinearInterpolator());
        flipAnimationOut.setFillAfter(true);
        flipAnimationOut.setDuration(ANIMATION_DURATION);

        Display display = getWindowManager().getDefaultDisplay();
        display.getSize(screenSize);
        centerX = screenSize.x / 2; // in pixels;
        centerY = screenSize.y / 2; // in pixels;
        viewRoot.startAnimation(flipAnimationOut);// start our anim
    }

    private class FlipAnimationOut extends Animation {

        private float toDegree = 90.0f;

        @Override
        public void initialize(int width, int height, int parentWidth, int parentHeight) {
            super.initialize(width, height, parentWidth, parentHeight);
            camera = new Camera();
        }

        // @param interpolatedTime The value of the normalized time (0.0 to 1.0)
        // @param t The Transformation object to fill in with the current transforms.
        protected void applyTransformation(float interpolatedTime, Transformation t) {

            float degrees = toDegree * interpolatedTime;
            float rad = (float) (degrees * Math.PI / 180.0f);
            Matrix matrix = t.getMatrix();
            camera.save();

            float dz = (float) (centerX * Math.sin(rad));
            camera.translate(0f, 0f, dz);
            camera.rotateY(degrees);

            camera.getMatrix(matrix);
            camera.restore();

            matrix.preTranslate(-centerX, -centerY);// M' = M * T(dx, dy)
            matrix.postTranslate(centerX, centerY); // M' = T(dx, dy) * M
        }
    }

    /**
     * When the animation is finished start the activity
     */
    private class FlipAnimationOutListener implements Animation.AnimationListener {
        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            finish();
            // disable Android's default animation, because we have a new animation on Activity2
            overridePendingTransition(0, 0);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }
    }
}
