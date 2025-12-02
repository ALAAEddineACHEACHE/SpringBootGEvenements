package annotations;


import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("#currentUser.tokenType == T(org.example.springsecuritydemo.model.enums.TokenType).ACCESS_TOKEN")
public @interface AccessTokenOnly {
}
