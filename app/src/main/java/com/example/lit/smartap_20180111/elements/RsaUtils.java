package com.example.lit.smartap_20180111.elements;

import android.util.Base64;
import android.util.Log;

import com.example.lit.smartap_20180111.Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

public class RsaUtils {
    public static final String CHARSET = "UTF-8";
    public static final String RSA_ALGORITHM = "RSA";
    private static final String TAG="RsaUtils";
    //私钥var privateKey = []byte(`-----BEGIN RSA PRIVATE KEY-----MIICXQIBAAKBgQDZsfv1qscqYdy4vY+P4e3cAtmvppXQcRvrF1cB4drkv0haU24Y7m5qYtT52Kr539RdbKKdLAM6s20lWy7+5C0DgacdwYWd/7PeCELyEipZJL07Vro7Ate8Bfjya+wltGK9+XNUIHiumUKULW4KDx21+1NLAUeJ6PeW+DAkmJWF6QIDAQABAoGBAJlNxenTQj6OfCl9FMR2jlMJjtMrtQT9InQEE7m3m7bLHeC+MCJOhmNVBjaMZpthDORdxIZ6oCuOf6Z2+Dl35lntGFh5J7S34UP2BWzF1IyyQfySCNexGNHKT1G1XKQtHmtc2gWWthEg+S6ciIyw2IGrrP2Rke81vYHExPrexf0hAkEA9Izb0MiYsMCB/jemLJB0Lb3Y/B8xjGjQFFBQT7bmwBVjvZWZVpnMnXi9sWGdgUpxsCuAIROXjZ40IRZ2C9EouwJBAOPjPvV8Sgw4vaseOqlJvSq/C/pIFx6RVznDGlc8bRg7SgTPpjHG4G+M3mVgpCX1a/EU1mB+fhiJ2LAZ/pTtY6sCQGaW9NwIWu3DRIVGCSMm0mYh/3X9DAcwLSJoctiODQ1Fq9rreDE5QfpJnaJdJfsIJNtX1F+L3YceeBXtW0Ynz2MCQBI89KP274Is5FkWkUFNKnuKUK4WKOuEXEO+LpR+vIhs7k6WQ8nGDd4/mujoJBr5mkrwDPwqA3N5TMNDQVGv8gMCQQCaKGJgWYgvo3/milFfImbp+m7/Y3vCptarldXrYQWOAQjxwc71ZGBFDITYvdgJM1MTqc8xQek1FXn1vfpy2c6O-----END RSA PRIVATE KEY-----`)
    //公钥var publicKey = []byte(`-----BEGIN PUBLIC KEY-----MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDZsfv1qscqYdy4vY+P4e3cAtmvppXQcRvrF1cB4drkv0haU24Y7m5qYtT52Kr539RdbKKdLAM6s20lWy7+5C0DgacdwYWd/7PeCELyEipZJL07Vro7Ate8Bfjya+wltGK9+XNUIHiumUKULW4KDx21+1NLAUeJ6PeW+DAkmJWF6QIDAQAB-----END PUBLIC KEY-----`)

    public static Map<String, Key> createKeys(int keySize){
        //为RSA算法创建一个KeyPairGenerator对象
        KeyPairGenerator kpg;
        try{
            kpg = KeyPairGenerator.getInstance(RSA_ALGORITHM);
        }catch(NoSuchAlgorithmException e){
            throw new IllegalArgumentException("No such algorithm-->[" + RSA_ALGORITHM + "]");
        }

        //初始化KeyPairGenerator对象,密钥长度
        kpg.initialize(keySize);
        //生成密匙对
        KeyPair keyPair = kpg.generateKeyPair();
        //得到公钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        //String publicKeyStr = Base64.encodeToString(publicKey.getEncoded(),Base64.DEFAULT);
        byte[] publicKeyStr = publicKey.getEncoded();
        //得到私钥
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        //String privateKeyStr = Base64.encodeToString(privateKey.getEncoded(),Base64.DEFAULT);
        byte[] privateKeyStr = privateKey.getEncoded();
        Map<String,Key> keyPairMap = new HashMap<String, Key>();
        keyPairMap.put("publicKey", publicKey);
        keyPairMap.put("privateKey", privateKey);

        return keyPairMap;
    }

