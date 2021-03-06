/*
 * Copyright 2013-2023 Peng Li <madding.lip@gmail.com> Licensed under the AQNote License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.aqnote.com/licenses/LICENSE-1.0 Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and limitations under the
 * License.
 */
package com.aqnote.shared.encrypt.symmetric;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import com.aqnote.shared.encrypt.AQProviderUtil;

/**
 * 类AESEncrypt.java的实现描述：AES加密类
 * 
 * @author madding.lip May 7, 2012 3:09:01 PM
 */
public class AES {

    public final static String  CIPHER_NAME      = "AES/CBC/PKCS5Padding";
    private static final String ENCODE_UTF_8     = "UTF-8";
    public final static int     DEFAULT_KEY_SIZE = 128;

    private static Cipher       encodeCipher;
    private static Cipher       decodeCipher;

    static {
        AQProviderUtil.addBCProvider();
        // key size: 16 24 32
        generateCipher("www.aqnote.com/1");
    }

    public static byte[] encrypt(byte[] plaintext) throws RuntimeException {
        try {
            return encodeCipher.doFinal(plaintext);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String encrypt(String plaintext) throws RuntimeException {
        try {
            if (plaintext == null) {
                return null;
            }
            return new String(Hex.encodeHex(encodeCipher.doFinal(plaintext.getBytes(ENCODE_UTF_8))));
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] decrypt(byte[] cryptotext) throws RuntimeException {
        try {
            return decodeCipher.doFinal(cryptotext);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String decrypt(String cryptotext) throws RuntimeException {
        try {
            byte[] clearByte;
            if (cryptotext == null) {
                return null;
            }
            clearByte = decodeCipher.doFinal(Hex.decodeHex(cryptotext.toCharArray()));
            return new String(clearByte, ENCODE_UTF_8);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        } catch (DecoderException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private static void generateCipher(String rawKey) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(rawKey.getBytes(ENCODE_UTF_8), CIPHER_NAME);
            encodeCipher = Cipher.getInstance(CIPHER_NAME);
            encodeCipher.init(Cipher.ENCRYPT_MODE, keySpec);

            decodeCipher = Cipher.getInstance(CIPHER_NAME);
            byte iv[] = encodeCipher.getIV();
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            decodeCipher.init(Cipher.DECRYPT_MODE, keySpec, ivParameterSpec);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }

    }

}
