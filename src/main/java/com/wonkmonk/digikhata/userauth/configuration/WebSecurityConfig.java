package com.wonkmonk.digikhata.userauth.configuration;

import com.wonkmonk.digikhata.userauth.Utility.EmailHandler;
import com.wonkmonk.digikhata.userauth.Utility.OtpHandler;
import com.wonkmonk.digikhata.userauth.Utility.TokenHandler;
import com.wonkmonk.digikhata.userauth.constants.SecurityConstants;
import com.wonkmonk.digikhata.userauth.repository.OtpRepository;
import com.wonkmonk.digikhata.userauth.securityFilters.JWTAuthenticationFilter;
import com.wonkmonk.digikhata.userauth.securityFilters.JWTAuthorizationFilter;
import com.wonkmonk.digikhata.userauth.securityFilters.RestAuthenticationEntryPoint;
import com.wonkmonk.digikhata.userauth.services.CustomJwtService;
import com.wonkmonk.digikhata.userauth.services.CustomRetailerService;
import com.wonkmonk.digikhata.userauth.services.CustomUserDetailService;
import com.wonkmonk.digikhata.userauth.services.CustomUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private RestAuthenticationEntryPoint authenticationEntryPoint;
    @Autowired
    private CustomUserDetailService customUserDetailService;
    @Autowired
    JWTAuthorizationFilter jwtAuthorizationFilter;
//    @Autowired
//    OtpHandler otpHandler;
//    @Autowired
//    EmailHandler emailHandler;
//    @Autowired
//    OtpRepository otpRepository;
//    @Autowired
//    private CustomRetailerService customRetailerService;




    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors().and().csrf().disable();
        http
                .exceptionHandling()
                .authenticationEntryPoint(this.authenticationEntryPoint)
                .and()
                .authorizeRequests()
                .antMatchers("/**/signup").permitAll()
                .antMatchers("/**/signin").permitAll()
                .antMatchers("/**/verify").permitAll()
                .antMatchers("/**/sendmail").permitAll()
                .antMatchers("/**/resetpassword").permitAll()
                .antMatchers("/**/verifylink").permitAll()
                .anyRequest().authenticated()
                .and()
//                .addFilter(new JWTAuthenticationFilter(authenticationManager(),
//                        otpHandler,
//                        emailHandler,
//                        otpRepository,
//                        customRetailerService))
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
                // this disables session creation on Spring Security
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);


        http.headers().frameOptions().disable();
    }

//    @Override
//    public void configure(WebSecurity web){
//        web.ignoring()
//                .antMatchers("/*/")
//                .antMatchers("/eureka/**")
//                .antMatchers(HttpMethod.OPTIONS, "/**");
//    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customUserDetailService).passwordEncoder(bCryptPasswordEncoder());
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {

        return new BCryptPasswordEncoder(11);
    }



}
