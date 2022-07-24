package dernovyi.my_cards.service.impl;

import dernovyi.my_cards.domain.User;
import dernovyi.my_cards.domain.UserPrincipal;
import dernovyi.my_cards.enumeration.Role;
import dernovyi.my_cards.repository.UserRepository;
import dernovyi.my_cards.service.UserService;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;

import static dernovyi.my_cards.constant.UserImplConstant.*;


@Service
@Transactional
@Qualifier("UserDetailsService")
public class UserServiceImpl implements UserService, UserDetailsService {

    private Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder;
    private LoginAttemptService loginAttemptService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           BCryptPasswordEncoder passwordEncoder,
                           LoginAttemptService loginAttemptService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.loginAttemptService = loginAttemptService;
    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findUserByEmail(email);
        if (user == null) {
            LOGGER.error(NO_USER_FOUND_BY_EMAIL + email);

            throw new UsernameNotFoundException(NO_USER_FOUND_BY_EMAIL + email);
        }
        validateLoginAttempt(user);
        user.setLastLoginDateDisplay(user.getLastLoginDate());
        user.setLastLoginDate(new Date());
        userRepository.save(user);
        UserPrincipal userPrincipal = new UserPrincipal(user);
        LOGGER.info(RETURNING_FOUND_USER_BY_USERNAME + email);
        return userPrincipal;
    }

    private void validateLoginAttempt(User user) {
        if (user.isNonLocked()) {
            if (loginAttemptService.hasExceededMaxAttempts(user.getEmail())) {
                user.setNonLocked(false);
            } else {
                user.setNonLocked(true);
                loginAttemptService.addUserToLoginAttemptCache(user.getEmail());
            }
        } else {
            loginAttemptService.evictUserFromLoginAttemptCache(user.getEmail());
        }
    }

//    @Override
//    public User register(String firstName, String lastName, String email) throws UserNotFoundException, EmailExistException, IOException {
//        validateNewEmailAndOldEmail( EMPTY, email);
//        User user = new User();
//        user.setUserId(generateUserId());
//        String password = generatePassword();
//        String encodedPassword = encodePassword(password);
//        user.setFirstName(firstName);
//        user.setLastName(lastName);
//        user.setEmail(email);
//        user.setJoinDate(new Date());
//        user.setActive(true);
//        user.setNonLocked(true);
//        user.setRole(ROLE_USER.name());
//        user.setPassword(encodedPassword);
//        user.setAuthorities(ROLE_USER.getAuthorities());
//        user.setProfileImageUrl(getTemporaryProfileImageUrl(firstName));
//        userRepository.save(user);
////        emailGridService.sendNewPasswordEmail(user.getFirstName() +" "+ user.getLastName() , password, email);
//        LOGGER.info("New user password: "+ password);
//        return user;
//    }
//    @Override
//    public User addNewUser(String firstName, String lastName, String newEmail, String role, boolean isNonLocked, boolean isActive, MultipartFile profileImage) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException, MessagingException, NotAnImageFileException, StorageException, InvalidKeyException, URISyntaxException {
//        validateNewEmailAndOldEmail(EMPTY , newEmail);
//        User user = new User();
//        String password = generatePassword();
//        String encodedPassword = encodePassword(password);
//        user.setUserId(generateUserId());
//        user.setFirstName(firstName);
//        user.setLastName(lastName);
//        user.setJoinDate(new Date());
//        user.setEmail(newEmail);
//        user.setPassword(encodedPassword);
//        user.setActive(isActive);
//        user.setNonLocked(isNonLocked);
//        user.setRole(getRoleEnumName(role).name());
//        user.setAuthorities(getRoleEnumName(role).getAuthorities());
//        user.setProfileImageUrl(getTemporaryProfileImageUrl(firstName));
//        userRepository.save(user);
////        saveProfileImage(user, profileImage);
////        emailGridService.sendNewPasswordEmail(user.getFirstName() +" "+ user.getLastName() ,password, newEmail);
////        LOGGER.info("New user password: "+ password);
//        return user;
//    }


//
//    @Override
//    public User updateUser(String currentEmail, String newFirstName, String newLastName, String newEmail, String role, boolean isNonLocked, boolean isActive, MultipartFile profileImage) throws UserNotFoundException, UsernameExistException, EmailExistException, IOException, NotAnImageFileException, StorageException, InvalidKeyException, URISyntaxException {
//        User currentUser = validateNewEmailAndOldEmail(currentEmail, newEmail);
//        currentUser.setFirstName(newFirstName);
//        currentUser.setLastName(newLastName);
//        currentUser.setEmail(newEmail);
//        currentUser.setActive(isActive);
//        currentUser.setNonLocked(isNonLocked);
//        currentUser.setRole(getRoleEnumName(role).name());
//        currentUser.setAuthorities(getRoleEnumName(role).getAuthorities());
//        userRepository.save(currentUser);
////        saveProfileImage(currentUser, profileImage);
//        return currentUser;
//    }

