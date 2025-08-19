package com.example.demo.service;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

@Service
public class OpenAiService {

    public String extractKeyword(String input) throws Exception {
        // ① 프롬프트 구성
        String prompt = "너는 사용자 입력을 읽고 검색 키워드를 만드는 역할이야." +
                " 근처, 부근 이런 의존 명사는 키워드에 포함하지 말아줘\n"+
                " 장소로 유추될 수 없는(예시: 머리, 진통, 다리, 팔, 두통)키워드는 생성하지말아줘\n"+
                " 예시:\n" +
                "- \"친구랑 함께 놀만한 장소 추천해줘\" → \"놀거리\"\n" +
                "- \"용산구인데 나 우울해\" → \"용산구 카페\"\n" +
                "- \"강남역에서 심심해\" → \"강남역 놀거리\"\n" +
                "- \"신촌 너무 더워\" → \"신촌 카페\"\n" +
                "- \"나 심심해\" → \"놀거리\"\n" +
                "- \"머리, 배, 가슴, 다리, 팔 아파\" → \"병원\"\n" +
                "사용자 입력: " + input + "\n";

        // ② 올라마 서버에 POST 요청
        URL url = new URL("http://localhost:11434/api/generate");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        // ③ 요청 바디 전송
        JSONObject body = new JSONObject();
        body.put("model", "gemma3:4b");
        body.put("prompt", prompt);
        body.put("stream", false);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(body.toString().getBytes());
        }

        // ④ 응답 수신
        String responseText;
        try (Scanner scanner = new Scanner(conn.getInputStream()).useDelimiter("\\A")) {
            responseText = scanner.hasNext() ? scanner.next() : "";
        }

        JSONObject responseJson = new JSONObject(responseText);
        String keyword = responseJson.getString("response").trim().replaceAll("[\"\\n]", "");
        System.out.println("올라마가 생성한 키워드: " + keyword);
        return keyword;
    }
}
