package network.hoz.grpcer.core.config

enum class ClientMode {
    /**
     * Uses first available connection as main, any other is backup.
     */
    NORMAL,

    /**
     * Each call uses different connection.
     * If the connection fails, other is used.
     */
    ROUND_ROBIN
}