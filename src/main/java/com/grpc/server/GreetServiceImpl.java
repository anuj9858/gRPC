package com.grpc.server;

import com.proto.greet.GreetRequest;
import com.proto.greet.GreetResponse;
import com.proto.greet.GreetServiceGrpc;
import com.proto.greet.Greeting;
import io.grpc.stub.StreamObserver;

public class GreetServiceImpl extends GreetServiceGrpc.GreetServiceImplBase {

    @Override
    public void greet(GreetRequest request, StreamObserver<GreetResponse> responseObserver) {
        //fetching required fields
        Greeting greeting = request.getGreeting();
        String firstName = greeting.getFirstName();
        String result = "Hello"+firstName;

        //creating response
        GreetResponse response = GreetResponse.newBuilder()
                .setResult(result)
                .build();
        //send the response
        responseObserver.onNext(response);

        //complete rpc call
        responseObserver.onCompleted();

    }
}
