package network.hoz.grpcer.core.client

import io.grpc.Channel
import io.grpc.stub.AbstractStub
import lombok.extern.slf4j.Slf4j
import network.hoz.grpcer.core.config.GrpcerConfig

@Slf4j
open class AsyncGrpcClient<S : AbstractStub<S>?>(private val stubClass: Class<S>, config: GrpcerConfig?) :
    AbstractGrpcClient<S>(config) {
    override fun buildStub() {
        val stubChannel = channel.channel
        try {
            val newStubMethod = stubClass.getDeclaredMethod("newStub", Channel::class.java)
            val newStub = newStubMethod.invoke(stubClass, stubChannel)
            if (newStub.javaClass.isInstance(stubClass)) {
                stubReference.set(newStub as S)
            }
            AsyncGrpcClient.log.warn("Cannot create new Stub for {}", stubClass.simpleName)
        } catch (e: Exception) {
            throw UnsupportedOperationException("Creating of the stub failed!", e)
        }
    }
}