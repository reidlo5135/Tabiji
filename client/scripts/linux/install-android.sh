#!/usr/bin/env bash
# 디버그 APK를 연결된 기기/에뮬레이터에 설치.
# 사용: scripts/linux/install-android.sh
# 기기 확인: adb devices  (여러 대면 ANDROID_SERIAL=<serial> 로 지정)
set -euo pipefail

cd "$(dirname "$0")/../.."   # client/

if [[ ! -x ./gradlew ]]; then
  echo "gradlew가 없어요. A0 가이드의 '래퍼 생성'을 먼저 하세요." >&2
  exit 1
fi

if command -v adb >/dev/null 2>&1; then
  if ! adb devices | sed '1d' | grep -q 'device$'; then
    echo "연결된 기기/에뮬레이터가 없어요. 'adb devices'로 확인하세요." >&2
    exit 1
  fi
fi

./gradlew :androidApp:installDebug
