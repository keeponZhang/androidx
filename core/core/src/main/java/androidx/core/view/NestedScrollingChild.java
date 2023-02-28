/*
 * Copyright 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package androidx.core.view;

import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;

import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat.ScrollAxis;

/**
 * This interface should be implemented by {@link android.view.View View} subclasses that wish
 * to support dispatching nested scrolling operations to a cooperating parent
 * {@link android.view.ViewGroup ViewGroup}.
 *
 * <p>Classes implementing this interface should create a final instance of a
 * {@link NestedScrollingChildHelper} as a field and delegate any View methods to the
 * <code>NestedScrollingChildHelper</code> methods of the same signature.</p>
 *
 * <p>Views invoking nested scrolling functionality should always do so from the relevant
 * {@link ViewCompat}, {@link ViewGroupCompat} or {@link ViewParentCompat} compatibility
 * shim static methods. This ensures interoperability with nested scrolling views on Android
 * 5.0 Lollipop and newer.</p>
 */
public interface NestedScrollingChild {
    /**
     * Enable or disable nested scrolling for this view.
     *
     * <p>If this property is set to true the view will be permitted to initiate nested
     * scrolling operations with a compatible parent view in the current hierarchy. If this
     * view does not implement nested scrolling this will have no effect. Disabling nested scrolling
     * while a nested scroll is in progress has the effect of {@link #stopNestedScroll() stopping}
     * the nested scroll.</p>
     *
     * @param enabled true to enable nested scrolling, false to disable
     *
     * @see #isNestedScrollingEnabled()
     */
    void setNestedScrollingEnabled(boolean enabled);

    /**
     * Returns true if nested scrolling is enabled for this view.
     *
     * <p>If nested scrolling is enabled and this View class implementation supports it,
     * this view will act as a nested scrolling child view when applicable, forwarding data
     * about the scroll operation in progress to a compatible and cooperating nested scrolling
     * parent.</p>
     *
     * @return true if nested scrolling is enabled
     *
     * @see #setNestedScrollingEnabled(boolean)
     */
    boolean isNestedScrollingEnabled();

    /**
     * Begin a nestable scroll operation along the given axes.
     *
     * <p>A view starting a nested scroll promises to abide by the following contract:</p>
     *
     * <p>The view will call startNestedScroll upon initiating a scroll operation. In the case
     * of a touch scroll this corresponds to the initial {@link MotionEvent#ACTION_DOWN}.
     * In the case of touch scrolling the nested scroll will be terminated automatically in
     * the same manner as {@link ViewParent#requestDisallowInterceptTouchEvent(boolean)}.
     * In the event of programmatic scrolling the caller must explicitly call
     * {@link #stopNestedScroll()} to indicate the end of the nested scroll.</p>
     *
     * <p>If <code>startNestedScroll</code> returns true, a cooperative parent was found.
     * If it returns false the caller may ignore the rest of this contract until the next scroll.
     * Calling startNestedScroll while a nested scroll is already in progress will return true.</p>
     *
     * <p>At each incremental step of the scroll the caller should invoke
     * {@link #dispatchNestedPreScroll(int, int, int[], int[]) dispatchNestedPreScroll}
     * once it has calculated the requested scrolling delta. If it returns true the nested scrolling
     * parent at least partially consumed the scroll and the caller should adjust the amount it
     * scrolls by.</p>
     *
     * <p>After applying the remainder of the scroll delta the caller should invoke
     * {@link #dispatchNestedScroll(int, int, int, int, int[]) dispatchNestedScroll}, passing
     * both the delta consumed and the delta unconsumed. A nested scrolling parent may treat
     * these values differently. See
     * {@link NestedScrollingParent#onNestedScroll(View, int, int, int, int)}.
     * </p>
     *
     * @param axes Flags consisting of a combination of {@link ViewCompat#SCROLL_AXIS_HORIZONTAL}
     *             and/or {@link ViewCompat#SCROLL_AXIS_VERTICAL}.
     * @return true if a cooperative parent was found and nested scrolling has been enabled for
     *         the current gesture.
     *
     * @see #stopNestedScroll()
     * @see #dispatchNestedPreScroll(int, int, int[], int[])
     * @see #dispatchNestedScroll(int, int, int, int, int[])
     */
    /**
     * 开启一个嵌套滑动
     *
     * @param axes 支持的嵌套滑动方法，分为水平方向，竖直方向，或不指定
     * @return 如果返回true, 表示当前子控件已经找了一起嵌套滑动的view
     */
    boolean startNestedScroll(@ScrollAxis int axes);

