package network.hoz.grpcer.config;

public enum ClientMode {
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
