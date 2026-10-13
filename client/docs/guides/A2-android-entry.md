# A2 — Android 진입점: 같은 `App()`을 에뮬레이터에, 旅 런처 아이콘

> 브랜치: `client/feat-a2-android-entry` (Claude가 만들어 둠, A1 브랜치 위에 쌓음) · 커밋 footer `Refs: A2`
> 시각 기준: `design/handoff/handoff-v2/` (PR #7). 이번 단계에서 쓰는 것은 `docs/04_icons_favicon.md`와 `assets/logo/icon/`뿐이고, 둘 다 v1과 같다.

## 목표

A1에서 Desktop에 띄운 **바로 그 `App()`** 을 Android 에뮬레이터에서도 띄운다. 그리고 앱 서랍에 **旅 글리프 런처 아이콘**이 보이게 한다.

끝 그림은 두 가지다.
- 에뮬레이터 앱 서랍에 흰 원 안의 진회색 旅가 보인다. 누르면 Desktop과 같은 하늘색 화면에 "Tabiji"가 뜬다.
- 테마 아이콘을 켜면 旅가 시스템 색으로 물든다.

이번 단계에서 화면 코드는 한 줄도 바꾸지 않는다. **"공용 라이브러리 + 플랫폼별 실행 파일"** 구조가 실제로 어떻게 동작하는지 눈으로 확인하는 단계다.

> 로드맵 변경: v1 로드맵에서 A2에 있던 **Desktop 창 아이콘**(`Window(icon = …)`)은 A6으로 옮겼다. Compose Resources를 배우는 단계에서 같이 하는 편이 자연스럽다.

## 먼저 읽을 것 (30분)

| 문서 | 왜 |
| --- | --- |
| [Activity 소개](https://developer.android.com/guide/components/activities/intro-activities) | Android에는 `main()`이 없다. 대신 OS가 Activity를 만든다 |
| [앱 매니페스트 개요](https://developer.android.com/guide/topics/manifest/manifest-intro) | `<activity>`와 `intent-filter`가 "앱 서랍에 등록"하는 방식 |
| [Adaptive icon](https://developer.android.com/develop/ui/views/launch/icon_design_adaptive) | 108dp 캔버스, 66dp 안전영역, 전경·배경·monochrome 세 레이어 |
| [VectorDrawable](https://developer.android.com/develop/ui/views/graphics/vector-drawable-resources) | SVG를 Android 리소스로 옮길 때의 XML 형식 |
| [Kotlin 클래스](https://kotlinlang.org/docs/classes.html) · [상속](https://kotlinlang.org/docs/inheritance.html) · [null 안전성](https://kotlinlang.org/docs/null-safety.html) | 아래 "처음 보는 Kotlin 문법" 표의 원문 |
| [명령줄에서 에뮬레이터 실행](https://developer.android.com/studio/run/emulator-commandline) | 0번 순서의 `emulator -avd` |

## 처음 보는 Kotlin 문법 (이번 단계에서 만나는 것만)

| 문법 | 한 줄 설명 | C++로 빗대면 |
| --- | --- | --- |
| `class MainActivity : ComponentActivity()` | `:` 뒤가 부모 클래스다. 괄호 `()`는 **부모 생성자 호출**이다 | `class MainActivity : public ComponentActivity` + 초기화 목록의 `ComponentActivity()` |
| 기본이 `final` | Kotlin 클래스·함수는 기본적으로 상속·재정의가 막혀 있다. 부모가 `open`으로 열어 둔 것만 재정의할 수 있다 | C++의 `virtual`이 기본으로 꺼져 있는 셈이다 (`final`이 기본값) |
| `override fun onCreate(...)` | 부모 함수를 재정의할 때 `override`가 **필수**다. 빠뜨리면 컴파일 오류가 난다 | C++11 `override` 지정자. 다만 Kotlin에서는 선택이 아니라 의무다 |
| `Bundle?` | 타입 뒤 `?`는 "null일 수 있음"이다. `Bundle`과 `Bundle?`는 **서로 다른 타입**이다 | `Bundle*`(nullptr 가능)와 `Bundle&`(항상 유효)의 차이를 타입 시스템이 강제한다 |
| `super.onCreate(savedInstanceState)` | 부모 구현 호출 | `ComponentActivity::onCreate(savedInstanceState)` |

`savedInstanceState`는 화면 회전 등으로 Activity가 다시 만들어질 때 이전 상태를 담아 오는 꾸러미다. 처음 실행할 때는 null이라서 `?`가 붙는다. 이번 단계에서는 받아서 부모에게 넘기기만 한다.

## 핵심 개념 (오늘 분량)

**Android에는 `main()`이 없다.** Desktop은 우리가 `fun main()`을 쓰고 JVM이 그것을 부른다. Android는 반대로 **OS가 매니페스트에 등록된 클래스를 찾아서 직접 인스턴스를 만들고**, 라이프사이클 콜백(`onCreate` → `onStart` → `onResume` …)을 순서대로 부른다.

ROS 2로 빗대면 이렇다.
- Desktop의 `main()`은 노드 실행 파일의 `main()`에서 `rclcpp::spin(node)`을 직접 부르는 방식이다.
- Android Activity는 **컴포넌트 노드**에 가깝다. `RCLCPP_COMPONENTS_REGISTER_NODE`로 클래스를 등록해 두면 컨테이너가 로드해 준다. 매니페스트의 `<activity>`가 그 등록이다.
- 라이프사이클 콜백은 lifecycle node의 `on_configure`/`on_activate`와 비슷하다. 상태 전이를 OS가 주도한다는 점이 같다.

**`setContent { }` = Android판 `Window { }`.** A1에서 `App()`을 `Window` 밖에서 부르면 `LocalFontFamilyResolver not present`가 났다. `Window`가 폰트·밀도 같은 CompositionLocal을 제공하기 때문이었다. Android에서는 `setContent`가 그 역할을 한다. Activity 화면에 Compose를 붙이고 같은 환경값들을 내려준다. 그래서 `App()`은 반드시 `setContent`의 람다 안에서 불러야 한다.

**`intent-filter`의 MAIN + LAUNCHER = "앱 서랍에 아이콘으로 올려 줘".** Intent는 "이런 일을 할 Activity 있나요?"라는 시스템 메시지다. 앱 서랍(런처)은 MAIN 액션과 LAUNCHER 카테고리를 단 Activity만 아이콘으로 보여 준다. 시그널/슬롯에 빗대면, 이 필터는 "나는 이 시그널을 받는다"고 시스템에 미리 신고하는 것이다.

**Android 리소스와 한정자(qualifier).** `res/` 아래 폴더 이름이 곧 조건이다.
- `drawable/`: 모든 기기
- `mipmap-anydpi-v26/`: API 26 이상에서만 쓰인다

Qt의 `QFileSelector`(`+android/` 폴더)나 CMake의 조건부 소스와 같은 발상이다. 빌드하면 `R` 클래스가 생기고, XML에서는 `@drawable/이름`, `@mipmap/이름`으로 참조한다. **파일 이름은 소문자·숫자·밑줄만** 쓸 수 있다.

**Adaptive icon은 세 레이어다.**
- 배경(흰색 단색): 런처 규격상 필수다. 그래서 핸드오프의 "배경 없음" 원칙에서 예외인 곳이다.
- 전경(旅): 108dp 캔버스 안에 둔다. 런처가 원·둥근 사각형 등으로 잘라도 안전한 66dp 영역 안에 56dp 크기로 들어 있다.
- monochrome: Android 13+ 테마 아이콘용이다. 같은 글리프를 쓰고, 색은 시스템이 칠한다.

## 만들 파일 (직접 작성)

| 경로 | 내용 |
| --- | --- |
| `androidApp/src/main/kotlin/com/yamada/studio/tabiji/MainActivity.kt` | `class MainActivity : ComponentActivity()`. `onCreate`에서 `setContent { App() }` |
| `androidApp/src/main/AndroidManifest.xml` (수정) | `<application>`에 `android:icon`, 안에 `<activity>` + `intent-filter` |
| `androidApp/src/main/res/drawable/ic_launcher_tabi_foreground.xml` | 旅 글리프 VectorDrawable (SVG에서 손으로 변환) |
| `androidApp/src/main/res/drawable/ic_launcher_tabi_background.xml` | 흰색 108×108 VectorDrawable |
| `androidApp/src/main/res/mipmap-anydpi-v26/ic_launcher.xml` | `<adaptive-icon>`: 위 둘 + monochrome |

> 패키지 주의: `MainActivity.kt`는 `package com.yamada.studio.tabiji`다. `androidApp/build.gradle.kts`의 `namespace`가 같은 이름이라서, 매니페스트에서 `.MainActivity`(앞에 점)로 줄여 쓸 수 있다.

## 조립 순서

### 0. 에뮬레이터 부팅 (A0에서 남긴 숙제)

```bash
~/Android/Sdk/emulator/emulator -avd tabiji-api37 &
adb devices          # 부팅이 끝나면 "emulator-5554	device"
```

- 첫 부팅은 1~3분 걸린다. `adb devices`에 `offline`으로 보이면 조금 더 기다린다
- 창이 뜨지 않거나 `KVM` 관련 오류가 나면 출력 전체를 붙여넣는다
- IntelliJ의 Android 플러그인이 켜져 있으면 Device Manager에서 띄워도 같다

### 1. `MainActivity.kt` — 진입점

A1의 `main.kt`와 1:1로 대응한다.

```kotlin
class MainActivity : /* 부모 클래스 */() {
    override fun onCreate(savedInstanceState: Bundle?) {
        /* 1) 부모 onCreate 먼저 */
        /* 2) setContent { } 안에서 App() */
    }
}
```

| Desktop (`main.kt`) | Android (`MainActivity.kt`) |
| --- | --- |
| `fun main()`: JVM이 부름 | `onCreate`: OS가 부름 |
| `application { Window { App() } }` | `setContent { App() }` |
| 창 제목 = `Window(title = …)` | 앱 이름 = 매니페스트 `android:label` (이미 "Tabiji") |

import는 직접 적는다. `setContent`는 `androidx.activity.compose`에 있다. 이름이 비슷한 다른 패키지와 헷갈리기 쉽다.

```kotlin
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.yamada.studio.tabiji.app.App
```

> `targetSdk`가 35 이상이면 Android가 앱을 **상태바·내비게이션바 밑까지(edge-to-edge)** 그린다. 지금은 글자가 가운데라 문제없다. 시스템 바와 겹치는 부분(insets)은 A8에서 Phone 레이아웃을 만들 때 다룬다.

### 2. `AndroidManifest.xml` — 등록

`<application ... />`이 지금은 스스로 닫힌 태그다. 여는 태그와 닫는 태그로 바꾸고 그 사이에 `<activity>`를 넣는다. 필요한 부품은 아래와 같다.

```xml
<application
    ...기존 속성...
    android:icon="@mipmap/ic_launcher">

    <activity
        android:name="/* 클래스 */"
        android:exported="/* ? */">
        <intent-filter>
            <action android:name="android.intent.action.MAIN" />
            <category android:name="android.intent.category.LAUNCHER" />
        </intent-filter>
    </activity>
</application>
```

- `android:exported`는 "다른 앱(런처 포함)이 이 Activity를 열 수 있나"다. 런처가 열어야 하니 답은 정해져 있다. `intent-filter`가 있는 Activity는 이 속성을 **반드시 명시**해야 한다(API 31+). 빠뜨리면 빌드가 실패한다.
- 파일 위쪽 주석("A2에서 사용자가 추가한다")은 현재 상태에 맞게 고쳐 둔다.

### 3. 런처 아이콘 — SVG를 VectorDrawable로 손 변환

원본은 `design/handoff/handoff-v2/assets/logo/icon/`의 두 파일이다.
- `ic_launcher_tabi_foreground.svg`: `<g transform>` 하나 안에 `<path>` 하나
- `ic_launcher_tabi_background.svg`: 흰 `<rect>` 하나

VectorDrawable은 SVG의 축소판이라 대응표만 알면 옮길 수 있다.

| SVG | VectorDrawable |
| --- | --- |
| `<svg viewBox="0 0 108 108">` | `<vector android:width="108dp" android:height="108dp" android:viewportWidth="108" android:viewportHeight="108">` |
| `<g transform="translate(26 26) scale(0.56)">` | `<group android:translateX="26" android:translateY="26" android:scaleX="0.56" android:scaleY="0.56">` |
| `<path fill="#1C2633" d="M…Z"/>` | `<path android:fillColor="#1C2633" android:pathData="M…Z"/>` |
| `<rect width="108" height="108" fill="#FFFFFF"/>` | `<path>` 하나로: `pathData="M0,0h108v108h-108z"` |

- `<vector>`에는 `xmlns:android="http://schemas.android.com/apk/res/android"`가 필요하다
- `d` 값은 **통째로 복사**한다. 매우 길지만 손댈 필요 없다. `Q`(2차 베지어) 명령도 VectorDrawable이 그대로 이해한다
- `<group>`의 변환 순서(크기 조절 후 이동)는 SVG의 `translate(…) scale(…)`과 결과가 같다. 그래서 숫자를 그대로 옮기면 된다. 이 변환이 旅를 56dp로 줄여 안전영역 가운데에 둔다

`mipmap-anydpi-v26/ic_launcher.xml`의 모양은 핸드오프 `docs/04_icons_favicon.md`의 "Android 리소스 구성" 절에 이미 있다. 그대로 따르되 `<?xml …?>` 선언과 `xmlns:android`를 붙인다. monochrome 레이어는 전경과 **같은 drawable**을 가리킨다. 테마 아이콘에서는 시스템이 모양(알파)만 보고 색을 다시 칠하기 때문이다.

> IntelliJ에서 Android 플러그인이 켜져 있으면 `File ▸ New ▸ Vector Asset`으로 SVG를 자동 변환할 수도 있다. 이번에는 손 변환을 권한다. 경로가 하나뿐이고, VectorDrawable 구조를 한 번 직접 보는 게 이후 아이콘 22종(B1)을 다룰 때 도움이 된다.

> **API 24·25 기기**: adaptive icon은 API 26부터라서 `minSdk 24`인 이 앱은 24·25에서 기본 아이콘으로 보인다. 에뮬레이터는 API 37이라 이번 단계와 무관하다. 폴백 PNG는 A9(패키징) 전에 다시 본다 (worklog 열린 일).

### 4. 설치·실행

```bash
cd ~/ws/src/Tabiji/client
scripts/linux/install-android.sh
```

- **첫 Android 빌드는 오래 걸린다.** Android용 Compose 의존성과 build-tools를 받기 때문이다
- 설치가 끝나면 에뮬레이터 앱 서랍(홈에서 위로 쓸기)에서 **Tabiji**를 찾아 누른다. 바로 띄우려면:

  ```bash
  adb shell am start -n com.yamada.studio.tabiji/.MainActivity
  ```

- 테마 아이콘 확인: 홈 화면 길게 누르기 ▸ `Wallpaper & style`(배경화면 및 스타일) ▸ `Themed icons` 켜기. 단, 테마 아이콘은 **홈 화면에 둔 아이콘**에만 적용된다. 앱 서랍에서 끌어 홈에 놓고 확인한다

### 5. "같은 `App()`" 확인

`App.kt`의 `"Tabiji"`를 잠깐 다른 글자로 바꾼다. 그리고 `run-desktop.sh`와 `install-android.sh`를 각각 실행해 **두 플랫폼이 함께 바뀌는 것**을 본다. 확인했으면 원래대로 되돌린다.

`App.kt`는 `shared/commonMain`에 한 벌만 있다. CMake로 치면 공용 라이브러리 소스 하나를 실행 파일 두 개가 링크하는 구조다. A1 가이드에서 말한 분리가 바로 이것이다.

### 6. 빌드 확인

```bash
scripts/linux/build.sh
```

## 흔한 오류와 원인 읽기

| 증상 | 원인일 가능성 |
| --- | --- |
| `Unresolved reference: setContent` | import 누락이거나 다른 패키지의 `setContent`를 고름. `androidx.activity.compose.setContent`여야 한다 |
| `'onCreate' hides member of supertype ... and needs 'override' modifier` | `override` 누락 |
| `Manifest merger failed ... android:exported needs to be explicitly specified` | `<activity>`에 `android:exported` 없음 |
| 설치는 됐는데 앱 서랍에 없음 | `intent-filter`의 MAIN/LAUNCHER 오타. 문자열이 정확히 일치해야 한다 |
| 실행 즉시 종료, logcat에 `ClassNotFoundException: ...MainActivity` | `MainActivity.kt`의 패키지가 `com.yamada.studio.tabiji`가 아니거나 `android:name` 오타 |
| `resource drawable/ic_launcher_tabi_foreground ... not found` | 파일 이름·폴더 오타. 리소스 이름에 대문자·하이픈 금지 |
| `<g>`·`fill`·`d` 관련 XML 오류 | SVG 태그·속성이 남아 있음. 위 대응표대로 `android:` 속성으로 |
| 旅가 너무 크거나 잘림 | `<group>` 변환 누락. 100 유닛 글리프가 원래 크기로 그려진 것 |
| `install-android.sh`: "연결된 기기/에뮬레이터가 없어요" | 에뮬레이터 미부팅 또는 `offline`. `adb devices`로 확인 |
| 흰 화면만 뜸 | `setContent` 안에서 `App()`을 부르지 않음 |

크래시 로그는 아래 명령으로 본다.

```bash
adb logcat -s AndroidRuntime:E
```

## 완료 조건 (모두 눈으로)

- [ ] `adb devices`에 에뮬레이터가 `device`로 보임
- [ ] 앱 서랍에 흰 바탕 旅 아이콘(원형 마스크), 누르면 Desktop과 같은 하늘색 + "Tabiji" 화면
- [ ] 테마 아이콘을 켜면 홈 화면의 旅가 시스템 색으로 바뀜
- [ ] `App()` 문구를 바꾸면 Desktop·Android 둘 다 바뀜 (확인 후 되돌림)
- [ ] `build.sh` 초록, PR에서 `client-ci` 초록

## 끝나면 붙여넣어 줄 것

1. `MainActivity.kt`, `AndroidManifest.xml` 내용
2. 리소스 XML 3개 (`pathData`는 앞뒤 몇 글자만 남기고 `…`로 줄여도 된다)
3. `install-android.sh` 마지막 20줄
4. 에뮬레이터 스크린샷: 앱 서랍(아이콘), 앱 화면, 테마 아이콘
5. `build.sh` 마지막 10줄

→ 진단을 통과하면 Claude가 커밋(`feat(client): add android entry point and launcher icon`, `Refs: A2`)과 push를 하고 PR을 연다. CI를 확인한 뒤 사용자가 squash merge한다. 그다음 A3(인트로 사분면 + 잉크 경계)로 간다.
