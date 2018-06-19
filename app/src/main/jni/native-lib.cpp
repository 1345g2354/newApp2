//
// Created by a on 2018/5/31.
//

#include "stdio.h"
#include "jni.h"
#include "string"

#ifdef __cplusplus
extern "C"{
#endif

static jclass contextClass;
static jclass signatureClass;
static jclass packageNameClass;
static jclass packageInfoClass;

/**
    之前生成好的签名字符串
*/

const char* RELEASE_SIGN = "308202f5308201dda0030201020204623f1988300d06092a864886f70d01010b0500302a31133011060355040b130a5472616365436c756465311330110603550403130a7875796f6e676368616f3020170d3138303630343038353434395a180f32303530303532373038353434395a302a31133011060355040b130a5472616365436c756465311330110603550403130a7875796f6e676368616f30820122300d06092a864886f70d01010105000382010f003082010a0282010100bb641fcf13129ebf198c6fa4a5b083348857c65b45a6f467bee916f5fb969a72d53d2adcaf4328232f233b952074ff54251670cb1a5dbf532a79a9358dbbb1230df8f70ab79f6c4df37698b4d1f657f0b543d1907446c48a44c2169261a8249afaa12566230777e8f57598d8b9c72ef9081229ad3925a0a7bfe6f8b4a88a94e18cd595f171287f51b256d664240f3ccccd6eb40aa6b0589181c8c386c7ca720d2316f9c8d18bc74f6dd2ce387e40d4afc358a68c6fbb1e6a3078f49f5c00bc99fd94b360065c2430a3e800014d894e1f8d224db2e0d6093eeec6b91658fa4bdf43d92f42b0f211947adfa6fff077eb9a96e595f0486ef41411c4f2031aef17a10203010001a321301f301d0603551d0e0416041432da242d9adb1d98d0a347667ef6ff4cfdbb1208300d06092a864886f70d01010b0500038201010034d58aeb25c7c7b3db10580bb399529a6f9f6fa60bee3d1b86a45c50f8ebf9f19f4bfcdb53ec002fd1e1dcf2c654394f674be51521dda4c605fc74ad10263d9a3ca6f878d146351afee2322fc259e927859af07b1dc2e6b2c90cc26d0b4c11f5c2fb4bc8fc4c1a27ef3d15f8a840310b9bd46db3ae403b6df2bf0bbdc9d598d28aa630ce04ee7ef740b06dbd98caa9bfee03d135e475ec65688650bd7bf2112ffa81c9431a0ea201a711cd0cb3b1a48b5df9c05a54627482349b24e85d54747a1a05dec01b1ceff76903efff3ea35f1cb89e71b4617f8a1a253211a1f9cce3d1d265e9f66419961e6c3f9bf239258f76c82a2ac8f88615ef64f3ff51cdbdc65c";
/**
 * 密钥
 */
const char* RELEASE_SECRET = "q1w2e3r4t5y6kiju";
  char*  RELEASE_SECRETS[]= {"q1w2e3r4t5y6kiju","q1w2e3r4t5y6kiju"};
/*
    根据context对象,获取签名字符串
*/
const char* getSignString(JNIEnv *env,jobject contextObject) {
    jmethodID getPackageManagerId = (env)->GetMethodID(contextClass, "getPackageManager","()Landroid/content/pm/PackageManager;");
    jmethodID getPackageNameId = (env)->GetMethodID(contextClass, "getPackageName","()Ljava/lang/String;");
    jmethodID signToStringId = (env)->GetMethodID(signatureClass, "toCharsString","()Ljava/lang/String;");
    jmethodID getPackageInfoId = (env)->GetMethodID(packageNameClass, "getPackageInfo","(Ljava/lang/String;I)Landroid/content/pm/PackageInfo;");
    jobject packageManagerObject =  (env)->CallObjectMethod(contextObject, getPackageManagerId);
    jstring packNameString =  (jstring)(env)->CallObjectMethod(contextObject, getPackageNameId);
    jobject packageInfoObject = (env)->CallObjectMethod(packageManagerObject, getPackageInfoId,packNameString, 64);
    jfieldID signaturefieldID =(env)->GetFieldID(packageInfoClass,"signatures", "[Landroid/content/pm/Signature;");
    jobjectArray signatureArray = (jobjectArray)(env)->GetObjectField(packageInfoObject, signaturefieldID);
    jobject signatureObject =  (env)->GetObjectArrayElement(signatureArray,0);
    return (env)->GetStringUTFChars((jstring)(env)->CallObjectMethod(signatureObject, signToStringId),0);
}


JNIEXPORT jstring JNICALL Java_com_hangyjx_syygzapp_utils_JniUtil_getSecretCode
  (JNIEnv *env,jobject thiz,jobject contextObject,jint postion){
    const char* signStrng =   getSignString(env,contextObject);


    if(strcmp(signStrng,RELEASE_SIGN)==0)//签名一致  返回合法的 api key，否则返回错误
    {int length = 0;
        length =  sizeof(RELEASE_SECRETS)/sizeof(RELEASE_SECRETS[0]);
        if(length <= postion){
            return (env)->NewStringUTF("downnew");
        } else{
            return (env)->NewStringUTF(RELEASE_SECRETS[postion]);
        }

    }else
    {
       return (env)->NewStringUTF("error");
    }
  };

  /**
      利用OnLoad钩子,初始化需要用到的Class类.
  */
  JNIEXPORT jint JNICALL JNI_OnLoad (JavaVM* vm,void* reserved){

       JNIEnv* env = NULL;
       jint result=-1;
       if(vm->GetEnv((void**)&env, JNI_VERSION_1_4) != JNI_OK)
         return result;

       contextClass = (jclass)env->NewGlobalRef((env)->FindClass("android/content/Context"));
       signatureClass = (jclass)env->NewGlobalRef((env)->FindClass("android/content/pm/Signature"));
       packageNameClass = (jclass)env->NewGlobalRef((env)->FindClass("android/content/pm/PackageManager"));
       packageInfoClass = (jclass)env->NewGlobalRef((env)->FindClass("android/content/pm/PackageInfo"));

       return JNI_VERSION_1_4;
   }

  #ifdef __cplusplus
  }
  #endif


