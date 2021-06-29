package com.wonkmonk.digikhata.userauth.securityFilters;

import com.wonkmonk.digikhata.userauth.Exception.TokenExpiredException;
import com.wonkmonk.digikhata.userauth.Utility.TokenHandler;
import com.wonkmonk.digikhata.userauth.constants.SecurityConstants;
import com.wonkmonk.digikhata.userauth.models.ApplicationUser;
import com.wonkmonk.digikhata.userauth.models.CustomUserDetail;
import com.wonkmonk.digikhata.userauth.models.Role;
import com.wonkmonk.digikhata.userauth.services.CustomJwtService;
import com.wonkmonk.digikhata.userauth.services.CustomUserService;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class JWTAuthorizationFilter extends OncePerRequestFilter {


    @Autowired
    private CustomJwtService customJwtService;
    @Autowired
    private SecurityConstants securityConstants;
    @Autowired
    private CustomUserService customUserService;



//    private final SecurityConstants securityConstants = new SecurityConstants();


    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws IOException, ServletException {

        String header = req.getHeader(securityConstants.getHeaderString());

        if (header == null || !header.startsWith(securityConstants.getTokenPrefix())) {
            chain.doFilter(req, res);
            return;
        }
        try {
            UsernamePasswordAuthenticationToken authentication = getAuthentication(req);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        catch(Exception failed){
            // reset the security context
            SecurityContextHolder.clearContext();
        }

        chain.doFilter(req, res);
    }



    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) throws TokenExpiredException {
        String token = request.getHeader(securityConstants.getHeaderString());
        TokenHandler tokenHandler = new TokenHandler(securityConstants);
        if (token != null) {
            // parse the token.
            String user = null;
            try {
                user = tokenHandler.parseJwtToken(token);
            }
            catch (ExpiredJwtException e){
                throw new TokenExpiredException("token expired");
            }
            boolean isJwtTokenPresentInDb = customJwtService.isTokenPresentInDb(token);
            if (user != null && isJwtTokenPresentInDb) {
                ApplicationUser applicationUser = customUserService.findUserByUsername(user);
                List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                Set<Role> roles = applicationUser.getRoles();
                for (Role role : roles) {
                    authorities.add(new SimpleGrantedAuthority(role.getName()));
                }
               return new UsernamePasswordAuthenticationToken(user, null, authorities);
            }

            return null;
        }
        return null;
    }
}
