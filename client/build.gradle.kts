// 루트 프로젝트(client/)는 코드가 없다. 플러그인을 여기서 "apply false"로 한 번만 선언해
// 하위 모듈들이 같은 클래스로더의 같은 버전 플러그인을 쓰게 한다 (버전은 gradle/libs.versions.toml).
plugins {
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlinJvm) apply false
    alias(libs.plugins.androidMultiplatformLibrary) apply false
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
}
