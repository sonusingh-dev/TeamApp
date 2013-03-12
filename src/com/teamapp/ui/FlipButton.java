package com.teamapp.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import com.teamapp.apis.anim.ActivitySwitcher;
import com.teamapp.ui.R;
import com.teamapp.ui.alerts.TeamAppAlerts;
import com.teamapp.ui.club.NewsListAct;
import com.teamapp.ui.team.GameDetailsAct;

public class FlipButton extends Button {

	private Context mContext;
	private View mView;

	public FlipButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		mView = ((Activity) mContext).getWindow().getDecorView()
				.findViewById(android.R.id.content);

		setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				if (LoginAct.isClubFree) {
					TeamAppAlerts.showMessageDialog(mContext, "");
				}
				
				if (R.id.btnClub == view.getId()) {
					animatedStartActivityLeftIn();
				} else if (R.id.btnTeam == view.getId()) {
					animatedStartActivityRightIn();
				}
			}
		});
	}
	
	private void animatedStartActivityRightIn() {
		// we only animateOut this activity here.
		// The new activity will animateIn from its onResume() - be sure to
		// implement it.
		final Intent intent = new Intent(mContext, GameDetailsAct.class);
		// disable default animation for new intent
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		
		ActivitySwitcher.animationRightIn(mView,
				((Activity) mContext).getWindowManager(),
				new ActivitySwitcher.AnimationFinishedListener() {
					public void onAnimationFinished() {
						intent.putExtra("anim", 1);
						mContext.startActivity(intent);
					}
				});
	}

	private void animatedStartActivityLeftIn() {

		// we only animateOut this activity here.
		// The new activity will animateIn from its onResume() - be sure to
		// implement it.
		final Intent intent = new Intent(mContext, NewsListAct.class);
		// disable default animation for new intent
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		
		ActivitySwitcher.animationLeftIn(mView,
				((Activity) mContext).getWindowManager(),
				new ActivitySwitcher.AnimationFinishedListener() {
					public void onAnimationFinished() {
						intent.putExtra("anim", 1);
						mContext.startActivity(intent);
					}
				});
	}
}
