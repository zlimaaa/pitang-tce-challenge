package br.com.api.pitang.utils;

import br.com.api.pitang.configs.security.UserDetail;
import br.com.api.pitang.data.models.User;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.compile;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.springframework.security.core.Authentication;
import static org.springframework.security.core.context.SecurityContextHolder.getContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GenericUtils {

    public static String generatePasswordHash(final String password) {
        BCryptPasswordEncoder bCrypt = new BCryptPasswordEncoder();
        return bCrypt.encode(password);
    }

    public static User getUserLogged() {
        Authentication authentication = getContext().getAuthentication();
        if (authentication == null) return null;
        UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        return userDetail.getUser();
    }

    public static boolean isValidEmail(String email) {
        if (!isBlank(email)) {
            String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
            Pattern pattern = compile(expression, CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(email);
            return matcher.matches();
        }
        return false;
    }

}
