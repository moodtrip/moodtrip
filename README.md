# ✈️ MOODTRIP

> **당신의 감정과 상황에 딱 맞는 장소를 찾아주는 AI 기반 장소 추천 서비스**

MOODTRIP은 사용자의 **감정 또는 상황을 입력받아**, 로컬 LLM(Ollama)을 통해 맞춤형 장소 키워드를 생성하고,  
**실시간 크롤링 및 지도 정보를 기반으로 실제 존재하는 장소를 추천해주는 웹 서비스**입니다.

---

## 📌 프로젝트 소개

기존의 단순 카테고리 기반 검색에서 벗어나,

> “우울할 때 혼자 생각 정리하기 좋은 장소”  
> “친구들과 신나게 놀 수 있는 곳”

처럼 \"사용자의 감정과 맥락(Context)\"을 이해하는 AI 기반 장소 추천 플랫폼입니다.

🎯 핵심 특징  
- 감정 기반 추천 (우울, 기쁨, 설렘 등)  
- 자유로운 문장 기반 상황 추천  
- 실시간 네이버 지도 크롤링 (평점, 주소, 이미지, 링크 제공)  
- 실제 존재하는 장소 기반 추천

---

## 💻 개발 환경 (Environment)

| 항목              | 내용                         |
|-----------------|----------------------------|
| OS              | Ubuntu                     |
| Language        | Java 17                    |
| Framework       | Spring Boot 3.5.0          |
| Build Tool      | Maven                      |
| IDE             | IntelliJ IDEA              |
| LLM             | Ollama (gemma3:4b)         |
| Crawling        | Selenium, Chrome WebDriver |
| Version Control | Git                        |

---

## ⚙️ 적용된 기술

### 🛠 Backend
- Spring Boot (Web, JPA)
- Selenium & Jsoup
- Ollama + Gemma3:4b
- Thymeleaf

### 🎨 Frontend
- HTML5, CSS3, JavaScript
- Custom Responsive CSS

### ☁ Infrastructure
- Ubuntu Server
- Git Repository
- Maven

---

## 🚀 주요 기능 (Features)

### 1️⃣ AI 기반 키워드 생성 (`OpenAiService`)
- 감정 또는 문장 입력 → 장소 키워드 & 추천 사유 생성  
  예: *"고요한 북카페"*, *"노을 보기 좋은 공원"*

---

### 2️⃣ 실시간 장소 크롤링 (`NaverCrawlerService`)
| 제공 정보 | 설명 |
|-----------|------|
| 장소명 | 실제 네이버 지도 기반 |
| 이미지 | 대표사진 |
| 주소 | 도로명 및 지번 |
| 평점 | 사용자 리뷰 기반 |
| 지도 링크 | 네이버 지도 연결 |

---

### 3️⃣ 위치 기반 맞춤 추천
- 사용자 현재 위치 기반 주변 추천

---

### 4️⃣ 반응형 웹 UI
- PC / 모바일 모두 대응
- 사용자 친화 UI/UX

---

### ⚙️개발자

