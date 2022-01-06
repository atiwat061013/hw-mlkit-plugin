import { WebPlugin } from '@capacitor/core';

import { FaceDetectionOptions, FaceVerificationOptions, HuaweiMlkitPlugin, TextRecognitionOptions } from './definitions';

export class HuaweiMlkitWeb extends WebPlugin implements HuaweiMlkitPlugin {
  async TextRecognition(options: TextRecognitionOptions): Promise<any> {
    console.log('TextRecognition', options);
    return options;
  }

  async FaceDetection(options: FaceDetectionOptions): Promise<any> {
    console.log('FaceDetection', options);
    return options;
  }

  async FaceVerification(options: FaceVerificationOptions): Promise<any> {
    console.log('FaceVerification', options);
    return options;
  }
}
