package com.seen.plugin.mlkit;

import static com.huawei.hms.mlsdk.common.lens.MLAnalyzerMonitor.TAG;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

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
import com.huawei.hms.mlsdk.faceverify.MLFaceTemplateResult;
import com.huawei.hms.mlsdk.faceverify.MLFaceVerificationAnalyzer;
import com.huawei.hms.mlsdk.faceverify.MLFaceVerificationAnalyzerFactory;
import com.huawei.hms.mlsdk.faceverify.MLFaceVerificationAnalyzerSetting;
import com.huawei.hms.mlsdk.faceverify.MLFaceVerificationResult;
import com.huawei.hms.mlsdk.text.MLLocalTextSetting;
import com.huawei.hms.mlsdk.text.MLText;
import com.huawei.hms.mlsdk.text.MLTextAnalyzer;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.util.List;

@CapacitorPlugin(name = "HuaweiMlkit")
public class HuaweiMlkitPlugin extends Plugin {

    private HuaweiMlkit implementation = new HuaweiMlkit();
    MLTextAnalyzer analyzer;
    private MLFaceAnalyzer FaceAnalyzer;

    private ML3DFaceAnalyzer analyzer3D;
    private MLFaceVerificationAnalyzer FaceVerificationAnalyzer;
    private int REQUEST_CHOOSE_TEMPLATEPIC = 2001;

    private int REQUEST_CHOOSE_COMPAEPIC = 2002;
    Bitmap templateBitmap = null;
    Bitmap compareBitmap  = null;
    Bitmap templateBitmapCopy  = null;
    Bitmap compareBitmapCopy  = null;

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

    @PluginMethod
    public void FaceVerification(PluginCall call) {
        String FaceTemplate = call.getString("faceTemplate");
        String FaceCompare = call.getString("faceCompare");



        //initAnalyzer
        MLFaceVerificationAnalyzerSetting.Factory factory = new MLFaceVerificationAnalyzerSetting.Factory().setMaxFaceDetected(3);
        MLFaceVerificationAnalyzerSetting setting = factory.create();
        FaceVerificationAnalyzer = MLFaceVerificationAnalyzerFactory
                .getInstance()
                .getFaceVerificationAnalyzer(setting);

        //
//        recycleBitmap(templateBitmap);
//        recycleBitmap(templateBitmapCopy);
        byte[] decodedTemplateString = Base64.decode(FaceTemplate, Base64.DEFAULT);
        Bitmap decodedTemplateByte = BitmapFactory.decodeByteArray(decodedTemplateString, 0, decodedTemplateString.length);
//        templateBitmap = decodedTemplateByte;
//        templateBitmapCopy = decodedTemplateByte;
        templateBitmap = loadPic(decodedTemplateByte);
        templateBitmapCopy = templateBitmap.copy(Bitmap.Config.ARGB_8888, true);

//        recycleBitmap(compareBitmap);
//        recycleBitmap(compareBitmapCopy);
        byte[] decodedCompareString = Base64.decode(FaceCompare, Base64.DEFAULT);
        Bitmap decodedCompareByte = BitmapFactory.decodeByteArray(decodedCompareString, 0, decodedCompareString.length);
//        compareBitmap = decodedCompareByte;
//        compareBitmapCopy = decodedCompareByte;
        compareBitmap = loadPic(decodedCompareByte);
        compareBitmapCopy = compareBitmap.copy(Bitmap.Config.ARGB_8888, true);

        ///template
        if (templateBitmap == null) {
            return;
        }
        long startTemplateTime = System.currentTimeMillis();
        List<MLFaceTemplateResult> resultsTemplate = FaceVerificationAnalyzer.setTemplateFace(MLFrame.fromBitmap(templateBitmap));
        long endTimeTemplate = System.currentTimeMillis();
        StringBuilder sbTemplate = new StringBuilder();
        sbTemplate.append("##setTemplateFace|COST[");
        sbTemplate.append(endTimeTemplate - startTemplateTime);
        sbTemplate.append("]");
        if (resultsTemplate.isEmpty()) {
            sbTemplate.append("Failure!");
        } else {
            sbTemplate.append("Success!");
        }
        for (MLFaceTemplateResult template : resultsTemplate) {
            int idTemplate = template.getTemplateId();
            Rect location = template.getFaceInfo().getFaceRect();
                    Canvas canvas = new Canvas(templateBitmapCopy);
                    Paint paint = new Paint();
                    paint.setColor(Color.RED);
                    paint.setStyle(Paint.Style.STROKE);// Not Filled
                    paint.setStrokeWidth((location.right - location.left) / 50f);  // Line width
                    canvas.drawRect(location, paint);// framed
//                    templatePreview.setImageBitmap(templateBitmapCopy);
            sbTemplate.append("|Face[");
            sbTemplate.append(location);
            sbTemplate.append("]ID[");
            sbTemplate.append(idTemplate);
            sbTemplate.append("]");
        }
        sbTemplate.append("\n");
//        resultTextView.setText(sbTemplate.toString());

        ///compare
        if (compareBitmap == null) {
            return;
        }
        final long startCompareTime = System.currentTimeMillis();
        try {
            Task<List<MLFaceVerificationResult>> task = FaceVerificationAnalyzer.asyncAnalyseFrame(MLFrame.fromBitmap(compareBitmap));
            final StringBuilder sbCompare = new StringBuilder();
            sbCompare.append("##getFaceSimilarity|");
            task.addOnSuccessListener(new OnSuccessListener<List<MLFaceVerificationResult>>() {
                @Override
                public void onSuccess(List<MLFaceVerificationResult> mlCompareList) {
                    String ResultLocation = null;
                    String ResultID = null;
                    String ResultSimilarity = null;
                    long endCompareTime = System.currentTimeMillis();
                    sbCompare.append("COST[");
                    sbCompare.append(endCompareTime - startCompareTime);
                    sbCompare.append("]|Success!");
                    for (MLFaceVerificationResult template : mlCompareList) {
                        Rect location = template.getFaceInfo().getFaceRect();
                                Canvas canvas = new Canvas(compareBitmapCopy);
                                Paint paint = new Paint();
                                paint.setColor(Color.RED);
                                paint.setStyle(Paint.Style.STROKE);// Not Filled
                                paint.setStrokeWidth((location.right - location.left) / 50f);  // Line width
                                canvas.drawRect(location, paint);// framed
                        int id = template.getTemplateId();
                        float similarity = template.getSimilarity();
//                                comparePreview.setImageBitmap(compareBitmapCopy);
                        sbCompare.append("|Face[");
                        sbCompare.append(location);
                        sbCompare.append("]Id[");
                        sbCompare.append(id);
                        sbCompare.append("]Similarity[");
                        sbCompare.append(similarity);
                        sbCompare.append("]");
                        ResultLocation = String.valueOf(location);
                        ResultID = String.valueOf(id);
                        ResultSimilarity = String.valueOf(similarity);
                    }
                    sbCompare.append("\n");
//                    resultTextView.append(sbCompare.toString());
                    JSObject ret = new JSObject();
//                    ret.put("value", implementation.FaceVerification(sbCompare.toString()));
                    ret.put("status", implementation.FaceVerification("Success"));
                    ret.put("location", implementation.FaceVerification(ResultLocation));
                    ret.put("id", implementation.FaceVerification(ResultID));
                    ret.put("similarity", implementation.FaceVerification(ResultSimilarity));
                    ret.put("picTemplate", implementation.FaceVerification(BitmapToBase64(templateBitmapCopy)));
                    ret.put("picCom", implementation.FaceVerification(BitmapToBase64(compareBitmapCopy)));
                    call.resolve(ret);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    long endCompareTime = System.currentTimeMillis();
                    sbCompare.append("COST[");
                    sbCompare.append(endCompareTime - startCompareTime);
                    sbCompare.append("]|Failure!");
                    if (e instanceof MLException) {
                        MLException mlException = (MLException) e;
                        // Obtain error codes. Developers can process the error codes and display differentiated messages based on the error codes.
                        int errorCode = mlException.getErrCode();
                        // Obtain error information. Developers can quickly locate faults based on the error code.
                        String errorMessage = mlException.getMessage();
                        sbCompare.append("|ErrorCode[");
                        sbCompare.append(errorCode);
                        sbCompare.append("]Msg[");
                        sbCompare.append(errorMessage);
                        sbCompare.append("]");
                    } else {
                        sbCompare.append("|Error[");
                        sbCompare.append(e.getMessage());
                        sbCompare.append("]");
                    }
                    sbCompare.append("\n");
//                    resultTextView.append(sbCompare.toString());
                    JSObject ret = new JSObject();
                    ret.put("value", implementation.FaceVerification(sbCompare.toString()));
                    call.resolve(ret);
                }
            });
        }catch (RuntimeException e){
            Log.e(ContentValues.TAG,"Set the image containing the face for comparison.");
        }


    }

