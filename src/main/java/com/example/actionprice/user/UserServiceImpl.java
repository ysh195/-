package com.example.actionprice.user;

import com.example.actionprice.exception.UsernameAlreadyExistsException;
import com.example.actionprice.user.forms.UserRegisterForm;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : 연상훈
 * @created : 2024-10-06 오후 9:17
 * @updated : 2024-10-10 오전 11:07
 */
@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  /**
   * 유저 생성 기능. 대체로 회원가입
   * @param userRegisterForm
   * @author 연상훈
   * @created 2024-10-10 오전 11:05
   * @updated 2024-10-10 오전 11:05
   * @throw UsernameAlreadyExistsException
   */
  @Transactional
  @Override
  public String createUser(UserRegisterForm userRegisterForm) {
    log.info("--------------- [UserService] createUser ----------------");
    log.info("userRegisterForm: " + userRegisterForm);

    String inputed_username = userRegisterForm.getUsername();
    log.info("inputed_username: " + inputed_username);
    User existing_user = userRepository.findById(inputed_username).orElse(null);

    // 이미 존재하는 유저라면
    if(existing_user != null) {
      log.info(inputed_username + " already exists");
      throw new UsernameAlreadyExistsException("[username : " + inputed_username + "] already exists");
    }

    log.info(userRegisterForm.getUsername() + " is new user");
    // user 구성
    User newUser = User.builder()
        .username(userRegisterForm.getUsername())
        .password(passwordEncoder.encode(userRegisterForm.getPassword()))
        .email(userRegisterForm.getEmail())
        .build();

    // 권한은 일반 유저. 사용자 권한을 줄 때는 반드시 UserRole 사용
    newUser.addAuthorities(UserRole.ROLE_USER);

    // 저장
    userRepository.save(newUser);

    String result_str = newUser.getUsername() + "register successful";

    log.info(result_str);

    return result_str;
  }

  /**
   * 유저 로그인 기능
   * @author 연상훈
   * @created 2024-10-06 오후 9:17
   * @info 로그인 기능은 CustomSecurity와 LoginFilter로 처리하기 때문에 별도로 사용할 필요가 없음.
   */

  /**
   * 유저 로그아웃 기능
   * @author 연상훈
   * @created 2024-10-10 오전 10:23
   * @see : 로그아웃 기능은 CustomSecurity로 처리하기 때문에 별도로 사용할 필요가 없음.
   */

  /**
   * 해당 username을 가진 사용자가 존재하는지 체크하는 메서드.
   * @param username
   * @author 연상훈
   * @created 2024-10-10 오전 10:25
   * @updated 2024-10-10 오전 10:25
   * @see :
   * 존재하면 true / 존재하지 않으면 false 반환
   * 재사용 가능성이 높은 메서드인 만큼, 간단하게 username만 입력 받도록 구성
   */
  @Override
  public boolean checkUserExistsWithUsername(String username) {

    log.info("--------------- [UserService] check User Exists With Username ----------------");

    log.info("inputed_username: " + username);
    User existing_user = userRepository.findById(username).orElse(null);

    if(existing_user != null) {
      return true;
    }

    return false;
  }

  /**
   * 해당 email을 사용 중인 사용자가 존재하는지 체크하는 메서드.
   * @param email
   * @author 연상훈
   * @created 2024-10-10 오전 10:25
   * @updated 2024-10-10 오전 10:25
   * @see :
   * 존재하면 true / 존재하지 않으면 false 반환
   * 재사용 가능성이 높은 메서드인 만큼, 간단하게 email만 입력 받도록 구성
   */
  @Override
  public boolean checkUserExistsWithEmail(String email) {
    log.info("--------------- [UserService] check User Exists With Email ----------------");

    log.info("inputed_email: " + email);
    // DB에서 이메일로 유저 검색
    User existing_user = userRepository.findByEmail(email).orElse(null);

    if(existing_user != null) {
      return true;
    }

    return false;
  }


  public boolean checkValidityOfPassword(String password) {
    Pattern passwordPattern = Pattern.compile("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*\\W).{8,20}$"); // 8~20자, 영어+숫자+특수문자
    Matcher passwordMatcher = passwordPattern.matcher(password);

    return passwordMatcher.find() ? true : false;

  }
}
