import { WebPlugin } from '@capacitor/core';

import type { HuaweiMlkitPlugin } from './definitions';

export class HuaweiMlkitWeb extends WebPlugin implements HuaweiMlkitPlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
  
  async textRec(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
