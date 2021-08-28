package com.seen.plugin.mlkit;

import static com.huawei.hms.mlsdk.common.lens.MLAnalyzerMonitor.TAG;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.mlsdk.MLAnalyzerFactory;
import com.huawei.hms.mlsdk.common.MLException;
import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.hms.mlsdk.face.MLFace;
import com.huawei.hms.mlsdk.face.MLFaceAnalyzer;
import com.huawei.hms.mlsdk.face.MLFaceAnalyzerSetting;
import com.huawei.hms.mlsdk.face.face3d.ML3DFace;
import com.huawei.hms.mlsdk.face.face3d.ML3DFaceAnalyzer;
import com.huawei.hms.mlsdk.face.face3d.ML3DFaceAnalyzerSetting;
import com.huawei.hms.mlsdk.text.MLLocalTextSetting;
import com.huawei.hms.mlsdk.text.MLText;
import com.huawei.hms.mlsdk.text.MLTextAnalyzer;

import java.text.DecimalFormat;
import java.util.List;

@CapacitorPlugin(name = "HuaweiMlkit")
public class HuaweiMlkitPlugin extends Plugin {

    private HuaweiMlkit implementation = new HuaweiMlkit();
    MLTextAnalyzer analyzer;
    private MLFaceAnalyzer FaceAnalyzer;

    private ML3DFaceAnalyzer analyzer3D;

    @PluginMethod
    public void echo(PluginCall call) {
        String value = call.getString("value");

        JSObject ret = new JSObject();
        ret.put("PluginMethod[echo]value", implementation.echo(value));
        call.resolve(ret);
    }

    @PluginMethod
    public void TextRecognition(PluginCall call) {
        String base64 = call.getString("base64");
        if (base64 == null||base64==""||base64.isEmpty()){
            call.errorCallback("can't picture");
        }else {
            analyzer = MLAnalyzerFactory.getInstance().getLocalTextAnalyzer();
            MLLocalTextSetting setting = new MLLocalTextSetting.Factory()
                    .setOCRMode(MLLocalTextSetting.OCR_DETECT_MODE)
                    .setLanguage("en")
                    .create();

            this.analyzer = MLAnalyzerFactory.getInstance()
                    .getLocalTextAnalyzer(setting);

            byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

            MLFrame frame = MLFrame.fromBitmap(decodedByte);
            Task<MLText> task = this.analyzer.asyncAnalyseFrame(frame);
            task.addOnSuccessListener(new OnSuccessListener<MLText>() {
                @Override
                public void onSuccess(MLText mlText) {
                    String result = "";
                    List<MLText.Block> blocks = mlText.getBlocks();
                    for (MLText.Block block : blocks) {
                        for (MLText.TextLine line : block.getContents()) {
                            result += line.getStringValue() + "\n";
                        }
                    }
                    JSObject ret = new JSObject();
                    ret.put("value", implementation.TextRecognition(result));
                    call.resolve(ret);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception exception) {
                    String error = "Failure. ";
                    try {
                        MLException mlException = (MLException) exception;
                        error += "error code: " + mlException.getErrCode() + "\n" + "error message: " + mlException.getMessage();
                    } catch (Exception e) {
                        error += e.getMessage();

                    }
//                JSObject ret = new JSObject();
//                ret.put("value", implementation.textRec(error));
//                call.resolve(ret);
                    call.errorCallback(error);

                }
            });
        }




    }

