#!/usr/bin/env bash
# 현재 OS용 Desktop 설치 패키지 생성. Release CI(client-release)의 desktop job과 같은 태스크다.
# 사용: scripts/linux/package-desktop.sh
# Linux에서는 .deb가 나온다. jpackage가 deb를 만들려면 fakeroot와 dpkg-deb가 필요하다
#   (없으면: sudo apt install fakeroot)
set -euo pipefail

cd "$(dirname "$0")/../.."   # client/

if [[ ! -x ./gradlew ]]; then
  echo "gradlew가 없어요. A0 가이드의 '래퍼 생성'을 먼저 하세요." >&2
  exit 1
fi

# release 패키지는 ProGuard(코드 축소)를 거친다
./gradlew :desktopApp:packageReleaseDistributionForCurrentOS

echo
echo "산출물: desktopApp/build/compose/binaries/main-release/"
find desktopApp/build/compose/binaries/main-release -maxdepth 2 -type f \( -name '*.deb' -o -name '*.msi' -o -name '*.dmg' \) 2>/dev/null || true
