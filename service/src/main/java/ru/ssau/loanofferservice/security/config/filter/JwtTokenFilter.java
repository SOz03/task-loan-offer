package ru.ssau.loanofferservice.security.config.filter;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.ssau.loanofferservice.security.service.JwtTokenProvider;
import ru.ssau.loanofferservice.security.service.JwtUserDetailsService;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION = "Authorization";
    private static final String PREFIX_TOKEN = "Bearer ";

    private final JwtUserDetailsService service;
    private final JwtTokenProvider provider;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) {
        try {
            String jwt = parseJwt(request);

            if (jwt != null && provider.validate(jwt)) {
                String username = provider.getUsername(jwt);

                UserDetails userDetails = service.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                SecurityContextHolder.clearContext();
            }

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error("Cannot set user authentication");
            log.error("{}", e.getMessage());
            SecurityContextHolder.clearContext();
        }

    }

    private String parseJwt(HttpServletRequest request) {
        String authorization = request.getHeader(AUTHORIZATION);
        return StringUtils.isNotBlank(authorization) &&
                authorization.startsWith(PREFIX_TOKEN) ? authorization.substring(PREFIX_TOKEN.length()) : null;
    }
}
