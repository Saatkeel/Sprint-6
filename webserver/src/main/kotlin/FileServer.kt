import ru.sber.filesystem.VFilesystem
import ru.sber.filesystem.VPath
import java.io.IOException
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket

/**
 * A basic and very limited implementation of a file server that responds to GET
 * requests from HTTP clients.
 */
class FileServer {

    /**
     * Main entrypoint for the basic file server.
     *
     * @param socket Provided socket to accept connections on.
     * @param fs     A proxy filesystem to serve files from. See the VFilesystem
     *               class for more detailed documentation of its usage.
     * @throws IOException If an I/O error is detected on the server. This
     *                     should be a fatal error, your file server
     *                     implementation is not expected to ever throw
     *                     IOExceptions during normal operation.
     */
    @Throws(IOException::class)
    fun run(socket: ServerSocket, fs: VFilesystem) {

        /**
         * Enter a spin loop for handling client requests to the provided
         * ServerSocket object.
         */
        while (true) {
            socket.use {
                while (true) {
                    val clientSocket = it.accept()
                    handle(clientSocket, fs)
                }
            }

        }
    }

    private fun handle(socket: Socket, fs: VFilesystem) {
        socket.use { s ->
            // читаем от клиента сообщение
            val reader = s.getInputStream().bufferedReader()
            val clientRequest = reader.readLine()

            println("receive from ${socket.remoteSocketAddress}  > clientRequest $clientRequest")
            val method = clientRequest.substringBefore(' ') // хотел разные случаи для разных методов,

            when (method) {
                "GET" -> {
                    val path = clientRequest.drop(4).dropLast(9)
                    val serverResponse = getMethod(path,fs)
                    sendAnswer(s,serverResponse)
                }
            }
        }
    }

    private fun getMethod(path: String, fs: VFilesystem): String {
        val vPath = VPath(path)

        return if (fs.readFile(vPath) != null)
            HTTPSAnswers.OK_200.answer +
                    fs.readFile(vPath)
        else
            HTTPSAnswers.NOT_FOUND_404.answer

    }

    private fun sendAnswer(socket: Socket, serverResponse : String){

        val writer = PrintWriter(socket.getOutputStream())
        println("send to ${socket.remoteSocketAddress} > $serverResponse")
        writer.println(serverResponse)
        writer.flush()

    }

    enum class HTTPSAnswers(val answer: String) {
        NOT_FOUND_404(
            "HTTP/1.0 404 Not Found\r\n" +
                    "Server: FileServer\r\n" +
                    "\r\n"
        ),
        OK_200(
            "HTTP/1.0 200 OK\r\n" +
                    "Server: FileServer\r\n" +
                    "\r\n"
        )
    }
}


/*
* Done 2
* packet. In particular, we are interested in confirming this
* message is a GET and parsing out the path to the file we are
* GETing. Recall that for GET HTTP packets, the first line of the
* received packet will look something like:
*
*     GET /path/to/file HTTP/1.1
*/


/*
 * Done 3
 * HTTP reply and write it to Socket.getOutputStream(). If the file
 * exists, the HTTP reply should be formatted as follows:
 *
 *   HTTP/1.0 200 OK\r\n
 *   Server: FileServer\r\n
 *   \r\n
 *   FILE CONTENTS HERE\r\n
 *
 * If the specified file does not exist, you should return a reply
 * with an error code 404 Not Found. This reply should be formatted
 * as:
 *
 *   HTTP/1.0 404 Not Found\r\n
 *   Server: FileServer\r\n
 *   \r\n
 *
 * Don't forget to close the output stream.
 */