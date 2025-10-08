# android_virtual_cam
[简体中文](./README.md) | [繁體中文](./README_tc.md) | [English](./README_en.md)

Virtual camera based on Xposed

# Do not use for any illegal purposes. You are responsible for all consequences.

### Mainland China mirror (Gitee): https://gitee.com/w2016561536/android_virtual_cam

## Supported platforms:
- Android 5.0+

## Usage
1. Install this module and enable it in Xposed. For frameworks with scopes such as LSPosed, select the target app only; no need to select the system framework.
2. In system settings, grant the target app permission to read local storage, then force stop the target app. If the app does not request this permission, see step 3.
3. Open the target app. If the app fails to obtain read storage permission, a toast will indicate that the `Camera1` directory is redirected to the app private directory: `/[Internal Storage]/Android/data/[package_name]/files/Camera1/`. If no toast appears, the default `Camera1` directory is `/[Internal Storage]/DCIM/Camera1/`. If the directory does not exist, please create it manually.
> Note: The `Camera1` under the private directory only takes effect for that specific app.
4. Open the camera preview in the target app. A toast will show “width: … height: …”. Prepare a replacement video based on that resolution, place it in the `Camera1` directory, and name it `virtual.mp4`. If no toast appears when opening the camera, you do not need to adjust the video resolution.
5. If taking a photo in the target app still shows a real image and a toast appears saying `Photo detected` along with a resolution, prepare an image with that resolution, name it `1000.bmp`, and place it in the `Camera1` directory (other formats are supported by renaming the extension to bmp). If there is no toast when taking a photo, `1000.bmp` is ineffective.
6. If you need video audio playback, create a file named `no-silent.jpg` under `/[Internal Storage]/DCIM/Camera1/`. (Takes effect globally and in real time)
7. If you need to temporarily disable video replacement, create a file named `disable.jpg` under `/[Internal Storage]/DCIM/Camera1/`. (Takes effect globally and in real time)
8. If you find Toast messages annoying, create a file named `no_toast.jpg` under `/[Internal Storage]/DCIM/Camera1/`. (Takes effect globally and in real time)
9. Directory redirection message shows only once by default. If you missed the redirection toast, create `force_show.jpg` under `/[Internal Storage]/DCIM/Camera1/` to override the default. (Takes effect globally and in real time)
10. If you want to assign a different video per app, create `private_dir.jpg` under `/[Internal Storage]/DCIM/Camera1/` to force use of the app private directory. (Takes effect globally and in real time)
> Note: Items 6~10 can be toggled inside the app UI for convenience, or you can create the files manually.

## FAQ
A1. Front camera orientation issues?
Q1. In most cases, replacement video for the front camera needs a horizontal flip and a 90° clockwise rotation, and the processed video resolution should match the resolution shown in the toast. Sometimes this isn’t necessary; please judge based on the actual situation.

Q2. Black screen or camera fails to start?
A2. Some apps currently cannot be replaced successfully (especially system camera). Or the video path is incorrect (did you create two levels of Camera1, e.g. `./DCIM/Camera1/Camera1/virtual.mp4`? Only a single level is required).

Q3. Garbled image?
A3. Incorrect video resolution.

Q4. Distorted image?
A4. Use an editing tool to modify the original video to match the screen.

Q5. Creating `disable.jpg` has no effect?
A5. If app version `<= 4.0`, then files under `[Internal Storage]/DCIM/Camera1` take effect only for apps that have storage permission; other apps without permission should create the file under the app’s private directory.
If app version `>= 4.1`, then create it under `[Internal Storage]/DCIM/Camera1` regardless of whether the target app has permission.

## Feedback
Please report directly in issues. For BUG reports, please attach the Xposed module log.

## Acknowledgments:
Hook idea: https://github.com/wangwei1237/CameraHook  
H264 hardware decoding: https://github.com/zhantong/Android-VideoToImages  
JPEG to YUV: https://blog.csdn.net/jacke121/article/details/73888732  
