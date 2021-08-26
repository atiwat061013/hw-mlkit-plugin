// export interface FaceVerificationOptions {
//   faceTem?: string;
//   faceCom?: string;
  
// }

export interface TextRecognitionOptions {
  language?: string;
  base64?: string;
}
export interface HuaweiMlkitPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
  TextRecognition(options: TextRecognitionOptions): Promise<{ value: string }>;
  // faceVerification(options: { options: FaceVerificationOptions }): Promise<{ value: string }>;
}