    /**
     * Stop a nested scroll in progress.
     *
     * <p>Calling this method when a nested scroll is not currently in progress is harmless.</p>
     *
     * @see #startNestedScroll(int)
     */
    /**
     * 子控件停止嵌套滑动
     */
    void stopNestedScroll();

    /**
     * Returns true if this view has a nested scrolling parent.
     *
     * <p>The presence of a nested scrolling parent indicates that this view has initiated
     * a nested scroll and it was accepted by an ancestor view further up the view hierarchy.</p>
     *
     * @return whether this view has a nested scrolling parent
     */
    boolean hasNestedScrollingParent();

    /**
     * Dispatch one step of a nested scroll in progress before this view consumes any portion of it.
     *
     * <p>Nested pre-scroll events are to nested scroll events what touch intercept is to touch.
     * <code>dispatchNestedPreScroll</code> offers an opportunity for the parent view in a nested
     * scrolling operation to consume some or all of the scroll operation before the child view
     * consumes it.</p>
     *
     * @param dx Horizontal scroll distance in pixels
     * @param dy Vertical scroll distance in pixels
     * @param consumed Output. If not null, consumed[0] will contain the consumed component of dx
     *                 and consumed[1] the consumed dy.
     * @param offsetInWindow Optional. If not null, on return this will contain the offset
     *                       in local view coordinates of this view from before this operation
     *                       to after it completes. View implementations may use this to adjust
     *                       expected input coordinate tracking.
     * @return true if the parent consumed some or all of the scroll delta
     * @see #dispatchNestedScroll(int, int, int, int, int[])
     */
    /**
     * 在子控件滑动前，将事件分发给父控件，由父控件判断消耗多少
     *
     * @param dx             水平方向嵌套滑动的子控件想要变化的距离 dx<0 向右滑动 dx>0 向左滑动
     * @param dy             垂直方向嵌套滑动的子控件想要变化的距离 dy<0 向下滑动 dy>0 向上滑动
     * @param consumed       子控件传给父控件数组，用于存储父控件水平与竖直方向上消耗的距离，consumed[0] 水平消耗的距离，consumed[1] 垂直消耗的距离
     * @param offsetInWindow 子控件在当前window的偏移量
     * @return 如果返回true, 表示父控件已经消耗了
     */
    boolean dispatchNestedPreScroll(int dx, int dy, @Nullable int[] consumed,
                                    @Nullable int[] offsetInWindow);
    /**
     * Dispatch one step of a nested scroll in progress.
     *
     * <p>Implementations of views that support nested scrolling should call this to report
     * info about a scroll in progress to the current nested scrolling parent. If a nested scroll
     * is not currently in progress or nested scrolling is not
     * {@link #isNestedScrollingEnabled() enabled} for this view this method does nothing.</p>
     *
     * <p>Compatible View implementations should also call
     * {@link #dispatchNestedPreScroll(int, int, int[], int[]) dispatchNestedPreScroll} before
     * consuming a component of the scroll event themselves.</p>
     *
     * @param dxConsumed Horizontal distance in pixels consumed by this view during this scroll step
     * @param dyConsumed Vertical distance in pixels consumed by this view during this scroll step
     * @param dxUnconsumed Horizontal scroll distance in pixels not consumed by this view
     * @param dyUnconsumed Horizontal scroll distance in pixels not consumed by this view
     * @param offsetInWindow Optional. If not null, on return this will contain the offset
     *                       in local view coordinates of this view from before this operation
     *                       to after it completes. View implementations may use this to adjust
     *                       expected input coordinate tracking.
     * @return true if the event was dispatched, false if it could not be dispatched.
     * @see #dispatchNestedPreScroll(int, int, int[], int[])
     */
    /**
     * 当父控件消耗事件后，子控件处理后，又继续将事件分发给父控件,由父控件判断是否消耗剩下的距离。
     *
     * @param dxConsumed     水平方向嵌套滑动的子控件滑动的距离(消耗的距离)
     * @param dyConsumed     垂直方向嵌套滑动的子控件滑动的距离(消耗的距离)
     * @param dxUnconsumed   水平方向嵌套滑动的子控件未滑动的距离(未消耗的距离)
     * @param dyUnconsumed   垂直方向嵌套滑动的子控件未滑动的距离(未消耗的距离)
     * @param offsetInWindow 子控件在当前window的偏移量
     * @return 如果返回true, 表示父控件又继续消耗了
     */
    boolean dispatchNestedScroll(int dxConsumed, int dyConsumed,
            int dxUnconsumed, int dyUnconsumed, @Nullable int[] offsetInWindow);

