export interface FaceVerificationOptions {
  faceTemplate?: string;
  faceCompare?: string;
  
}
export interface TextRecognitionOptions {
  language?: string;
  base64?: string;
}

export interface FaceDetectionOptions {
  FaceImage?: string;
}

export interface HuaweiMlkitPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
  TextRecognition(options?: TextRecognitionOptions): Promise<{ value: string }>;
  FaceDetection(options?: FaceDetectionOptions): Promise<{ value: string }>
  FaceVerification(options?:  FaceVerificationOptions ): Promise<{ value: string }>;
}
