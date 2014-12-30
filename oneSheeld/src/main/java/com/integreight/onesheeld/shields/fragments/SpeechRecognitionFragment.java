package com.integreight.onesheeld.shields.fragments;

import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.integreight.onesheeld.R;
import com.integreight.onesheeld.shields.ShieldFragmentParent;
import com.integreight.onesheeld.shields.controller.SpeechRecognitionShield;
import com.integreight.onesheeld.shields.controller.utils.SpeechRecognition.RecognitionEventHandler;
import com.integreight.onesheeld.utils.customviews.OneSheeldTextView;

public class SpeechRecognitionFragment extends
		ShieldFragmentParent<SpeechRecognitionFragment> {

	View statusCircle;
	OneSheeldTextView statusHint, recognizedResult;
	TextView rmsIndicator;
	RelativeLayout.LayoutParams params;
	int stepValue = 0;
	int marginValue;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		v = inflater.inflate(R.layout.voice_recognition_shield_fragment_view,
				container, false);
		statusCircle = v.findViewById(R.id.statusCircle);
		statusHint = (OneSheeldTextView) v.findViewById(R.id.statusHint);
		rmsIndicator = (TextView) v.findViewById(R.id.rmsLevelIndicator);
		recognizedResult = (OneSheeldTextView) v
				.findViewById(R.id.recognizedResult);
		params = (LayoutParams) rmsIndicator.getLayoutParams();
		statusCircle.getViewTreeObserver().addOnGlobalLayoutListener(
				new ViewTreeObserver.OnGlobalLayoutListener() {

					@Override
					public void onGlobalLayout() {
						if (stepValue == 0)
							stepValue = statusCircle.getHeight() / 10;
					}
				});
		return v;

	}

	@Override
	public void onStart() {
		if (getApplication().getRunningShields().get(getControllerTag()) == null) {
			if (!reInitController())
				return;
		}
		((SpeechRecognitionShield) getApplication().getRunningShields().get(
				getControllerTag()))
				.setEventHandler(speechRecognitionEventHandler);
		statusCircle.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				((SpeechRecognitionShield) getApplication().getRunningShields()
						.get(getControllerTag())).startRecognizer();
			}
		});
		super.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}

	private RecognitionEventHandler speechRecognitionEventHandler = new RecognitionEventHandler() {

		@Override
		public void onResult(final List<String> result) {
			uiHandler.post(new Runnable() {

				@Override
				public void run() {
					if (canChangeUI()) {
						setOff();
						if (result.size() > 0)
							recognizedResult.setText(result.get(0)
									.toLowerCase());
					}
				}
			});
		}

		@Override
		public void onReadyForSpeach(Bundle params) {
			if (canChangeUI()) {
				uiHandler.post(new Runnable() {

					@Override
					public void run() {
						if (canChangeUI()) {
							recognizedResult.setText("");
							setON();
						}
					}
				});
			}
		}

		@Override
		public void onError(final String error, int errorCode) {
			uiHandler.post(new Runnable() {

				@Override
				public void run() {
					if (canChangeUI()) {
						setOff();
						Toast.makeText(activity, error, Toast.LENGTH_LONG)
								.show();
					}
				}
			});
		}

		@Override
		public void onEndOfSpeech() {
			if (canChangeUI()) {
				uiHandler.post(new Runnable() {

					@Override
					public void run() {
						recognizedResult.setText("");
						setOff();
					}
				});
			}
		}

		@Override
		public void onBeginingOfSpeech() {
			if (canChangeUI()) {
				uiHandler.post(new Runnable() {

					@Override
					public void run() {
						recognizedResult.setText("");
						setON();
					}
				});
			}
		}

		@Override
		public void onRmsChanged(final float rmsdB) {
			uiHandler.removeCallbacksAndMessages(null);
			uiHandler.post(new Runnable() {

				@Override
				public void run() {
					marginValue = (int) (stepValue * (rmsdB > 10 ? 10 : rmsdB));
					params.bottomMargin = marginValue < 0 ? 0 : marginValue;
					rmsIndicator.requestLayout();
				}
			});
		}
	};

	private void setOff() {
		rmsIndicator.setVisibility(View.INVISIBLE);
		statusCircle.setBackgroundColor(getResources().getColor(
				R.color.voice_rec_circle_red));
		statusHint.setText(R.string.tabToSpeak);
	}

	private void setON() {
		rmsIndicator.setVisibility(View.VISIBLE);
		statusCircle.setBackgroundColor(getResources().getColor(
				R.color.voice_rec_circle_green));
		statusHint.setText(R.string.speak);
	}

	private void initializeFirmata() {
		if (getApplication().getRunningShields().get(getControllerTag()) == null) {
			getApplication().getRunningShields().put(getControllerTag(),
					new SpeechRecognitionShield(activity, getControllerTag()));
		}

	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void doOnServiceConnected() {
		initializeFirmata();

	}

}
