#!/usr/bin/env bash
# 디버그 빌드 + 공용 테스트. CI(client-ci)와 같은 태스크를 돈다.
# 사용: scripts/linux/build.sh [--clean]
#   --clean  먼저 build/ 산출물을 지우고 처음부터 빌드
set -euo pipefail

cd "$(dirname "$0")/../.."   # client/

tasks=()
for arg in "$@"; do
  case "$arg" in
    --clean) tasks+=(clean) ;;
    -h|--help) sed -n '2,4p' "$0"; exit 0 ;;
    *) echo "알 수 없는 옵션: $arg" >&2; exit 2 ;;
  esac
done

if [[ ! -x ./gradlew ]]; then
  echo "gradlew가 없어요. A0 가이드의 '래퍼 생성'을 먼저 하세요." >&2
  exit 1
fi

tasks+=(
  :androidApp:assembleDebug   # Android 디버그 APK
  :desktopApp:compileKotlin   # Desktop 컴파일
  :shared:jvmTest             # commonTest (+ jvmTest)를 JVM에서 실행
)

./gradlew "${tasks[@]}"

echo
echo "APK:          androidApp/build/outputs/apk/debug/"
echo "테스트 리포트: shared/build/reports/tests/jvmTest/index.html"
