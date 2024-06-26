# HIDE

![600_340-해상도-300](https://github.com/INOCAM-REALPROJECT-TEAM8/Back-end/assets/123007169/a7b05ff5-cd72-4429-a9c4-721c6aa4ca9f)


# 프로젝트 기획
- **남다른 음악적 취향**을 가진 사람들이 자신의 음악적 취향을 마음껏 공유하는 사이트입니다.
- **AKA. 숨듣명**에서 영감을 받아 본인의 남다른, 숨겨둔 취향의 음악을 익명으로 다같이 공유하며 마음껏 즐기자는 취지에서 기획하게 되었습니다.


# 주요 기능

| 음악 검색 기능 | 검색 창을 통해서 원하는 곡을 검색할 수 있습니다. |
| --- | --- |
| 음악 추천 기능 | 유저가 팔로우한 사람, 등록한 별점, 최근 들은 곡을 바탕으로 음악을 추천해주는 기능입니다. 숨어서 듣는 음악에 맞게 유저가 좋아하는 취향에 맞도록 추천하기 위해서 로직을 구현했습니다. |
| 소셜 로그인 | Oauth를 활용해서 카카오톡, 구글 계정이 있다면 간편하게 로그인 할 수 있습니다. |
| 플레이 리스트 | 마음에 드는 곡을 내 플레이 리스트에 저장할 수 있습니다. 플레이 리스트는 유저끼리 공개 되어있어 누가 어떤 곡을 저장했는지 자유롭게 볼 수 있습니다. |
| 팔로우 기능 | 곡의 상세 페이지에서 별점을 준 사람, 댓글을 단 사람 등을 보면서 나와 취향이 비슷한 사람이라고 생각되면 팔로우 할 수 있습니다. 유저 페이지에서 팔로워 목록, 팔로잉 목록도 확인할 수 있습니다. |
| 별점 기능 | 곡에 별점을 등록할 수 있습니다. 곡 상세 페이지에서 평균 별점을 확인할 수 있습니다. |
| 댓글 기능 | 곡에 코멘트를 남길 수 있습니다. |
| 채팅 기능 | 유저와 1:1 실시간 채팅이 가능합니다. 로그인 된 상태라면 채팅창을 보고있지 않아도 채팅 메시지를 배너 알림으로 볼 수 있습니다. |
| 회원 정보 수정 기능 | 내 프로필 이미지를 등록하거나 닉네임을 변경할 수 있습니다. |


# 기술 스택                                                                                             
| Spring Boot, Spring Data JPA, Query DSL, Spring Security, Github Action, Docker, JUnit5, MySQL, STOMP |

# 프로젝트 아키텍쳐
![Cloud Architecture (2)](https://github.com/INOCAM-REALPROJECT-TEAM8/Back-end/assets/123007169/d6ab4211-c8de-4070-9bba-66b9002f2f66)

# 기술적 의사결정

## CI/CD

<img src="https://img.shields.io/badge/GitHub_Actions-2088FF?style=flat-square&logo=githubactions&logoColor=white"/> : 프로젝트의 지속적인 통합 및 배포를 위해 Github Action을 도입했습니다. 전통적인 CI/CD 도구인 Jenkins에 비해 GitHub Action은 프로젝트 설정이 간편하며, GitHub 저장소와의 통합이 원활하여 선택하였습니다. 또한, 별도의 서버 구축 및 관리가 필요 없기 때문에 초기 구축 비용과 유지 관리 비용을 절감할 수 있었습니다. 각 개발 단계에서 자동화된 테스트와 빌드를 통해 코드의 안정성을 지속적으로 확보하였습니다.

<img src="https://img.shields.io/badge/Gradle-02303A?style=flat-square&logo=gradle&logoColor=white"/> : 프로젝트의 의존성 관리뿐만 아니라, 테스트 자동화의 핵심 도구로 Gradle을 활용하였습니다. Gradle을 통해 꼼꼼하게 작성된 테스트 코드를 지속적으로 검증하며, 이를 통해 코드 품질의 일관성을 유지하고, 버그 발생 리스크를 최소화하였습니다. 테스트 코드 작성은 소프트웨어 개발에서 결함을 조기에 발견하고, 요구 사항의 정확한 구현을 보장하는 중요한 과정입니다. 이러한 테스트 코드의 중요성을 깊이 인지하고 있으며, Gradle의 테스트 자동화 기능을 통해 프로젝트에 적극적으로 반영하였습니다.

<img src="https://img.shields.io/badge/Docker_Hub-2496ED?style=flat-square&logo=docker&logoColor=white"/> : 애플리케이션의 독립적 실행 환경을 보장하기 위해 Docker를 사용했고, Docker Hub를 통해 이미지 버전 관리 및 배포를 효율화했습니다.

## 리버스 프록시 & 로드밸런싱

<img src="https://img.shields.io/badge/Nginx-009639?style=flat-square&logo=nginx&logoColor=white"/> : Apache와 같은 다른 웹 서버에 비해 Nginx는 비동기 이벤트 기반 구조를 활용해 높은 동시 접속 처리 능력을 보유하고 있습니다. 이 특성은 높은 트래픽 환경에서도 우수한 성능을 유지하며, 적은 자원을 효율적으로 활용할 수 있게 해줍니다. 또한, Nginx의 리버스 프록시 및 로드밸런싱 기능은 웹 애플리케이션의 확장성과 안정성을 높이는 데 크게 기여합니다. 이러한 이유들로, Nginx를 웹 서버로서 선택하게 되었습니다.

## 테스트

<img src="https://img.shields.io/badge/Junit5-25A162?style=flat-square&logo=Junit5&logoColor=white"/> :  개발된 코드가 예상대로 동작하는지 미리 테스트하여 코드의 예측가능성을 높이고 안정적인 CI를 구축해야 했습니다. gradle로 빌드 시 실행될 단위 테스트, 컨트롤러 테스트 검사를 위해 Junit5를 사용했습니다.

##  쿼리 최적화

<img src="https://img.shields.io/badge/Query_DSL-02303A?style=flat-square&logo=&logoColor=white"/> :  프로젝트 진행 중, ORM의 한계로 인해 복잡한 쿼리 작성과 세부 설정에 어려움을 겪게 되었습니다. 이에 따라, 보다 안정적이고 효율적인 방법을 모색하던 중, QueryDSL의 타입 안전성에 주목하게 되었습니다. 컴파일 시점의 오류 감지 능력은 잘못된 쿼리 작성의 위험성을 크게 줄여주었고, 직관적인 문법 구조는 코드의 가독성을 향상시켰습니다. 더불어, 유지보수 과정에서도 쿼리 로직의 명확성이 이를 더욱 원활하게 만들어 주었습니다. 이러한 이유로, QueryDSL을 프로젝트에 적극 도입하기로 결정하였습니다.

##  인증 & 인가

<img src="https://img.shields.io/badge/Spring_Security-6DB33F?style=flat-square&logo=springsecurity&logoColor=white"/>  :  기본적인 일반 로그인 기능뿐만 아니라 다양한 인증, 인가 옵션을 제공하기 때문에 선택했습니다. 특히, CSRF 보호와 SQL 인젝션 등 다양한 보안 취약점에 대한 방어 기능을 내장하고 있어, 이를 통해 개발 속도와 애플리케이션의 안정성을 높일 수 있었습니다.

# 트러블 슈팅

##  N+1 문제

💡 프로젝트 중, 유저의 최근 들은 트랙 카운트 조회 시 'User' 엔티티의 불필요한 데이터 로딩 문제를 발견하였습니다. PK인 'user_id'만 필요한 상황에서 전체 'User' 정보를 불러와 성능 저하의 원인이 되었습니다.

이 문제를 해결하기 위해, **Query DSL**을 도입하여 필요한 필드만 선택적으로 불러오는 쿼리를 작성하였습니다. 결과적으로, 데이터베이스의 부하를 크게 줄이며 응답 시간의 개선(약 61.54% 감축)을 이루었습니다.

##  반복되는 로그

💡 로깅은 서비스의 효율성과 안정성을 높이기 위한 핵심 도구입니다. 'trackId'는 모니터링을 통해 자주 사용되는 트랙의 데이터를 캐싱하는 데 활용하기 위해 로그에 포함시켜야 했으며, 'userId'는 에러 로그가 발생했을 때 해당 유저의 행동 패턴을 추적하여 서비스 품질을 개선하기 위한 목적으로 로그에 기록해야 했습니다.

그러나 이러한 중요한 정보들이 로그 메시지에 반복적으로 포함되어야 하는 요구 사항 때문에 코드의 가독성과 유지 보수성에 문제가 발생하게 되었습니다. 이를 해결하기 위해, AOP(Aspect-Oriented Programming) 기법을 활용하여 로그 작성 과정에서의 반복적인 작업을 자동화하였습니다.

MDC와 AOP를 결합하여 'trackId'와 'userId'와 같은 정보를 중앙에서 효율적으로 관리하게 되었습니다. 이 접근 방식은 로깅의 일관성을 높이면서도 코드의 중복을 크게 줄이는 데 큰 도움이 되었습니다.


##  API 할당량 부족

💡 처음 프로젝트에서 음악 재생 기능을 구현할 때는 유튜브 API를 활용했습니다. 사용자가 음악을 검색하면 검색 API를 호출해서 나온 첫 번째 결과 영상을 재생하게 설정했습니다.
  
유튜브 API의 할당량 제한 때문에 문제가 발생했습니다. 한 번의 검색 요청에 할당량 100이 소모되고, 하루 할당량은 총 10,000입니다. 이 제한 때문에 검색 요청을 하루에 단 100번만 할 수 있었습니다.

할당량 증가 요청을 유튜브에 보냈지만, 승인되지 않았습니다. 이후 다른 방법을 찾다가 Spotify Play Button을 활용하기로 결정했습니다. Spotify의 Play Button을 통해 제공되는 iframe을 활용해, 30초 미리듣기 기능을 제공하게 되었습니다.


##  분산 토큰 관리 문제

💡 부하 테스트 중, 스포티파이의 access token 및 로그인 refresh 토큰 관련 이슈를 발견했습니다. 여러 요청이 동시에 발생할 경우 분할된 애플리케이션에서 각자의 토큰을 따로 가지고 있어서 발생하는 비효율적인 동작 및 불필요한 요청이 관찰되었습니다. 

특히, 로그인 과정에서 이중 발급된 refresh 토큰 중 어떤 것을 읽어와야 하는지 결정하는 데 어려움이 있어 로그인이 정상적으로 진행되지 않는 경우도 발생했습니다. 이러한 문제점을 해결하기 위해 중앙 집중식의 토큰 관리 방안을 고려했고, 결론적으로 Redis를 활용하여 토큰을 캐시하는 방식으로 문제를 극복하기로 결정했습니다. 

Redis의 빠른 응답 속도와 분산 처리 기능을 활용하여 토큰 정보를 효율적으로 관리하면서 동시에 안정적인 서비스 환경을 구축할 수 있었습니다.


# ERD 
![8rCvbFx3WkkarWNaZ](https://github.com/INOCAM-REALPROJECT-TEAM8/Back-end/assets/123007169/d343120b-f90e-40fb-a7e8-6d95c63f1de5)

# 맡은 역할
## 전선웅(BE) : 기여도 20 / 100
  - **기술적 의사결정 주도**
  - **ERD 및 아키텍처 설계**: ERD로 데이터베이스 구조 표현, 시스템 아키텍처 정의
  - **RESTful API 개발**: 재생 횟수 기반 탑 트랙, 최근 트랙, 사용자별 추천 트랙, 트랙 상세 정보 조회, 사용자 활동 기록, 별점 관리 등
  - **CI/CD 파이프라인 구축**: 자동화 빌드, 테스트, 배포 파이프라인 설정
  - **단위 및 기능 테스트 구현**: 코드 검증을 위한 단위 및 기능 테스트 케이스 작성
  - **코드 품질 개선 및 리팩토링**: 코드의 효율성 및 가독성 향상 작업
