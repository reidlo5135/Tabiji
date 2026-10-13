#!/usr/bin/env bash
# Desktop(JVM) 앱 실행.
# 사용: scripts/linux/run-desktop.sh [--hot] [--log]
#   --hot  Compose Hot Reload로 실행 (:desktopApp:hotRun --auto). 저장하면 재시작 없이 UI가 바뀐다
#   --log  출력을 build/logs/run-desktop-<시각>.log 에도 저장 (진단 때 붙여넣기용)
set -euo pipefail

cd "$(dirname "$0")/../.."   # client/

log=false
task=(:desktopApp:run)
for arg in "$@"; do
  case "$arg" in
    --hot) task=(:desktopApp:hotRun --auto) ;;
    --log) log=true ;;
    -h|--help) sed -n '2,5p' "$0"; exit 0 ;;
    *) echo "알 수 없는 옵션: $arg" >&2; exit 2 ;;
  esac
done

if [[ ! -x ./gradlew ]]; then
  echo "gradlew가 없어요. A0 가이드의 '래퍼 생성'을 먼저 하세요." >&2
  exit 1
fi

if $log; then
  mkdir -p build/logs
  logfile="build/logs/run-desktop-$(date +%Y%m%d-%H%M%S).log"
  echo "로그: client/$logfile"
  ./gradlew "${task[@]}" 2>&1 | tee "$logfile"
else
  ./gradlew "${task[@]}"
fi
