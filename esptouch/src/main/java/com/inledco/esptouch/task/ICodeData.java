package com.inledco.esptouch.task;

/**
 * Created by liruya on 2017/9/16.
 */

public interface ICodeData
{
    /**
     * Get the byte[] to be transformed.
     *
     *
     * @return the byte[] to be transfromed
     */
    byte[] getBytes();

    /**
     * Get the char[](u8[]) to be transfromed.
     *
     * @return the char[](u8) to be transformed
     */
    char[] getU8s();
}
