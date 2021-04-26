package com.grpc.client;

import com.google.protobuf.ByteString;
import com.proto.greet.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GreetingClient {

    public static void main(String[] args) {
        System.out.println("Hello i am gRPC client");
        GreetingClient main = new GreetingClient();
        main.run();

    }

    private void run(){
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost",50051)
                .usePlaintext()
                .build();
        //doUnaryCall(channel);
        //doServerStreamingCall(channel);
        //doClientStreamingCall(channel);
        doFileStream(channel,"C:/Users/Admin/Downloads/Documents/LearningSpark2.0.pdf");
        System.out.println("Shutting Down channel");
        channel.shutdown();
    }

    private void doUnaryCall(ManagedChannel channel){
        //created greet service client (blocking-synchronous)
        GreetServiceGrpc.GreetServiceBlockingStub greetClient = GreetServiceGrpc.newBlockingStub(channel);
        //created protocol buffer greeting message
        Greeting greeting = Greeting.newBuilder()
                .setFirstName("Anuj")
                .setLastName("Agrawal")
                .build();
        //created greet request
        GreetRequest greetRequest = GreetRequest.newBuilder()
                .setGreeting(greeting)
                .build();

        //called rpc and get back greet Response
        GreetResponse greetResponse = greetClient.greet(greetRequest);
        System.out.println(greetResponse.getResult());
    }

    private void doServerStreamingCall(ManagedChannel channel){
        System.out.println("Creating Stub");
        ManyGreetServiceGrpc.ManyGreetServiceBlockingStub manyGreetClient = ManyGreetServiceGrpc.newBlockingStub(channel);

        ManyGreetRequest manyGreetRequest = ManyGreetRequest.newBuilder()
                .setGreeting(Greeting.newBuilder().setFirstName("Anuj"))
                .build();
        manyGreetClient.manyGreet(manyGreetRequest)
                .forEachRemaining(manyGreetResponse -> {
                    System.out.println(manyGreetResponse.getResult());
                });
    }

    private void doClientStreamingCall(ManagedChannel channel){
        System.out.println("Creating Stub");
        LongGreetServiceGrpc.LongGreetServiceStub asyncClient = LongGreetServiceGrpc.newStub(channel);
        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<LongGreetRequest> requestObserver = asyncClient.longGreet(new StreamObserver<LongGreetResponse>() {
            @Override
            public void onNext(LongGreetResponse longGreetResponse) {
                //we get a response from the server
                System.out.println("Received a response from server");
                System.out.println(longGreetResponse.getResult());
                //onNext will be called only once
            }

            @Override
            public void onError(Throwable throwable) {
                //we get an error from the server

            }

            @Override
            public void onCompleted() {
                //server is done sending us data
                //this method will be called right after OnNext
                System.out.println("Server has completed sending us something");
                latch.countDown();
            }
        });

        System.out.println("Sending message 1");
        requestObserver.onNext(LongGreetRequest.newBuilder()
                .setGreeting(Greeting.newBuilder()
                .setFirstName("Anuj"))
                .build()
        );
        System.out.println("Sending message 2");
        requestObserver.onNext(LongGreetRequest.newBuilder()
                .setGreeting(Greeting.newBuilder()
                        .setFirstName("Monali"))
                .build()
        );
        System.out.println("Sending message 3");
        requestObserver.onNext(LongGreetRequest.newBuilder()
                .setGreeting(Greeting.newBuilder()
                        .setFirstName("Dipika"))
                .build()
        );

        // we tell the server that client is done sending data
        requestObserver.onCompleted();
        try {
            latch.await(3L, TimeUnit.SECONDS);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void doFileStream(ManagedChannel channel, String path) {
        System.out.println("Creating Stub");
        FileStreamServiceGrpc.FileStreamServiceStub asyncClient = FileStreamServiceGrpc.newStub(channel);
        CountDownLatch latch = new CountDownLatch(1);
        StreamObserver<PushFileRequest> requestStreamObserver = asyncClient.pushFile(new StreamObserver<PushFileResponse>() {
            @Override
            public void onNext(PushFileResponse pushFileResponse) {
                System.out.println("Received response from server");
                System.out.println(pushFileResponse.getResult());

            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onCompleted() {
                System.out.println("Server has completed sending us something");
                latch.countDown();
            }
        });
        FileInputStream inputStream = null;
        System.out.println("Sending data from client");
        try {
            inputStream = new FileInputStream(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        byte[] buffer = new byte[1024];
        int n = 1;
        try{
            while(n>0) {
                n = inputStream.read(buffer);
                if(n<=0) {
                    requestStreamObserver.onCompleted();
                    latch.await(3L, TimeUnit.SECONDS);
                    return;
                }

                requestStreamObserver.onNext(PushFileRequest.newBuilder()
                        .setDataChunks(ByteString.copyFrom(buffer, 0, n))
                        .build());
            }
            // we tell the server that client is done sending data


        }
        catch (IOException | InterruptedException e1){
            e1.printStackTrace();
        }

    }
}
