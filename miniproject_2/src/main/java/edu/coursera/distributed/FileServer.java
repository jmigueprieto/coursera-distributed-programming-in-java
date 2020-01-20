package edu.coursera.distributed;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * A basic and very limited implementation of a file server that responds to GET
 * requests from HTTP clients.
 */
public final class FileServer {
    /**
     * Main entrypoint for the basic file server.
     *
     * @param socket Provided socket to accept connections on.
     * @param fs A proxy filesystem to serve files from. See the PCDPFilesystem
     *           class for more detailed documentation of its usage.
     * @throws IOException If an I/O error is detected on the server. This
     *                     should be a fatal error, your file server
     *                     implementation is not expected to ever throw
     *                     IOExceptions during normal operation.
     */
    public void run(final ServerSocket socket, final PCDPFilesystem fs)
            throws IOException {
        /*
         * Enter a spin loop for handling client requests to the provided
         * ServerSocket object.
         */

        while (true) {
            Socket s = socket.accept();
            BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
            String[] request = br.readLine().split("\\s");

            if (!request[0].equals("GET")) {
                socket.close();
                continue;
            }

            PCDPPath path = new PCDPPath(request[1]);
            String fileContent = fs.readFile(path);
            Writer writer = new OutputStreamWriter(s.getOutputStream());
            String response;
            if (fileContent != null) {
                response = "HTTP/1.0 200 OK\r\n" +
                        "Server: FileServer\r\n" +
                        "\r\n" +
                        fileContent +
                        "\r\n";
            } else {
                response = "HTTP/1.0 404 Not Found\r\n" +
                        "Server: FileServer\r\n" +
                        "\r\n";
            }
            writer.write(response);
            writer.close();
        }
    }
}
