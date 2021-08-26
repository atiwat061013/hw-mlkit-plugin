package com.seen.plugin.mlkit;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

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
import com.huawei.hms.mlsdk.text.MLLocalTextSetting;
import com.huawei.hms.mlsdk.text.MLText;
import com.huawei.hms.mlsdk.text.MLTextAnalyzer;

import java.util.List;

@CapacitorPlugin(name = "HuaweiMlkit")
public class HuaweiMlkitPlugin extends Plugin {

    private HuaweiMlkit implementation = new HuaweiMlkit();
    MLTextAnalyzer analyzer;

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

//        String encodedImage = value;
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
}
