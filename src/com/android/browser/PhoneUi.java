/*
 * Copyright (C) 2010 The Android Open Source Project
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

package com.android.browser;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.android.browser.UrlInputView.StateListener;

/**
 * Ui for regular phone screen sizes
 */
public class PhoneUi extends BaseUi {

	private static final String LOGTAG = "PhoneUi";
	private static final int MSG_INIT_NAVSCREEN = 100;

	private PieControlPhone mPieControl;
	private NavScreen mNavScreen;
	private AnimScreen mAnimScreen;
	private NavigationBarPhone mNavigationBar;
	private int mActionBarHeight;

	boolean mExtendedMenuOpen;
	boolean mOptionsMenuOpen;
	boolean mAnimating;

	/**
	 * @param browser
	 * @param controller
	 */
	public PhoneUi(Activity browser, UiController controller) {
		super(browser, controller);
		setUseQuickControls(BrowserSettings.getInstance().useQuickControls());
		mNavigationBar = (NavigationBarPhone) mTitleBar.getNavigationBar();
		TypedValue heightValue = new TypedValue();
		browser.getTheme().resolveAttribute(
				com.android.internal.R.attr.actionBarSize, heightValue, true);
		mActionBarHeight = TypedValue.complexToDimensionPixelSize(
				heightValue.data, browser.getResources().getDisplayMetrics());
	}

	// nakashima
	// QCの機能　URL編集
	@Override
	public void editUrl(boolean clearInput) {
		if (mUseQuickControls) {
			// mTitleBar.setShowProgressOnly(false);
		}
		super.editUrl(clearInput);
	}

	@Override
	public boolean dispatchKey(int code, KeyEvent event) {
		return false;
	}

	@Override
	public void onProgressChanged(Tab tab) {
		if (tab.inForeground()) {
			int progress = tab.getLoadProgress();
			// mTitleBar.setProgress(progress);
			if (progress == 100) {
				if (!mOptionsMenuOpen || !mExtendedMenuOpen) {
					// suggestHideTitleBar();
					if (mUseQuickControls) {
						// mTitleBar.setShowProgressOnly(false);
					}
				}
			} else {
				if (!mOptionsMenuOpen || mExtendedMenuOpen) {
					if (mUseQuickControls) {
						//mTitleBar.setShowProgressOnly(true);
						setTitleGravity(Gravity.TOP);
					}
					//showTitleBar();
				}
			}
		}
	}


	@Override
	public void setActiveTab(final Tab tab) {
		// Request focus on the top window.
		if (mUseQuickControls) {
			mPieControl.forceToTop(mContentView);
		}

	}

	// menu handling callbacks

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		updateMenuState(mActiveTab, menu);
		return true;
	}

	@Override
	public void updateMenuState(Tab tab, Menu menu) {

		MenuItem newtab = menu.findItem(R.id.new_tab_menu_id);
		if (newtab != null && !mUseQuickControls) {
			newtab.setVisible(false);
		}
		MenuItem incognito = menu.findItem(R.id.incognito_menu_id);
		if (incognito != null) {
			incognito.setVisible( mUseQuickControls);
		}

	}




	@Override
	public void setUseQuickControls(boolean useQuickControls) {
		mUseQuickControls = useQuickControls;
		mTitleBar.setUseQuickControls(mUseQuickControls);
		if (useQuickControls) {
			mPieControl = new PieControlPhone(mActivity, mUiController, this);
			mPieControl.attachToContainer(mContentView);
			WebView web = getWebView();
			if (web != null) {
				web.setEmbeddedTitleBar(null);
			}
		} else {
			if (mPieControl != null) {
				mPieControl.removeFromContainer(mContentView);
			}
			WebView web = getWebView();
			if (web != null) {
				// make sure we can re-parent titlebar
				if ((mTitleBar != null) && (mTitleBar.getParent() != null)) {
					((ViewGroup) mTitleBar.getParent()).removeView(mTitleBar);
				}
				web.setEmbeddedTitleBar(mTitleBar);
			}
			setTitleGravity(Gravity.NO_GRAVITY);
		}
		updateUrlBarAutoShowManagerTarget();
	}

}
