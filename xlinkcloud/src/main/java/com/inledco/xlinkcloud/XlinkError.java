package com.inledco.xlinkcloud;

/**
 * Created by liruya on 2017/9/18.
 */

public class XlinkError
{
    private HttpError error;

    public XlinkError ( HttpError httpError )
    {
        error = httpError;
    }

    public HttpError getHttpError ()
    {
        return error;
    }

    public void setHttpError ( HttpError httpError )
    {
        error = httpError;
    }

    @Override
    public String toString ()
    {
        return error.toString();
    }

    public class HttpError
    {
        private int code;
        private String msg;

        public HttpError ( int code, String msg )
        {
            this.code = code;
            this.msg = msg;
        }

        public int getCode ()
        {
            return code;
        }

        public void setCode ( int code )
        {
            this.code = code;
        }

        public String getMsg ()
        {
            return msg;
        }

        public void setMsg ( String msg )
        {
            this.msg = msg;
        }

        @Override
        public String toString ()
        {
            return "Code: " + code + "\r\nMsg: " + msg;
        }
    }
}
