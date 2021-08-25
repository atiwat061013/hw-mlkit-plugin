export interface HuaweiMlkitPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
