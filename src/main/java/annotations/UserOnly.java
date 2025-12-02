package annotations;


import org.springframework.security.access.prepost.PreAuthorize;
import java.lang.annotation.*;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasRole(T(org.example.springsecuritydemo.model.enums.AccountType).USER.value) " +
        "and #currentUser.tokenType == T(org.example.springsecuritydemo.model.enums.TokenType).ACCESS_TOKEN")
public @interface UserOnly {
}