    //QByteArray 公钥指数e = "60b9f369b49f943f7c2157e91a8112db571701558fe75856357d838aa33e3ecaf28ef4ebb053f0bbdaba49e117ae047244092d15d0d5ef45e74bbb30e652fb6b";
    // QByteArray 模n = "357dcd1ca1f549f1e850f5b3949eacb02aea6cad80680c4de3fc57219b644b1575dae60dc8ada09bde7b72000bbec6807277aa56a74f5875556a2dc95c59cd85c89cbbbb4ec5f1d42a7c5712978ff0c5a76b54a6445ce997d860e662aaa20c62338c4ee8b17dd8c733adc63ecaa48f552ddbafa7f26e55fdb67328c81400827";
    // QByteArray 私钥指数d = "7513b94e954ad0b430459dd3e02b793b20bcc8fc5586ab78cf8245358033dc1e5231f0cf270f37e8b0bdb021c9c210a8e5a5f798ef53d61c903fb6f1451f60b788d7f5d3f24fb617873797c0363135bd11bc10ee4ff89b10c5d6c21cee99013f506eb8bbe3698e9f10a0f66572c4f04a61cd89d16b5fbceec171f0ffbc1ac3";


    public static String getTAG() {
        return TAG;
    }

    /**
     * 得到公钥
     * @param publicKey 密钥字符串（经过base64编码）
     * @throws Exception
     */
    public static RSAPublicKey getPublicKey(RSAPublicKey publicKey) throws NoSuchAlgorithmException,InvalidKeySpecException {
        //通过X509编码的Key指令获得公钥对象
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        //byte[] bytes=publicKey.getBytes();
        //X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(publicKey);

        //摸
        String modulusstr = publicKey.getModulus().toString();
        String testmodulus = Utils.hexStr2DecStr("357dcd1ca1f549f1e850f5b3949eacb02aea6cad80680c4de3fc57219b644b1575dae60dc8ada09bde7b72000bbec6807277aa56a74f5875556a2dc95c59cd85c89cbbbb4ec5f1d42a7c5712978ff0c5a76b54a6445ce997d860e662aaa20c62338c4ee8b17dd8c733adc63ecaa48f552ddbafa7f26e55fdb67328c81400827");

        BigInteger modulus = new BigInteger(modulusstr);
        //BigInteger modulus = new BigInteger("");


        //公钥指数
        String publicExponentstr = publicKey.getPublicExponent().toString();
        //String testpublicexp = Utils.hexString2decString("60b9f369b49f943f7c2157e91a8112db571701558fe75856357d838aa33e3ecaf28ef4ebb053f0bbdaba49e117ae047244092d15d0d5ef45e74bbb30e652fb6b");
        BigInteger publicExponent = new BigInteger(publicExponentstr);
        //RSAPublicKey key = (RSAPublicKey) keyFactory.generatePublic(publicKey);
        RSAPublicKeySpec keySpec = new RSAPublicKeySpec(modulus,publicExponent);
        RSAPublicKey key = (RSAPublicKey)keyFactory.generatePublic(keySpec);
        /*
        Log.d(TAG, "getPublicKey: Modulus length="+key.getModulus().bitLength()+
                " Modulus="+key.getModulus().toString()+
                " PublicExponent length="+key.getPublicExponent().bitLength()+
                " PublicExponent="+key.getPublicExponent().toString());
                */
        return key;
    }

