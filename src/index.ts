import { registerPlugin } from '@capacitor/core';

import type { HuaweiMlkitPlugin } from './definitions';

const HuaweiMlkit = registerPlugin<HuaweiMlkitPlugin>('HuaweiMlkit', {
  web: () => import('./web').then(m => new m.HuaweiMlkitWeb()),
});

export * from './definitions';
export { HuaweiMlkit };
