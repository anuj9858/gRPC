package com.grpc.server;

import com.proto.greet.ManyGreetRequest;
import com.proto.greet.ManyGreetResponse;
import com.proto.greet.ManyGreetServiceGrpc;
import io.grpc.stub.StreamObserver;

public class ManyGreetServiceimpl extends ManyGreetServiceGrpc.ManyGreetServiceImplBase {
    @Override
    public void manyGreet(ManyGreetRequest request, StreamObserver<ManyGreetResponse> responseObserver) {
        String firstName = request.getGreeting().getFirstName();
        try {
            for (int i = 0; i < 10; i++) {
                String result = "hello " + firstName + " request no " + i;
                ManyGreetResponse response = ManyGreetResponse.newBuilder()
                        .setResult(result)
                        .build();
                responseObserver.onNext(response);
                Thread.sleep(1000L);
            }
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
        finally {
            responseObserver.onCompleted();
        }
    }
}
