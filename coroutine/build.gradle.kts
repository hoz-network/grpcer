plugins {
    kotlin("jvm")
}

dependencies {
    api(project(":core"))

    api("com.google.protobuf", "protobuf-kotlin", "3.20.0")
    api("io.grpc", "grpc-kotlin-stub", "1.2.1")
}