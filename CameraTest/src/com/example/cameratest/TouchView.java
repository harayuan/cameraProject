package com.example.cameratest;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

public class TouchView extends View {
	private PreviewSurface mPreviewSurface;

	private ScaleGestureDetector mScaleDetector;
	private float mScaleFactor = 1.f;

	private float mLastTouchX;
	private float mLastTouchY;

	final private float orgWidth = 30;
	final private float orgHeight = 30;
	//private float targetLeft;
	//private float targetTop;
	private float width;
	private float height;
	private Paint line;

	private RectF targetRect;
	private RectF calRectR;
	private RectF calRectG;
	private RectF calRectB;

	private int displayWidth;
	private int displayHeight;

	private static final int INVALID_POINTER_ID = -1;
	// The "active pointer" is the one currently moving our object.
	private int mActivePointerId = INVALID_POINTER_ID;

	public TouchView(Context context, int displayWidth, int displayHeight, PreviewSurface mPreviewSurface) {
		this(context, null, 0);
		init(context, displayWidth, displayHeight, mPreviewSurface);
	}

	public TouchView(Context context, AttributeSet attrs, int displayWidth, int displayHeight, PreviewSurface mPreviewSurface) {
		this(context, attrs, 0);
		init(context, displayWidth, displayHeight, mPreviewSurface);
	}

	public TouchView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	private void init(Context context, int displayWidth, int displayHeight, PreviewSurface mPreviewSurface) {
		this.displayWidth = displayWidth;
		this.displayHeight = displayHeight;
		this.mPreviewSurface = mPreviewSurface;
		if (mPreviewSurface.getCamera() == null)
			Log.i("camera", "TouchView: mCamera is null");

		width = 30;
		height = 30;
		line = new Paint();

		targetRect = new RectF(100, 100, 100 + width, 100 + height);
		calRectR = new RectF(50, 50, 80, 80);
		calRectG = new RectF(150, 50, 180, 80);
		calRectB = new RectF(250, 50, 280, 80);

		// Create our ScaleGestureDetector
		mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
	}

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.save();
		line.setColor(Color.RED);
		line.setStyle(Paint.Style.STROKE);
		canvas.drawRect(calRectR, line);
		line.setColor(Color.GREEN);
		line.setStyle(Paint.Style.STROKE);
		canvas.drawRect(calRectG, line);
		line.setColor(Color.BLUE);
		line.setStyle(Paint.Style.STROKE);
		canvas.drawRect(calRectB, line);
		line.setColor(Color.WHITE);
		line.setStyle(Paint.Style.STROKE);
		line.setStrokeWidth(3);
		canvas.drawRect(targetRect, line);
		canvas.restore();
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		// Let the ScaleGestureDetector inspect all events.
		mScaleDetector.onTouchEvent(ev);
		int actFlag = 0;

		final int action = ev.getAction();
		switch (action & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN: {
				Log.e("camera", "ACTION DOWN");
				Log.e("camera", String.format("flag: %d", actFlag));
				actFlag = actFlag == 1 ? 0 : 1;
				if (actFlag == 1) {
					final float x = ev.getX();
					final float y = ev.getY();

					mLastTouchX = x;
					mLastTouchY = y;
					mActivePointerId = ev.getPointerId(0);

					// set focus rectangle with width=40
					Rect focusRect = new Rect();
					focusRect.set((int) x - 20, (int) y - 20, (int) x + 20, (int) y + 20);
					Log.e("camera", String.format("focus before: %d %d %d %d", focusRect.left, focusRect.top, focusRect.right, focusRect.bottom));

					if (focusRect.left < 0) {
						focusRect.left = 0;
					} else if (focusRect.right > displayWidth) {
						focusRect.right = displayWidth;
					}
					if (focusRect.top < 0) {
						focusRect.top = 0;
					} else if (focusRect.bottom > displayHeight) {
						focusRect.bottom = displayHeight;
					}
					mPreviewSurface.setFocus(focusRect);
					Log.e("camera", String.format("focus after: %d %d %d %d", focusRect.left, focusRect.top, focusRect.right, focusRect.bottom));
				}

				break;
			}

			case MotionEvent.ACTION_MOVE: {
				Log.e("camera", "ACTION MOVE");
				final int pointerIndex = ev.findPointerIndex(mActivePointerId);
				final float x = ev.getX(pointerIndex);
				final float y = ev.getY(pointerIndex);

				// Only move if the ScaleGestureDetector isn't processing a gesture.
				if (!mScaleDetector.isInProgress()) {
					Log.e("camera", "ACTION MOVE not in progress");

					final float dx = x - mLastTouchX;
					final float dy = y - mLastTouchY;

					targetRect.offset(dx, dy);

					if (targetRect.left < 0) {
						targetRect.left = 0;
						targetRect.right = width;
					} else if (targetRect.right > displayWidth) {
						targetRect.left = displayWidth - width;
						targetRect.right = displayWidth;
					}
					if (targetRect.top < 0) {
						targetRect.top = 0;
						targetRect.bottom = width;
					} else if (targetRect.bottom > displayHeight) {
						targetRect.top = displayHeight - height;
						targetRect.bottom = displayHeight;
					}

					invalidate();
				}
				mLastTouchX = x;
				mLastTouchY = y;

				break;
			}

			case MotionEvent.ACTION_UP: {
				mActivePointerId = INVALID_POINTER_ID;
				Log.e("camera", "ACTION UP");

				break;
			}

			case MotionEvent.ACTION_CANCEL: {
				Log.e("camera", "ACTION CANCEL");
				mActivePointerId = INVALID_POINTER_ID;
				break;
			}

			case MotionEvent.ACTION_POINTER_UP: { //non primary pointer going up
				Log.e("camera", "ACTION POINTER UP");
				actFlag = 0;
				final int pointerIndex = (ev.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
				final int pointerId = ev.getPointerId(pointerIndex);
				if (pointerId == mActivePointerId) {
					// This was our active pointer going up. Choose a new active pointer and adjust accordingly.
					final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
					Log.e("camera", String.format("index: %d", pointerIndex));
					mLastTouchX = ev.getX(newPointerIndex);
					mLastTouchY = ev.getY(newPointerIndex);
					mActivePointerId = ev.getPointerId(newPointerIndex);
				}
				break;
			}
		}

		return true;
	}

	private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			mScaleFactor *= detector.getScaleFactor();
			// Don't let the object get too small or too large.
			mScaleFactor = Math.max(0.5f, Math.min(mScaleFactor, 10f));

			width = orgWidth * mScaleFactor;
			height = orgHeight * mScaleFactor;

			targetRect.inset((targetRect.width() - width) / 2, (targetRect.height() - height) / 2);
			if (targetRect.left < 0) {
				targetRect.left = 0;
				targetRect.right = width;
			} else if (targetRect.right > displayWidth) {
				targetRect.left = displayWidth - width;
				targetRect.right = displayWidth;
			}
			if (targetRect.top < 0) {
				targetRect.top = 0;
				targetRect.bottom = width;
			} else if (targetRect.bottom > displayHeight) {
				targetRect.top = displayHeight - height;
				targetRect.bottom = displayHeight;
			}

			invalidate();
			return true;
		}
	}

	public int getIconWidth() {
		return (int) width;
	}

	public int getIconHeight() {
		return (int) height;
	}

	public RectF getTargetRect() {
		return targetRect;
	}

	public RectF getCalRectR() {
		return calRectR;
	}

	public RectF getCalRectG() {
		return calRectG;
	}

	public RectF getCalRectB() {
		return calRectB;
	}

}