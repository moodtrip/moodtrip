package com.example.demo.service;

import com.example.demo.model.PlaceInfo;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Scanner;

@Service
public class OpenAiService {

    public String extractKeyword(String input) throws Exception {
        String prompt = "너는 사용자 입력을 읽고 검색 키워드를 만드는 역할이야." +
                " 근처, 부근 이런 의존 명사는 키워드에 포함하지 말아줘\n"+
                " 장소로 유추될 수 없는(예시: 머리, 진통, 다리, 팔, 두통)키워드는 생성하지 말아줘\n"+
                " 친구라고 입력 받았을 때 남자친구, 여자친구 명시하지 않았으면 데이트 코스같은 키워드로 추천하지 마\n"+
                " 예시:\n" +
                "- \"친구랑 함께 놀만한 장소 추천해줘\" → \"놀거리\"\n" +
                "- \"용산구인데 나 우울해\" → \"용산구 카페\"\n" +
                "- \"강남역에서 심심해\" → \"강남역 놀거리\"\n" +
                "- \"신촌 너무 더워\" → \"신촌 카페\"\n" +
                "- \"나 심심해\" → \"놀거리\"\n" +
                "- \"머리, 배, 가슴, 다리, 팔 아파\" → \"병원\"\n" +
                "사용자 입력: " + input + "\n";

        String responseText = callOllama(prompt);
        String keyword = new JSONObject(responseText).getString("response")
                .trim().replaceAll("[\"\\n]", "");
        System.out.println("🔍 올라마가 생성한 키워드: " + keyword);
        return keyword;
    }

    public List<PlaceInfo> summarizeAndSortPlaces(List<PlaceInfo> places) throws Exception {
        StringBuilder prompt = new StringBuilder();
        prompt.append("다음은 장소 추천 결과입니다. 평점이 높은 순으로 5곳을 추천하고 요약해서 JSON 형식으로 정리해주세요.\n");

        for (int i = 0; i < places.size(); i++) {
            PlaceInfo p = places.get(i);
            prompt.append(String.format("%d. 이름: %s, 평점: %.1f, 주소: %s, 설명: %s\n",
                    i + 1, p.getName(), p.getRating(), p.getAddress(), p.getDescription()));
        }

        prompt.append("\n출력은 다음 JSON 형식만 사용해주세요:\n")
                .append("[{\"name\":\"장소명\", \"rating\":4.5, \"address\":\"주소\", \"description\":\"설명\"}, ...]");

        String responseText = callOllama(prompt.toString());
        String response = new JSONObject(responseText).getString("response");

        // JSON 파싱
        return PlaceInfo.fromJsonArray(response);
    }

    private String callOllama(String prompt) throws Exception {
        URL url = new URL("http://localhost:11434/api/generate");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        JSONObject body = new JSONObject();
        body.put("model", "gemma3:4b");
        body.put("prompt", prompt);
        body.put("stream", false);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(body.toString().getBytes());
        }

        try (Scanner scanner = new Scanner(conn.getInputStream()).useDelimiter("\\A")) {
            return scanner.hasNext() ? scanner.next() : "";
        }
    }
}
