package com.example.demo.service;

import com.example.demo.model.PlaceInfo;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

@Service
public class OpenAiService {
    public String markdownCleaner(String str){
        String cleaned = str
                .replaceAll("```json", "")
                .replaceAll("```", "")
                .trim();
        return cleaned;
    }

    public List<String> extractKeyword(String input) throws Exception {
        String prompt =
                "너는 사용자의 입력을 바탕으로 \"검색에 사용할 장소 키워드\"를 생성하는 역할을 한다.\n"
                + "출력은 반드시 JSON 배열 형식으로만 해야 하며, 키워드들과 마지막 이유 이외의 문장/설명/마크다운은 절대 포함하지 않는다.\n"
                + "\n"
                + "----------------------------------\n"
                + "[규칙]\n"
                + "1) 반드시 장소로 해석 가능한 키워드만 생성한다. (예: 카페, 공원, 영화관, 놀거리, 음식점/맛집, 도서관, 술집, 스파, 북카페, 노래방, 미술관, 박물관, 서점, 전시회, 실내체육시설, 보드게임카페, 방탈출, 찜질방, 온천 등)\n"
                + "2) \"근처, 부근\" 같은 의존 명사는 키워드에 포함하지 않는다.\n"
                + "3) 신체 부위/증상(머리, 배, 다리, 팔, 두통 등)은 장소 키워드로 만들지 않는다. 단, 의료 관련이면 \"병원\" 같은 장소로 변환한다.\n"
                + "4) 감정 표현을 장소로 매핑한다. 입력이 감정적일수록 상황에 맞는 장소를 선택한다.\n"
                + "   - 우울: 카페, 공원, 북카페, 산책로, 미술관 등\n"
                + "   - 심심: 놀거리, 영화관, 보드게임카페, 방탈출, 오락실 등\n"
                + "   - 즐거움/신남: 놀이공원, 맛집, 전시회, 페스티벌 등\n"
                + "   - 조용히 쉬기: 도서관, 북카페, 공원, 스파, 찜질방, 온천 등\n"
                + "   - 헤어짐/실연: 술집, 카페, 노래방, 드라이브코스 등\n"
                + "5) \"친구\"라는 단어가 나와도 남자친구/여자친구로 오해하지 않는다. 특정 관계가 명시되지 않았다면 데이트 관련 키워드는 생성하지 않는다.\n"
                + "6) 지역/지하철역/지명 등이 포함되면 그 장소 앞에 그대로 접두로 붙인다. (예: \"용산구 카페\", \"강남역 영화관\")\n"
                + "7) 키워드는 3개만 생성한다. 간결하게 장소 범주만 출력한다.\n"
                + "8) 키워드 생성 후, 마지막 원소로 사용자가 입력한 상황에 대한 장소 추천의 전체적인 이유를 한 문장으로 추가한다(입력받은 상황에 대해서 간단한 입력을 포함한다)\n"
                + "8-1) 예시(우울해서 빵사고싶어: 우울해서 빵을 사고싶으니 제과점을 추천해드립니다)\n"
                + "8-2) 장소 추천의 전체적인 이유 한 문장에는 사용자가 입력한 상황이 반드시 포함되어야한다(장소에 대한 사용자의 입력이 있을경우 장소에 초점을 맞추어 추천한다)\n"
                + "9) 출력은 반드시 JSON 배열만 포함한다. 다른 텍스트는 포함하지 않는다.\n"
                + "10) 출력형식은 반드시 따라야한다.\n"
                + "11) 검색 키워드는 반드시 한글로만 구성한다.\n"
                + "12) 컴퓨터 수리나 고장 등 예기치 못한 상황 발생 입력받을 시 키워드를 반드시 전자상가로 검색\n"
                + "\n"
                + "----------------------------------\n"
                + "[출력 형식]\n"
                + "[\"키워드1\", \"키워드2\", \"키워드3\", \"선정 이유\"]\n"
                + "\n"
                + "----------------------------------\n"
                + "[사용자 입력]\n"
                + input + "\n"
                + "\n"
                + "[출력]\n";

        // Ollama 호출
        String responseText = callOllama(prompt).trim();
        System.out.println("올라마가 생성한 키워드 : " + responseText);

        // AI 응답(JSON 배열)을 파싱
        // 0. 마크다운 제거
        responseText = markdownCleaner(responseText);

        // 1. 최상위 JSON 파싱
        JSONObject obj = new JSONObject(responseText);

        // 2. "response" 필드 꺼내기 (String 타입)
        String responseArrayStr = obj.getString("response");

        // 3. String → JSONArray 변환
        JSONArray arr = new JSONArray(responseArrayStr);

        List<String> keywords = new ArrayList<>();

        for (int i = 0; i < arr.length(); i++) {
            keywords.add(arr.getString(i));
        }

//        System.out.println("올라마가 생성한 키워드: " + keywords);
        return keywords;
    }

