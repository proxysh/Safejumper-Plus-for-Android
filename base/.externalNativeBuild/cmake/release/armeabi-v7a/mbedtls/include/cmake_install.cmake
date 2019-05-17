# Install script for directory: /Users/itsvekus/Documents/Vinditek/safejumper-android/base/src/main/cpp/mbedtls/include

# Set the install prefix
if(NOT DEFINED CMAKE_INSTALL_PREFIX)
  set(CMAKE_INSTALL_PREFIX "/usr/local")
endif()
string(REGEX REPLACE "/$" "" CMAKE_INSTALL_PREFIX "${CMAKE_INSTALL_PREFIX}")

# Set the install configuration name.
if(NOT DEFINED CMAKE_INSTALL_CONFIG_NAME)
  if(BUILD_TYPE)
    string(REGEX REPLACE "^[^A-Za-z0-9_]+" ""
           CMAKE_INSTALL_CONFIG_NAME "${BUILD_TYPE}")
  else()
    set(CMAKE_INSTALL_CONFIG_NAME "Release")
  endif()
  message(STATUS "Install configuration: \"${CMAKE_INSTALL_CONFIG_NAME}\"")
endif()

# Set the component getting installed.
if(NOT CMAKE_INSTALL_COMPONENT)
  if(COMPONENT)
    message(STATUS "Install component: \"${COMPONENT}\"")
    set(CMAKE_INSTALL_COMPONENT "${COMPONENT}")
  else()
    set(CMAKE_INSTALL_COMPONENT)
  endif()
endif()

# Install shared libraries without execute permission?
if(NOT DEFINED CMAKE_INSTALL_SO_NO_EXE)
  set(CMAKE_INSTALL_SO_NO_EXE "0")
endif()

