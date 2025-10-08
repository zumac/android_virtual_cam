# android_virtual_cam
[简体中文](./README.md) | [繁體中文](./README_tc.md) | [English](./README_en.md)

A virtual camera based on Xposed

## DO NOT USE FOR ANY ILLEGAL PURPOSES. YOU ARE RESPONSIBLE FOR ALL CONSEQUENCES.

## Supported platform
- Android 5.0+

## Usage
1. Install this module and enable it in Xposed. For frameworks with scopes such as LSPosed, select the target app; do not select the System Framework.
2. In system settings, grant the target app permission to access local storage, and force stop the app. If the app does not request this permission, see step 3.
3. Open the target app. If it does not have storage permission, a toast will indicate that the `Camera1` directory has been redirected to the app’s private directory: `/[Internal Storage]/Android/data/[package_name]/files/Camera1/`. If no toast appears, the default `Camera1` directory is `/[Internal Storage]/DCIM/Camera1/`. If the directory does not exist, please create it manually.
> Attention: The `Camera1` folder under the private directory only affects that single app.
4. Open the camera in the target app. A toast will show the resolution (width, height). Adjust the replacement video to match that resolution, name it `virtual.mp4`, and place it under the `Camera1` directory.
5. If a toast appears when taking a photo ("Photo detected") showing a resolution, prepare a photo with the same resolution, name it `1000.bmp`, and place it under `Camera1` (other formats are supported by renaming to .bmp). If no toast appears, `1000.bmp` has no effect.
6. To play video audio, create `no-silent.jpg` under `/[Internal Storage]/DCIM/Camera1/`. (Global, real-time effect)
7. To temporarily disable video replacement, create `disable.jpg` under `/[Internal Storage]/DCIM/Camera1/`. (Global, real-time effect)
8. If you find toasts annoying, create `no_toast.jpg` under `/[Internal Storage]/DCIM/Camera1/`. (Global, real-time effect)
9. The directory redirection toast shows only once by default. If you missed it, create `force_show.jpg` under `/[Internal Storage]/DCIM/Camera1/` to override the default. (Global, real-time effect)
10. To assign a different video per app, create `private_dir.jpg` under `/[Internal Storage]/DCIM/Camera1/` to force using the app private directory. (Global, real-time effect)
> Note: Items 6–10 can be configured in the app UI, or by creating the files manually.

## FAQ
Q1. Front camera orientation issues?
A1. In most cases, the front camera replacement video needs a horizontal flip and a 90° clockwise rotation, and the processed video resolution should match the toast. Sometimes this isn’t necessary—use your judgment.

Q2. Black screen, camera fails to start?
A2. Some apps cannot be hooked (especially the system camera). Or the video path is wrong (did you create two levels of Camera1, like `./DCIM/Camera1/Camera1/virtual.mp4`? Only one level is needed).

Q3. Garbled screen?
A3. Wrong video resolution.

Q4. Distorted image?
A4. Use an editor to adjust the video to the screen.

Q5. `disable.jpg` has no effect?
A5. If app version `<= 4.0`, then files under `[Internal Storage]/DCIM/Camera1` take effect only for apps WITH storage permission; apps without permission should create the file under the PRIVATE DIRECTORY. If app version `>= 4.1`, create it under `[Internal Storage]/DCIM/Camera1` regardless of the target app’s permissions.

## Question report:
Raise it in issues directly. If it is a bug, please attach Xposed module logs.

## Credit
Provide hook method: https://github.com/wangwei1237/CameraHook
H.264 hardware decode: https://github.com/zhantong/Android-VideoToImages
JPEG-YUV convert: https://blog.csdn.net/jacke121/article/details/73888732
