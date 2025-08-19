package com.example.demo.controller;

import com.example.demo.model.PlaceInfo;
import com.example.demo.model.User;
import com.example.demo.service.RecommendService;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Controller
public class WebPageController {
    private final RecommendService recommendService;
    private final UserService userService;

    public WebPageController(RecommendService recommendService, UserService userService) {
        this.recommendService = recommendService;
        this.userService = userService;
    }

    @GetMapping("/")
    public String index() {

        return "index";  // 로그인 한 경우만 index.html 렌더링
    }

    // 로그인 폼
//    @GetMapping("/signin")
//    public String showLoginForm(Model model) {
//        // 이미 로그인된 경우 메인으로 리다이렉트
//        return "signin";
//    }

    // 로그인 처리
//    @PostMapping("/signin")
//    public String login(@RequestParam String email,
//                        @RequestParam String password,
//                        HttpSession session,
//                        Model model) {
//
//        if (userService.validateLogin(email, password)) {
//            User user = userService.getUserByEmail(email);
//            if (user != null) {
//                session.setAttribute("user", user);  // ✅ 이게 누락되면 모든 요청에서 NPE 납니다
//                return "redirect:/";
//            }
//        }
//
//        model.addAttribute("error", "로그인 실패");
//        return "signin";
//    }

    // 회원가입 폼 보여주기
//    @GetMapping("/signup")
//    public String showSignupForm(Model model) {
//        model.addAttribute("user", new User());
//        return "signup";
//    }

    // 회원가입 처리
//    @PostMapping("/signup")
//    public String processSignup(@Valid @ModelAttribute User user,
//                                BindingResult bindingResult,
//                                Model model) {
//
////        System.out.println("회원가입 시도 - 이름: " + user.getName() + ", 이메일: " + user.getEmail());
//
//        // 유효성 검사 실패 시
//        if (bindingResult.hasErrors()) {
//            System.out.println("유효성 검사 실패:");
//            bindingResult.getAllErrors().forEach(error ->
//                    System.out.println("- " + error.getDefaultMessage())
//            );
//            return "signup";
//        }
//
//        // 추가 검증
//        if (user.getName() == null || user.getEmail() == null || user.getPassword() == null ||
//                user.getName().trim().isEmpty() || user.getEmail().trim().isEmpty() || user.getPassword().trim().isEmpty()) {
//            model.addAttribute("error", "모든 필드를 입력해주세요.");
//            return "signup";
//        }
//
//        boolean success = userService.register(user);
//        if (success) {
//            model.addAttribute("message", "회원가입이 완료되었습니다! 로그인해주세요.");
//            userService.printAllUsers(); // 디버깅용
//            return "redirect:/signin";
//        } else {
//            model.addAttribute("error", "이미 등록된 이메일입니다.");
//            return "signup";
//        }
//    }

    // 추천 처리
//    @PostMapping("/recommend")
//    public String processSearch(@RequestParam("keyword") String input,
//                                HttpSession session,
//                                Model model) {
//
//        try {
//            User user = (User) session.getAttribute("user");
//            List<PlaceInfo> places = recommendService.recommendPlaces(input);
//            model.addAttribute("user", user);
//            model.addAttribute("keyword", input);
//            model.addAttribute("places", places);
//        } catch (Exception e) {
//            model.addAttribute("errorMessage", "추천 결과를 가져오는 중 오류가 발생했습니다.");
//            e.printStackTrace();
//        }
//        return "recommendResult";
//    }

    @PostMapping("/recommend")
    public String processSearch(@RequestParam("keyword") String input) {
        if (input == null || input.trim().isEmpty()) {
            return "redirect:/";
        }
        String encodedKeyword = URLEncoder.encode(input, StandardCharsets.UTF_8);
        return "redirect:/recommend?keyword=" + encodedKeyword;
    }

    @GetMapping("/recommend")
    public String showRecommend(@RequestParam("keyword") String keyword,
                                HttpSession session,
                                Model model) {
        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                return "redirect:/";
            }

            User user = (User) session.getAttribute("user");
            List<PlaceInfo> places = recommendService.recommendPlaces(keyword);

            model.addAttribute("user", user);
            model.addAttribute("keyword", keyword);
            model.addAttribute("places", places != null ? places : List.of());

        } catch (Exception e) {
            model.addAttribute("errorMessage", "추천 결과를 가져오는 중 오류가 발생했습니다.");
            e.printStackTrace();
        }
        return "recommendResult";
    }

    // 로그아웃
//    @GetMapping("/logout")
//    public String logout(HttpSession session) {
//        User user = (User) session.getAttribute("user");
//        if (user != null) {
//            System.out.println("로그아웃: " + user.getName());
//        }
//        session.invalidate();
//        return "redirect:/signin";
//    }

    @GetMapping("/place/{index}")
    public String placeDetail(@PathVariable("index") int index,
                              @RequestParam("keyword") String keyword,
                              HttpSession session,
                              Model model) {
//        if (session.getAttribute("user") == null) {
//            return "redirect:/signin";
//        }

        try {
            List<PlaceInfo> places = recommendService.recommendPlaces(keyword);

            if (index >= 0 && index < places.size()) {
                PlaceInfo place = places.get(index);
                model.addAttribute("place", place);
                model.addAttribute("keyword", keyword);
                model.addAttribute("user", session.getAttribute("user"));
                return "placeDetail";
            } else {
                return "redirect:/";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/";
        }
    }

    // 디버깅용 엔드포인트 (개발 시에만 사용)
//    @GetMapping("/debug/users")
//    @ResponseBody
//    public String debugUsers() {
//        userService.printAllUsers();
//        return "사용자 목록이 콘솔에 출력되었습니다.";
//    }
}
