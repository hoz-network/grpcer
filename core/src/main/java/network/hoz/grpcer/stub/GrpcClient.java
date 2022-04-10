package network.hoz.grpcer.stub;

import io.grpc.Channel;
import io.grpc.stub.AbstractStub;
import network.hoz.grpcer.config.GrpcerConfig;

import java.util.concurrent.atomic.AtomicReference;

public class GrpcClient<S extends AbstractStub<S>> {
    private final Class<S> stubClass;
    private final GrpcerConfig config;
    private final AtomicReference<S> stub = new AtomicReference<>();
    private GrpcChannel channel;

    public GrpcClient(Class<S> stubClass, GrpcerConfig config) {
        this.stubClass = stubClass;
        this.config = config;
        this.channel = new GrpcChannel(config);

        channel.atRenew(this::buildStub);
    }

    public void switchChannel(GrpcChannel channel) {
        this.channel = channel;
        buildStub();
    }

    public S getStub() {
        if (stub.get() == null) {
            buildStub();
        }

        return stub.get();
    }

    protected void buildStub() {
        final var stubChannel = channel.getChannel();

        try {
            final var newStubMethod = stubClass.getDeclaredMethod("newStub", Channel.class);
            final var newStub = (AbstractStub<?>) newStubMethod.invoke(stubClass, stubChannel);
            if (newStub.getClass().isInstance(stubClass)) {
                stub.set((S) newStub);
            }
        } catch (Exception e) {
            throw new UnsupportedOperationException("Cannot invoke [newStub] via reflection!", e);
        }
    }
}
