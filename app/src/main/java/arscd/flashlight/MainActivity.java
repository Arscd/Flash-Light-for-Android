package arscd.flashlight;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;


import java.io.IOException;


public class MainActivity extends Activity {

    private ImageView buttonImage;
    private AnimationDrawable animation;

    private ImageView saundImage;
    private Camera camera;
    private Parameters params;
    private MediaPlayer mp;
    private boolean isFlashOn;
    private boolean hasFlash = false;
    private Boolean statusSaund = true;

    private SurfaceTexture surfaceTexture = new SurfaceTexture(0);

    public void playAnim(View v) {
        Log.d("animButton", "Click");
        buttonImage.setBackgroundResource(R.drawable.animation_description_on);
        animation = (AnimationDrawable) buttonImage.getBackground();
        animation.stop();
        animation.start();
    }

    public void playAnimOff(View v) {
        Log.d("animButton", "Click");
        buttonImage.setBackgroundResource(R.drawable.animation_description_off);
        animation = (AnimationDrawable) buttonImage.getBackground();
        animation.stop();
        animation.start();
    }

    public void haseFlash() {
//      Проверяем поддержку работы с фонариком на устройстве:
        hasFlash = getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        if (!hasFlash) {
//            Если вспышка не поддерживается, показываем
//            диалоговое окно с ошибкой и закрываем приложение:
            AlertDialog alert = new AlertDialog.Builder(MainActivity.this)
                    .create();
            alert.setTitle("Ошибка");
            alert.setMessage("Ваше устройство не поддерживает работу с вспышкой!");
            alert.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
//                    Закрываем приложение:
                    finish();
                }
            });
            alert.show();
            return;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        saundImage = (ImageView) findViewById(R.id.volume);
        buttonImage = (ImageView) findViewById(R.id.btnSwitch);

        setterSaundImage();

        haseFlash();

        getCamera();

        buttonImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                playSound();
                if (isFlashOn) {
                    playAnimOff(v);
                    turnOffFlash();
                    Log.d("animButton", "playAnimOff(v)");
                } else {
                    playAnim(v);
                    turnOnFlash();
                    Log.d("animButton", "playAnim(v)");

                }
            }
        });

        saundImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onOffSaund();
                setterSaundImage();
            }
        });
    }

    private void getCamera() {
        if (camera == null) {
            try {
                camera = Camera.open();
                camera.setPreviewTexture(surfaceTexture);
                params = camera.getParameters();
            } catch (RuntimeException e) {
                Log.e("Ошибка, невозможно запустить: ", e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void turnOnFlash() {
        if (!isFlashOn) {
            if (camera == null || params == null) {
                return;
            }
            params = camera.getParameters();
            params.setFlashMode(Parameters.FLASH_MODE_TORCH);
            camera.setParameters(params);
            camera.startPreview();
            isFlashOn = true;
        }
    }

    private void turnOffFlash() {
        if (isFlashOn) {
            if (camera == null || params == null) {
                return;
            }
            params = camera.getParameters();
            params.setFlashMode(Parameters.FLASH_MODE_OFF);
            camera.setParameters(params);
            camera.stopPreview();
            isFlashOn = false;
        }
    }

    private void setterSaundImage() {
        if (statusSaund) {
            saundImage.setImageResource(R.drawable.volume_on);
        } else {
            saundImage.setImageResource(R.drawable.volume_off);
        }
    }

    private void onOffSaund() {
        if (statusSaund) {
            statusSaund = false;
        } else {
            statusSaund = true;
        }
    }

    private void playSound() {
        if (statusSaund) {
            mp = MediaPlayer.create(MainActivity.this, R.raw.sound);
            mp.setOnCompletionListener(new OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.release();
                }
            });
            mp.start();
        } else return;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("animButton", "onDestroy()");

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("animButton", "onPause()");
//        turnOffFlash();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("animButton", "onRestart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("animButton", "onResume()");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("animButton", "onStart()");
        getCamera();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("animButton", "onStop()");
//        Закрываем работу камеры:
        if (camera != null) {
            camera.release();
            camera = null;
        }
        isFlashOn = false;
        buttonImage.setBackgroundResource(R.drawable.part_animation_1);
    }
}