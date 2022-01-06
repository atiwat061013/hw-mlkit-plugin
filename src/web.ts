import { WebPlugin } from '@capacitor/core';

import { FaceDetectionOptions, FaceVerificationOptions, HuaweiMlkitPlugin, TextRecognitionOptions } from './definitions';

export class HuaweiMlkitWeb extends WebPlugin implements HuaweiMlkitPlugin {
  async TextRecognition(options: TextRecognitionOptions): Promise<any> {
    console.log('ECHO', options);
    return options;
  }

  async FaceDetection(options: FaceDetectionOptions): Promise<any> {
    console.log('ECHO', options);
    return options;
  }

  async FaceVerification(options: FaceVerificationOptions): Promise<any> {
    return options;
  }
}
