package com.yamada.studio.tabiji

/**
 * Linux에서는 Skia를 CPU(소프트웨어) 렌더링으로 띄운다.
 *
 * 하이브리드 GPU 노트북(예: 내장 AMD + 외장 NVIDIA)처럼 모니터마다 연결된 GPU가 다르면,
 * 창을 다른 모니터로 옮길 때 OpenGL 그리기 표면의 크기가 갱신되지 않아 창 아래쪽이 빈 띠로 남는다.
 * 레이아웃은 맞게 계산되지만 그 결과를 그리는 판이 예전 크기에 머문다.
 * 소프트웨어 렌더링에서는 이 문제가 없다는 것을 확인했다 (2026-10-15, `SKIKO_RENDER_API=SOFTWARE`).
 *
 * - Skia가 첫 창을 만들기 전에 불러야 한다
 * - 이미 `-Dskiko.renderApi=…` 또는 `SKIKO_RENDER_API`로 렌더러를 정했다면 그 값을 존중한다.
 *   GPU 렌더링으로 되돌리려면 `SKIKO_RENDER_API=OPENGL`로 실행한다
 */
internal fun useSoftwareRenderingOnLinux() {
    if (!System.getProperty("os.name").orEmpty().startsWith("Linux")) return
    if (System.getProperty("skiko.renderApi") != null || System.getenv("SKIKO_RENDER_API") != null) return
    System.setProperty("skiko.renderApi", "SOFTWARE")
}
