package br.com.api.pitang.services;

import static br.com.api.pitang.constants.MessagesConstants.EMAIL_ALREADY_EXISTS;
import static br.com.api.pitang.constants.MessagesConstants.INVALID_FIELDS;
import static br.com.api.pitang.constants.MessagesConstants.LOGIN_ALREADY_EXISTS;
import static br.com.api.pitang.constants.MessagesConstants.MISSING_FIELDS;
import static br.com.api.pitang.constants.MessagesConstants.USER_NOT_FOUND;
import br.com.api.pitang.data.dtos.UserDTO;
import br.com.api.pitang.data.models.User;
import br.com.api.pitang.exceptions.ValidationException;
import br.com.api.pitang.repositories.UserRepository;
import static br.com.api.pitang.utils.DozerConverter.convertObject;
import static br.com.api.pitang.utils.GenericUtils.generatePasswordHash;
import static br.com.api.pitang.utils.GenericUtils.isValidEmail;
import java.time.LocalDate;
import static java.time.LocalDateTime.now;
import javax.persistence.EntityNotFoundException;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class UserService {

    @Autowired
    private UserRepository repository;


    @Transactional(rollbackFor = Exception.class)
    public UserDTO save(UserDTO userDTO) {
        User user = this.convertDTOtoEntity(userDTO);

        this.validateFields(user);
        this.unique(user);

        user = repository.save(user);
        return this.convertEntityToDTO(user);
    }

    public Page<UserDTO> findAll(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<User> users =  this.repository.findAll(pageable);
        return users.map(this::convertEntityToDTO);
    }

    public UserDTO findById(Long id) {
        return convertEntityToDTO(this.repository.findDistinctById(id)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND)));
    }

    public User findByLogin(String login) {
        return this.repository.findDistinctByLogin(login.toLowerCase())
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND));
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateLastLogin(Long userId) {
        this.repository.updateLastLogin(userId, now());
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        this.findById(id);
        this.repository.deleteById(id);
    }


    private void validateFields(User user) throws ValidationException {

        if (isBlank(user.getFirstName()) ||
                isBlank(user.getLastName()) ||
                user.getBirthDate() == null ||
                isBlank(user.getEmail()) ||
                isBlank(user.getLogin()) ||
                isBlank(user.getPhone()))
            throw new ValidationException(MISSING_FIELDS);

        if (user.getBirthDate().isAfter(LocalDate.now()) ||
                !isValidEmail(user.getEmail()) ||
                user.getPhone().length() != 11)
            throw new ValidationException(INVALID_FIELDS);

        if (user.getId() == null)
           this.validateInsert(user);
        else
            this.validateUpdate(user);

        user.setLogin(user.getLogin().toLowerCase());
        user.setEmail(user.getEmail().toLowerCase());

    }

    private void validateInsert(User user) {
        if (isBlank(user.getPassword()))
            throw new ValidationException(MISSING_FIELDS);

        user.setCreatedAt(now());
        user.setPassword(generatePasswordHash(user.getPassword()));
    }

    private void validateUpdate(User user) {
        User userSaved = this.convertDTOtoEntity(this.findById(user.getId()));

        if (!isBlank(user.getPassword()))
            user.setPassword(generatePasswordHash(user.getPassword()));
        else
            user.setPassword(userSaved.getPassword());

        user.setCreatedAt(userSaved.getCreatedAt());
        user.setLastLogin(userSaved.getLastLogin());
    }

    private void unique(User user) throws ValidationException {
        Long userId = user.getId() == null ? 0L : user.getId();
        Long countLogin = repository.countByLoginAndIdNot(user.getLogin(), userId);
        Long countEmail = repository.countByEmailAndIdNot(user.getEmail(), userId);

        if (countLogin > 0L)
            throw new ValidationException(LOGIN_ALREADY_EXISTS);
        if (countEmail > 0L)
            throw new ValidationException(EMAIL_ALREADY_EXISTS);
    }

    private User convertDTOtoEntity(UserDTO entityDTO) {
        return convertObject(entityDTO, User.class);
    }

    private UserDTO convertEntityToDTO(User user) {
        return convertObject(user, UserDTO.class);
    }

}
