package cn.zjc.security;

/**
 * created by IntelliJ IDEA
 *
 * @author zjc
 * @time 2015/12/30-10:48
 */

import javax.crypto.*;
import javax.crypto.interfaces.DHPrivateKey;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Encryption {

    /**
     * MD5�㷨
     */
    private static final String ALGORITHM_MD5 = "MD5";
    /**
     * SHA�㷨
     */
    private static final String ALGORITHM_SHA = "SHA";
    /**
     * HMAC�㷨
     */
    private static final String ALGORITHM_MAC = "HmacMD5";
    /**
     * DES�㷨
     */
    private static final String ALGORITHM_DES = "DES";
    /**
     * PBE�㷨
     */
    private static final String ALGORITHM_PBE = "PBEWITHMD5andDES";

    /**
     * AESkey
     */
    private static final String KEY_AES = "AES";

    /**
     * AES�㷨
     */
    private static final String ALGORITHM_AES = "AES/CBC/PKCS5Padding";

    /**
     * RSA�㷨
     */
    private static final String KEY_ALGORITHM = "RSA";

    /**
     * ����ǩ��
     */
    private static final String SIGNATURE_ALGORITHM = "MD5withRSA";

    /**
     * ��Կ
     */
    private static final String RSAPUBLIC_KEY = "RSAPublicKey";

    /**
     * ˽Կ
     */
    private static final String RSAPRIVATE_KEY = "RSAPrivateKey";

    /**
     * D-H�㷨
     */
    private static final String ALGORITHM_DH = "DH";

    /**
     * Ĭ����Կ�ֽ���
     * <p>
     * <pre>
     * DH
     * Default Keysize 1024
     * Keysize must be a multiple of 64, ranging from 512 to 1024 (inclusive).
     * </pre>
     */
    private static final int DH_KEY_SIZE = 1024;

    /**
     * DH��������Ҫһ�ֶԳƼ����㷨�����ݼ��ܣ���������ʹ��DES��Ҳ����ʹ�������ԳƼ����㷨��
     */
    private static final String SECRET_ALGORITHM = "DES";

    /**
     * DH��Կ
     */
    private static final String DHPUBLIC_KEY = "DHPublicKey";

    /**
     * DH˽Կ
     */
    private static final String DHPRIVATE_KEY = "DHPrivateKey";

    /**
     * Java��Կ��(Java Key Store��JKS)KEY_STORE
     */
    private static final String KEY_STORE = "JKS";

    private static final String X509 = "X.509";

    /**
     * ��ϢժҪ�㷨
     *
     * @param algorithm �㷨����
     * @param data      Ҫ���ܵ��ַ���
     * @return ���ؼ��ܺ��ժҪ��Ϣ
     */
    private static String encryptEncode(String algorithm, String data) {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            return EncryptionUtil.byteArrayToHexStr(md.digest(data.getBytes()));
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * ʹ��MD5����
     *
     * @param data Ҫ���ܵ��ַ���
     * @return ���ؼ��ܺ����Ϣ
     */
    public static String MD5Encode(String data) {
        return encryptEncode(ALGORITHM_MD5, data);
    }

    public static void main(String[] args) {
        String pwd = "lqw124";
        System.out.println(MD5Encode(pwd));
    }

    /**
     * ʹ��SHA����
     *
     * @param data Ҫ���ܵ��ַ���
     * @return ���ؼ��ܺ����Ϣ
     */
    public static String SHAEncode(String data) {
        return encryptEncode(ALGORITHM_SHA, data);
    }

    /**
     * ����HMAC��Կ
     *
     * @return ������Կ��Ϣ
     */
    public static String generateMACKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM_MAC);
            SecretKey secretKey = keyGenerator.generateKey();
            return EncryptionUtil.byteArrayToBase64Str(secretKey.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * ʹ��HMAC����
     *
     * @param data Ҫ���ܵ��ַ���
     * @param key  ��Կ
     * @return ���ؼ��ܺ����Ϣ
     */
    public static String HMACEncode(String data, String key) {
        Key k = toKey(key, ALGORITHM_MAC);
        try {
            Mac mac = Mac.getInstance(k.getAlgorithm());
            mac.init(k);
            return EncryptionUtil.byteArrayToBase64Str(mac.doFinal(data.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * ��base64��������Կ�ַ���ת������Կ����
     *
     * @param key       ��Կ�ַ���
     * @param algorithm �����㷨
     * @return ������Կ����
     */
    private static Key toKey(String key, String algorithm) {
        SecretKey secretKey = new SecretKeySpec(EncryptionUtil.base64StrToByteArray(key), algorithm);
        return secretKey;
    }

    /**
     * ����DES��Կ
     *
     * @param seed ��Կ����
     * @return ����base64�������Կ�ַ���
     */
    public static String generateDESKey(String seed) {
        try {
            KeyGenerator kg = KeyGenerator.getInstance(ALGORITHM_DES);
            kg.init(new SecureRandom(seed.getBytes()));
            SecretKey secretKey = kg.generateKey();
            return EncryptionUtil.byteArrayToBase64Str(secretKey.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * DES����
     *
     * @param data Ҫ���ܵ�����
     * @param key  ��Կ
     * @return ���ؼ��ܺ������(����base64����)
     */
    public static String DESEncrypt(String data, String key) {
        return DESCipher(data, key, Cipher.ENCRYPT_MODE);
    }

    /**
     * DES����
     *
     * @param data Ҫ���ܵ�����
     * @param key  ��Կ
     * @return ���ؽ��ܺ������
     */
    public static String DESDecrypt(String data, String key) {
        return DESCipher(data, key, Cipher.DECRYPT_MODE);
    }

    /**
     * DES�ļ��ܽ���
     *
     * @param data Ҫ���ܻ���ܵ�����
     * @param key  ��Կ
     * @param mode ���ܻ����ģʽ
     * @return ���ؼ��ܻ���ܵ�����
     */
    private static String DESCipher(String data, String key, int mode) {
        try {
            Key k = toKey(key, ALGORITHM_DES);
            Cipher cipher = Cipher.getInstance(ALGORITHM_DES);
            cipher.init(mode, k);
            return mode == Cipher.DECRYPT_MODE ? new String(cipher.doFinal(EncryptionUtil.base64StrToByteArray(data))) : EncryptionUtil.byteArrayToBase64Str(cipher.doFinal(data.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * ������
     *
     * @return ����base64����������Ϣ
     */
    public static String generatePBESalt() {
        byte[] salt = new byte[8];
        Random random = new Random();
        random.nextBytes(salt);
        return EncryptionUtil.byteArrayToBase64Str(salt);
    }

    /**
     * PBE(Password-based encryption�����������)����
     *
     * @param data     Ҫ���ܵ�����
     * @param password ����
     * @param salt     ��
     * @return ���ؼ��ܺ������(����base64����)
     */
    public static String PBEEncrypt(String data, String password, String salt) {
        return PBECipher(data, password, salt, Cipher.ENCRYPT_MODE);
    }

    /**
     * PBE(Password-based encryption�����������)����
     *
     * @param data     Ҫ���ܵ�����
     * @param password ����
     * @param salt     ��
     * @return ���ؽ��ܺ������
     */
    public static String PBEDecrypt(String data, String password, String salt) {
        return PBECipher(data, password, salt, Cipher.DECRYPT_MODE);
    }

    /**
     * PBE���ܽ���
     *
     * @param data     Ҫ���ܽ��ܵ���Ϣ
     * @param password ����
     * @param salt     ��
     * @param mode     ���ܻ����ģʽ
     * @return ���ؼ��ܽ��ܺ������
     */
    private static String PBECipher(String data, String password, String salt, int mode) {
        try {
            Key secretKey = toPBEKey(password);
            PBEParameterSpec paramSpec = new PBEParameterSpec(EncryptionUtil.base64StrToByteArray(salt), 100);
            Cipher cipher = Cipher.getInstance(ALGORITHM_PBE);
            cipher.init(mode, secretKey, paramSpec);
            return mode == Cipher.DECRYPT_MODE ? new String(cipher.doFinal(EncryptionUtil.base64StrToByteArray(data))) : EncryptionUtil.byteArrayToBase64Str(cipher.doFinal(data.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * ����PBEkey
     *
     * @param password ʹ�õ�����
     * @return �������ɵ�PBEkey
     */
    private static Key toPBEKey(String password) {
        PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray());
        try {
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM_PBE);
            SecretKey secretKey = keyFactory.generateSecret(keySpec);
            return secretKey;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * ����AESkey
     *
     * @param keySize key��λ��
     * @param seed    �������
     * @return ����base64������key��Ϣ
     */
    public static String generateAESKey(int keySize, String seed) {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance(KEY_AES);
            kgen.init(keySize, new SecureRandom(seed.getBytes()));
            SecretKey key = kgen.generateKey();
            return EncryptionUtil.byteArrayToBase64Str(key.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * AES����
     *
     * @param data               Ҫ���ܵ�����
     * @param key                ��Կ
     * @param algorithmParameter �㷨����
     * @return ���ؼ�������
     */
    public static String AESEncrypt(String data, String key, String algorithmParameter) {
        return AESCipher(data, key, algorithmParameter, Cipher.ENCRYPT_MODE);
    }

    /**
     * AES����
     *
     * @param data               Ҫ���ܵ�����
     * @param key                ��Կ
     * @param algorithmParameter �㷨����
     * @return ���ؽ�������
     */
    public static String AESDecrypt(String data, String key, String algorithmParameter) {
        return AESCipher(data, key, algorithmParameter, Cipher.DECRYPT_MODE);
    }

    /**
     * ʵ��AES���ܽ���
     *
     * @param data               Ҫ���ܻ���ܵ�����
     * @param key                ��Կ
     * @param algorithmParameter �㷨����
     * @param mode               ���ܻ����
     * @return ���ؼ��ܻ���ܵ�����
     */
    private static String AESCipher(String data, String key, String algorithmParameter, int mode) {
        try {
            Key k = toKey(key, KEY_AES);
            AlgorithmParameterSpec paramSpec = new IvParameterSpec(algorithmParameter.getBytes());
            Cipher ecipher = Cipher.getInstance(ALGORITHM_AES);
            ecipher.init(mode, k, paramSpec);
            return mode == Cipher.DECRYPT_MODE ? new String(ecipher.doFinal(EncryptionUtil.base64StrToByteArray(data))) : EncryptionUtil.byteArrayToBase64Str(ecipher.doFinal(data.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * ����ǩ��
     *
     * @param data       Ҫǩ��������
     * @param privateKey ˽Կ
     * @return ����ǩ����Ϣ
     */
    public static String RSASign(String data, String privateKey) {
        try {
            // ������base64�����˽Կ
            byte[] keyBytes = EncryptionUtil.base64StrToByteArray(privateKey);
            // ����PKCS8EncodedKeySpec����
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
            // KEY_ALGORITHM ָ���ļ����㷨
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            // ȡ˽Կ�׶���
            PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec);
            // ��˽Կ����Ϣ��������ǩ��
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initSign(priKey);
            signature.update(EncryptionUtil.base64StrToByteArray(data));
            return EncryptionUtil.byteArrayToBase64Str(signature.sign());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * ��֤ǩ��
     *
     * @param data      Ҫ��֤������
     * @param publicKey ��Կ
     * @param sign      ǩ����Ϣ
     * @return ������֤�ɹ�״̬
     */
    public static boolean RSAVerify(String data, String publicKey, String sign) {
        try {
            // ������base64����Ĺ�Կ
            byte[] keyBytes = EncryptionUtil.base64StrToByteArray(publicKey);
            // ����X509EncodedKeySpec����
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            // KEY_ALGORITHM ָ���ļ����㷨
            Signature signature;
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            // ȡ��Կ�׶���
            PublicKey pubKey = keyFactory.generatePublic(keySpec);
            signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initVerify(pubKey);
            signature.update(EncryptionUtil.base64StrToByteArray(data));
            // ��֤ǩ���Ƿ�����
            return signature.verify(EncryptionUtil.base64StrToByteArray(sign));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * ˽Կ����
     *
     * @param data Ҫ���ܵ��ַ���
     * @param key  ˽Կ
     * @return ���ؽ��ܺ���ַ���
     */
    public static String RSADecryptByPrivateKey(String data, String key) {
        try {
            // ����Կ����
            byte[] keyBytes = EncryptionUtil.base64StrToByteArray(key);
            // ȡ��˽Կ
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
            // �����ݽ���
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return new String(cipher.doFinal(EncryptionUtil.base64StrToByteArray(data)));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * ��Կ����
     *
     * @param data Ҫ���ܵ�����
     * @param key  ��Կ
     * @return ���ؽ��ܺ������
     */
    public static String RSADecryptByPublicKey(String data, String key) {
        try {
            // ����Կ����
            byte[] keyBytes = EncryptionUtil.base64StrToByteArray(key);
            // ȡ�ù�Կ
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            Key publicKey = keyFactory.generatePublic(x509KeySpec);
            // �����ݽ���
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            return new String(cipher.doFinal(EncryptionUtil.base64StrToByteArray(data)));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * ��Կ����
     *
     * @param data Ҫ���ܵ�����
     * @param key  ��Կ
     * @return ���ؼ��ܵ�����
     */
    public static String RSAEncryptByPublicKey(String data, String key) {
        try {
            // �Թ�Կ����
            byte[] keyBytes = EncryptionUtil.base64StrToByteArray(key);
            // ȡ�ù�Կ
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            Key publicKey = keyFactory.generatePublic(x509KeySpec);
            // �����ݼ���
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return EncryptionUtil.byteArrayToBase64Str(cipher.doFinal(data.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * ˽Կ����
     *
     * @param data Ҫ���ܵ�����
     * @param key  ˽Կ
     * @return ���ؼ��ܺ������
     */
    public static String RSAEncryptByPrivateKey(String data, String key) {
        try {
            // ����Կ����
            byte[] keyBytes = EncryptionUtil.base64StrToByteArray(key);
            // ȡ��˽Կ
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
            // �����ݼ���
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            return EncryptionUtil.byteArrayToBase64Str(cipher.doFinal(data.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * ���˽Կ
     *
     * @param keyMap ��Կ��
     * @return ���ؾ���base64�����˽Կ
     */
    public static String getRSAPrivateKey(Map<String, Object> keyMap) {
        Key key = (Key) keyMap.get(RSAPRIVATE_KEY);
        return EncryptionUtil.byteArrayToBase64Str(key.getEncoded());
    }

    /**
     * ��ù�Կ(base64����)
     *
     * @param keyMap ��Կ��
     * @return ���ؾ���base64����Ĺ�Կ
     */
    public static String getRSAPublicKey(Map<String, Object> keyMap) {
        Key key = (Key) keyMap.get(RSAPUBLIC_KEY);
        return EncryptionUtil.byteArrayToBase64Str(key.getEncoded());
    }

    /**
     * ��ʼ����Կ��
     *
     * @return ������Կ��
     */
    public static Map<String, Object> initRSAKey() {
        Map<String, Object> keyMap = new HashMap<String, Object>(2);
        try {
            KeyPairGenerator keyPairGen = KeyPairGenerator
                    .getInstance(KEY_ALGORITHM);
            keyPairGen.initialize(1024);
            KeyPair keyPair = keyPairGen.generateKeyPair();
            // ��Կ
            RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
            // ˽Կ
            RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
            keyMap.put(RSAPUBLIC_KEY, publicKey);
            keyMap.put(RSAPRIVATE_KEY, privateKey);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return keyMap;
    }

    /**
     * ��ʼ���׷���Կ��
     *
     * @return ���ؼ׷���Կ��
     */
    public static Map<String, Object> initDHKey() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM_DH);
            keyPairGenerator.initialize(DH_KEY_SIZE);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            // �׷���Կ
            DHPublicKey publicKey = (DHPublicKey) keyPair.getPublic();
            // �׷�˽Կ
            DHPrivateKey privateKey = (DHPrivateKey) keyPair.getPrivate();
            Map<String, Object> keyMap = new HashMap<String, Object>(2);
            keyMap.put(DHPUBLIC_KEY, publicKey);
            keyMap.put(DHPRIVATE_KEY, privateKey);
            return keyMap;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * ʹ�ü׷���Կ��ʼ���ҷ���Կ��
     *
     * @param key �׷���Կ
     * @return �����ҷ���Կ��
     */
    public static Map<String, Object> initDHKey(String key) {
        try {
            // �����׷���Կ
            byte[] keyBytes = EncryptionUtil.base64StrToByteArray(key);
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_DH);
            PublicKey pubKey = keyFactory.generatePublic(x509KeySpec);
            // �ɼ׷���Կ�����ҷ���Կ
            DHParameterSpec dhParamSpec = ((DHPublicKey) pubKey).getParams();
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(keyFactory.getAlgorithm());
            keyPairGenerator.initialize(dhParamSpec);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            // �ҷ���Կ
            DHPublicKey publicKey = (DHPublicKey) keyPair.getPublic();
            // �ҷ�˽Կ
            DHPrivateKey privateKey = (DHPrivateKey) keyPair.getPrivate();
            Map<String, Object> keyMap = new HashMap<String, Object>(2);
            keyMap.put(DHPUBLIC_KEY, publicKey);
            keyMap.put(DHPRIVATE_KEY, privateKey);
            return keyMap;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * DH����
     *
     * @param data       Ҫ���ܵ�����
     * @param publicKey  �׷����ҷ���Կ
     * @param privateKey �׷����ҷ�˽Կ
     * @return ���ܽ��
     */
    public static String DHEncrypt(String data, String publicKey, String privateKey) {
        try {
            // ���ɱ�����Կ
            SecretKey secretKey = getDHSecretKey(publicKey, privateKey);
            // ���ݼ���
            Cipher cipher = Cipher.getInstance(secretKey.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return EncryptionUtil.byteArrayToBase64Str(cipher.doFinal(data.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * DH����
     *
     * @param data       Ҫ���ܵ�����
     * @param publicKey  ��Կ
     * @param privateKey ˽Կ
     * @return ���ؽ��ܽ��
     */
    public static String DHDecrypt(String data, String publicKey, String privateKey) {
        try {
            // ���ɱ�����Կ
            SecretKey secretKey = getDHSecretKey(publicKey, privateKey);
            // ���ݽ���
            Cipher cipher = Cipher.getInstance(secretKey.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(EncryptionUtil.base64StrToByteArray(data)));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * ���ɱ�����Կ
     *
     * @param publicKey  ��Կ
     * @param privateKey ˽Կ
     * @return ���ر�����Կ
     */
    private static SecretKey getDHSecretKey(String publicKey, String privateKey) {
        try {
            // ��ʼ����Կ
            byte[] pubKeyBytes = EncryptionUtil.base64StrToByteArray(publicKey);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_DH);
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(pubKeyBytes);
            PublicKey pubKey = keyFactory.generatePublic(x509KeySpec);
            // ��ʼ��˽Կ
            byte[] priKeyBytes = EncryptionUtil.base64StrToByteArray(privateKey);
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(priKeyBytes);
            Key priKey = keyFactory.generatePrivate(pkcs8KeySpec);
            KeyAgreement keyAgree = KeyAgreement.getInstance(keyFactory.getAlgorithm());
            keyAgree.init(priKey);
            keyAgree.doPhase(pubKey, true);
            // ���ɱ�����Կ
            SecretKey secretKey = keyAgree.generateSecret(SECRET_ALGORITHM);
            return secretKey;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * ��ȡ˽Կ
     *
     * @param keyMap ��Կ��
     * @return ����base64�����˽Կ
     */
    public static String getDHPrivateKey(Map<String, Object> keyMap) {
        Key key = (Key) keyMap.get(DHPRIVATE_KEY);
        return EncryptionUtil.byteArrayToBase64Str(key.getEncoded());
    }

    /**
     * ��ȡ��Կ
     *
     * @param keyMap ��Կ��
     * @return ����base64����Ĺ�Կ
     */
    public static String getDHPublicKey(Map<String, Object> keyMap) {
        Key key = (Key) keyMap.get(DHPUBLIC_KEY);
        return EncryptionUtil.byteArrayToBase64Str(key.getEncoded());
    }

    /**
     * ��ȡ˽Կ
     *
     * @param keyStorePath keystore�ļ�·��
     * @param alias        ����
     * @param password     ����
     * @return ����˽Կ
     */
    private static PrivateKey getKeyStorePrivateKey(String keyStorePath, String alias, String password) {
        try {
            KeyStore ks = getKeyStore(keyStorePath, password);
            PrivateKey key = (PrivateKey) ks.getKey(alias, password.toCharArray());
            return key;
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * ��ȡ��Կ
     *
     * @param certificatePath ֤���ļ�·��
     * @return ���ع�Կ
     */
    private static PublicKey getCertificatePublicKey(String certificatePath) {
        try {
            Certificate certificate = getCertificate(certificatePath);
            PublicKey key = certificate.getPublicKey();
            return key;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * ����֤���ļ�
     *
     * @param certificatePath ֤���ļ�·��
     * @return ����֤��
     */
    private static Certificate getCertificate(String certificatePath) {
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance(X509);
            FileInputStream in = new FileInputStream(certificatePath);
            Certificate certificate = certificateFactory.generateCertificate(in);
            in.close();
            return certificate;
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * ��ȡ֤��
     *
     * @param keyStorePath keystore�ļ�·��
     * @param alias        ����
     * @param password     ����
     * @return ����֤��
     */
    private static Certificate getCertificate(String keyStorePath, String alias, String password) {
        try {
            KeyStore ks = getKeyStore(keyStorePath, password);
            Certificate certificate = ks.getCertificate(alias);
            return certificate;
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * ����KeyStore�ļ�
     *
     * @param keyStorePath keystore�ļ���ַ
     * @param password     keystore����
     * @return ����KeyStore
     */
    private static KeyStore getKeyStore(String keyStorePath, String password) {
        try {
            FileInputStream is = new FileInputStream(keyStorePath);
            KeyStore ks = KeyStore.getInstance(KEY_STORE);
            ks.load(is, password.toCharArray());
            is.close();
            return ks;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * ��������
     *
     * @param data         Ҫ���ܵ�����
     * @param keyStorePath keystore·��
     * @param alias        ����
     * @param password     ����
     * @return ���ؼ��ܺ������
     */
    public static String encryptByPrivateKey(String data, String keyStorePath,
                                             String alias, String password) {
        try {
            // ȡ��˽Կ
            PrivateKey privateKey = getKeyStorePrivateKey(keyStorePath, alias, password);
            // �����ݼ���
            Cipher cipher = Cipher.getInstance(privateKey.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            return EncryptionUtil.byteArrayToBase64Str(cipher.doFinal(data.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * ˽Կ����
     *
     * @param data         Ҫ���ܵ�����
     * @param keyStorePath keystore·��
     * @param alias        ����
     * @param password     ����
     * @return ���ؽ��ܺ������
     */
    public static String decryptByPrivateKey(String data, String keyStorePath, String alias, String password) {
        try {
            // ȡ��˽Կ
            PrivateKey privateKey = getKeyStorePrivateKey(keyStorePath, alias, password);
            // �����ݼ���
            Cipher cipher = Cipher.getInstance(privateKey.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return new String(cipher.doFinal(EncryptionUtil.base64StrToByteArray(data)));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * ˽Կ����
     *
     * @param data            Ҫ���ܵ�����
     * @param certificatePath ֤��·��
     * @return ���ؼ��ܺ����Ϣ
     */
    public static String encryptByPublicKey(String data, String certificatePath) {
        try {
            // ȡ�ù�Կ
            PublicKey publicKey = getCertificatePublicKey(certificatePath);
            // �����ݼ���
            Cipher cipher = Cipher.getInstance(publicKey.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return EncryptionUtil.byteArrayToBase64Str(cipher.doFinal(data.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * ��Կ����
     *
     * @param data            Ҫ���ܵ�����
     * @param certificatePath ֤��·��
     * @return ���ؽ�����Ϣ
     */
    public static String decryptByPublicKey(String data, String certificatePath) {
        try {
            // ȡ�ù�Կ
            PublicKey publicKey = getCertificatePublicKey(certificatePath);
            // �����ݼ���
            Cipher cipher = Cipher.getInstance(publicKey.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            return new String(cipher.doFinal(EncryptionUtil.base64StrToByteArray(data)));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * ��֤֤���Ƿ����
     *
     * @param certificatePath ֤��·��
     * @return ������֤���
     */
    public static boolean verifyCertificate(String certificatePath) {
        return verifyCertificate(new Date(), certificatePath);
    }

    /**
     * ��֤֤���Ƿ����
     *
     * @param date            ����
     * @param certificatePath ֤��·��
     * @return ������֤���
     */
    public static boolean verifyCertificate(Date date, String certificatePath) {
        boolean status = true;
        try {
            // ȡ��֤��
            Certificate certificate = getCertificate(certificatePath);
            // ��֤֤���Ƿ���ڻ���Ч
            status = verifyCertificate(date, certificate);
        } catch (Exception e) {
            status = false;
        }
        return status;
    }

    /**
     * ��֤֤���Ƿ����
     *
     * @param date        ����
     * @param certificate ֤��
     * @return ������֤���
     */
    private static boolean verifyCertificate(Date date, Certificate certificate) {
        boolean status = true;
        try {
            X509Certificate x509Certificate = (X509Certificate) certificate;
            x509Certificate.checkValidity(date);
        } catch (Exception e) {
            status = false;
        }
        return status;
    }

    /**
     * �������ݽ���ǩ��
     *
     * @param sign         Ҫǩ������Ϣ
     * @param keyStorePath keystore�ļ�λ��
     * @param alias        ����
     * @param password     ����
     * @return ����ǩ����Ϣ
     */
    public static String sign(String sign, String keyStorePath, String alias, String password) {
        try {
            // ���֤��
            X509Certificate x509Certificate = (X509Certificate) getCertificate(
                    keyStorePath, alias, password);
            // ��ȡ˽Կ
            KeyStore ks = getKeyStore(keyStorePath, password);
            // ȡ��˽Կ
            PrivateKey privateKey = (PrivateKey) ks.getKey(alias, password
                    .toCharArray());
            // ����ǩ��
            Signature signature = Signature.getInstance(x509Certificate
                    .getSigAlgName());
            signature.initSign(privateKey);
            signature.update(EncryptionUtil.base64StrToByteArray(sign));
            return EncryptionUtil.byteArrayToBase64Str(signature.sign());
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * ��֤ǩ����Ϣ
     *
     * @param data            Ҫ��֤����Ϣ
     * @param sign            ǩ����Ϣ
     * @param certificatePath ֤��·��
     * @return ������֤���
     */
    public static boolean verify(String data, String sign, String certificatePath) {
        try {
            // ���֤��
            X509Certificate x509Certificate = (X509Certificate) getCertificate(certificatePath);
            // ��ù�Կ
            PublicKey publicKey = x509Certificate.getPublicKey();
            // ����ǩ��
            Signature signature = Signature.getInstance(x509Certificate
                    .getSigAlgName());
            signature.initVerify(publicKey);
            signature.update(EncryptionUtil.base64StrToByteArray(data));
            return signature.verify(EncryptionUtil.base64StrToByteArray(sign));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * ��֤֤��
     *
     * @param date         ����
     * @param keyStorePath keystore�ļ�·��
     * @param alias        ����
     * @param password     ����
     * @return ������֤���
     */
    public static boolean verifyCertificate(Date date, String keyStorePath,
                                            String alias, String password) {
        boolean status = true;
        try {
            Certificate certificate = getCertificate(keyStorePath, alias,
                    password);
            status = verifyCertificate(date, certificate);
        } catch (Exception e) {
            status = false;
        }
        return status;
    }

    /**
     * ��֤֤��
     *
     * @param keyStorePath keystore�ļ�·��
     * @param alias        ����
     * @param password     ����
     * @return ������֤���
     */
    public static boolean verifyCertificate(String keyStorePath, String alias,
                                            String password) {
        return verifyCertificate(new Date(), keyStorePath, alias, password);
    }

}


