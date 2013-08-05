package dolphin.android.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.ActionBar;

import dolphin.android.libs.R;

public class DoneDiscardActionBar extends View
{
	ActionBar mActionBar;

	//	public DoneDiscardActionBar(Context context, AttributeSet attrs,
	//			int defStyle)
	//	{
	//		super(context, attrs, defStyle);
	//		// TODO Auto-generated constructor stub
	//	}
	//
	//	public DoneDiscardActionBar(Context context, AttributeSet attrs)
	//	{
	//		super(context, attrs);
	//		// TODO Auto-generated constructor stub
	//	}
	//
	//	public DoneDiscardActionBar(Context context)
	//	{
	//		super(context);
	//		// TODO Auto-generated constructor stub
	//	}

	public DoneDiscardActionBar(Context context, ActionBar actionBar)
	{
		super(context);
		mActionBar = actionBar;

		LayoutInflater inflater =
			(LayoutInflater) mActionBar.getThemedContext().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		final View customActionBarView = inflater.inflate(
			R.layout.actionbar_custom_view_done_discard, null);

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

		customActionBarView.findViewById(R.id.actionbar_discard)
				.setOnClickListener(
					new View.OnClickListener() {
						@Override
						public void onClick(View v)
						{
							if (onButtonClickListener != null)
								onButtonClickListener.onDiscard(v);
						}
					});

		// Show the custom action bar view and hide the normal Home icon and title.
		//final ActionBar actionBar = getActionBar();
		mActionBar.setDisplayOptions(
			ActionBar.DISPLAY_SHOW_CUSTOM,
			ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME
				| ActionBar.DISPLAY_SHOW_TITLE);

		mActionBar.setCustomView(customActionBarView,
			new ActionBar.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.MATCH_PARENT));
	}

	public interface OnButtonClickListener
	{
		public void onDone(View v);

		public void onDiscard(View v);
	}

	private OnButtonClickListener onButtonClickListener;

	public void setOnButtonClickListener(OnButtonClickListener l)
	{
		onButtonClickListener = l;
	}

}
