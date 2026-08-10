package com.car.rental.common.security;

import com.car.rental.common.util.JwtUtil;
import com.car.rental.common.util.SecurityUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String REDIS_TOKEN_BLACKLIST_PREFIX = "token:blacklist:";
    private static final String REDIS_USER_PREFIX = "user:info:";

    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = extractToken(request);

        if (!StringUtils.hasText(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        if (isTokenBlacklisted(token)) {
            log.warn("Token is blacklisted: {}", token);
            filterChain.doFilter(request, response);
            return;
        }

        if (!jwtUtil.validateToken(token)) {
            log.warn("Invalid JWT token");
            filterChain.doFilter(request, response);
            return;
        }

        // 仅接受 access token，refresh token 不得用于接口鉴权
        if (!JwtUtil.TOKEN_TYPE_ACCESS.equals(jwtUtil.getTokenType(token))) {
            log.warn("Non-access token used for authentication, rejected");
            filterChain.doFilter(request, response);
            return;
        }

        Long userId = jwtUtil.getUserIdFromToken(token);
        String username = jwtUtil.getUsernameFromToken(token);

        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            List<SimpleGrantedAuthority> authorities = loadUserAuthorities(userId);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userId, null, authorities);
            authentication.setDetails(username);

            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug("Authenticated user: {} (id: {})", username, userId);
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(HEADER_AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(TOKEN_PREFIX.length());
        }
        return null;
    }

    private boolean isTokenBlacklisted(String token) {
        String key = REDIS_TOKEN_BLACKLIST_PREFIX + token;
        Boolean hasKey = redisTemplate.hasKey(key);
        return Boolean.TRUE.equals(hasKey);
    }

    @SuppressWarnings("unchecked")
    private List<SimpleGrantedAuthority> loadUserAuthorities(Long userId) {
        String redisKey = REDIS_USER_PREFIX + userId;
        Object cached = redisTemplate.opsForValue().get(redisKey);

        if (cached instanceof java.util.Map) {
            java.util.Map<String, Object> userMap = (java.util.Map<String, Object>) cached;
            Object permissions = userMap.get("permissions");
            if (permissions instanceof List) {
                List<String> permList = (List<String>) permissions;
                return permList.stream()
                        .map(SimpleGrantedAuthority::new)
                        .toList();
            }
        }

        return Collections.emptyList();
    }
}