package com.wordpress.ilyaps;

import java.nio.ByteBuffer;

public class Attachment {
    private ByteBuffer byteBuffer;

    public Attachment(ByteBuffer byteBuffer) {
        this.byteBuffer = byteBuffer;
    }

    public ByteBuffer getByteBuffer() {
        return byteBuffer;
    }

}