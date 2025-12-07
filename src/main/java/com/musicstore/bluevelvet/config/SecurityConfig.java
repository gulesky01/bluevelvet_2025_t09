package com.musicstore.bluevelvet.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;
import java.util.Collections;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private DataSource dataSource;

//    @Bean
//    public WebSecurityCustomizer webSecurityCustomizer() {
//        return web -> web.ignoring().requestMatchers(
//                "/swagger-ui/**", "/v3/api-docs/**"
//        );
//    }

//    @Bean
//    public UserDetailsService userDetailsService() {
//        UserDetails user = User.builder()
//                .username("rey")
//                .password(passwordEncoder().encode("rey-pass"))
//                .roles("ADMIN")
//                .build();
//        return new InMemoryUserDetailsManager(Collections.singleton(user));
//    }

//    @Bean
//    UserDetailsManager users(DataSource dataSource) {
//        UserDetails user = User.builder()
//                .username("user")
//                .password("{bcrypt}$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW")
//                .roles("USER")
//                .build();
//        UserDetails admin = User.builder()
//                .username("admin")
//                .password("{bcrypt}$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW")
//                .roles("USER", "ADMIN")
//                .build();
//        JdbcUserDetailsManager users = new JdbcUserDetailsManager(dataSource);
//        users.createUser(user);
//        users.createUser(admin);
//        return users;
//    }

//    @Bean
//    public UserDetailsService userDetailsService(DataSource source){
//        return new JdbcDaoImpl();
//    }


    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth)
            throws Exception {
        auth.jdbcAuthentication()
                .dataSource(dataSource)
                .usersByUsernameQuery("select email as user,password,enabled "
                        + "from db.users "
                        + "where email = ?")
                .authoritiesByUsernameQuery("select users.email as user, authorities.authority as authority "
                        + "from db.authorities as authorities "
                        + "inner join db.users as users "
                        + "on users.id = authorities.ref_user "
                        + "where users.email like ?");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
          return http
                  .csrf(csrf -> csrf.disable())
                  .httpBasic(Customizer.withDefaults())
                  .authorizeHttpRequests(auth -> auth
                          .requestMatchers("/login", "/css/**", "/js/**",
                                  "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                          .requestMatchers("/dasboard/**").hasAnyRole("Administrator","SalesManager", "ShippingManager")
                          .anyRequest().authenticated()
                  )
                      .formLogin((form) -> form
                           .loginPage("/login")
                           .loginProcessingUrl("/perform_login")
                              .defaultSuccessUrl("/dashboard", true)
                           .permitAll()
                   )
                  .logout(logout -> logout
                          .logoutUrl("/logout")
                          .logoutSuccessUrl("/login?logout")
                          .invalidateHttpSession(true)
                          .clearAuthentication(true)
                          .permitAll()
                  )
                  .build() ;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
