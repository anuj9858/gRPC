package com.grpc.server;

import com.proto.greet.FileStreamServiceGrpc;
import com.proto.greet.PushFileRequest;
import com.proto.greet.PushFileResponse;
import io.grpc.stub.StreamObserver;

public class FileStreamServiceImpl extends FileStreamServiceGrpc.FileStreamServiceImplBase {
    @Override
    public StreamObserver<PushFileRequest> pushFile(StreamObserver<PushFileResponse> responseObserver) {
        StreamObserver<PushFileRequest> requestObserver = new StreamObserver<PushFileRequest>() {
            int count = 0;

            @Override
            public void onNext(PushFileRequest pushFileRequest) {
                count++;
                System.out.println("Recieved chunk number " + count + " value" + pushFileRequest.getDataChunks());
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onCompleted() {
                responseObserver.onNext(
                        PushFileResponse.newBuilder()
                                .setResult("Trasfer Completed")
                                .build()
                );
                responseObserver.onCompleted();
            }
        };
        return requestObserver;
    }

}

