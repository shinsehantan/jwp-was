package jwp.was.webapplicationserver.service;

import static jwp.was.util.Constants.USER_EMAIL;
import static jwp.was.util.Constants.USER_ID;
import static jwp.was.util.Constants.USER_NAME;
import static jwp.was.util.Constants.USER_OTHER_EMAIL;
import static jwp.was.util.Constants.USER_OTHER_ID;
import static jwp.was.util.Constants.USER_OTHER_NAME;
import static jwp.was.util.Constants.USER_OTHER_PASSWORD;
import static jwp.was.util.Constants.USER_PASSWORD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import jwp.was.webapplicationserver.configure.maker.ConfigureMaker;
import jwp.was.webapplicationserver.controller.dto.LoginRequest;
import jwp.was.webapplicationserver.controller.dto.UserRequest;
import jwp.was.webapplicationserver.db.DataBase;
import jwp.was.webapplicationserver.db.DataBaseTest;
import jwp.was.webapplicationserver.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserServiceTest {

    private ConfigureMaker configureMaker = ConfigureMaker.getInstance();
    private UserService userService = configureMaker.getConfigure(UserService.class);
    private DataBase dataBase = configureMaker.getConfigure(DataBase.class);

    @AfterEach
    void tearDown() {
        DataBaseTest.clear();
    }

    @DisplayName("User 생성 테스트")
    @Test
    void createUser_Success() {
        UserRequest userRequest = new UserRequest(USER_ID, USER_PASSWORD, USER_NAME, USER_EMAIL);
        User user = userService.createUser(userRequest);

        assertThat(user).isEqualTo(userRequest.toUser());
        assertThat(user.getUserId()).isEqualTo(userRequest.getUserId());
        assertThat(user.getPassword()).isEqualTo(userRequest.getPassword());
        assertThat(user.getName()).isEqualTo(userRequest.getName());
        assertThat(user.getEmail()).isEqualTo(userRequest.getEmail());

        assertThat(dataBase.findAll()).hasSize(1);
        assertThat(dataBase.findUserById(user.getUserId())).isEqualTo(user);
    }

    @DisplayName("다른 User 생성 테스트")
    @Test
    void createUser_OtherUser_Success() {
        UserRequest userRequest = new UserRequest(USER_ID, USER_PASSWORD, USER_NAME, USER_EMAIL);
        UserRequest userOtherRequest = new UserRequest(
            USER_OTHER_ID, USER_OTHER_PASSWORD, USER_OTHER_NAME, USER_OTHER_EMAIL
        );
        User user = userService.createUser(userRequest);
        assertThat(user).isEqualTo(userRequest.toUser());
        assertThat(user.getUserId()).isEqualTo(userRequest.getUserId());
        assertThat(user.getPassword()).isEqualTo(userRequest.getPassword());
        assertThat(user.getName()).isEqualTo(userRequest.getName());
        assertThat(user.getEmail()).isEqualTo(userRequest.getEmail());

        User userOther = userService.createUser(userOtherRequest);
        assertThat(userOther).isEqualTo(userOtherRequest.toUser());
        assertThat(userOther.getUserId()).isEqualTo(userOtherRequest.getUserId());
        assertThat(userOther.getPassword()).isEqualTo(userOtherRequest.getPassword());
        assertThat(userOther.getName()).isEqualTo(userOtherRequest.getName());
        assertThat(userOther.getEmail()).isEqualTo(userOtherRequest.getEmail());

        assertThat(dataBase.findAll()).hasSize(2);
        assertThat(dataBase.findUserById(user.getUserId())).isEqualTo(user);
        assertThat(dataBase.findUserById(userOther.getUserId())).isEqualTo(userOther);
    }

    @DisplayName("동일한 UserId일 경우의 User 생성 테스트, 예외 발생")
    @Test
    void createUser_ExistsUser_ThrownException() {
        UserRequest userRequest = new UserRequest(USER_ID, USER_PASSWORD, USER_NAME, USER_EMAIL);
        userService.createUser(userRequest);
        UserRequest userEqualIdRequest = new UserRequest(
            USER_ID, USER_PASSWORD, USER_NAME, USER_EMAIL
        );
        assertThatThrownBy(() -> userService.createUser(userEqualIdRequest))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("로그인 - True, 성공시")
    @Test
    void login_Success_ReturnTrue() {
        UserRequest userRequest = new UserRequest(USER_ID, USER_PASSWORD, USER_NAME, USER_EMAIL);
        userService.createUser(userRequest);

        LoginRequest loginRequest = new LoginRequest(USER_ID, USER_PASSWORD);
        boolean login = userService.login(loginRequest);

        assertThat(login).isTrue();
    }

    @DisplayName("로그인 - False, 실패시")
    @Test
    void login_Failed_ReturnFalse() {
        LoginRequest loginRequest = new LoginRequest(USER_ID, USER_PASSWORD);
        boolean login = userService.login(loginRequest);

        assertThat(login).isFalse();
    }

    @DisplayName("모든 유저 조회 - 성공")
    @Test
    void findAllUser_Success() {
        UserRequest userRequest = new UserRequest(USER_ID, USER_PASSWORD, USER_NAME, USER_EMAIL);
        User user = userService.createUser(userRequest);

        List<User> allUser = userService.findAllUser();

        assertThat(allUser).isNotNull();
        assertThat(allUser).isNotEmpty();
        assertThat(allUser).contains(user);
    }
}
