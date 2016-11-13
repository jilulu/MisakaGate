package com.mahoucoder.misakagate.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mahoucoder.misakagate.GateApplication;
import com.mahoucoder.misakagate.R;
import com.mahoucoder.misakagate.api.models.Thread;
import com.mahoucoder.misakagate.utils.GateUtils;

import java.util.List;

/**
 * Created by jamesji on 29/10/2016.
 */

public class DividerItemDecoration extends RecyclerView.ItemDecoration {
    private List<Thread> mData;
    private Paint mPaint;
    private Rect mBounds;

    private static final int mTitleHeight = (int) GateUtils.dp2px(GateApplication.getGlobalContext(), 30);
    private static int COLOR_TITLE_BG = Color.parseColor("#FFDFDFDF");
    private static int COLOR_TITLE_FONT = Color.parseColor("#FF000000");

    public DividerItemDecoration(Context context, List<Thread> datas) {
        super();
        mData = datas;
        mPaint = new Paint();
        mBounds = new Rect();
        int titleFontSize = (int) GateUtils.dp2px(GateApplication.getGlobalContext(), 18);
        mPaint.setTextSize(titleFontSize);
        mPaint.setAntiAlias(true);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewLayoutPosition();
        if (position > -1) {
            if (position == 0) {
                outRect.set(0, mTitleHeight, 0, 0);
            } else {
                String tag = getTimeDiff(mData.get(position).lastpost);
                String prevTag = getTimeDiff(mData.get(position - 1).lastpost);
                if (null != tag && !prevTag.equals(tag)) {
                    outRect.set(0, mTitleHeight, 0, 0);
                } else {
                    outRect.set(0, 0, 0, 0);
                }
            }
        }
    }

    private void drawTitleArea(Canvas c, int left, int right, View child, RecyclerView.LayoutParams params, int position, String text) {
        mPaint.setColor(COLOR_TITLE_BG);
        c.drawRect(left, child.getTop() - params.topMargin - mTitleHeight, right, child.getTop() - params.topMargin, mPaint);
        mPaint.setColor(COLOR_TITLE_FONT);
        mPaint.getTextBounds(text, 0, text.length(), mBounds);
        c.drawText(text, child.getPaddingLeft(), child.getTop() - params.topMargin - (mTitleHeight / 2 - mBounds.height() / 2), mPaint);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            int position = params.getViewLayoutPosition();

            String tag = getTimeDiff(mData.get(position).lastpost);
            if (position == 0) {
                drawTitleArea(c, left, right, child, params, position, tag);
            } else {
                String prevTag = getTimeDiff(mData.get(position - 1).lastpost);
                if (position > 0 && null != tag && !tag.equals(prevTag)) {
                    drawTitleArea(c, left, right, child, params, position, tag);
                }
            }
        }
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int firstVisibleItemPosition = ((LinearLayoutManager) (parent.getLayoutManager())).findFirstVisibleItemPosition();

        String tag = getTimeDiff(mData.get(firstVisibleItemPosition).lastpost);
        View child = parent.findViewHolderForLayoutPosition(firstVisibleItemPosition).itemView;
        mPaint.setColor(COLOR_TITLE_BG);
        c.drawRect(parent.getPaddingLeft(), parent.getPaddingTop(), parent.getRight() - parent.getPaddingRight(), parent.getPaddingTop() + mTitleHeight, mPaint);
        mPaint.setColor(COLOR_TITLE_FONT);
        mPaint.getTextBounds(tag, 0, tag.length(), mBounds);
        c.drawText(tag, child.getPaddingLeft(),
                parent.getPaddingTop() + mTitleHeight - (mTitleHeight / 2 - mBounds.height() / 2),
                mPaint);
    }

    private String getTimeDiff(String lastpost) {
        String tag;
        try {
            tag = GateUtils.calculateTimeDiffInDays(Long.parseLong(lastpost));
        } catch (Exception e) {
            e.printStackTrace();
            tag = GateApplication.getGlobalContext().getString(R.string.some_day);
        }
        return tag;
    }
}