    @PluginMethod
    public void FaceDetection(PluginCall call) {
        String FaceImage = call.getString("FaceImage");
            // Create a face analyzer. You can create an analyzer using the provided customized face detection parameter
            MLFaceAnalyzerSetting setting = new MLFaceAnalyzerSetting.Factory()
                    // Fast detection of continuous video frames.
                    // MLFaceAnalyzerSetting.TYPE_PRECISION: indicating the precision preference mode.
                    // This mode will detect more faces and be more precise in detecting key points and contours, but will run slower.
                    // MLFaceAnalyzerSetting.TYPE_SPEED: representing a preference for speed.
                    // This will detect fewer faces and be less precise in detecting key points and contours, but will run faster.
                    .setPerformanceType(MLFaceAnalyzerSetting.TYPE_SPEED)
                    // Mode for an analyzer to detect facial features and expressions.
                    // MLFaceAnalyzerSetting.TYPE_FEATURES: indicating that facial features and expressions are detected.
                    // MLFaceAnalyzerSetting.TYPE_UNSUPPORT_FEATURES: indicating that facial features and expressions are not detected.
                    .setFeatureType(MLFaceAnalyzerSetting.TYPE_FEATURES)
                    // Sets the mode for an analyzer to detect key face points.
                    // MLFaceAnalyzerSetting.TYPE_KEYPOINTS: indicating that key face points are detected.
                    // MLFaceAnalyzerSetting.TYPE_UNSUPPORT_KEYPOINTS: indicating that key face points are not detected.
                    .setKeyPointType(MLFaceAnalyzerSetting.TYPE_KEYPOINTS)
                    // Sets the mode for an analyzer to detect facial contours.
                    // MLFaceAnalyzerSetting.TYPE_SHAPES: indicating that facial contours are detected.
                    // MLFaceAnalyzerSetting.TYPE_UNSUPPORT_SHAPES: indicating that facial contours are not detected.
                    .setShapeType(MLFaceAnalyzerSetting.TYPE_SHAPES)
                    // Sets whether to disable pose detection.
                    // true: Disable pose detection.
                    // false: Enable pose detection (default value).
                    .setPoseDisabled(false)
                    .create();
        FaceAnalyzer = MLAnalyzerFactory.getInstance().getFaceAnalyzer(setting);
            // Create an MLFrame by using the bitmap. Recommended image size: large than 320*320, less than 1920*1920.
//        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.face_image);
        byte[] decodedString = Base64.decode(FaceImage, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

            MLFrame frame = MLFrame.fromBitmap(decodedByte);
            // Call the asyncAnalyseFrame method to perform face detection
            Task<List<MLFace>> task = FaceAnalyzer.asyncAnalyseFrame(frame);
            task.addOnSuccessListener(new OnSuccessListener<List<MLFace>>() {
                @Override
                public void onSuccess(List<MLFace> faces) {
                    // Detection success.
                    if (faces.size() > 0) {
                        Log.d(TAG,"onSuccess");
                        DecimalFormat decimalFormat = new DecimalFormat("0.000");
                        String result =
                                "Left eye open Probability: " + decimalFormat.format(faces.get(0).getFeatures().getLeftEyeOpenProbability());
                        result +=
                                "\nRight eye open Probability: " + decimalFormat.format(faces.get(0).getFeatures().getRightEyeOpenProbability());
                        result += "\nMoustache Probability: " + decimalFormat.format(faces.get(0).getFeatures().getMoustacheProbability());
                        result += "\nGlass Probability: " + decimalFormat.format(faces.get(0).getFeatures().getSunGlassProbability());
                        result += "\nHat Probability: " + decimalFormat.format(faces.get(0).getFeatures().getHatProbability());
                        result += "\nAge: " + faces.get(0).getFeatures().getAge();
                        result += "    Gender: " + ((faces.get(0).getFeatures().getSexProbability() > 0.5f) ? "Female" : "Male");
                        result += "\nRotationAngleY: " + decimalFormat.format(faces.get(0).getRotationAngleY());
                        result += "    RotationAngleZ: " + decimalFormat.format(faces.get(0).getRotationAngleZ());
                        result += "    RotationAngleX: " + decimalFormat.format(faces.get(0).getRotationAngleX());
                        JSObject ret = new JSObject();
                        ret.put("value", implementation.FaceDetection(result));
                        call.resolve(ret);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    // Detection failure.
                    Log.d(TAG,"onFailure");
                    JSObject ret = new JSObject();
                    ret.put("value", implementation.FaceDetection("onFailure"));
                    call.resolve(ret);
                }
            });
    }

}
