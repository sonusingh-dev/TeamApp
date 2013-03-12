/*
 * Copyright (C) 2011 Hyves
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

package com.teamapp.apis.hyves;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Display;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.teamapp.apis.hyves.ShareOnHyves.LoginCompleteCmd;

public class HyvesDialog extends Dialog {
	private String url;
	private ProgressDialog spinner;
	private WebView webView;
	private LinearLayout content;
	LoginCompleteCmd loginCompleteCmd;

	Dialog thisDialog;

	public HyvesDialog(Context context, String url,
			LoginCompleteCmd loginCompleteCmd) {
		super(context);
		this.url = url;
		this.loginCompleteCmd = loginCompleteCmd;

		thisDialog = this;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.spinner = new ProgressDialog(getContext());
		this.spinner.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.spinner.setMessage("Loading. Please wait...");

		this.content = new LinearLayout(getContext());
		this.content.setOrientation(LinearLayout.VERTICAL);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		buildWebView();
		Display display = getWindow().getWindowManager().getDefaultDisplay();
		final float scale = getContext().getResources().getDisplayMetrics().density;
		float[] dimensions = display.getWidth() < display.getHeight() ? Globals.LoginDialogGUI.DIMENSIONS_PORTRAIT
				: Globals.LoginDialogGUI.DIMENSIONS_LANDSCAPE;
		addContentView(this.content, new FrameLayout.LayoutParams(
				(int) (dimensions[0] * scale), (int) (dimensions[1] * scale)));
	}

	private void buildWebView() {
		this.webView = new WebView(getContext());
		this.webView.setVerticalScrollBarEnabled(false);
		this.webView.setHorizontalScrollBarEnabled(false);
		this.webView.setWebViewClient(new HyvesDialog.webViewClient());
		this.webView.getSettings().setJavaScriptEnabled(true);
		this.webView.loadUrl(this.url);
		this.webView.setLayoutParams(new FrameLayout.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT));
		this.content.addView(this.webView);
	}

	private class webViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (url.startsWith(Globals.Hyves.REDIRECT_URI)) {
				Bundle values = GenUtil.parseUrl(url,
						Globals.Hyves.REDIRECT_PRE);
				loginCompleteCmd.LoginComplete(values);

				thisDialog.dismiss();
				return true;
			}

			return false;
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			spinner.show();
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			spinner.dismiss();
		}
	}

}
