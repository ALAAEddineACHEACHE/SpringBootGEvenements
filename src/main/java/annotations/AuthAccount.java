package annotations;

//import org.springframework.security.access.prepost.PreAuthorize;
//import java.lang.annotation.*;
//
//@Target({ ElementType.METHOD, ElementType.TYPE })
//@Retention(RetentionPolicy.RUNTIME)
//@annotations.AccessTokenOnly
//@PreAuthorize("hasAnyRole(T(org.example.springsecuritydemo.model.enums.AccountType).ADMIN.value," +
//        " T(org.example.springsecuritydemo.model.enums.AccountType).HR.value, " +
//        "T(org.example.springsecuritydemo.model.enums.AccountType).CANDIDATE.value)" +
//        "and #currentUser.tokenType == T(org.example.springsecuritydemo.model.enums.TokenType).ACCESS_TOKEN")
//public @interface AuthAccount {
//}