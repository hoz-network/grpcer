package network.hoz.grpcer.config;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.File;
import java.time.Duration;

@Data
public class GrpcerConfig {
    private Connection connection = new Connection();
    private ClientMode mode = ClientMode.NORMAL;

    @Data
    public static class Connection {
        private boolean enabled = true;
        /**
         * What port the gRPC server uses
         */
        private Integer port = 6969;
        /**
         * Host of the gRPC server
         */
        private String host = "localhost";
        /**
         * How much duration should we wait before shutting down the clients.
         */
        private Duration shutdownWait = Duration.ofSeconds(3);
        /**
         * After what duration should be this connection checked.
         */
        private Duration checkInterval = Duration.ofSeconds(15);
        /**
         * After what duration should the channel close connection in idle - doing nothing.
         */
        private Duration idleTimeout = Duration.ofMinutes(30);
        /**
         * Security of the client
         */
        private Security security = new Security();
        /**
         * Use at your own risk :)
         */
        private Unsafe unsafe = new Unsafe();
    }

    /**
     * Holder for the security settings
     */
    @Data
    public static class Security {
        private boolean enabled = false;
        private BasicSecurity basic = new BasicSecurity();
        private SslSecurity ssl = new SslSecurity();
    }

    /**
     * Basic authentication uses token as the security.
     * Not really secure, but at least something :)
     */
    @Data
    public static class BasicSecurity {
        private String token;
    }

    /**
     * SSL configuration
     */
    @Data
    public static class SslSecurity {
        private boolean trustAll;

        private File keystoreFile;
        private String keystorePassword;
    }

    @Data
    public static class Unsafe {
        private String overrideAuthority;
    }
}
