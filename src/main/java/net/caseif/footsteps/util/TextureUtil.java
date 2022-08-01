package net.caseif.footsteps.util;

import org.lwjgl.BufferUtils;

import java.util.OptionalInt;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.*;

public class TextureUtil {
    public static OptionalInt loadTexture(String resName) {
        try (var is = TextureUtil.class.getClassLoader().getResourceAsStream(resName)) {
            var texBytes = is.readAllBytes();
            var texBuf = BufferUtils.createByteBuffer(texBytes.length);
            var texWidthBuf = BufferUtils.createIntBuffer(1);
            var texHeightBuf = BufferUtils.createIntBuffer(1);
            var texChannelsBuf = BufferUtils.createIntBuffer(1);

            texBuf.put(texBytes);
            texBuf.rewind();

            var loadedTex = stbi_load_from_memory(texBuf, texWidthBuf, texHeightBuf, texChannelsBuf, 4);

            var texWidth = texWidthBuf.get();
            var texHeight = texHeightBuf.get();
            var texChannels = texChannelsBuf.get();

            if (loadedTex == null) {
                throw new RuntimeException("Failed to load texture");
            }

            var texHandleBuf = BufferUtils.createIntBuffer(1);
            glGenTextures(texHandleBuf);
            var texHandle = texHandleBuf.get();
            glBindTexture(GL_TEXTURE_2D, texHandle);
            loadedTex.rewind();
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, texWidth, texHeight, 0, GL_RGBA, GL_UNSIGNED_BYTE, loadedTex);
            glBindTexture(GL_TEXTURE_2D, 0);

            stbi_image_free(loadedTex);

            return OptionalInt.of(texHandle);
        }
        catch (Exception ex){
            ex.printStackTrace();
            return OptionalInt.empty();
        }
    }
}
