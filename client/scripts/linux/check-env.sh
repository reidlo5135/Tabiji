#!/usr/bin/env bash
# 클라이언트 개발 환경 확인. 아무것도 설치·수정하지 않고 존재 여부만 출력한다.
# 사용: scripts/linux/check-env.sh   (client/에서)
# 종료 코드: 모두 OK면 0, 하나라도 빠지면 1
set -uo pipefail

missing=0
ok()   { printf '  [ OK ] %-14s %s\n' "$1" "$2"; }
fail() { printf '  [FAIL] %-14s %s\n' "$1" "$2"; missing=1; }

echo "Tabiji client — 환경 확인"

# 1) JDK 17 이상 (Gradle·AGP·jpackage 실행용)
if command -v java >/dev/null 2>&1; then
  java_ver_line="$(java -version 2>&1 | head -n 1)"
  java_major="$(printf '%s' "$java_ver_line" | sed -E 's/.*version "([0-9]+).*/\1/')"
  if [[ "$java_major" =~ ^[0-9]+$ ]] && (( java_major >= 17 )); then
    ok "JDK" "$java_ver_line"
  else
    fail "JDK" "17 이상 필요: $java_ver_line"
  fi
else
  fail "JDK" "java 명령 없음"
fi

# 2) ANDROID_HOME (Android SDK 위치)
if [[ -n "${ANDROID_HOME:-}" && -d "${ANDROID_HOME}" ]]; then
  ok "ANDROID_HOME" "$ANDROID_HOME"
else
  fail "ANDROID_HOME" "설정 안 됨 또는 디렉터리 없음 (${ANDROID_HOME:-unset})"
fi

# 3) adb (기기 설치·로그)
if command -v adb >/dev/null 2>&1; then
  ok "adb" "$(command -v adb)"
elif [[ -n "${ANDROID_HOME:-}" && -x "${ANDROID_HOME}/platform-tools/adb" ]]; then
  fail "adb" "SDK에는 있지만 PATH에 없음: \$ANDROID_HOME/platform-tools를 PATH에 추가"
else
  fail "adb" "없음 (SDK platform-tools 설치 필요)"
fi

# 4) gh (GitHub CLI, PR·CI 로그)
if command -v gh >/dev/null 2>&1; then
  ok "gh" "$(gh --version | head -n 1)"
else
  fail "gh" "없음"
fi

echo
if (( missing == 0 )); then
  echo "모두 OK"
else
  echo "빠진 항목이 있어요. client/docs/guides/A0-environment.md를 참고하세요."
fi
exit "$missing"