    //감정검색 테스트버전
    public List<String> emotionAiSearch(String input) throws Exception {
        String prompt = "."
                + "너는 아래와 같은 형식의 사용자의 입력 감정 태그를 바탕으로 \"검색에 사용할 장소 키워드\"를 생성하는 역할을 한다.\n"
                + "[사용자의 입력 형식]\n"
                + "[키워드1] [키워드2] [키워드3] ..."
                + "출력은 반드시 JSON 배열 형식으로만 해야 하며, 키워드들과 마지막 이유 이외의 문장/설명/마크다운은 절대 포함하지 않는다.\n"
                + "\n"
                + "----------------------------------\n"
                + "[규칙]\n"
                + "1) 반드시 장소로 해석 가능한 키워드만 생성한다. (예: 카페, 공원, 영화관, 놀거리, 음식점/맛집, 도서관, 술집, 스파, 북카페, 노래방, 미술관, 박물관, 서점, 전시회, 실내체육시설, 보드게임카페, 방탈출, 찜질방, 온천 등)\n"
                + "2) \"근처, 부근\" 같은 의존 명사는 키워드에 포함하지 않는다.\n"
                + "3) 신체 부위/증상(머리, 배, 다리, 팔, 두통 등)은 장소 키워드로 만들지 않는다. 단, 의료 관련이면 \"병원\" 같은 장소로 변환한다.\n"
                + "4) 감정 표현을 장소로 매핑한다. 입력된 감정이 여러 개일 수 있으며, 복합적일수록 그 상황에 어울리는 장소를 종합적으로 선택한다.\n"
                + " - 단일 감정은 해당 감정에 맞는 장소를 중심으로 추천한다.\n"
                + " - 두 가지 이상의 감정이 주어지면, 공통된 분위기를 고려해 장소를 제안한다.\n"
                + "  - 우울: 카페, 공원, 북카페, 산책로, 미술관 등\n"
                + "  - 스트레스: 놀거리, 영화관, 보드게임카페, 방탈출, 오락실 등\n"
                + "  - 즐거움/신남: 놀이공원, 맛집, 전시회, 페스티벌 등\n"
                + "  - 조용히 쉬기/사색: 도서관, 북카페, 공원, 스파, 찜질방, 온천 등\n"
                + "  - 헤어짐/실연/그리움: 술집, 카페, 노래방, 드라이브코스 등\n"
                + "  - 외로움, 즐거움: 바다, 클럽, 술집 등\n"
                + "  - 우울, 외로움: 카페, 서점, 공원 등\n"
                + "  - 스트레스, 활기 : 헬스장, 노래방, 클럽\n"
                + "5) 지역/지하철역/지명 등이 포함되면 그 장소 앞에 그대로 접두로 붙인다. (예: \"용산구 카페\", \"강남역 영화관\")\n"
                + "6) 키워드는 3개만 생성한다. 간결하게 장소 범주만 출력한다.\n"
                + "7) 키워드 생성 후, 마지막 원소로 사용자가 선택한 감정에 대한 장소 추천의 전체적인 이유를 한 문장으로 추가한다\n"
                + "7-1) 예시(신남: 신나는 기분을 위해 즐겁고 신나는 장소를 골랐습니다)\n"
                + "7-2) 장소 추천의 전체적인 이유 한 문장에는 사용자가 입력한 감정이 반드시 포함되어야한다\n"
                + "8) 출력은 반드시 JSON 배열만 포함한다. 다른 텍스트는 포함하지 않는다.\n"
                + "9) 출력형식은 반드시 따라야한다.\n"
                + "10) 검색 키워드는 반드시 한글로만 구성한다.\n"
                + "11) 컴퓨터 수리나 고장 등 예기치 못한 상황 발생 입력받을 시 키워드를 반드시 전자상가로 검색\n"
                + "\n"
                + "----------------------------------\n"
                + "[출력 형식]\n"
                + "[\"키워드1\", \"키워드2\", \"키워드3\", \"선정 이유\"]\n"
                + "\n"
                + "----------------------------------\n"
                + "[사용자 입력]\n"
                + input + "\n"
                + "\n"
                + "[출력]\n";

        System.out.println("사용자의 입력 : " + input);

        // Ollama 호출
        String responseText = callOllama(prompt).trim();
        System.out.println("올라마가 생성한 키워드 : " + responseText);

        // AI 응답(JSON 배열)을 파싱
        // 0. 마크다운 제거
        responseText = markdownCleaner(responseText);

        // 1. 최상위 JSON 파싱
        JSONObject obj = new JSONObject(responseText);

        // 2. "response" 필드 꺼내기 (String 타입)
        String responseArrayStr = obj.getString("response");

        // 3. String → JSONArray 변환
        JSONArray arr = new JSONArray(responseArrayStr);

        List<String> keywords = new ArrayList<>();

        for (int i = 0; i < arr.length(); i++) {
            keywords.add(arr.getString(i));
        }

        return keywords;
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
//        URL url = new URL("http://localhost:11434/api/generate");
//        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//        conn.setRequestMethod("POST");
//        conn.setRequestProperty("Content-Type", "application/json");
//        conn.setDoOutput(true);
//
//        JSONObject body = new JSONObject();
//        body.put("model", "gemma3:4b");
//        body.put("prompt", prompt);
//        body.put("stream", false);
//
//        try (OutputStream os = conn.getOutputStream()) {
//            os.write(body.toString().getBytes());
//        }
//
//        try (Scanner scanner = new Scanner(conn.getInputStream()).useDelimiter("\\A")) {
//            return scanner.hasNext() ? scanner.next() : "";
//        }
//    }
        URL url = new URL("http://localhost:11434/api/generate");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        JSONObject body = new JSONObject();
        body.put("model", "gemma3:4b");
        body.put("prompt", prompt);
        body.put("stream", false); // 그대로 false 유지

        try (OutputStream os = conn.getOutputStream()) {
            os.write(body.toString().getBytes());
        }

        // 전체 응답 읽기
        StringBuilder rawResponse = new StringBuilder();
        try (Scanner scanner = new Scanner(conn.getInputStream())) {
            while (scanner.hasNextLine()) {
                rawResponse.append(scanner.nextLine().trim());
            }
        }

        // 응답 문자열에 JSON 객체가 여러 개 붙어있을 수 있으니
        // 마지막 JSON 객체만 안전하게 추출
        String responseText = rawResponse.toString();

        // 여러 JSON이 붙어있으면 "}{", "}{" 기준으로 split
        int lastObjIndex = responseText.lastIndexOf("{");
        if (lastObjIndex != -1) {
            responseText = responseText.substring(lastObjIndex);
        }

        JSONObject obj = new JSONObject(responseText);
        return responseText; // response 필드만 반환
    }
}
