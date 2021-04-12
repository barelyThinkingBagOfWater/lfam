package ch.xavier.movies.repositories;

import ch.xavier.common.movies.Movie;
import com.google.gson.Gson;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import org.xerial.snappy.SnappyInputStream;
import org.xerial.snappy.SnappyOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class SnappyRedisCodec implements RedisCodec<String, Movie> {

    private final Gson gson = new Gson();
    private final StringCodec stringCodec = new StringCodec();

    @Override
    public String decodeKey(ByteBuffer bytes) {
        return (String) stringCodec.decodeKey(bytes);
    }

    @Override
    public Movie decodeValue(ByteBuffer bytes) {
        try {
            return gson.fromJson(stringCodec.decodeValue(decompress(bytes)), Movie.class);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public ByteBuffer encodeKey(String key) {
        return stringCodec.encodeKey(key);
    }


    @Override
    public ByteBuffer encodeValue(Movie movie) {
        try {
            return compress(stringCodec.encodeValue(gson.toJson(movie)));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private ByteBuffer compress(ByteBuffer source) throws IOException {
        if (source.remaining() == 0) {
            return source;
        }

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        SnappyOutputStream compressor = new SnappyOutputStream(buffer);
        try {
            getByteBufInputStream(source).transferTo(compressor);
        } finally {
            compressor.close();
        }
        return ByteBuffer.wrap(buffer.toByteArray());
    }

    private ByteBuffer decompress(ByteBuffer source) throws IOException {
        if (source.remaining() == 0) {
            return source;
        }

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        SnappyInputStream decompressor = new SnappyInputStream(getByteBufInputStream(source));
        try {
            decompressor.transferTo(buffer);
        } finally {
            decompressor.close();
        }
        return ByteBuffer.wrap(buffer.toByteArray());
    }

    private ByteBufInputStream getByteBufInputStream(ByteBuffer source) {
        return new ByteBufInputStream(Unpooled.wrappedBuffer(source));
    }
}