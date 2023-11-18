package de.torsm.socks

import org.junit.jupiter.api.extension.ExtendWith
import java.net.InetSocketAddress
import java.net.SocketException
import kotlin.test.Test
import kotlin.test.assertFailsWith

@ExtendWith(MockServers::class)
class SOCKS5Tests {

    @Test
    fun `SOCKS4 Not allowed`() {
        createClientSocket(4).use { clientSocket ->
            assertFailsWith<SocketException> {
                clientSocket.connect(mockServerJava)
            }
        }
    }

    @Test
    fun `IPv4 Ping Pong`() {
        createClientSocket(5).use { clientSocket ->
            clientSocket.connect(mockServerJava)
            clientSocket.ping()
            clientSocket.assertPong()
        }
    }

    @Test
    fun `IPv6 Ping Pong`() {
        val mockServer = InetSocketAddress("::1", mockServer.port)
        createClientSocket(5).use { clientSocket ->
            clientSocket.connect(mockServerJava)
            clientSocket.ping()
            clientSocket.assertPong()
        }
    }

    @Test
    fun `Hostname Ping Pong`() {
        val mockServer = InetSocketAddress.createUnresolved("localhost", mockServer.port)
        createClientSocket(5).use { clientSocket ->
            clientSocket.connect(mockServer)
            clientSocket.ping()
            clientSocket.assertPong()
        }
    }

    @Test
    fun `Unreachable Host`() {
        val unreachableHost = mockServer.withPort(8081).toJavaInetAddress()
        createClientSocket(5).use { clientSocket ->
            assertFailsWith<SocketException> {
                clientSocket.connect(unreachableHost)
            }
        }
    }
}
