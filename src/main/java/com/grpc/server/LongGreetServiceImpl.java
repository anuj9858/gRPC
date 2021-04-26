package com.grpc.server;

import com.proto.greet.LongGreetRequest;
import com.proto.greet.LongGreetResponse;
import com.proto.greet.LongGreetServiceGrpc;
import io.grpc.stub.StreamObserver;

public class LongGreetServiceImpl extends LongGreetServiceGrpc.LongGreetServiceImplBase {

    @Override
    public StreamObserver<LongGreetRequest> longGreet(StreamObserver<LongGreetResponse> responseObserver) {
        StreamObserver<LongGreetRequest> requestObserver = new StreamObserver<LongGreetRequest>() {
            String result = "";

            @Override
            public void onNext(LongGreetRequest longGreetRequest) {
                result += "Hello "+ longGreetRequest.getGreeting().getFirstName()+ "! ";
                //everytime client sends a message
            }

            @Override
            public void onError(Throwable throwable) {
                //client sends an error
            }

            @Override
            public void onCompleted() {
                //client is done
                responseObserver.onNext(
                        LongGreetResponse.newBuilder()
                        .setResult(result)
                        .build()
                );
                responseObserver.onCompleted();
            }
        };
        return requestObserver;
    }
}
