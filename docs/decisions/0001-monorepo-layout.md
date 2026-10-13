# 0001. 모노레포 + 폴더 기준 소유권 + 단기 브랜치

- 상태: 채택
- 날짜: 2026-10-13
- 결정한 사람: @<CLIENT_OWNER>
- 영역: 공용

## 맥락

- Tabiji는 클라이언트(Kotlin/CMP), 서버, AI 세 영역을 각각 다른 사람이 맡는 3인 팀 프로젝트다
- 클라이언트와 서버는 REST 계약(`api/openapi.yaml`)을 공유하고, 디자인 핸드오프는 클라이언트와 AI·디자인 담당이 같이 본다
- 클라이언트 담당은 Kotlin·CMP 학습이 목적이라 진행 속도가 들쭉날쭉하고, 단계마다 작은 PR을 자주 낸다
- 영역마다 언어·빌드 도구가 다르다 (클라이언트는 Gradle, 나머지는 미정)

## 선택지

| 선택지 | 장점 | 단점 |
| --- | --- | --- |
| A. 영역별 레포 3개 + 계약 레포 | 권한·CI 분리가 자연스럽다 | 계약 변경이 레포 3곳 PR로 흩어진다. 이슈·디자인 문서가 분산 |
| B. 모노레포 + 사람별 장기 브랜치 (`client-dev`, `server-dev`) | 각자 자유롭게 커밋 | 장기 브랜치끼리 멀어져 통합 시점에 충돌 폭발. `main`이 늘 낡음 |
| **C. 모노레포 + 폴더 기준 소유권 + 단기 브랜치** | 계약·디자인·문서가 한 PR에서 같이 바뀐다. `main`이 늘 최신 | 경로 필터 없는 CI는 낭비 → 영역별 CI에 `paths` 필터 필요 |

## 결정

**C를 택한다.**

- 장기 브랜치는 `main` 하나. 작업 브랜치는 **영역 prefix**로 시작하는 `<영역>/<type>-<slug>`(루트 공용·영역 횡단 작업은 `repo/`, 실험은 type `exp`), 수명 목표 1주. 브랜치 목록만 봐도 어느 영역 작업인지 보이게 하기 위해서다
- 소유권은 브랜치가 아니라 **폴더**로 나눈다. `.github/CODEOWNERS`가 경로별 리뷰어를 강제한다
- 각 영역은 독립 빌드 루트를 가진다 (`client/`는 독립 Gradle 루트). 루트에 공용 빌드 시스템을 두지 않는다
- CI는 영역별 워크플로(`<영역>-ci.yml`)에 `paths` 필터를 걸어, 남의 영역 변경으로 내 CI가 돌지 않게 한다
- 릴리스는 영역별 태그(`<영역>-vX.Y.Z`)로 독립적으로 낸다
- 머지는 squash merge만 허용해 `main` 기록을 "PR 하나 = 커밋 하나"로 유지한다

## 결과

- 쉬워지는 것: 계약 변경과 양쪽 구현을 같은 레포에서 추적, 디자인 핸드오프를 클라이언트 코드 옆에 둠, 이슈·라벨 하나로 관리
- 감수할 것: 영역별 CI 경로 필터와 필수 상태 체크 관리(경로 필터 때문에 안 도는 체크를 "필수"로 걸면 PR이 영원히 대기한다 → `docs/github.md` 참조)
- 다시 검토할 조건: 영역별 배포 권한을 레포 단위로 나눠야 할 때, 레포 크기·CI 시간이 문제가 될 때

## 참고

- [Trunk Based Development — short-lived feature branches](https://trunkbaseddevelopment.com/short-lived-feature-branches/)
- [GitHub — About code owners](https://docs.github.com/en/repositories/managing-your-repositorys-settings-and-features/customizing-your-repository/about-code-owners)