if("${CMAKE_INSTALL_COMPONENT}" STREQUAL "Unspecified" OR NOT CMAKE_INSTALL_COMPONENT)
  file(INSTALL DESTINATION "${CMAKE_INSTALL_PREFIX}/include/mbedtls" TYPE FILE PERMISSIONS OWNER_READ OWNER_WRITE GROUP_READ WORLD_READ FILES
    "/Users/itsvekus/Documents/Vinditek/safejumper-android/base/src/main/cpp/mbedtls/include/mbedtls/aes.h"
    "/Users/itsvekus/Documents/Vinditek/safejumper-android/base/src/main/cpp/mbedtls/include/mbedtls/aesni.h"
    "/Users/itsvekus/Documents/Vinditek/safejumper-android/base/src/main/cpp/mbedtls/include/mbedtls/arc4.h"
    "/Users/itsvekus/Documents/Vinditek/safejumper-android/base/src/main/cpp/mbedtls/include/mbedtls/asn1.h"
    "/Users/itsvekus/Documents/Vinditek/safejumper-android/base/src/main/cpp/mbedtls/include/mbedtls/asn1write.h"
    "/Users/itsvekus/Documents/Vinditek/safejumper-android/base/src/main/cpp/mbedtls/include/mbedtls/base64.h"
    "/Users/itsvekus/Documents/Vinditek/safejumper-android/base/src/main/cpp/mbedtls/include/mbedtls/bignum.h"
    "/Users/itsvekus/Documents/Vinditek/safejumper-android/base/src/main/cpp/mbedtls/include/mbedtls/blowfish.h"
    "/Users/itsvekus/Documents/Vinditek/safejumper-android/base/src/main/cpp/mbedtls/include/mbedtls/bn_mul.h"
    "/Users/itsvekus/Documents/Vinditek/safejumper-android/base/src/main/cpp/mbedtls/include/mbedtls/camellia.h"
    "/Users/itsvekus/Documents/Vinditek/safejumper-android/base/src/main/cpp/mbedtls/include/mbedtls/ccm.h"
    "/Users/itsvekus/Documents/Vinditek/safejumper-android/base/src/main/cpp/mbedtls/include/mbedtls/certs.h"
    "/Users/itsvekus/Documents/Vinditek/safejumper-android/base/src/main/cpp/mbedtls/include/mbedtls/check_config.h"
    "/Users/itsvekus/Documents/Vinditek/safejumper-android/base/src/main/cpp/mbedtls/include/mbedtls/cipher.h"
    "/Users/itsvekus/Documents/Vinditek/safejumper-android/base/src/main/cpp/mbedtls/include/mbedtls/cipher_internal.h"
    "/Users/itsvekus/Documents/Vinditek/safejumper-android/base/src/main/cpp/mbedtls/include/mbedtls/cmac.h"
    "/Users/itsvekus/Documents/Vinditek/safejumper-android/base/src/main/cpp/mbedtls/include/mbedtls/compat-1.3.h"
    "/Users/itsvekus/Documents/Vinditek/safejumper-android/base/src/main/cpp/mbedtls/include/mbedtls/config.h"
    "/Users/itsvekus/Documents/Vinditek/safejumper-android/base/src/main/cpp/mbedtls/include/mbedtls/ctr_drbg.h"
    "/Users/itsvekus/Documents/Vinditek/safejumper-android/base/src/main/cpp/mbedtls/include/mbedtls/debug.h"
    "/Users/itsvekus/Documents/Vinditek/safejumper-android/base/src/main/cpp/mbedtls/include/mbedtls/des.h"
    "/Users/itsvekus/Documents/Vinditek/safejumper-android/base/src/main/cpp/mbedtls/include/mbedtls/dhm.h"
    "/Users/itsvekus/Documents/Vinditek/safejumper-android/base/src/main/cpp/mbedtls/include/mbedtls/ecdh.h"
    "/Users/itsvekus/Documents/Vinditek/safejumper-android/base/src/main/cpp/mbedtls/include/mbedtls/ecdsa.h"
    "/Users/itsvekus/Documents/Vinditek/safejumper-android/base/src/main/cpp/mbedtls/include/mbedtls/ecjpake.h"
    "/Users/itsvekus/Documents/Vinditek/safejumper-android/base/src/main/cpp/mbedtls/include/mbedtls/ecp.h"
    "/Users/itsvekus/Documents/Vinditek/safejumper-android/base/src/main/cpp/mbedtls/include/mbedtls/ecp_internal.h"
    "/Users/itsvekus/Documents/Vinditek/safejumper-android/base/src/main/cpp/mbedtls/include/mbedtls/entropy.h"
    "/Users/itsvekus/Documents/Vinditek/safejumper-android/base/src/main/cpp/mbedtls/include/mbedtls/entropy_poll.h"
    "/Users/itsvekus/Documents/Vinditek/safejumper-android/base/src/main/cpp/mbedtls/include/mbedtls/error.h"
    "/Users/itsvekus/Documents/Vinditek/safejumper-android/base/src/main/cpp/mbedtls/include/mbedtls/gcm.h"
    "/Users/itsvekus/Documents/Vinditek/safejumper-android/base/src/main/cpp/mbedtls/include/mbedtls/havege.h"
    "/Users/itsvekus/Documents/Vinditek/safejumper-android/base/src/main/cpp/mbedtls/include/mbedtls/hmac_drbg.h"
    "/Users/itsvekus/Documents/Vinditek/safejumper-android/base/src/main/cpp/mbedtls/include/mbedtls/md.h"
    "/Users/itsvekus/Documents/Vinditek/safejumper-android/base/src/main/cpp/mbedtls/include/mbedtls/md2.h"
    "/Users/itsvekus/Documents/Vinditek/safejumper-android/base/src/main/cpp/mbedtls/include/mbedtls/md4.h"
    "/Users/itsvekus/Documents/Vinditek/safejumper-android/base/src/main/cpp/mbedtls/include/mbedtls/md5.h"
    "/Users/itsvekus/Documents/Vinditek/safejumper-android/base/src/main/cpp/mbedtls/include/mbedtls/md_internal.h"
    "/Users/itsvekus/Documents/Vinditek/safejumper-android/base/src/main/cpp/mbedtls/include/mbedtls/memory_buffer_alloc.h"
    "/Users/itsvekus/Documents/Vinditek/safejumper-android/base/src/main/cpp/mbedtls/include/mbedtls/net.h"
    "/Users/itsvekus/Documents/Vinditek/safejumper-android/base/src/main/cpp/mbedtls/include/mbedtls/net_sockets.h"
    "/Users/itsvekus/Documents/Vinditek/safejumper-android/base/src/main/cpp/mbedtls/include/mbedtls/oid.h"
    "/Users/itsvekus/Documents/Vinditek/safejumper-android/base/src/main/cpp/mbedtls/include/mbedtls/padlock.h"
    "/Users/itsvekus/Documents/Vinditek/safejumper-android/base/src/main/cpp/mbedtls/include/mbedtls/pem.h"
    "/Users/itsvekus/Documents/Vinditek/safejumper-android/base/src/main/cpp/mbedtls/include/mbedtls/pk.h"
    "/Users/itsvekus/Documents/Vinditek/safejumper-android/base/src/main/cpp/mbedtls/include/mbedtls/pk_internal.h"
    "/Users/itsvekus/Documents/Vinditek/safejumper-android/base/src/main/cpp/mbedtls/include/mbedtls/pkcs11.h"
    "/Users/itsvekus/Documents/Vinditek/safejumper-android/base/src/main/cpp/mbedtls/include/mbedtls/pkcs12.h"
    "/Users/itsvekus/Documents/Vinditek/safejumper-android/base/src/main/cpp/mbedtls/include/mbedtls/pkcs5.h"
    "/Users/itsvekus/Documents/Vinditek/safejumper-android/base/src/main/cpp/mbedtls/include/mbedtls/platform.h"
    "/Users/itsvekus/Documents/Vinditek/safejumper-android/base/src/main/cpp/mbedtls/include/mbedtls/platform_time.h"
    "/Users/itsvekus/Documents/Vinditek/safejumper-android/base/src/main/cpp/mbedtls/include/mbedtls/ripemd160.h"
    "/Users/itsvekus/Documents/Vinditek/safejumper-android/base/src/main/cpp/mbedtls/include/mbedtls/rsa.h"
    "/Users/itsvekus/Documents/Vinditek/safejumper-android/base/src/main/cpp/mbedtls/include/mbedtls/rsa_internal.h"
    "/Users/itsvekus/Documents/Vinditek/safejumper-android/base/src/main/cpp/mbedtls/include/mbedtls/sha1.h"
    "/Users/itsvekus/Documents/Vinditek/safejumper-android/base/src/main/cpp/mbedtls/include/mbedtls/sha256.h"
    "/Users/itsvekus/Documents/Vinditek/safejumper-android/base/src/main/cpp/mbedtls/include/mbedtls/sha512.h"
    "/Users/itsvekus/Documents/Vinditek/safejumper-android/base/src/main/cpp/mbedtls/include/mbedtls/ssl.h"
    "/Users/itsvekus/Documents/Vinditek/safejumper-android/base/src/main/cpp/mbedtls/include/mbedtls/ssl_cache.h"
    "/Users/itsvekus/Documents/Vinditek/safejumper-android/base/src/main/cpp/mbedtls/include/mbedtls/ssl_ciphersuites.h"
    "/Users/itsvekus/Documents/Vinditek/safejumper-android/base/src/main/cpp/mbedtls/include/mbedtls/ssl_cookie.h"
    "/Users/itsvekus/Documents/Vinditek/safejumper-android/base/src/main/cpp/mbedtls/include/mbedtls/ssl_internal.h"
    "/Users/itsvekus/Documents/Vinditek/safejumper-android/base/src/main/cpp/mbedtls/include/mbedtls/ssl_ticket.h"
    "/Users/itsvekus/Documents/Vinditek/safejumper-android/base/src/main/cpp/mbedtls/include/mbedtls/threading.h"
    "/Users/itsvekus/Documents/Vinditek/safejumper-android/base/src/main/cpp/mbedtls/include/mbedtls/timing.h"
    "/Users/itsvekus/Documents/Vinditek/safejumper-android/base/src/main/cpp/mbedtls/include/mbedtls/version.h"
    "/Users/itsvekus/Documents/Vinditek/safejumper-android/base/src/main/cpp/mbedtls/include/mbedtls/x509.h"
    "/Users/itsvekus/Documents/Vinditek/safejumper-android/base/src/main/cpp/mbedtls/include/mbedtls/x509_crl.h"
    "/Users/itsvekus/Documents/Vinditek/safejumper-android/base/src/main/cpp/mbedtls/include/mbedtls/x509_crt.h"
    "/Users/itsvekus/Documents/Vinditek/safejumper-android/base/src/main/cpp/mbedtls/include/mbedtls/x509_csr.h"
    "/Users/itsvekus/Documents/Vinditek/safejumper-android/base/src/main/cpp/mbedtls/include/mbedtls/xtea.h"
    )
endif()

