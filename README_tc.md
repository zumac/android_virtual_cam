# android_virtual_cam
[简体中文](./README.md) | [繁體中文](./README_tc.md) | [English](./README_en.md)

A virtual camera based on Xposed

# Do not use for any illegal purposes. You are responsible for all consequences.

## Mainland China mirror (Gitee): https://gitee.com/w2016561536/android_virtual_cam

## Supported platforms:
- Android 5.0+

## Usage
1. Install this module and enable it in Xposed. For frameworks with scopes such as LSPosed, select the target app; no need to select the system framework.
2. In system settings, grant the target app permission to read local storage, then force stop the app. If the app does not request this permission, see step 3.
3. Open the target app. If it fails to obtain read storage permission, a toast will indicate that the `Camera1` directory is redirected to the app private directory: `/[Internal Storage]/Android/data/[package_name]/files/Camera1/`. If no toast appears, the default `Camera1` directory is `/[Internal Storage]/DCIM/Camera1/`. If the directory does not exist, please create it manually.
> Note: The `Camera1` under the private directory only takes effect for that specific app.
4. Open the camera preview in the target app. A toast will show “width: … height: …”. Prepare a replacement video based on that resolution, place it in the `Camera1` directory, and name it `virtual.mp4`. If there is no toast when opening the camera, you do not need to adjust the video resolution.
5. If taking a photo in the target app still shows a real image and a toast appears saying `Photo detected` with a resolution, prepare a photo with that resolution, name it `1000.bmp`, and place it in the `Camera1` directory (other formats are supported by renaming the extension to bmp). If there is no toast when taking a photo, `1000.bmp` has no effect.
6. To play video audio, create `no-silent.jpg` under `/[Internal Storage]/DCIM/Camera1/`. (Global, real-time effect)
7. To temporarily disable video replacement, create `disable.jpg` under `/[Internal Storage]/DCIM/Camera1/`. (Global, real-time effect)
8. If you find Toast messages annoying, create `no_toast.jpg` under `/[Internal Storage]/DCIM/Camera1/`. (Global, real-time effect)
9. The directory redirection message shows only once by default. If you missed it, create `force_show.jpg` under `/[Internal Storage]/DCIM/Camera1/` to override the default. (Global, real-time effect)
10. To assign a different video per app, create `private_dir.jpg` under `/[Internal Storage]/DCIM/Camera1/` to force using the app private directory. (Global, real-time effect)
> Note: Items 6–10 can be configured in the app UI for convenience, or you can create the files manually.

## FAQ
Q1. Front camera orientation problem?
A1. In most cases, the replacement video for the front camera needs a horizontal flip and a 90° clockwise rotation, and the processed video resolution should match the resolution shown in the toast message. Sometimes this is not necessary; please judge based on the actual situation.

Q2. Black screen, camera fails to start?
A2. Some apps currently cannot be replaced successfully (especially the system camera). Or the video path is incorrect (did you create two levels of Camera1, like `./DCIM/Camera1/Camera1/virtual.mp4`? Only one level is required).

Q3. Garbled screen?
A3. Incorrect video resolution.

Q4. Distorted image?
A4. Use an editing tool to modify the original video to match the screen.

Q5. Creating `disable.jpg` is ineffective?
A5. If app version `<= 4.0`, then files under `[Internal Storage]/DCIM/Camera1` only take effect for apps WITH storage permission; other apps without permission should create the file under the PRIVATE DIRECTORY. If app version `>= 4.1`, create it under `[Internal Storage]/DCIM/Camera1` regardless of permissions.

## Feedback
Please report directly in issues. If reporting a BUG, please attach the Xposed module log.

## Acknowledgments
Provide hook idea: https://github.com/wangwei1237/CameraHook
H.264 hardware decoding: https://github.com/zhantong/Android-VideoToImages
JPEG to YUV: https://blog.csdn.net/jacke121/article/details/73888732
