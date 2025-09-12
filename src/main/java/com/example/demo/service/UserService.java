//package com.example.demo.service;
//
//import com.example.demo.model.User;
//import org.springframework.stereotype.Service;
//
//import java.util.*;
//
//@Service
//public class UserService {
//    private final Map<String, User> userStore = new HashMap<>();
//
//    public UserService() {
//        // 테스트 계정 등록
//        User testUser = new User();
//        testUser.setName("테스트 유저"); // name도 추가해야 index.html에 이름 표시됨
//        testUser.setEmail("asdf@as.com");
//        testUser.setPassword("asdf");
//        userStore.put(testUser.getEmail(), testUser);
//    }
//
//    public boolean validateLogin(String email, String password) {
//        User user = userStore.get(email);
//        return user != null && user.getPassword().equals(password);
//    }
//
//    public boolean register(User user) {
//        if (user == null || user.getEmail() == null) {
//            System.out.println("register() error: user or email is null");
//            return false;
//        }
//        System.out.println("회원가입 시도: " + user.getEmail());
//        if (userStore.containsKey(user.getEmail())) return false;
//
//        userStore.put(user.getEmail(), user);
//        return true;
//    }
//
//    public User getUserByEmail(String email) {
//        return userStore.get(email);
//    }
//}
package com.example.demo.service;

import com.example.demo.model.User;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {
    private final Map<String, User> userStore = new HashMap<>();

    public UserService() {
        // 테스트 계정 등록
        User testUser = new User();
        testUser.setName("테스트 유저");
        testUser.setEmail("asdf@as.com");
        testUser.setPassword("asdf");
        userStore.put(testUser.getEmail(), testUser);

        System.out.println("테스트 계정이 생성되었습니다:");
        System.out.println("이메일: asdf@as.com");
        System.out.println("비밀번호: asdf");
    }

    public boolean validateLogin(String email, String password) {
        if (email == null || password == null) {
            return false;
        }

        User user = userStore.get(email.toLowerCase().trim());
        boolean isValid = user != null && user.getPassword().equals(password);

        System.out.println("로그인 시도 - 이메일: " + email + ", 성공: " + isValid);
        return isValid;
    }

    public boolean register(User user) {
        if (user == null || user.getEmail() == null || user.getName() == null || user.getPassword() == null) {
            System.out.println("회원가입 실패: 필수 정보 누락");
            return false;
        }

        // 이메일을 소문자로 변환하고 공백 제거
        String email = user.getEmail().toLowerCase().trim();
        String name = user.getName().trim();
        String password = user.getPassword();

        // 빈 값 체크
        if (email.isEmpty() || name.isEmpty() || password.isEmpty()) {
            System.out.println("회원가입 실패: 빈 값 존재");
            return false;
        }

        // 이메일 중복 체크
        if (userStore.containsKey(email)) {
            System.out.println("회원가입 실패: 이미 존재하는 이메일 - " + email);
            return false;
        }

        // 새 사용자 객체 생성 (기존 객체의 참조 문제 방지)
        User newUser = new User();
        newUser.setName(name);
        newUser.setEmail(email);
        newUser.setPassword(password);

        userStore.put(email, newUser);
        System.out.println("회원가입 성공 - 이름: " + name + ", 이메일: " + email);
        System.out.println("현재 등록된 사용자 수: " + userStore.size());

        return true;
    }

    public User getUserByEmail(String email) {
        if (email == null) {
            return null;
        }
        return userStore.get(email.toLowerCase().trim());
    }

    // 디버깅용 메소드
    public void printAllUsers() {
        System.out.println("=== 등록된 모든 사용자 ===");
        for (Map.Entry<String, User> entry : userStore.entrySet()) {
            User user = entry.getValue();
            System.out.println("이메일: " + entry.getKey() + ", 이름: " + user.getName());
        }
        System.out.println("총 " + userStore.size() + "명");
    }
}