    public static RSAPublicKey getPublicKey(String modulus, String exponent) throws NoSuchAlgorithmException,InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        BigInteger bigmodulus = new BigInteger(modulus);
        BigInteger bigexponent = new BigInteger(exponent);
        RSAPublicKeySpec keySpec = new RSAPublicKeySpec(bigmodulus,bigexponent);
        RSAPublicKey key = (RSAPublicKey)keyFactory.generatePublic(keySpec);
        return key;
    }

    /**
     * 得到私钥
     * @param privateKey 密钥字符串（经过base64编码）
     * @throws Exception
     */
    public static RSAPrivateKey getPrivateKey(RSAPrivateKey privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        //通过PKCS#8编码的Key指令获得私钥对象
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        //PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(privateKey);
        //摸
        String modulusstr = privateKey.getModulus().toString();
        //String testmodulus = Utils.hexString2decString("357dcd1ca1f549f1e850f5b3949eacb02aea6cad80680c4de3fc57219b644b1575dae60dc8ada09bde7b72000bbec6807277aa56a74f5875556a2dc95c59cd85c89cbbbb4ec5f1d42a7c5712978ff0c5a76b54a6445ce997d860e662aaa20c62338c4ee8b17dd8c733adc63ecaa48f552ddbafa7f26e55fdb67328c81400827");

        BigInteger modulus = new BigInteger(modulusstr);

        //私钥指数
        String privatekExponentstr = privateKey.getPrivateExponent().toString();
        //String testprivate = Utils.hexString2decString("7513b94e954ad0b430459dd3e02b793b20bcc8fc5586ab78cf8245358033dc1e5231f0cf270f37e8b0bdb021c9c210a8e5a5f798ef53d61c903fb6f1451f60b788d7f5d3f24fb617873797c0363135bd11bc10ee4ff89b10c5d6c21cee99013f506eb8bbe3698e9f10a0f66572c4f04a61cd89d16b5fbceec171f0ffbc1ac3");
        BigInteger privateExponent = new BigInteger(privatekExponentstr);

        RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(modulus,privateExponent);
        RSAPrivateKey key = (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
        /*
        Log.d(TAG, "getPrivateKey: Modulus length="+key.getModulus().bitLength()+
                ";Modulus="+key.getModulus().toString()+
                "PrivateExponent length="+key.getPrivateExponent().bitLength()+
                "PrivateExponent="+key.getPrivateExponent().toString());
                */
        return key;
    }

    public static RSAPrivateKey getPrivateKey(String modulus, String exponent) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        BigInteger bigmodulus = new BigInteger(modulus);
        BigInteger bigprivateExponent = new BigInteger(exponent);
        RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(bigmodulus,bigprivateExponent);
        RSAPrivateKey key = (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
        return key;
    }

    public static byte[] publicEncrypt(byte[] data,RSAPublicKey publicKey){
        try{
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return cipher.doFinal(data);
            //return Base64.encodeToString(rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, data.getBytes(CHARSET), publicKey.getModulus().bitLength()),Base64.DEFAULT);
        }catch(Exception e){
            throw new RuntimeException("加密字符串[" + data + "]时遇到异常", e);
        }
    }

    /**
     * 公钥加密
     * @param data 明文数据
     * @param publicKey 公钥
     * @return 使用公钥加密后的密文
     */
    public static byte[] publicEncrypt(String data, RSAPublicKey publicKey){
        try{
            return publicEncrypt(data.getBytes(CHARSET),publicKey);
            //return Base64.encodeToString(rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, data.getBytes(CHARSET), publicKey.getModulus().bitLength()),Base64.DEFAULT);
        }catch(Exception e){
            throw new RuntimeException("加密字符串[" + data + "]时遇到异常", e);
        }
    }

    /**
     * 私钥解密
     * @param data 加密后的数据
     * @param privateKey 私钥
     * @return 解密后的明文
     */

    public static byte[] privateDecrypt(byte[] data, RSAPrivateKey privateKey){
        try{
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] bytes=cipher.doFinal(data);
            return bytes;
        }catch(Exception e){
            throw new RuntimeException("解密字符串[" + Utils.convertByteToHexString(data) + "]时遇到异常", e);
        }
    }

    /**
     * 私钥加密
     * @param data 明文
     * @param privateKey 私钥
     * @return 使用私钥加密后的密文
     */

    public static String privateEncrypt(String data, RSAPrivateKey privateKey){
        try{
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            return Base64.encodeToString(rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, data.getBytes(CHARSET), privateKey.getModulus().bitLength()),Base64.DEFAULT);
        }catch(Exception e){
            throw new RuntimeException("加密字符串[" + data + "]时遇到异常", e);
        }
    }

    /**
     * 公钥解密
     * @param data
     * @param publicKey
     * @return
     */

    public static String publicDecrypt(String data, RSAPublicKey publicKey){
        try{
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            return new String(rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, Base64.decode(data,Base64.DEFAULT), publicKey.getModulus().bitLength()), CHARSET);
        }catch(Exception e){
            throw new RuntimeException("解密字符串[" + data + "]时遇到异常", e);
        }
    }


    /** 分包 暂时未用
     * @param cipher
     * @param opmode
     * @param datas
     * @param keySize
     * @return
     */
    private static byte[] rsaSplitCodec(Cipher cipher, int opmode, byte[] datas, int keySize){
        int maxBlock = 0;
        if(opmode == Cipher.DECRYPT_MODE){
            maxBlock = keySize / 8;
        }else{
            maxBlock = keySize / 8 - 11;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] buff;
        int i = 0;
        try{
            while(datas.length > offSet){
                if(datas.length-offSet > maxBlock){
                    buff = cipher.doFinal(datas, offSet, maxBlock);
                }else{
                    buff = cipher.doFinal(datas, offSet, datas.length-offSet);
                }
                out.write(buff, 0, buff.length);
                i++;
                offSet = i * maxBlock;
            }
        }catch(Exception e){
            throw new RuntimeException("加解密阀值为["+maxBlock+"]的数据时发生异常", e);
        }
        byte[] resultDatas = out.toByteArray();
        try {
            out.close();
        }catch (IOException e){
            throw new RuntimeException("OutputStream close error:"+e,e);
        }
        return resultDatas;
    }
}
