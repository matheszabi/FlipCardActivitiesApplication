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

public class Activity1 extends Activity implements IAnimationConstant {


    private FlipAnimationOut flipAnimationOut = new FlipAnimationOut();
    private FlipAnimationOutListener flipAnimationOutListener = new FlipAnimationOutListener();


    private FlipAnimationIn flipAnimationIn = new FlipAnimationIn();
    private FlipAnimationInListener flipAnimationInListener = new FlipAnimationInListener();

    private Point screenSize = new Point();
    private int centerX, centerY;
    private Camera camera;
    private View viewRoot;


    private boolean needFlipIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity1);
        // start animation
        flipAnimationOut.setAnimationListener(flipAnimationOutListener);
        flipAnimationOut.setInterpolator(new LinearInterpolator());
        flipAnimationOut.setFillAfter(true);
        flipAnimationOut.setDuration(ANIMATION_DURATION);
        // back animation:
        flipAnimationIn.setAnimationListener(flipAnimationInListener);
        flipAnimationIn.setInterpolator(new LinearInterpolator());
        flipAnimationIn.setFillAfter(true);
        flipAnimationIn.setDuration(ANIMATION_DURATION);

        needFlipIn = false;

        Display display = getWindowManager().getDefaultDisplay();
        display.getSize(screenSize);
        centerX = screenSize.x / 2; // in pixels;
        centerY = screenSize.y / 2; // in pixels;

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(needFlipIn){
            viewRoot = findViewById(android.R.id.content);

            viewRoot.clearAnimation();// Cancels any animations for this view.
            viewRoot.startAnimation(flipAnimationIn);// start our anim
        }
    }

    @Override
    protected void onPause() {
        needFlipIn = true;
        super.onPause();
    }

    // it is bind at xml
    public void btFlip1Clicked(View v) {

        viewRoot = findViewById(android.R.id.content);

        viewRoot.clearAnimation();// Cancels any animations for this view.
        viewRoot.startAnimation(flipAnimationOut);// start our anim
    }

    /**
     * It will rotate -90 degree and after that it will start the second activity
     */
    private class FlipAnimationOut extends Animation {
        private float toDegree = -90.0f;

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
            camera.translate(0f, 0f, -dz);
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
            Intent intent = new Intent(Activity1.this, Activity2.class);
            Bundle bundle = new Bundle();
            bundle.putBoolean(Activity2.BUNDLE_ROTATE_IN, true);
            intent.putExtras(bundle);
            startActivity(intent);
            // disable Android's default animation, because we have a new animation on Activity2
            overridePendingTransition(0, 0);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }
    }


    private class FlipAnimationIn extends Animation {
        private float fromDegree = -90.0f;

        @Override
        public void initialize(int width, int height, int parentWidth, int parentHeight) {
            super.initialize(width, height, parentWidth, parentHeight);
            camera = new Camera();
        }

        // @param interpolatedTime The value of the normalized time (0.0 to 1.0)
        // @param t The Transformation object to fill in with the current transforms.
        protected void applyTransformation(float interpolatedTime, Transformation t) {

            float degrees = fromDegree * (1-interpolatedTime);
            float rad = (float) (degrees * Math.PI / 180.0f);
            Matrix matrix = t.getMatrix();
            camera.save();

            float dz = (float) (centerX * Math.sin(rad));
            camera.translate(0f, 0f, -dz);
            camera.rotateY(degrees);

            camera.getMatrix(matrix);
            camera.restore();

            matrix.preTranslate(-centerX, -centerY);// M' = M * T(dx, dy)
            matrix.postTranslate(centerX, centerY); // M' = T(dx, dy) * M
        }
    }


    private class FlipAnimationInListener implements Animation.AnimationListener {
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


}
