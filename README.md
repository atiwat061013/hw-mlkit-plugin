# hwmlkit

Huawei Ml Kit

## Install

```bash
npm install hwmlkit
npx cap sync
```

## API

<docgen-index>

* [`echo(...)`](#echo)
* [`TextRecognition(...)`](#textrecognition)
* [`FaceDetection(...)`](#facedetection)
* [Interfaces](#interfaces)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### echo(...)

```typescript
echo(options: { value: string; }) => any
```

| Param         | Type                            |
| ------------- | ------------------------------- |
| **`options`** | <code>{ value: string; }</code> |

**Returns:** <code>any</code>

--------------------


### TextRecognition(...)

```typescript
TextRecognition(options: TextRecognitionOptions) => any
```

| Param         | Type                                                                      |
| ------------- | ------------------------------------------------------------------------- |
| **`options`** | <code><a href="#textrecognitionoptions">TextRecognitionOptions</a></code> |

**Returns:** <code>any</code>

--------------------


### FaceDetection(...)

```typescript
FaceDetection(options: FaceDetectionOptions) => any
```

| Param         | Type                                                                  |
| ------------- | --------------------------------------------------------------------- |
| **`options`** | <code><a href="#facedetectionoptions">FaceDetectionOptions</a></code> |

**Returns:** <code>any</code>

--------------------


### Interfaces


#### TextRecognitionOptions

| Prop           | Type                |
| -------------- | ------------------- |
| **`language`** | <code>string</code> |
| **`base64`**   | <code>string</code> |


#### FaceDetectionOptions

| Prop            | Type                |
| --------------- | ------------------- |
| **`FaceImage`** | <code>string</code> |

</docgen-api>