| 이름                                      | 역할 | 개발 부분                  |
|-----------------------------------------|----|------------------------|
| 정아형 (https://github.com/wnddjgld)       | 팀장 | 백엔드 / 프롬프트 엔지니어링       |
| 배형권 (https://github.com/smcmfmf)        | 팀원 | 백엔드 / 크롤링 / UI, UX 수정  |
| 김용재 (https://github.com/bernadette1008) | 팀원 | 백엔드 / 크롤링 / 프로젝트 버전 관리 / 디자인 설계 |
| 윤준석 (https://github.com/Emto0103)       | 팀원 | 프론트엔드 / 디자인 수정         |
---

## 📦 초기 설치 및 환경 구성

---

>### 🔹 1. Ubuntu 서버 환경 설정 (Ollama 설치)
>
>```bash
>
>sudo apt update && sudo apt upgrade -y
>
>sudo apt install curl -y
>
>curl -fsSL https://ollama.com/install.sh | sh
>
>ollama --version
>
>ollama pull gemma3:4b
>
>ollama ps
>
>ollama run gemma3:4b --keepalive (원하는 시간)h
>```
>
>### 🔹 1-1. Gemma3:4b 실행 로그 확인
>```bash
>
>history | grep keepalive
>```

---

## 📁 프로젝트 설치 및 실행

---

>### 🔹 2. 초기 서비스 설치 과정
>```bash
>
>moodtrip@2025-moodtrip:
>
>sudo apt install -y openjdk-17-jdk
>sudo apt install -y maven
>sudo apt install -y git
>
>git clone https://github.com/moodtrip/moodtrip.git
>
>cd moodtrip/
>
>mvn clean package -DskipTests
>```
>
>### 🔹 2-1. 서비스 실행 과정 (테스트용)
>```bash
>
>sudo java -jar target/demo-0.0.1-SNAPSHOT.jar --server.port=80
>```
>
>### 🔹 2-2. 서비스 실행 과정 (서비스용)
>```bash
>
>sudo nohup java -jar target/demo-0.0.1-SNAPSHOT.jar --server.port=80 > app.log 2>&1
>```
>
>### 🔹 2-3. 서비스 중지
>```bash
>
>sudo pkill -f 'demo-0.0.1-SNAPSHOT.jar'
>```
>
>### 🔹 2-4. 서비스 로그 및 에러 확인
>```bash
>
>tail -f app.log
>```

---

## 🔄 프로젝트 업데이트

---

>### 🔹 3. 서비스 최신화
>```bash
>
>moodtrip@2025-moodtrip:
>
>git pull origin main
>
>mvn clean package -DskipTests
>```

---

## 🛑 사용 중인 서비스 확인 및 정지

---

>### 🔹 4. 프로세스 조회
>```bash
>
>moodtrip@2025-moodtrip:~/moodtrip$ sudo lsof -i :80
>
>--------출력문--------
>COMMAND    PID     USER   FD   TYPE  DEVICE SIZE/OFF NODE NAME
>ssh       3801 moodtrip    4u  IPv4 3072698      0t0  TCP localhost:43136->local                                                    host:http (FIN_WAIT2)
>ssh       3801 moodtrip    5u  IPv4 3072692      0t0  TCP localhost:43114->local                                                    host:http (FIN_WAIT2)
>ssh       3801 moodtrip    7u  IPv4 6631507      0t0  TCP localhost:53158->local                                                    host:http (CLOSE_WAIT)
>ssh       3801 moodtrip    8u  IPv4 6630243      0t0  TCP localhost:59220->local                                                    host:http (CLOSE_WAIT)
>ssh       3801 moodtrip    9u  IPv4 6627514      0t0  TCP localhost:52254->local                                                    host:http (CLOSE_WAIT)
>ssh       3801 moodtrip   13u  IPv4 3072694      0t0  TCP localhost:43128->local                                                    host:http (FIN_WAIT2)
>ssh       3801 moodtrip   14u  IPv4 6627520      0t0  TCP localhost:60662->local                                                    host:http (CLOSE_WAIT)
>ssh       3801 moodtrip   15u  IPv4 6627521      0t0  TCP localhost:60668->local                                                    host:http (CLOSE_WAIT)
>ssh       3801 moodtrip   16u  IPv4 6630317      0t0  TCP localhost:55902->local                                                    host:http (CLOSE_WAIT)
>ssh       3801 moodtrip   17u  IPv4 6631505      0t0  TCP localhost:53142->local                                                    host:http (CLOSE_WAIT)
>http    142270     _apt    8u  IPv4 2804331      0t0  TCP 2025-moodtrip:51410->u                                                    buntu-mirror-2.ps6.canonical.com:http (CLOSE_WAIT)
>http    142271     _apt    5u  IPv4 2803034      0t0  TCP 2025-moodtrip:46958->u                                                    buntu-mirror-1.ps6.canonical.com:http (CLOSE_WAIT)
>java    168920     root   25u  IPv6 3073482      0t0  TCP localhost:http->localh                                                    ost:43114 (CLOSE_WAIT)
>java    168920     root   28u  IPv6 3073483      0t0  TCP localhost:http->localh                                                    ost:43128 (CLOSE_WAIT)
>java    168920     root   30u  IPv6 3073486      0t0  TCP localhost:http->localh                                                    ost:43136 (CLOSE_WAIT)
>java    389140     root    9u  IPv6 6627507      0t0  TCP *:http (LISTEN)
>java    389140     root   13u  IPv6 6634488      0t0  TCP 2025-moodtrip:http->19                                                    2.168.24.195:64074 (ESTABLISHED)
>java    389140     root   14u  IPv6 6634627      0t0  TCP 2025-moodtrip:http->19                                                    2.168.24.195:64091 (ESTABLISHED)
>```
>
>### 🔹 4-1. 프로세스 정지
>```bash
>
>moodtrip@2025-moodtrip:~/moodtrip$ sudo kill -9 (해당 PID 코드)
>```
