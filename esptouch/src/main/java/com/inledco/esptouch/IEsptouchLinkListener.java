package com.inledco.esptouch;

import java.util.List;

/**
 * Created by liruya on 2017/9/16.
 */

public interface IEsptouchLinkListener
{
    void onLinked( IEsptouchResult result );

    void onCompleted( List< IEsptouchResult > results );
}
