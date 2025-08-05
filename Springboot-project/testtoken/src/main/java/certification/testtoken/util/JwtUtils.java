package certification.testtoken.util;

import certification.testtoken.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;

import org.springframework.stereotype.Component;

/**
 * JWT工具类
 * 负责生成Token、验证Token有效性以及从Token中提取信息
 * JWT (JSON Web Token) 是一种紧凑的、自包含的方式，用于在各方之间安全地传输信息作为JSON对象
 */
@Component 
public class JwtUtils {
    /**
     * 签名密钥
     * 使用HS256算法自动生成密钥（jjwt库的Keys工具类）
     * 注意：生产环境中应使用固定密钥并从安全配置中读取，避免每次启动生成新密钥导致旧Token失效
     */
    //秘钥的32位字符串
    private static final String SECRET_KEY_STR = "nV3v24WwrPRrSAcU3wYAnWZ2n4PYMY7J";
    //签名密钥,从32位字符串转成256位字节
    private static final Key SECRET_KEY = Keys.hmacShaKeyFor(SECRET_KEY_STR.getBytes());

    
    /**
     * Token有效期（毫秒）
     * 此处设置为1小时（3600000毫秒 = 60秒 * 60分钟）
     */
    private static final long EXPIRATION_TIME_IN_MILLIS = 3600000;

    /**
     * 生成JWT Token
     * @param user 用户实体对象，用于获取用户名信息
     * @return 生成的JWT字符串
     */
    public String generateToken(User user) {
        // 计算Token过期时间：当前时间 + 有效期
        Date expirationDate = new Date(System.currentTimeMillis() + EXPIRATION_TIME_IN_MILLIS);

        // 构建并生成JWT Token
        return Jwts.builder()
                // 设置主题(Subject)：通常存储用户名或用户唯一标识
                .setSubject(user.getUsername())
                // 设置签发时间(Issued At)：Token生成的时间
                .setIssuedAt(new Date())
                // 设置过期时间(Expiration)：Token失效的时间
                .setExpiration(expirationDate)
                // 添加自定义声明(Claim)：存储额外的用户信息（此处存储用户名）
                .claim("username", user.getUsername())
                // 设置签名：使用指定的密钥和HS256算法进行签名，确保Token不被篡改
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                // 压缩生成最终的Token字符串
                .compact();
    }

    /**
     * 验证Token的有效性
     * 验证内容包括：Token格式是否正确、签名是否有效、是否已过期
     * @param token 待验证的JWT字符串
     * @return 验证通过返回true，否则返回false
     */
    public boolean validateToken(String token) {
        try {
            // 构建JWT解析器，设置签名密钥（必须与生成Token时使用的密钥一致）
            Jws<Claims> claimsJws = Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    // 解析Token并验证签名
                    .parseClaimsJws(token);
            
            // 检查Token是否过期：过期时间是否在当前时间之后
            // 如果过期时间在当前时间之前，说明Token已失效
            return !claimsJws.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            // 捕获所有可能的异常，包括：
            // - SignatureException：签名验证失败（Token被篡改或密钥错误）
            // - ExpiredJwtException：Token已过期
            // - MalformedJwtException：Token格式错误
            // - IllegalArgumentException：Token为空或空白
            // 任何异常都表示Token无效
            return false;
        }
    }

    /**
     * 从Token中提取用户名
     * @param token JWT字符串
     * @return 提取到的用户名，提取失败返回null
     */
    public String extractUsername(String token) {
        try {
            // 解析Token获取声明集合（Claims包含了Token中的所有信息）
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            
            // 优先从自定义声明中获取用户名
            // 如果自定义声明中没有，则从主题(Subject)中获取
            String username = claims.get("username", String.class);
            return username != null ? username : claims.getSubject();
        } catch (Exception e) {
            // 提取失败（如Token无效、格式错误等），返回null
            return null;
        }
    }

    /**
     * 从Token中提取过期时间
     * @param token JWT字符串
     * @return 提取到的过期时间，提取失败返回null
     */
    public Date extractExpiration(String token) {
        try {
            // 解析Token获取声明集合
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            
            // 返回过期时间
            return claims.getExpiration();
        } catch (Exception e) {
            // 提取失败，返回null
            return null;
        }
    }
}
    