    /**
     * Dispatch a fling to a nested scrolling parent before it is processed by this view.
     *
     * <p>Nested pre-fling events are to nested fling events what touch intercept is to touch
     * and what nested pre-scroll is to nested scroll. <code>dispatchNestedPreFling</code>
     * offsets an opportunity for the parent view in a nested fling to fully consume the fling
     * before the child view consumes it. If this method returns <code>true</code>, a nested
     * parent view consumed the fling and this view should not scroll as a result.</p>
     *
     * <p>For a better user experience, only one view in a nested scrolling chain should consume
     * the fling at a time. If a parent view consumed the fling this method will return false.
     * Custom view implementations should account for this in two ways:</p>
     *
     * <ul>
     *     <li>If a custom view is paged and needs to settle to a fixed page-point, do not
     *     call <code>dispatchNestedPreFling</code>; consume the fling and settle to a valid
     *     position regardless.</li>
     *     <li>If a nested parent does consume the fling, this view should not scroll at all,
     *     even to settle back to a valid idle position.</li>
     * </ul>
     *
     * <p>Views should also not offer fling velocities to nested parent views along an axis
     * where scrolling is not currently supported; a {@link android.widget.ScrollView ScrollView}
     * should not offer a horizontal fling velocity to its parents since scrolling along that
     * axis is not permitted and carrying velocity along that motion does not make sense.</p>
     *
     * @param velocityX Horizontal fling velocity in pixels per second
     * @param velocityY Vertical fling velocity in pixels per second
     * @return true if a nested scrolling parent consumed the fling
     */
    /**
     * 当子控件产生fling滑动时，判断父控件是否处拦截fling，如果父控件处理了fling，那子控件就没有办法处理fling了。
     *
     * @param velocityX 水平方向上的速度 velocityX > 0  向左滑动，反之向右滑动
     * @param velocityY 竖直方向上的速度 velocityY > 0  向上滑动，反之向下滑动
     * @return 如果返回true, 表示父控件拦截了fling
     */
    boolean dispatchNestedPreFling(float velocityX, float velocityY);

    /**
     * Dispatch a fling to a nested scrolling parent.
     *
     * <p>This method should be used to indicate that a nested scrolling child has detected
     * suitable conditions for a fling. Generally this means that a touch scroll has ended with a
     * {@link VelocityTracker velocity} in the direction of scrolling that meets or exceeds
     * the {@link ViewConfiguration#getScaledMinimumFlingVelocity() minimum fling velocity}
     * along a scrollable axis.</p>
     *
     * <p>If a nested scrolling child view would normally fling but it is at the edge of
     * its own content, it can use this method to delegate the fling to its nested scrolling
     * parent instead. The parent may optionally consume the fling or observe a child fling.</p>
     *
     * @param velocityX Horizontal fling velocity in pixels per second
     * @param velocityY Vertical fling velocity in pixels per second
     * @param consumed true if the child consumed the fling, false otherwise
     * @return true if the nested scrolling parent consumed or otherwise reacted to the fling
     */
    /**
     * 当父控件不拦截子控件的fling,那么子控件会调用该方法将fling，传给父控件进行处理
     *
     * @param velocityX 水平方向上的速度 velocityX > 0  向左滑动，反之向右滑动
     * @param velocityY 竖直方向上的速度 velocityY > 0  向上滑动，反之向下滑动
     * @param consumed  子控件是否可以消耗该fling，也可以说是子控件是否消耗掉了该fling
     * @return 父控件是否消耗了该fling
     */
    boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed);

}
