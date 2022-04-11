package network.hoz.grpcer.kotlin

import io.grpc.CallOptions
import io.grpc.Channel
import io.grpc.kotlin.AbstractCoroutineStub
import network.hoz.grpcer.core.client.AbstractGrpcClient
import network.hoz.grpcer.core.config.GrpcerConfig

open class CoroutineGrpcClient<S : AbstractCoroutineStub<S>>(
    private val stubClass: Class<S>,
    config: GrpcerConfig
) : AbstractGrpcClient<S>(config) {

    override fun buildStub() {
        val stubChannel = channel.channel

        try {
            val newStubConstructor = stubClass.getDeclaredConstructor(Channel::class.java, CallOptions::class.java)
            newStubConstructor.isAccessible = true
            val newStub = newStubConstructor.newInstance(stubChannel, CallOptions.DEFAULT)

            if (newStub.javaClass.isInstance(stubClass)) {
                stubReference.set(newStub as S)
            }

        } catch (e: Exception) {
            throw UnsupportedOperationException("Creating of the stub failed!", e)
        }
    }
}