package cn.zjc.security;

/**
 * created by IntelliJ IDEA
 *
 * @author zjc
 * @time 2015/12/30-11:19
 */

import org.junit.Test;

public class TestENC {

    @Test
    public void TestMD5() {
        String MDs = Encryption.MD5Encode("Hello World!");
        System.out.println(MDs);
    }

    @Test
    public void TestSHA() {
        String SHAs = Encryption.SHAEncode("Hello World!");
        System.out.println(SHAs);
    }

    @Test
    public void TestDES() {
        String key = Encryption.generateDESKey("Java");
        System.out.println("key:" + key);
        String ENDESs = Encryption.DESEncrypt("Hello World!", key);
        System.out.println("encrype:" + ENDESs);
        String DEDESs = Encryption.DESDecrypt(ENDESs, key);
        System.out.println("decrypt:" + DEDESs);
    }
}
