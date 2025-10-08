package com.example.vcam;
import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.InputConfiguration;
import android.hardware.camera2.params.OutputConfiguration;
import android.hardware.camera2.params.SessionConfiguration;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookMain implements IXposedHookLoadPackage {
    public static Surface mSurface;
    public static SurfaceTexture mSurfacetexture;
    public static MediaPlayer mMediaPlayer;
    public static SurfaceTexture fake_SurfaceTexture;
    public static Camera origin_preview_camera;
    public static Camera camera_onPreviewFrame;
    public static Camera start_preview_camera;
    public static volatile byte[] data_buffer = {0};
    public static byte[] input;
    public static int mhight;
    public static int mwidth;
    public static boolean is_someone_playing;
    public static boolean is_hooked;
    public static VideoToFrames hw_decode_obj;
    public static VideoToFrames c2_hw_decode_obj;
    public static VideoToFrames c2_hw_decode_obj_1;
    public static SurfaceTexture c1_fake_texture;
    public static Surface c1_fake_surface;
    public static SurfaceHolder ori_holder;
    public static MediaPlayer mplayer1;
    public static Camera mcamera1;
    public int imageReaderFormat = 0;
    public static boolean is_first_hook_build = true;
    public static int onemhight;
    public static int onemwidth;
    public static Class camera_callback_calss;
    public static String video_path = "/storage/emulated/0/DCIM/Camera1/";
    public static Surface c2_preview_Surfcae;
    public static Surface c2_preview_Surfcae_1;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        Context appContext = null;
        try {
            appContext = (Context) XposedHelpers.callMethod(XposedHelpers.callStaticMethod(XposedHelpers.findClass("android.app.ActivityThread", null), "currentApplication"), "getApplicationContext");
        } catch (Exception e) {
            XposedBridge.log("[VirtualCam] Context acquisition failed");
        }
        
        // Check if app has storage permission
        if (appContext != null && appContext.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            video_path = "/storage/emulated/0/DCIM/Camera1/";
            XposedBridge.log("[VirtualCam] Using shared directory: " + video_path);
        } else {
            // Redirect to app private directory
            if (appContext != null) {
                video_path = appContext.getExternalFilesDir(null) + "/Camera1/";
            }
            XposedBridge.log("[VirtualCam] Using private directory: " + video_path);
        }
        
        // Create directory if it doesn't exist
        File directory = new File(video_path);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        
        // Check control files
        boolean disableHook = new File(video_path + "disable.jpg").exists();
        boolean noToast = new File(video_path + "no_toast.jpg").exists();
        boolean forceShow = new File(video_path + "force_show.jpg").exists();
        boolean privateDir = new File(video_path + "private_dir.jpg").exists();
        boolean noSilent = new File(video_path + "no-silent.jpg").exists();
        
        if (disableHook) {
            XposedBridge.log("[VirtualCam] Hook disabled by control file");
            return;
        }

        // Hook Camera1 API
        try {
            Class<?> cameraClass = XposedHelpers.findClass("android.hardware.Camera", lpparam.classLoader);
            
            // Hook setPreviewDisplay method
            XposedHelpers.findAndHookMethod(cameraClass, "setPreviewDisplay", SurfaceHolder.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    SurfaceHolder holder = (SurfaceHolder) param.args[0];
                    if (holder != null) {
                        ori_holder = holder;
                        Camera camera = (Camera) param.thisObject;
                        mcamera1 = camera;
                        
                        // Setup fake surface for video playback
                        setupFakeSurface(camera);
                        
                        // Start video playback
                        if (mplayer1 == null) {
                            mplayer1 = new MediaPlayer();
                            try {
                                String videoFile = video_path + "virtual.mp4";
                                if (new File(videoFile).exists()) {
                                    mplayer1.setDataSource(videoFile);
                                    mplayer1.setSurface(c1_fake_surface);
                                    mplayer1.setLooping(true);
                                    if (noSilent) {
                                        mplayer1.setVolume(1.0f, 1.0f);
                                    } else {
                                        mplayer1.setVolume(0.0f, 0.0f);
                                    }
                                    mplayer1.prepareAsync();
                                    mplayer1.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                        @Override
                                        public void onPrepared(MediaPlayer mp) {
                                            mp.start();
                                        }
                                    });
                                }
                            } catch (Exception e) {
                                XposedBridge.log("[VirtualCam] MediaPlayer setup error: " + e.getMessage());
                            }
                        }
                    }
                }
            });
            
            // Hook setPreviewTexture method
            XposedHelpers.findAndHookMethod(cameraClass, "setPreviewTexture", SurfaceTexture.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    SurfaceTexture texture = (SurfaceTexture) param.args[0];
                    if (texture != null) {
                        Camera camera = (Camera) param.thisObject;
                        mcamera1 = camera;
                        
                        // Setup fake surface texture for video playback
                        setupFakeSurfaceTexture(camera, texture);
                    }
                }
            });
            
        } catch (Exception e) {
            XposedBridge.log("[VirtualCam] Camera1 hook failed: " + e.getMessage());
        }
        
        // Hook Camera2 API
        try {
            Class<?> sessionClass = XposedHelpers.findClass("android.hardware.camera2.CameraCaptureSession", lpparam.classLoader);
            
            // Hook capture method for Camera2
            XposedHelpers.findAndHookMethod(sessionClass, "capture", CaptureRequest.class, 
                CameraCaptureSession.CaptureCallback.class, Handler.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    CaptureRequest request = (CaptureRequest) param.args[0];
                    if (request != null) {
                        // Handle Camera2 capture requests
                        handleCamera2Capture(request);
                    }
                }
            });
            
        } catch (Exception e) {
            XposedBridge.log("[VirtualCam] Camera2 hook failed: " + e.getMessage());
        }
        
        if (!noToast) {
            showToast(appContext, "Virtual Camera Hook Loaded");
        }
    }
    
    private void setupFakeSurface(Camera camera) {
        try {
            c1_fake_texture = new SurfaceTexture(0);
            c1_fake_surface = new Surface(c1_fake_texture);
            
            Camera.Parameters params = camera.getParameters();
            Camera.Size previewSize = params.getPreviewSize();
            
            mwidth = previewSize.width;
            mhight = previewSize.height;
            
            c1_fake_texture.setDefaultBufferSize(mwidth, mhight);
            
            XposedBridge.log("[VirtualCam] Preview size: Width=" + mwidth + ", Height=" + mhight);
            
        } catch (Exception e) {
            XposedBridge.log("[VirtualCam] Fake surface setup error: " + e.getMessage());
        }
    }
    
    private void setupFakeSurfaceTexture(Camera camera, SurfaceTexture originalTexture) {
        try {
            c1_fake_texture = new SurfaceTexture(0);
            c1_fake_surface = new Surface(c1_fake_texture);
            
            Camera.Parameters params = camera.getParameters();
            Camera.Size previewSize = params.getPreviewSize();
            
            mwidth = previewSize.width;
            mhight = previewSize.height;
            
            c1_fake_texture.setDefaultBufferSize(mwidth, mhight);
            
            // Set up frame available listener to copy frames to original texture
            c1_fake_texture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
                @Override
                public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                    // Copy frame data to original texture
                    copyFrameToOriginalTexture(originalTexture);
                }
            });
            
        } catch (Exception e) {
            XposedBridge.log("[VirtualCam] Fake surface texture setup error: " + e.getMessage());
        }
    }
    
    private void handleCamera2Capture(CaptureRequest request) {
        try {
            // Handle Camera2 API capture requests
            // This is a simplified implementation
            XposedBridge.log("[VirtualCam] Camera2 capture request intercepted");
            
        } catch (Exception e) {
            XposedBridge.log("[VirtualCam] Camera2 capture handling error: " + e.getMessage());
        }
    }
    
    private void copyFrameToOriginalTexture(SurfaceTexture originalTexture) {
        try {
            // Update the original texture with fake content
            // This is a simplified implementation
            originalTexture.updateTexImage();
            
        } catch (Exception e) {
            XposedBridge.log("[VirtualCam] Frame copy error: " + e.getMessage());
        }
    }
    
    private void showToast(Context context, String message) {
        if (context != null) {
            try {
                Handler mainHandler = new Handler(context.getMainLooper());
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                XposedBridge.log("[VirtualCam] Toast display error: " + e.getMessage());
            }
        }
    }
}
