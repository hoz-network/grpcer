package network.hoz.grpcer.core.channel

import com.iamceph.resulter.core.DataResultable
import com.iamceph.resulter.kotlin.dataResultable
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContext
import mu.KLogger
import network.hoz.grpcer.core.config.GrpcConnection
import network.hoz.grpcer.core.config.GrpcerConfig
import java.io.FileInputStream
import java.security.KeyStore
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference
import javax.net.ssl.TrustManagerFactory

class GrpcChannel(
    private val log: KLogger,
    private val connection: GrpcConnection
) {

    private val renewActions = mutableMapOf<String, Runnable>()

    private val activeConnection = AtomicReference<GrpcConnection>()
    private val activeChannel = AtomicReference<ManagedChannel>()
    private val backupChannel = AtomicReference<ManagedChannel>()

    fun atRenew(name: String, block: Runnable) {
        renewActions[name] = block
    }

    fun getChannel(): ManagedChannel {

    }

    private fun callRenewCallbacks() {
        renewActions.forEach { (name, action) ->
            log.debug { "Calling renew action[$name]" }
            action.run()
        }
    }

    private fun setActiveChannel(connection: GrpcConnection) {
        val channelResult = prepareChannel(connection)
        if (channelResult.isFail) {
            log.warn(
                "[${connection.formatHost()}] Error replacing active gRPC channel - ${channelResult.message}",
                channelResult.error()
            )
            return
        }

        val active = activeChannel.get()
        active?.let {
            if (!it.isTerminated && !it.isShutdown) {
                log.trace { "Shutting down old channel.." }
                it.shutdownNow()
            }
        }

        activeChannel.set(channelResult.data())
        activeConnection.set(connection)

        log.trace { "Successfully changed active connection to ${connection.formatHost()}" }
        callRenewCallbacks()
    }

    private fun prepareChannel(connection: GrpcConnection): DataResultable<ManagedChannel> {
        return if (connection.security.enabled) {
            buildSecureChannel(connection)
        } else {
            buildChannel(connection)
        }
    }

    private fun buildChannel(connection: GrpcConnection): DataResultable<ManagedChannel> {
        return dataResultable {
            ManagedChannelBuilder
                .forAddress(connection.host, connection.port)
                .usePlaintext()
                .idleTimeout(connection.idleTimeout.seconds, TimeUnit.SECONDS)
                .build()
        }
    }

    private fun buildSecureChannel(connection: GrpcConnection): DataResultable<ManagedChannel> {
        return buildSslContext(connection)
            .map { sslContext ->
                val builder = NettyChannelBuilder
                    .forAddress(connection.host, connection.port)
                    .useTransportSecurity()
                    .sslContext(sslContext)
                    .idleTimeout(connection.idleTimeout.seconds, TimeUnit.SECONDS)
                    .nameResolverFactory()

                connection.unsafe.overrideAuthority?.let { builder.overrideAuthority(it) }

                return@map builder.build()
            }
    }

    private fun buildSslContext(connection: GrpcConnection): DataResultable<SslContext> {
        val keystore = connection.security.ssl.keystoreFile
            ?: return DataResultable.fail("KeyStoreFile is not defined!")
        val keystorePassword = connection.security.ssl.keystorePassword
            ?: return DataResultable.fail("KeyStorePassword is not defined!")

        return dataResultable {
            FileInputStream(keystore)
                .use {
                    val keyStore = KeyStore.getInstance("JKS")
                    keyStore.load(it, keystorePassword.toCharArray())

                    val trustManagerFactory =
                        TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
                    trustManagerFactory.init(keyStore)

                    GrpcSslContexts.forClient()
                        .trustManager(trustManagerFactory)
                        .build()
                }

        }
    }
}