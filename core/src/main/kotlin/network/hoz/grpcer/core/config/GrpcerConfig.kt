package network.hoz.grpcer.core.config

data class GrpcerConfig(
    var connections: MutableList<GrpcConnection> = mutableListOf(),
    var mode: ClientMode = ClientMode.NORMAL
)