    public static void recycleBitmap(Bitmap... bitmaps) {
        for (Bitmap bitmap : bitmaps) {
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
                bitmap = null;
            }
        }
    }

    private Bitmap loadPic(Bitmap picBitmap) {
        Log.d("loadPicURI", String.valueOf(picBitmap));
        Bitmap pic = null;
//        pic = loadFromPath( picBitmap, ((View) view.getParent()).getWidth(),
//                ((View) view.getParent()).getHeight()).copy(Bitmap.Config.ARGB_8888, true);

        pic = loadFromPath( picBitmap, 720,
                1280);

        if (pic == null) {
//            Toast.makeText(this.getApplicationContext(), R.string.please_select_picture, Toast.LENGTH_SHORT).show();
        }
//        view.setImageBitmap(pic);
        return pic;
    }
    public static Bitmap loadFromPath(Bitmap picBitmap, int width, int height) {
        Bitmap bitmap = zoomImage(picBitmap, width, height);
        return rotateBitmap(bitmap, 0);
    }

    private static Bitmap zoomImage(Bitmap imageBitmap, int targetWidth, int maxHeight) {
        float scaleFactor =
                Math.max(
                        (float) imageBitmap.getWidth() / (float) targetWidth,
                        (float) imageBitmap.getHeight() / (float) maxHeight);
        Bitmap resizedBitmap =
                Bitmap.createScaledBitmap(
                        imageBitmap,
                        (int) (imageBitmap.getWidth() / scaleFactor),
                        (int) (imageBitmap.getHeight() / scaleFactor),
                        true);

        return resizedBitmap;
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        Bitmap result = null;
        try {
            result = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
            Log.e(ContentValues.TAG, "Failed to rotate bitmap: " + e.getMessage());
        }
        if (result == null) {
            return bitmap;
        }
        return result;
    }

    public static String BitmapToBase64(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();

        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);

        return encoded;
    }

}
