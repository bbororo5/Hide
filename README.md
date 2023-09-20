# 🎵 팀 하이드

![600_340-해상도-300](https://github.com/INOCAM-REALPROJECT-TEAM8/Back-end/assets/123007169/a7b05ff5-cd72-4429-a9c4-721c6aa4ca9f)

## 🎇 프로젝트 기획
- **남다른 음악적 취향**을 가진 사람들이 자신의 음악적 취향을 마음껏 공유하는 사이트입니다.
- **AKA. 숨듣명**에서 영감을 받아 본인의 남다른, 숨겨둔 취향의 음악을 익명으로 다같이 공유하며 마음껏 즐기자는 취지에서 기획하게 되었습니다.

## 🛠 기술 스택
| 분류       | 기술 스택                                                                                                                                                                    |
|------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 프론트앤드 | Typescript, Vercel, Git Flow, Redux, React Query, Axios, Styled Components                                                                                                 |
| 백앤드     | Spring Boot, Github Action, Gradle, Docker Hub, Nginx, JUnit5, Jmeter, CloudWatch, Redis, MySQL, Query DSL, Spring Security, OAuth2.0, JWT, STOMP, S3, EC2, JAVA 17, Spring Data JPA |

## 📐 프로젝트 아키텍쳐
![Cloud Architecture (2)](https://github.com/INOCAM-REALPROJECT-TEAM8/Back-end/assets/123007169/d6ab4211-c8de-4070-9bba-66b9002f2f66)

## ⛓ ERD 
![8rCvbFx3WkkarWNaZ](https://github.com/INOCAM-REALPROJECT-TEAM8/Back-end/assets/123007169/d343120b-f90e-40fb-a7e8-6d95c63f1de5)

## 기술적 의사결정
### CI/CD

<aside>
<img src="https://prod-files-secure.s3.us-west-2.amazonaws.com/32cf2c6c-ad91-4108-8a6b-858eed1e2748/5ad71672-21a4-4d53-958e-04f0a33eb581/github_action.png" alt="https://prod-files-secure.s3.us-west-2.amazonaws.com/32cf2c6c-ad91-4108-8a6b-858eed1e2748/5ad71672-21a4-4d53-958e-04f0a33eb581/github_action.png" width="40px" /> **Github Action:** 프로젝트의 지속적인 통합 및 배포를 위해 Github Action을 도입했습니다. 전통적인 CI/CD 도구인 Jenkins에 비해 GitHub Action은 프로젝트 설정이 간편하며, GitHub 저장소와의 통합이 원활하여 선택하였습니다. 또한, 별도의 서버 구축 및 관리가 필요 없기 때문에 초기 구축 비용과 유지 관리 비용을 절감할 수 있었습니다. 각 개발 단계에서 자동화된 테스트와 빌드를 통해 코드의 안정성을 지속적으로 확보하였습니다.

</aside>

<aside>
<img src="https://prod-files-secure.s3.us-west-2.amazonaws.com/32cf2c6c-ad91-4108-8a6b-858eed1e2748/8d20c95c-f5bf-46ad-8739-87bc8034983f/gradle.png" alt="https://prod-files-secure.s3.us-west-2.amazonaws.com/32cf2c6c-ad91-4108-8a6b-858eed1e2748/8d20c95c-f5bf-46ad-8739-87bc8034983f/gradle.png" width="40px" /> **Gradle**: 프로젝트의 의존성 관리뿐만 아니라, 테스트 자동화의 핵심 도구로 Gradle을 활용하였습니다. Gradle을 통해 꼼꼼하게 작성된 테스트 코드를 지속적으로 검증하며, 이를 통해 코드 품질의 일관성을 유지하고, 버그 발생 리스크를 최소화하였습니다. 테스트 코드 작성은 소프트웨어 개발에서 결함을 조기에 발견하고, 요구 사항의 정확한 구현을 보장하는 중요한 과정입니다. 이러한 테스트 코드의 중요성을 깊이 인지하고 있으며, Gradle의 테스트 자동화 기능을 통해 프로젝트에 적극적으로 반영하였습니다.

</aside>

<aside>
<img src="https://prod-files-secure.s3.us-west-2.amazonaws.com/32cf2c6c-ad91-4108-8a6b-858eed1e2748/33852417-cfe6-43da-8e70-ee786febf3dd/docker.png" alt="https://prod-files-secure.s3.us-west-2.amazonaws.com/32cf2c6c-ad91-4108-8a6b-858eed1e2748/33852417-cfe6-43da-8e70-ee786febf3dd/docker.png" width="40px" /> **Docker Hub**: 애플리케이션의 독립적 실행 환경을 보장하기 위해 Docker를 사용했고, Docker Hub를 통해 이미지 버전 관리 및 배포를 효율화했습니다.

</aside>

### 리버스 프록시 & 로드밸런싱

<aside>
<img src="https://prod-files-secure.s3.us-west-2.amazonaws.com/32cf2c6c-ad91-4108-8a6b-858eed1e2748/e20504eb-83ab-4fee-959c-79053b17582c/nginx.png" alt="https://prod-files-secure.s3.us-west-2.amazonaws.com/32cf2c6c-ad91-4108-8a6b-858eed1e2748/e20504eb-83ab-4fee-959c-79053b17582c/nginx.png" width="40px" /> **Nginx**: Apache와 같은 다른 웹 서버에 비해 Nginx는 비동기 이벤트 기반 구조를 활용해 높은 동시 접속 처리 능력을 보유하고 있습니다. 이 특성은 높은 트래픽 환경에서도 우수한 성능을 유지하며, 적은 자원을 효율적으로 활용할 수 있게 해줍니다. 또한, Nginx의 리버스 프록시 및 로드밸런싱 기능은 웹 애플리케이션의 확장성과 안정성을 높이는 데 크게 기여합니다. 이러한 이유들로, Nginx를 웹 서버로서 선택하게 되었습니다.

</aside>

### 테스트

<aside>
<img src="https://prod-files-secure.s3.us-west-2.amazonaws.com/32cf2c6c-ad91-4108-8a6b-858eed1e2748/4f3aa8a2-0291-40fe-8879-9ef5660b5345/asdffffffdvdvdv.png" alt="https://prod-files-secure.s3.us-west-2.amazonaws.com/32cf2c6c-ad91-4108-8a6b-858eed1e2748/4f3aa8a2-0291-40fe-8879-9ef5660b5345/asdffffffdvdvdv.png" width="40px" /> **Junit5** :  어노테이션과 람다 표현식을 지원해서 유연하고 읽기 쉬운 테스트 코드를 작성 하게 도와줍니다. 모듈 테스트, 컨트롤러 테스트 등을 위해서 사용했습니다.

</aside>

<aside>
<img src="https://prod-files-secure.s3.us-west-2.amazonaws.com/32cf2c6c-ad91-4108-8a6b-858eed1e2748/4a8a55eb-4739-4f39-97cb-71e5fbda3135/header_jmeter.jpg" alt="https://prod-files-secure.s3.us-west-2.amazonaws.com/32cf2c6c-ad91-4108-8a6b-858eed1e2748/4a8a55eb-4739-4f39-97cb-71e5fbda3135/header_jmeter.jpg" width="40px" /> **Jmeter** : 서버가 트래픽을 얼마나 견딜 수 있는지, 응답 시간이 얼마나 걸리는지 등을 미리 알아야 할 필요성을 느꼈습니다. JMeter를 도입하면 HTTP, FTP, JDBC 등 다양한 프로토콜에 대한 테스트가 가능하고, 그 결과를 다양한 형태로 분석할 수 있습니다. 결론적으로, 부하와 응답 시간 테스트를 위해서 사용했습니다.

</aside>

## 로깅 & 모니터링

<aside>
<img src="https://prod-files-secure.s3.us-west-2.amazonaws.com/32cf2c6c-ad91-4108-8a6b-858eed1e2748/59e47446-5b71-4fd0-8b4f-44a5c8a1dfc2/log.png" alt="https://prod-files-secure.s3.us-west-2.amazonaws.com/32cf2c6c-ad91-4108-8a6b-858eed1e2748/59e47446-5b71-4fd0-8b4f-44a5c8a1dfc2/log.png" width="40px" /> **LogBack**: 프로젝트의 초기 설정 복잡성을 최소화하면서, 효과적인 로깅 시스템을 구축하는 것이 필요했습니다. Spring Boot는 기본적으로 Logback을 로깅 라이브러리로 사용하기 때문에, 별도의 로깅 의존성을 추가하거나 제거할 필요가 없었습니다. 이러한 이유로, 초기 설정의 복잡성을 피하면서도 효과적인 로깅 시스템을 구축하기 위해 Logback을 선택하였습니다.

</aside>

<aside>
<img src="https://prod-files-secure.s3.us-west-2.amazonaws.com/32cf2c6c-ad91-4108-8a6b-858eed1e2748/2befc000-c663-4013-b89c-916b55c6874c/cloudwatch.png" alt="https://prod-files-secure.s3.us-west-2.amazonaws.com/32cf2c6c-ad91-4108-8a6b-858eed1e2748/2befc000-c663-4013-b89c-916b55c6874c/cloudwatch.png" width="40px" /> **CloudWatch**: 클라우드 환경에서 실시간으로 애플리케이션의 상태를 모니터링하고, 문제 발생 시 즉시 대응하기 위한 효과적인 도구가 필요했습니다. AWS CloudWatch는 실시간 모니터링 및 로깅, 사용자 정의 알람 설정 등 다양한 기능을 제공하여 시스템 전체를 한 눈에 볼 수 있습니다. AWS 환경에서 운영되는 애플리케이션의 모니터링에 최적화되어 있어, 서비스 품질을 보장하며 효과적인 대응이 가능하게 되었습니다.

</aside>

### 캐싱

<aside>
<img src="https://prod-files-secure.s3.us-west-2.amazonaws.com/32cf2c6c-ad91-4108-8a6b-858eed1e2748/0326e115-f213-4432-b0c3-5ec594cdd99d/1529926dfdfd.png" alt="https://prod-files-secure.s3.us-west-2.amazonaws.com/32cf2c6c-ad91-4108-8a6b-858eed1e2748/0326e115-f213-4432-b0c3-5ec594cdd99d/1529926dfdfd.png" width="40px" /> **Redis** : 소셜 네트워크 프로젝트에서는 각자의 계정에 접근하는 것이 서비스를 제대로 사용하기 위한 첫 단추가 된다는 점에서, 거의 모든 사용자가 로그인 요청을 한다고 판단했습니다. 따라서 응답 시간의 최소화가 필수적입니다. 사용자의 경험성의 극대화를 위하여 refresh토큰을 Redis에 저장하기로 결정했습니다.

또한 저희의 WAS는 2개로 운영되고 있습니다. 각 서버가 Spotify API의 access token을 독립적으로 관리하게 되면, 토큰 갱신 요청이 불필요하게 중복되어 실행되는 경우가 생깁니다. 이러한 문제점을 해결하기 위해, AWS Elasticache의 Redis를 도입하여 access token을 중앙 집중식으로 관리하게 되었습니다. 이렇게 함으로써, 모든 서버는 동일한 토큰 정보를 참조하게 되어 중복된 토큰 갱신 요청을 방지하고, 전체적인 시스템의 효율성을 높였습니다.

</aside>

## 데이터베이스 복제

<aside>
<img src="https://prod-files-secure.s3.us-west-2.amazonaws.com/32cf2c6c-ad91-4108-8a6b-858eed1e2748/7aaa4865-da63-41de-bbb4-89ed6b94b1ae/mysql.png" alt="https://prod-files-secure.s3.us-west-2.amazonaws.com/32cf2c6c-ad91-4108-8a6b-858eed1e2748/7aaa4865-da63-41de-bbb4-89ed6b94b1ae/mysql.png" width="40px" /> **MySQL**: MySQL을 프로젝트에서 사용하면서, DB 하나로 모든 CRUD 작업을 처리하는 부하가 증가할 것으로 예상되었습니다. 특히 타인의 프로필에 자주 접근하는 소셜 프로젝트의 특성을 고려하면, SELECT 쿼리의 요청이 빈번히 발생할 것으로 예상하였습니다. 이를 효율적으로 처리하기 위한 방법이 필요했습니다.

이 문제에 대한 해결책으로, 우리는 읽기 전용 복제본을 도입하기로 결정했습니다. 이 복제본은 SELECT 쿼리에 대한 요청을 전담하게 되어, 원본 DB의 부하를 상당히 줄여주게 됩니다. 더불어, 이러한 복제 전략을 DB 부하의 분산만이 아니라, 장애 발생 시 대응책으로도 활용됩니다. 만약 주 DB에 문제가 발생하면, 복제본을 통해 데이터의 일관성을 유지하고 서비스의 중단 없이 운영을 이어갈 수 있습니다. 이러한 점에서, 복제 전략의 도입은 서비스의 안전성을 높이기 위한 중요 전략으로 작용하였습니다.

</aside>

## 쿼리 최적화

<aside>
<img src="https://prod-files-secure.s3.us-west-2.amazonaws.com/32cf2c6c-ad91-4108-8a6b-858eed1e2748/c7ef3a3a-12c2-48c4-be7e-4b5b0de25a6d/query_dsl.png" alt="https://prod-files-secure.s3.us-west-2.amazonaws.com/32cf2c6c-ad91-4108-8a6b-858eed1e2748/c7ef3a3a-12c2-48c4-be7e-4b5b0de25a6d/query_dsl.png" width="40px" /> **쿼리 DSL**:  프로젝트 진행 중, ORM의 한계로 인해 복잡한 쿼리 작성과 세부 설정에 어려움을 겪게 되었습니다. 이에 따라, 보다 안정적이고 효율적인 방법을 모색하던 중, QueryDSL의 타입 안전성에 주목하게 되었습니다. 컴파일 시점의 오류 감지 능력은 잘못된 쿼리 작성의 위험성을 크게 줄여주었고, 직관적인 문법 구조는 코드의 가독성을 향상시켰습니다. 더불어, 유지보수 과정에서도 쿼리 로직의 명확성이 이를 더욱 원활하게 만들어 주었습니다. 이러한 이유로, QueryDSL을 프로젝트에 적극 도입하기로 결정하였습니다.

</aside>

## 인증 & 인가

#주요 기능
| 음악 검색 기능 | 검색 창을 통해서 원하는 곡을 검색할 수 있습니다. |
| --- | --- |
| 음악 추천 기능 | 유저가 팔로우한 사람, 등록한 별점, 최근 들은 곡을 바탕으로 음악을 추천해주는 기능입니다.
숨어서 듣는 음악에 맞게 유저가 좋아하는 취향에 맞도록 추천하기 위해서 로직을 구현했습니다. |
| 소셜 로그인 | Oauth를 활용해서 카카오톡, 구글 계정이 있다면 간편하게 로그인 할 수 있습니다. |
| 플레이 리스트 | 마음에 드는 곡을 내 플레이 리스트에 저장할 수 있습니다. 플레이 리스트는 유저끼리 공개 되어있어 누가 어떤 곡을 저장했는지 자유롭게 볼 수 있습니다. |
| 팔로우 기능 | 곡의 상세 페이지에서 별점을 준 사람, 댓글을 단 사람 등을 보면서 나와 취향이 비슷한 사람이라고 생각되면 팔로우 할 수 있습니다.
유저 페이지에서 팔로워 목록, 팔로잉 목록도 확인할 수 있습니다. |
| 별점 기능 | 곡에 별점을 등록할 수 있습니다. 
곡 상세 페이지에서 평균 별점을 확인할 수 있습니다. |
| 댓글 기능 | 곡에 코멘트를 남길 수 있습니다. |
| 채팅 기능 | 유저와 1:1 실시간 채팅이 가능합니다. 로그인 된 상태라면 채팅창을 보고있지 않아도 채팅 메시지를 배너 알림으로 볼 수 있습니다. |
| 회원 정보 수정 기능 | 내 프로필 이미지를 등록하거나 닉네임을 변경할 수 있습니다. |

#트러블 슈팅
## N+1 문제

<aside>
💡 프로젝트 중, 유저의 최근 들은 트랙 카운트 조회 시 **`User`** 엔티티의 불필요한 데이터 로딩 문제를 발견하였습니다. **`user_id`**만 필요한 상황에서 전체 **`User`** 정보를 불러와 성능 저하의 원인이 되었습니다.

이 문제를 해결하기 위해, **Query DSL**을 도입하여 필요한 필드만 선택적으로 불러오는 쿼리를 작성하였습니다. 결과적으로, 데이터베이스의 부하를 크게 줄이며 응답 시간의 개선(약 61.54% 감축)을 이루었습니다.

</aside>

## **나쁜 가독성과 유지보수성**

<aside>
💡 스포티파이 API 호출을 담당하는 클래스는 초기에 동기식 방식으로 설계되었으나, 시간이 흐름에 따라 클래스의 복잡성이 증가하는 문제에 직면했습니다. 이를 해결하기 위해, 우선 공통된 기능을 재사용 가능한 모듈로 분리하여 중복 코드를 제거하고, 스포티파이 API의 다양한 호출 방법을 유연하게 대응할 수 있도록 인터페이스 기반의 설계를 도입했습니다.

추가로, API 호출 중 발생할 수 있는 예외 상황들에 대응하기 위해 통합된 예외 처리 메커니즘을 구현하여 안정성을 강화했으며, 추후 음악 스트리밍 서비스의 API 호출 로직 추가를 위한 확장성 또한 고려했습니다. 

이렇게 진행한 추상화 작업의 큰 장점은 비동기식 방식으로의 전환을 계획하게 되었을 때 드러났습니다. 이미 구조화되어 있던 코드 덕분에, 비동기 호출로의 변경 작업이 예상보다 훨씬 간편하게 진행되었고, 이 변경을 통해 시스템의 응답 시간과 리소스 활용도가 크게 향상되었습니다.

</aside>

## 반복되는 로그

<aside>
💡 로그 작성 중, 일부 중요 정보가 반복적으로 사용되는 문제점을 인지하게 되었습니다. **`trackId`**와 **`userId`**와 같은 정보가 여러 로그에서 계속적으로 사용되어 코드의 가독성과 유지 보수성에 문제가 생기게 되었습니다. 

이 문제를 해결하기 위해, AOP(Aspect-Oriented Programming)의 기법을 활용하여 자주 사용되는 정보를 중앙에서 관리하고 추적하기로 결정했습니다.

MDC를 활용하여 로그 메시지에 중복적으로 표시되던 정보를 효과적으로 관리하면서 로그 작성을 더 편리하고 효율적으로 수행할 수 있게 되었습니다. 이 접근 방식은 로깅의 일관성을 높이면서도 코드의 중복을 크게 줄이는 데 큰 도움이 되었습니다.

</aside>

## API 할당량 부족

<aside>
💡 처음 프로젝트에서 음악 재생 기능을 구현할 때는 유튜브 API를 활용했습니다. 사용자가 음악을 검색하면 검색 API를 호출해서 나온 첫 번째 결과 영상을 재생하게 설정했습니다.
유튜브 API의 할당량 제한 때문에 문제가 발생했습니다. 한 번의 검색 요청에 할당량 100이 소모되고, 하루 할당량은 총 10,000입니다. 이 제한 때문에 검색 요청을 하루에 단 100번만 할 수 있었습니다.
할당량 증가 요청을 유튜브에 보냈지만, 승인되지 않았습니다. 이후 다른 방법을 찾다가 Spotify Play Button을 활용하기로 결정했습니다. Spotify의 Play Button을 통해 제공되는 iframe을 활용해, 30초 미리듣기 기능을 제공하게 되었습니다.

</aside>

## 분산 토큰 관리 문제
