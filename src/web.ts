import { WebPlugin } from '@capacitor/core';

import type { HuaweiMlkitPlugin, TextRecognitionOptions } from './definitions';

export class HuaweiMlkitWeb extends WebPlugin implements HuaweiMlkitPlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
  
  async TextRecognition(options: TextRecognitionOptions): Promise<any> {
    console.log('ECHO', options);
    return options;
  }

  // async faceVerification(options: FaceVerificationOptions): Promise<{ value: string }> {
  //   return options;
  // }
}
