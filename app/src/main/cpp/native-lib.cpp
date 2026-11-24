#include <jni.h>
#include <string>

namespace {
    constexpr unsigned char MASK[4] = {0x5A, 0x33, 0x91, 0xC7};

    std::string decode(const unsigned char* data, size_t len) {
        std::string out(len, '\0');
        for (size_t i = 0; i < len; ++i) {
            out[i] = static_cast<char>(data[i] ^ MASK[i % 4]);
        }
        return out;
    }

    // Banner Ad Unit ID (XOR-encoded)
    constexpr unsigned char BANNER_AD_ENC[] = {
            0x39, 0x52, 0xBC, 0xA6, 0x2A, 0x43, 0xBC, 0xB7, 0x2F, 0x51, 0xBC,
            0xF5, 0x68, 0x01, 0xA2, 0xFF, 0x6A, 0x00, 0xA5, 0xF7, 0x6F, 0x07,
            0xA1, 0xF4, 0x6D, 0x01, 0xA6, 0xE8, 0x63, 0x01, 0xA6, 0xF0, 0x6D,
            0x03, 0xA3, 0xF0, 0x6B, 0x00
    };

    // Contact Formspree Endpoint (XOR-encoded)
    constexpr unsigned char CONTACT_ENDPOINT_ENC[] = {
            0x32, 0x47, 0xE5, 0xB7, 0x29, 0x09, 0xBE, 0xE8, 0x3C, 0x5C, 0xE3, 0xAA,
            0x29, 0x43, 0xE3, 0xA2, 0x3F, 0x1D, 0xF8, 0xA8, 0x75, 0x55, 0xBE, 0xBF,
            0x23, 0x49, 0xF4, 0xA9, 0x36, 0x52, 0xFE
    };
}

extern "C" {

JNIEXPORT jstring JNICALL
Java_com_bulk_app_utils_NativeBridge_getBannerAdId(JNIEnv* env, jclass /* clazz */) {
    std::string decoded = decode(BANNER_AD_ENC, sizeof(BANNER_AD_ENC));
    return env->NewStringUTF(decoded.c_str());
}

JNIEXPORT jstring JNICALL
Java_com_bulk_app_utils_NativeBridge_getContactEndpoint(JNIEnv* env, jclass /* clazz */) {
    std::string decoded = decode(CONTACT_ENDPOINT_ENC, sizeof(CONTACT_ENDPOINT_ENC));
    return env->NewStringUTF(decoded.c_str());
}

}