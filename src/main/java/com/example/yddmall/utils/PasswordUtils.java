package com.example.yddmall.utils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PasswordUtils {
    // 加密算法参数
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int ITERATIONS = 65536; // 迭代次数
    private static final int KEY_LENGTH = 256;   // 密钥长度
    private static final int SALT_LENGTH = 16;   // 盐值长度(字节)

    /**
     * 生成加密后的密码和盐值
     * @param rawPassword 原始密码
     * @return 长度为2的数组，[0]为加密后的密码，[1]为盐值
     */
    public static String[] encryptPassword(String rawPassword) {
        try {
            // 1. 生成随机盐值
            byte[] salt = generateSalt();

            // 2. 加密密码
            byte[] encryptedPassword = hashPassword(rawPassword.toCharArray(), salt);

            // 3. 转换为Base64字符串便于存储
            String encodedPassword = Base64.getEncoder().encodeToString(encryptedPassword);
            String encodedSalt = Base64.getEncoder().encodeToString(salt);

            return new String[]{encodedPassword, encodedSalt};
        } catch (Exception e) {
            throw new RuntimeException("密码加密失败", e);
        }
    }

    /**
     * 验证密码是否匹配
     * @param rawPassword 待验证的原始密码
     * @param storedPassword 存储的加密密码
     * @param storedSalt 存储的盐值
     * @return 是否匹配
     */
    public static boolean verifyPassword(String rawPassword, String storedPassword, String storedSalt) {
        try {
            // 1. 解码存储的盐值和密码
            byte[] salt = Base64.getDecoder().decode(storedSalt);
            byte[] storedHash = Base64.getDecoder().decode(storedPassword);

            // 2. 使用相同的盐值加密待验证密码
            byte[] computedHash = hashPassword(rawPassword.toCharArray(), salt);

            // 3. 比较两个哈希值是否相同
            return slowEquals(storedHash, computedHash);
        } catch (Exception e) {
            throw new RuntimeException("密码验证失败", e);
        }
    }

    /**
     * 生成随机盐值
     */
    private static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return salt;
    }

    /**
     * 使用PBKDF2算法进行密码哈希
     */
    private static byte[] hashPassword(char[] password, byte[] salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH);
        SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
        return factory.generateSecret(spec).getEncoded();
    }

    /**
     * 安全比较两个字节数组，防止时序攻击
     */
    private static boolean slowEquals(byte[] a, byte[] b) {
        int diff = a.length ^ b.length;
        for (int i = 0; i < a.length && i < b.length; i++) {
            diff |= a[i] ^ b[i];
        }
        return diff == 0;
    }

    // 测试示例
    public static void main(String[] args) {
        // 原始密码
        String password = "user123456";

        // 加密密码
        String[] result = encryptPassword(password);
        String encryptedPwd = result[0];
        String salt = result[1];
        System.out.println("加密后的密码: " + encryptedPwd);
        System.out.println("盐值: " + salt);

        // 验证密码
        boolean isMatch = verifyPassword(password, encryptedPwd, salt);
        System.out.println("密码验证结果: " + isMatch); // 输出true

        // 验证错误密码
        boolean isWrong = verifyPassword("wrong123", encryptedPwd, salt);
        System.out.println("错误密码验证结果: " + isWrong); // 输出false
    }
}

