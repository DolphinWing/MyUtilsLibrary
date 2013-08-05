package dolphin.android.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.actionbarsherlock.app.ActionBar;

import dolphin.android.libs.R;

public class DoneActionBar extends View
{
	ActionBar mActionBar;

	public DoneActionBar(Context context, ActionBar actionBar)
	{
		super(context);
		mActionBar = actionBar;

		// Inflate a "Done" custom action bar view to serve as the "Up" affordance.
		LayoutInflater inflater =
			(LayoutInflater) mActionBar.getThemedContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View customActionBarView = inflater.inflate(
			R.layout.actionbar_custom_view_done, null);
		customActionBarView.findViewById(R.id.actionbar_done)
				.setOnClickListener(
					new View.OnClickListener() {
						@Override
						public void onClick(View v)
						{
							if (onButtonClickListener != null)
								onButtonClickListener.onDone(v);
						}
					});

		// Show the custom action bar view and hide the normal Home icon and title.
		//final ActionBar actionBar = getActionBar();
		mActionBar.setDisplayOptions(
			ActionBar.DISPLAY_SHOW_CUSTOM,
			ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME
				| ActionBar.DISPLAY_SHOW_TITLE);
		mActionBar.setCustomView(customActionBarView);
	}

	public interface OnButtonClickListener
	{
		public void onDone(View v);
	}

	private OnButtonClickListener onButtonClickListener;

	public void setOnButtonClickListener(OnButtonClickListener l)
	{
		onButtonClickListener = l;
	}
}
