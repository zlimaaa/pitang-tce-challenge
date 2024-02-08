package br.com.api.pitang.configs.security;

import br.com.api.pitang.data.models.User;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserDetail extends org.springframework.security.core.userdetails.User {

    private static final long serialVersionUID = 1L;

    private User user;

    public UserDetail(User user) {
        super(user.getLogin(), user.getPassword(), List.of(user));
        this.user = user;
    }
}