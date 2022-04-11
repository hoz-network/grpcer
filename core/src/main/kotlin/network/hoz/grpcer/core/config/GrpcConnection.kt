package network.hoz.grpcer.core.config

import java.io.File
import java.time.Duration

data class GrpcConnection(
    var enabled: Boolean = true,
    /**
     * What port the gRPC server uses
     */
    var port: Int = 6969,
    /**
     * Host of the gRPC server
     */
    var host: String = "localhost",
    /**
     * How much duration should we wait before shutting down the clients.
     */
    var shutdownWait: Duration = Duration.ofSeconds(3),
    /**
     * After what duration should be this connection checked.
     */
    var checkIntervar: Duration = Duration.ofSeconds(15),
    /**
     * After what duration should the channel close connection in idle - doing nothing.
     */
    var idleTimeout: Duration = Duration.ofMinutes(30),
    /**
     * Security of the client
     */
    var security: Security = Security(),
    /**
     * Use at your own risk :)
     */
    var unsafe: Unsafe = Unsafe(),
) {
    /**
     * Holder for the security settings
     */
    data class Security(
        var enabled: Boolean = false,
        var basic: BasicSecurity = BasicSecurity(),
        var ssl: SslSecurity = SslSecurity()
    )

    /**
     * Basic authentication uses token as the security.
     * Not really secure, but at least something :)
     */
    data class BasicSecurity(
        var token: String? = null
    )

    /**
     * SSL configuration
     */
    data class SslSecurity(
        var trustAll: Boolean = false,
        var keystoreFile: File? = null,
        var keystorePassword: String? = null
    )

    data class Unsafe(
        var overrideAuthority: String? = null
    )

    fun formatHost(): String = "$host:$port"
}
