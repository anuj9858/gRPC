package com.grpc.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class GreetingServer {
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Hello grpc");
        Server server = ServerBuilder.forPort(50051)
                .addService(new FileStreamServiceImpl())
                .build();
        server.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Receiving Shutdown Request");
            server.shutdown();
            System.out.println("Successfully stopped server");
        }
        ));

        server.awaitTermination();
    }
}
