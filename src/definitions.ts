export interface HuaweiMlkitPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
  textRec(options: { value: string }): Promise<{ value: string }>;
}
