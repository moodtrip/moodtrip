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
    }

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
    public String SituationS() {
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

        return "redirect:/recommend?keyword=" + encodedKeyword + "&type=emotion&selected=" + encodedKeyword;
    }

    // 상황 입력 처리
    @PostMapping("/processSituation")
    public String processSituation(@RequestParam("situationText") String situationText,
                                   @RequestParam(value = "lat", required = false) String latitude,
                                   @RequestParam(value = "lng", required = false) String longitude,
                                   Model model) {
        if (situationText == null || situationText.trim().isEmpty()) {
            return "redirect:/selection?mode=situation";
        }

        String encodedKeyword = URLEncoder.encode(situationText, StandardCharsets.UTF_8);
        return "redirect:/recommend?keyword=" + encodedKeyword + "&type=situation&selected=" + encodedKeyword;
    }

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
                                @RequestParam(value = "type", required = false) String type,
                                @RequestParam(value = "selected", required = false) String selected,
                                HttpSession session,
                                Model model) {
        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                return "redirect:/";
            }

            User user = (User) session.getAttribute("user");
            PlaceDto placesDto = recommendService.recommendPlaces(keyword);
            List<PlaceInfo> places = placesDto.getPlaces();
            places = places.stream().distinct().collect(Collectors.toList());
            String comment = placesDto.getComment();

            model.addAttribute("user", user);
            model.addAttribute("keyword", keyword);
            model.addAttribute("places", places != null ? places : List.of());
            model.addAttribute("comment", comment);
            model.addAttribute("type", type); // emotion 또는 situation
            model.addAttribute("selected", selected); // 선택된 감정/상황 텍스트


        } catch (Exception e) {
            model.addAttribute("errorMessage", "추천 결과를 가져오는 중 오류가 발생했습니다.");
            e.printStackTrace();
        }
        return "recommendResult";
    }

    @GetMapping("/place/{index}")
    public String placeDetail(@PathVariable("index") int index,
                              @RequestParam("keyword") String keyword,
                              HttpSession session,
                              Model model) {
        try {
            PlaceDto placesDto = recommendService.recommendPlaces(keyword);
            List<PlaceInfo> places = placesDto.getPlaces();

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
}