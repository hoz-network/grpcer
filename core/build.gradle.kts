plugins {
    id("io.freefair.lombok") version "6.4.2"
}

dependencies {
    api("com.google.protobuf", "protobuf-java", "3.20.0")
    api("io.grpc", "grpc-stub", "1.45.1")
    api("io.grpc", "grpc-netty-shaded", "1.45.1")
    api("io.grpc", "grpc-services", "1.45.1")
    api("io.grpc", "grpc-protobuf", "1.45.1")
}