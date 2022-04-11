package network.hoz.grpcer.core.client

import io.grpc.stub.AbstractStub
import network.hoz.grpcer.core.channel.GrpcChannel
import network.hoz.grpcer.core.config.GrpcerConfig
import java.util.concurrent.atomic.AtomicReference

abstract class AbstractGrpcClient<S : AbstractStub<S>>
protected constructor(config: GrpcerConfig) {
    protected val stubReference = AtomicReference<S>()

    protected lateinit var channel: GrpcChannel

    init {
        switchChannel(GrpcChannel(config))
    }

    val stub: S
        get() {
            if (stubReference.get() == null) {
                buildStub()
            }
            return stubReference.get()
        }

    fun switchChannel(channel: GrpcChannel) {
        this.channel = channel
        buildStub()
        channel.atRenew("channelSwitch") { buildStub() }
    }

    protected abstract fun buildStub()
}