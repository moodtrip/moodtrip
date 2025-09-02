package com.example.demo.controller;

import com.example.demo.dto.PlaceDto;
import com.example.demo.model.PlaceInfo;
import com.example.demo.model.User;
import com.example.demo.service.RecommendService;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

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
        return "index";
    }

    @GetMapping("/situationSearching")
    public String situationSearching() {
        return "situationSearch";
    } // 프로토타입 (메인화면)

    // 감정/상황 선택 화면
    @GetMapping("/selection")
    public String showSelection(@RequestParam("mode") String mode,
                                @RequestParam(value = "lat", required = false) String latitude,
                                @RequestParam(value = "lng", required = false) String longitude,
                                Model model) {
        model.addAttribute("mode", mode);
        model.addAttribute("latitude", latitude);
        model.addAttribute("longitude", longitude);

        if ("emotion".equals(mode)) {
            return "emotionSelection";
        } else if ("situation".equals(mode)) {
            return "situationInput";
        }
        return "redirect:/";
    }

    @GetMapping("/SituationS")
    public String SituationS(@RequestParam(value = "lat", required = false) String latitude,
                             @RequestParam(value = "lng", required = false) String longitude,
                             Model model) {
        model.addAttribute("latitude", latitude);
        model.addAttribute("longitude", longitude);
        return "situationInput";
    }

    // 감정 선택 처리
    @PostMapping("/processEmotion")
    public String processEmotion(@RequestParam("emotions") List<String> emotions,
                                 @RequestParam(value = "lat", required = false) String latitude,
                                 @RequestParam(value = "lng", required = false) String longitude,
                                 Model model) {
        // 선택된 감정들을 키워드로 변환하여 추천 처리
        String emotionKeyword = String.join(" ", emotions);
        String encodedKeyword = URLEncoder.encode(emotionKeyword, StandardCharsets.UTF_8);
        System.out.println(emotionKeyword);

        // lat, lng 파라미터도 함께 전달
        String redirectUrl = "redirect:/recommend/emotion?keyword=" + encodedKeyword + "&selected=" + encodedKeyword;
        if (latitude != null && !latitude.isEmpty()) {
            redirectUrl += "&lat=" + URLEncoder.encode(latitude, StandardCharsets.UTF_8);
        }
        if (longitude != null && !longitude.isEmpty()) {
            redirectUrl += "&lng=" + URLEncoder.encode(longitude, StandardCharsets.UTF_8);
        }

        return redirectUrl;
    }

    // 상황 입력 처리
    @PostMapping("/processSituation")
    public String processSituation(@RequestParam("situationText") String situationText,
                                   @RequestParam(value = "lat", required = false) String latitude,
                                   @RequestParam(value = "lng", required = false) String longitude,
                                   Model model) {
        if (situationText == null || situationText.trim().isEmpty()) {
            String redirectUrl = "redirect:/selection?mode=situation";
            if (latitude != null && !latitude.isEmpty()) {
                redirectUrl += "&lat=" + URLEncoder.encode(latitude, StandardCharsets.UTF_8);
            }
            if (longitude != null && !longitude.isEmpty()) {
                redirectUrl += "&lng=" + URLEncoder.encode(longitude, StandardCharsets.UTF_8);
            }
            return redirectUrl;
        }

        String encodedKeyword = URLEncoder.encode(situationText, StandardCharsets.UTF_8);

        // lat, lng 파라미터도 함께 전달
        String redirectUrl = "redirect:/recommend/situation?keyword=" + encodedKeyword + "&selected=" + encodedKeyword;
        if (latitude != null && !latitude.isEmpty()) {
            redirectUrl += "&lat=" + URLEncoder.encode(latitude, StandardCharsets.UTF_8);
        }
        if (longitude != null && !longitude.isEmpty()) {
            redirectUrl += "&lng=" + URLEncoder.encode(longitude, StandardCharsets.UTF_8);
        }

        return redirectUrl;
    }

    @PostMapping("/recommend")
    public String processSearch(@RequestParam("keyword") String input) {
        if (input == null || input.trim().isEmpty()) {
            return "redirect:/";
        }
        String encodedKeyword = URLEncoder.encode(input, StandardCharsets.UTF_8);
        return "redirect:/recommend/general?keyword=" + encodedKeyword;
    }

    // 기존 /recommend 엔드포인트 (하위 호환성을 위해 유지)
    @GetMapping("/recommend")
    public String showRecommendLegacy(@RequestParam("keyword") String keyword,
                                      @RequestParam(value = "type", required = false) String type,
                                      @RequestParam(value = "selected", required = false) String selected,
                                      @RequestParam(value = "lat", required = false) String latitude,
                                      @RequestParam(value = "lng", required = false) String longitude,
                                      HttpSession session,
                                      Model model) {
        // type이 있으면 새로운 URL로 리다이렉트
        if (type != null) {
            String redirectUrl = "/recommend/" + type + "?keyword=" + URLEncoder.encode(keyword, StandardCharsets.UTF_8);
            if (selected != null) {
                redirectUrl += "&selected=" + URLEncoder.encode(selected, StandardCharsets.UTF_8);
            }
            if (latitude != null && !latitude.isEmpty()) {
                redirectUrl += "&lat=" + URLEncoder.encode(latitude, StandardCharsets.UTF_8);
            }
            if (longitude != null && !longitude.isEmpty()) {
                redirectUrl += "&lng=" + URLEncoder.encode(longitude, StandardCharsets.UTF_8);
            }
            return "redirect:" + redirectUrl;
        }

        // type이 없으면 general로 처리
        String redirectUrl = "redirect:/recommend/general?keyword=" + URLEncoder.encode(keyword, StandardCharsets.UTF_8);
        if (latitude != null && !latitude.isEmpty()) {
            redirectUrl += "&lat=" + URLEncoder.encode(latitude, StandardCharsets.UTF_8);
        }
        if (longitude != null && !longitude.isEmpty()) {
            redirectUrl += "&lng=" + URLEncoder.encode(longitude, StandardCharsets.UTF_8);
        }
        return redirectUrl;
    }

    // 새로운 PathVariable 기반 엔드포인트
    @GetMapping("/recommend/{searchType}")
    public String showRecommend(@PathVariable("searchType") String searchType,
                                @RequestParam("keyword") String keyword,
                                @RequestParam(value = "selected", required = false) String selected,
                                @RequestParam(value = "lat", required = false) String latitude,
                                @RequestParam(value = "lng", required = false) String longitude,
                                HttpSession session,
                                Model model) {
        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                return "redirect:/";
            }

            // searchType 검증 (emotion, situation, general만 허용)
            if (!searchType.equals("emotion") && !searchType.equals("situation") && !searchType.equals("general")) {
                return "redirect:/";
            }

            User user = (User) session.getAttribute("user");
            PlaceDto placesDto = recommendService.recommendPlaces(keyword, searchType);
            List<PlaceInfo> places = placesDto.getPlaces();
            places = places.stream().distinct().collect(Collectors.toList());
            String comment = placesDto.getComment();

            model.addAttribute("user", user);
            model.addAttribute("keyword", keyword);
            model.addAttribute("places", places != null ? places : List.of());
            model.addAttribute("comment", comment);
            model.addAttribute("type", searchType);
            model.addAttribute("selected", selected);
            // lat, lng도 모델에 추가
            model.addAttribute("latitude", latitude);
            model.addAttribute("longitude", longitude);

        } catch (Exception e) {
            model.addAttribute("errorMessage", "추천 결과를 가져오는 중 오류가 발생했습니다.");
            e.printStackTrace();
        }
        return "recommendResult";
    }

    @GetMapping("/place/{index}")
    public String placeDetail(@PathVariable("index") int index,
                              @RequestParam("keyword") String keyword,
                              @RequestParam(value = "lat", required = false) String latitude,
                              @RequestParam(value = "lng", required = false) String longitude,
                              HttpSession session,
                              Model model) {
        try {
            PlaceDto placesDto = recommendService.detailPlace(keyword);
            List<PlaceInfo> places = placesDto.getPlaces();

            if (index >= 0 && index < places.size()) {
                PlaceInfo place = places.get(index);
                model.addAttribute("place", place);
                model.addAttribute("keyword", keyword);
                model.addAttribute("user", session.getAttribute("user"));
                model.addAttribute("latitude", latitude);
                model.addAttribute("longitude", longitude);
                return "placeDetail";
            } else {
                return "redirect:/";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/";
        }
    }
}