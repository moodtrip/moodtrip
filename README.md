# 초기 서비스 설치 과정 (ollama)
## (apt 서버 업데이트)
```
sudo apt update && sudo apt upgrade -y
```
## (curl 설치)
```
sudo apt install curl -y
```
## (ollama 설치)
```
curl -fsSL https://ollama.com/install.sh | sh
```
## (설치된 ollama 버전 확인)
```
ollama --version
```
## (gemma3:4b 버전 설치)
```
ollama pull gemma3:4b
```
# 초기 서비스 설치 과정 (github Repository)

## (경로 확인)
```
moodtrip@2025-moodtrip:
```
## (프로젝트 다운로드)
```
git clone https://github.com/moodtrip/moodtrip.git
```
## (moodtrip@2025-moodtrip:~/moodtrip$ 경로 이동)
```
cd moodtrip/
```
## (프로젝트 빌드 후 jar 생성)
```
mvn clean package -DskipTests
```
## (생성된 jar 80포트로 실행)
```
sudo java -jar target/demo-0.0.1-SNAPSHOT.jar --server.port=80
```
# 서비스 최신화 과정 (github Repository)
## (경로 확인)
```
moodtrip@2025-moodtrip:
```
## (main 브렌치에서 프로젝트 변경사항 다운로드)
```
git pull origin main
```
## (프로젝트 리빌드 후 jar 생성)
```
mvn clean package -DskipTests
```
## (생성된 jar 80포트로 실행)
```
sudo java -jar target/demo-0.0.1-SNAPSHOT.jar --server.port=80
```
# 서비스 중지

## (프로세스 강제 종료)
```
"ctrl + c"
```
# 사용 중인 서비스 확인 코드

## (80포트를 사용하고 있는 서비스만 조회)
```
moodtrip@2025-moodtrip:~/moodtrip$ sudo lsof -i :80
--------출력문--------
COMMAND    PID     USER   FD   TYPE  DEVICE SIZE/OFF NODE NAME
ssh       3801 moodtrip    4u  IPv4 3072698      0t0  TCP localhost:43136->local                                                    host:http (FIN_WAIT2)
ssh       3801 moodtrip    5u  IPv4 3072692      0t0  TCP localhost:43114->local                                                    host:http (FIN_WAIT2)
ssh       3801 moodtrip    7u  IPv4 6631507      0t0  TCP localhost:53158->local                                                    host:http (CLOSE_WAIT)
ssh       3801 moodtrip    8u  IPv4 6630243      0t0  TCP localhost:59220->local                                                    host:http (CLOSE_WAIT)
ssh       3801 moodtrip    9u  IPv4 6627514      0t0  TCP localhost:52254->local                                                    host:http (CLOSE_WAIT)
ssh       3801 moodtrip   13u  IPv4 3072694      0t0  TCP localhost:43128->local                                                    host:http (FIN_WAIT2)
ssh       3801 moodtrip   14u  IPv4 6627520      0t0  TCP localhost:60662->local                                                    host:http (CLOSE_WAIT)
ssh       3801 moodtrip   15u  IPv4 6627521      0t0  TCP localhost:60668->local                                                    host:http (CLOSE_WAIT)
ssh       3801 moodtrip   16u  IPv4 6630317      0t0  TCP localhost:55902->local                                                    host:http (CLOSE_WAIT)
ssh       3801 moodtrip   17u  IPv4 6631505      0t0  TCP localhost:53142->local                                                    host:http (CLOSE_WAIT)
http    142270     _apt    8u  IPv4 2804331      0t0  TCP 2025-moodtrip:51410->u                                                    buntu-mirror-2.ps6.canonical.com:http (CLOSE_WAIT)
http    142271     _apt    5u  IPv4 2803034      0t0  TCP 2025-moodtrip:46958->u                                                    buntu-mirror-1.ps6.canonical.com:http (CLOSE_WAIT)
java    168920     root   25u  IPv6 3073482      0t0  TCP localhost:http->localh                                                    ost:43114 (CLOSE_WAIT)
java    168920     root   28u  IPv6 3073483      0t0  TCP localhost:http->localh                                                    ost:43128 (CLOSE_WAIT)
java    168920     root   30u  IPv6 3073486      0t0  TCP localhost:http->localh                                                    ost:43136 (CLOSE_WAIT)
java    389140     root    9u  IPv6 6627507      0t0  TCP *:http (LISTEN)
java    389140     root   13u  IPv6 6634488      0t0  TCP 2025-moodtrip:http->19                                                    2.168.24.195:64074 (ESTABLISHED)
java    389140     root   14u  IPv6 6634627      0t0  TCP 2025-moodtrip:http->19                                                    2.168.24.195:64091 (ESTABLISHED)
```
## 사용 중인 서비스 정지 코드
```
moodtrip@2025-moodtrip:~/moodtrip$ sudo kill -9 (해당 PID 코드)
```