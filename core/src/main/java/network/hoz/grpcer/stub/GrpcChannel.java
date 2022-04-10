package network.hoz.grpcer.stub;

import io.grpc.ManagedChannel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import network.hoz.grpcer.config.GrpcerConfig;

import java.util.concurrent.atomic.AtomicReference;

@RequiredArgsConstructor
public class GrpcChannel {
    private final GrpcerConfig config;

    private AtomicReference<GrpcerConfig.Connection> currentConnection = new AtomicReference<>();
    private AtomicReference<ManagedChannel> activeChannel = new AtomicReference<>();
    private AtomicReference<ManagedChannel> backupChannel = new AtomicReference<>();

    public void atRenew(Runnable runnable) {

    }

    public ManagedChannel getChannel() {
        //TODO
        return activeChannel.get();
    }
}
