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
import java.time.LocalDateTime;
import static java.time.LocalDateTime.now;
import javax.persistence.EntityNotFoundException;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class UserService {

    @Autowired
    private UserRepository repository;

    @Transactional(rollbackFor = Exception.class)
    public UserDTO save(UserDTO userDTO) {
        User user = convertDTOtoEntity(userDTO);

        validateFields(user);
        unique(user);

        user = repository.save(user);
        return convertEntityToDTO(user);
    }

    public Page<UserDTO> findAll(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<User> users = repository.findAllUsersOrderByTotalUsageCounterAndLoginPage(pageable);
        return users.map(this::convertEntityToDTO);
    }

    public UserDTO findById(Long id) {
        return convertEntityToDTO(repository.findDistinctById(id)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND)));
    }

    public User findByLogin(String login) {
        return repository.findDistinctByLogin(login.toLowerCase())
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND));
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateLastLogin(Long userId) {
        repository.updateLastLogin(userId, now());
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        findById(id);
        repository.deleteById(id);
    }

    /**
     * validacao de campos vazio: firstName, lastName, birthDate, email, login, phone
     * validacao de campos invalidos: birthDate (data futura), email (xxx@xx),
     * phone (ddd + digito 9 + numero) , senha (menor que 6 caracteres)
     *
     * @param user
     * @throws ValidationException caso falhe em alguma das verificacoes acima
     */
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
            validateInsert(user);
        else
            validateUpdate(user);

        user.setLogin(user.getLogin().toLowerCase());
        user.setEmail(user.getEmail().toLowerCase());

    }

    /**
     * valida se a senha e vazia ou se e menor que 6 caracteres
     * seta a data de criacao e criptografa a senha do usuario
     * @param user
     * @throws ValidationException caso falhe em alguma das verificacoes acima
     */
    private void validateInsert(User user) {
        if (isBlank(user.getPassword()))
            throw new ValidationException(MISSING_FIELDS);

        if (user.getPassword().length() < 6)
            throw new ValidationException(INVALID_FIELDS);

        user.setCreatedAt(now());
        user.setPassword(generatePasswordHash(user.getPassword()));
    }

    /**
     * nao e obrigatorio a atualizacao da senha, porem caso haja tal
     * atualizacao, a senha devera ser maior ou igual a 6 caracteres
     * @param user
     * @throws ValidationException caso falhe na validacao acima
     */
    private void validateUpdate(User user) {
        User userSaved = convertDTOtoEntity(findById(user.getId()));

        if (!isBlank(user.getPassword())) {
            if (user.getPassword().length() < 6)
                throw new ValidationException(INVALID_FIELDS);
            else
                user.setPassword(generatePasswordHash(user.getPassword()));
        } else
            user.setPassword(userSaved.getPassword());

        user.setCreatedAt(userSaved.getCreatedAt());
        user.setLastLogin(userSaved.getLastLogin());
    }

    /**
     * validacao de unicidade para os campos login e email
     *
     * @param user
     * @throws ValidationException caso haja outro usuario com o mesmo login ou mesmo email
     */
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

    /**
     * Job para deletar usuarios no banco cuja data do ultimo login tenha
     * ultrapassado os 30 dias, esse job roda diariamente as 23:30 da noite
     */
    @Scheduled(cron = "${spring.task.scheduling.cron}")
    @Transactional(rollbackFor = Exception.class)
    public void deleteInactiveUsers() {
        repository.deleteInactiveUsers(LocalDateTime.now().minusDays(30));
    }

}
