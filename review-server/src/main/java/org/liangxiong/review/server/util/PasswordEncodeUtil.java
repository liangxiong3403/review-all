package org.liangxiong.review.server.util;

import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;

import java.util.HashMap;
import java.util.Map;

/**
 * @author liangxiong
 * @version 1.0.0
 * @date 2020-07-14
 * @description 密码处理工具类
 **/
public class PasswordEncodeUtil {

    private PasswordEncodeUtil() {}

    /**
     * 密码处理工具
     *
     * @return
     */
    public static PasswordEncoder getPasswordEncoderInstance() {
        String idForEncode = "bcrypt";
        Map<String, PasswordEncoder> encoders = new HashMap<>(8);
        encoders.put(idForEncode, new BCryptPasswordEncoder(8));
        encoders.put("argon2", new Argon2PasswordEncoder());
        encoders.put("pbkdf2", new Pbkdf2PasswordEncoder());
        encoders.put("scrypt", new SCryptPasswordEncoder());
        encoders.put("sha256", new StandardPasswordEncoder());
        return new DelegatingPasswordEncoder(idForEncode, encoders);
    }
}
