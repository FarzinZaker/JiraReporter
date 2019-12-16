package jirareporter

import org.apache.commons.codec.binary.Hex

import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import java.security.Key

class AESCryption {

    private static final String ALGO = "AES"
    static final def keyValue = "JiraPlanner12345".getBytes()

    static String encrypt(String Data) throws Exception {
        Key key = generateKey()
        Cipher c = Cipher.getInstance(ALGO)
        c.init(Cipher.ENCRYPT_MODE, key)
        byte[] encVal = c.doFinal(Data.getBytes("UTF-8"))
        return new String(Hex.encodeHex(encVal))
    }

    static String decrypt(String encryptedData) throws Exception {
        Key key = generateKey()
        Cipher c = Cipher.getInstance(ALGO)
        c.init(Cipher.DECRYPT_MODE, key)
        byte[] decValue = c.doFinal(Hex.decodeHex(encryptedData.toCharArray()))
        return new String(decValue)
    }

    private static Key generateKey() throws Exception {
        Key key = new SecretKeySpec(keyValue, ALGO)
        return key
    }
}