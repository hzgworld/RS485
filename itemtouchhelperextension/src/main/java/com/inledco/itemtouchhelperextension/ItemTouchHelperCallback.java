package com.inledco.itemtouchhelperextension;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * Created by liruya on 2017/5/23.
 */

public class ItemTouchHelperCallback extends ItemTouchHelperExtension.Callback
{
    private boolean mSwipeEnabled = true;
    private boolean mDragEnabled;
    private int mSwipeFlags = ItemTouchHelper.START;
    private int mDragFlags = 0;
    private boolean mSwipeSpring = false;

    public ItemTouchHelperCallback ()
    {
    }

    public ItemTouchHelperCallback ( boolean swipeEnabled, boolean dragEnabled )
    {
        mSwipeEnabled = swipeEnabled;
        mDragEnabled = dragEnabled;
    }

    public ItemTouchHelperCallback ( boolean swipeEnabled, boolean dragEnabled, boolean swipeSpring )
    {
        mSwipeEnabled = swipeEnabled;
        mDragEnabled = dragEnabled;
        mSwipeSpring = swipeSpring;
    }

    public ItemTouchHelperCallback ( boolean swipeEnabled, boolean dragEnabled, int swipeFlags, int dragFlags )
    {
        mSwipeEnabled = swipeEnabled;
        mDragEnabled = dragEnabled;
        mSwipeFlags = swipeFlags;
        mDragFlags = dragFlags;
    }

    public ItemTouchHelperCallback ( boolean swipeEnabled, boolean dragEnabled, int swipeFlags, int dragFlags, boolean swipeSpring )
    {
        mSwipeEnabled = swipeEnabled;
        mDragEnabled = dragEnabled;
        mSwipeFlags = swipeFlags;
        mDragFlags = dragFlags;
        mSwipeSpring = swipeSpring;
    }

    @Override
    public int getMovementFlags ( RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder )
    {
        return makeMovementFlags( 0, mSwipeFlags );
        //        return makeMovementFlags( 0, mSwipeFlags );
    }

    @Override
    public boolean onMove ( RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target )
    {
        return false;
    }

    @Override
    public void onSwiped ( RecyclerView.ViewHolder viewHolder, int direction )
    {

    }

    @Override
    public boolean isLongPressDragEnabled ()
    {
        //        return super.isLongPressDragEnabled() && mDragEnabled;
        return mDragEnabled;
    }

    @Override
    public boolean isItemViewSwipeEnabled ()
    {
        //        return super.isItemViewSwipeEnabled() && mSwipeEnabled;
        return mSwipeEnabled;
    }

    @Override
    public void onChildDraw ( Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState,
                              boolean isCurrentlyActive )
    {
        if ( viewHolder instanceof SwipeItemViewHolder )
        {
            if ( mDragEnabled && dY != 0 && dX == 0 )
            {
                super.onChildDraw( c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive );
            }
            SwipeItemViewHolder holder = (SwipeItemViewHolder) viewHolder;
            float x = dX;
            if ( !mSwipeSpring )
            {
                if ( dX < 0 - holder.getActionWidth() )
                {
                    x = 0 - holder.getActionWidth();
                }
            }
            holder.getContentView().setTranslationX( x );
        }
    }
}