//    @Override
//    public void deleteUser(String email) throws  InvalidKeyException, StorageException, URISyntaxException {
//        User user = userRepository.findUserByEmail(email);
////        Path userFolder = Paths.get(USER_FOLDER + user.getEmail()).toAbsolutePath().normalize();
////        FileUtils.deleteDirectory(new File(userFolder.toString()));
////        this.storageService.deleteContainer(user.getUserId());
//        userRepository.deleteByEmail(email);
//
//    }

//    @Override
//    public void resetPassword(String email) throws EmailNotFoundException, IOException {
//        User user = userRepository.findUserByEmail(email);
//        if(user ==null){
//            throw new EmailNotFoundException(NO_USER_FOUND_BY_EMAIL + email);
//        }
//        String password =generatePassword();
//        user.setPassword(encodePassword(password));
//        userRepository.save(user);
////        emailGridService.sendNewPasswordEmail(user.getFirstName() +" "+ user.getLastName() ,password, email);
//    }

//    @Override
//    public void updatePassword(String email, String oldPassword, String newPassword) throws EmailNotFoundException, MessagingException, PasswordNotCorrectException, IOException {
//        User user = userRepository.findUserByEmail(email);
//        if(user ==null){
//            throw new EmailNotFoundException(NO_USER_FOUND_BY_EMAIL + email);
//        }
//        if(!passwordEncoder.matches(oldPassword, user.getPassword())){
//            throw new PasswordNotCorrectException(YOUR_OLD_PASSWORD_NOT_CORRECT);
//        }
//        user.setPassword(encodePassword(newPassword));
//        userRepository.save(user);
////        emailGridService.sendNewPasswordEmail(user.getFirstName() +" "+ user.getLastName() ,newPassword, email);
//    }

//    @Override
//    public User updateProfileImage(String email, MultipartFile profileImage) throws UserNotFoundException, EmailExistException, IOException, NotAnImageFileException, StorageException, InvalidKeyException, URISyntaxException {
//        User user = validateNewEmailAndOldEmail( email , null);
//        saveProfileImage(user, profileImage);
//        return user;
//    }


//    private void saveProfileImage(User user, MultipartFile profileImage) throws IOException, NotAnImageFileException, StorageException, InvalidKeyException, URISyntaxException {
//        if(profileImage != null){
//            if(!Arrays.asList(IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE, IMAGE_GIF_VALUE).contains(profileImage.getContentType())){
//                throw new NotAnImageFileException(profileImage.getOriginalFilename() + "is not an image file. Please upload an image");
//            }
//            if(user.getProfileImageUrl().contains(user.getUserId())){
//                this.storageService.removeInStorage(user.getProfileImageUrl() , user.getUserId());
//            }
//                URI uri = this.storageService.saveToStorage(profileImage, user.getUserId());
//                user.setProfileImageUrl(uri.toString());
//                this.userRepository.save(user);
//
//        }
//    }

//    private String setProfileImage(String username) {
//        return ServletUriComponentsBuilder.fromCurrentContextPath().path(USER_IMAGE_PATH + username + FORWARD_SLASH + username + DOT + JPG_EXTENSION).toUriString();
//    }

    private Role getRoleEnumName(String role) {
        return Role.valueOf(role.toUpperCase());

    }

//    private String getTemporaryProfileImageUrl(String name) {
//        return ServletUriComponentsBuilder.fromCurrentContextPath().path(DEFAULT_USER_IMAGE_PATH + FORWARD_SLASH + name).toUriString();
//    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    private String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(10);
    }

    private String generateUserId() {
        return RandomStringUtils.randomNumeric(10);
    }

//    public User validateNewEmailAndOldEmail(String currentEmail, String newEmail) throws  EmailExistException, UserNotFoundException {
//        User userByNewEmail = findUserByEmail(newEmail);
//        if(StringUtils.isNoneBlank(currentEmail)){
//            User currentUser = findUserByEmail(currentEmail);
//            if(currentUser == null){
//                throw new UserNotFoundException(NO_USER_FOUND_BY_EMAIL + currentEmail);
//            }
//            if(userByNewEmail != null && !currentUser.getId().equals(userByNewEmail.getId())){
//                throw new EmailExistException(EMAIL_ALREADY_EXISTS);
//            }
//            return currentUser;
//
//        }else {
//            if(userByNewEmail != null ){
//                throw new EmailExistException(EMAIL_ALREADY_EXISTS);
//            }
//            return  null;
//        }
//    }

//    @Override
//    public List<User> getUsers() {
//        return userRepository.findAll();
//    }
//
//    @Override
//    public User findUserByEmail(String email) {
//        return userRepository.findUserByEmail(email);
//    }

}
