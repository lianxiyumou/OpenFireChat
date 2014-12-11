package com.example.openfirechat.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.example.openfirechat.R;
import com.example.openfirechat.model.EmojiInfo;
import com.example.openfirechat.util.AndroidUtil;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


/**
 * 
 * 表情主控件
 * 
 * @author Zhengcw
 * 
 *         2013-09-16
 * 
 */
public class EmojiView extends RelativeLayout {
	private ViewPager mViewPager;
	private CircleView mCircleView;
	private GridView mGridView;
	private Context mContext;
	private int mMsgType = 0;
	private CallBack mCallBack;
	private ArrayList<EmojiInfo> emojiInfos;
	private int mTabIndex = 0;
	private static int VIEW_PAGER_HEIGTH_INCLUD_MARGIN = 160;
	private static int VIEW_PAGER_HEIGTH = 120;
	private static int TAB_VIEW_HEIGTH = 20;
	private static int TAB_VIEW_WIDTH = 30;
	private static int SLIDING_VIEW_HEIGTH = 15;

	public EmojiView(Context context) {
		super(context);
		init(context);
	}

	public EmojiView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public EmojiView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {
		this.mContext = context;
		View layoutView = LayoutInflater.from(context).inflate(R.layout.emoji_view_layout, this);
		mViewPager = (ViewPager) layoutView.findViewById(R.id.emiji_view_pager);
		mViewPager.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, AndroidUtil.dip2px(mContext,
				VIEW_PAGER_HEIGTH_INCLUD_MARGIN)));
		mCircleView = (CircleView) layoutView.findViewById(R.id.emoji_sliding_view);
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
				AndroidUtil.dip2px(mContext, SLIDING_VIEW_HEIGTH));
		params.addRule(CENTER_IN_PARENT);
		params.addRule(BELOW, mViewPager.getId());
		mCircleView.setLayoutParams(params);
		mGridView = (GridView) layoutView.findViewById(R.id.emoji_choose_grid_view);

	}

	/**
	 * 设置控件的原始数据
	 * 
	 */
	public void setEmojiData(ArrayList<EmojiInfo> emojiDataList) {
		this.emojiInfos = emojiDataList;
		if (emojiInfos != null && emojiInfos.size() > 0) {
			int chooseTabSize = emojiInfos.size();
			mGridView.setLayoutParams(new LinearLayout.LayoutParams(AndroidUtil.dip2px(mContext, TAB_VIEW_WIDTH) * 2
					* chooseTabSize, LinearLayout.LayoutParams.WRAP_CONTENT));
			mGridView.setNumColumns(chooseTabSize);
			mGridView.setAdapter(new ChooseTabAdapter(mContext, emojiInfos));
			EmojiInfo info = emojiInfos.get(0);
			mMsgType = info.getEmojiType();
			initData(info.getEmojiData(), info.getEmojiColumns(), info.getEmojiLines());
		}
	}

	/**
	 * 
	 * 显示对应的表情类型
	 * 
	 * 静态表情
	 * 
	 */
	private void initData(ArrayList<HashMap<String, Object>> data, int columns, int line) {
		List<GridView> images = new ArrayList<GridView>();
		int itemtCount = columns * line;
		int emojiCount = itemtCount - 1;
		int mSize = (data.size() / emojiCount) + (data.size() % emojiCount > 0 ? 1 : 0);
		mCircleView.initCircle(mSize);
		for (int i = 0; i < mSize; i++) {
			GridView gridView = new GridView(mContext);
			gridView.setNumColumns(columns);
			gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
			ArrayList<HashMap<String, Object>> tempData = new ArrayList<HashMap<String, Object>>();
			tempData.clear();
			if (i < mSize - 1) {
				tempData = new ArrayList<HashMap<String, Object>>();
				for (int j = i * emojiCount; j < i * emojiCount + emojiCount; j++) {
					tempData.add(data.get(j));
				}
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("imageItem", R.drawable.emoji_delete_icon);
				map.put("textItem", "删除");
				tempData.add(map);
			} else {
				tempData = new ArrayList<HashMap<String, Object>>();
				int result = data.size() % emojiCount;
				int count;
				if (result != 0) {
					count = data.size() + emojiCount - result;
					for (int j = i * emojiCount; j < count; j++) {
						if (j < data.size()) {
							tempData.add(data.get(j));
						} else {
							tempData.add(null);
						}
					}
				} else {
					count = data.size();
					for (int j = i * emojiCount; j < count; j++) {
						tempData.add(data.get(j));
					}
				}

				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("imageItem", R.drawable.emoji_delete_icon);
				map.put("textItem", "删除");
				tempData.add(map);
			}
			EmojiAdapter adapter = new EmojiAdapter(mContext, tempData, line, columns);
			gridView.setAdapter(adapter);
			images.add(gridView);
		}
		mCircleView.chooseCircle(0);
		mViewPager.setAdapter(new ViewPAgerAdapter(mContext, images));

		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				mCircleView.chooseCircle(position);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
	}

	/**
	 * 
	 * 显示对应的表情类型
	 * 
	 * 动态表情
	 * 
	 */
	private void initDataForAnimation(ArrayList<HashMap<String, Object>> data, int columns, int line) {
		List<GridView> images = new ArrayList<GridView>();
		int itemtCount = columns * line;
		int size = (data.size() / itemtCount) + (data.size() % itemtCount > 0 ? 1 : 0);
		mCircleView.initCircle(size);
		for (int i = 0; i < size; i++) {
			GridView gridView = new GridView(mContext);
			gridView.setNumColumns(columns);
			gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
			ArrayList<HashMap<String, Object>> tempData = new ArrayList<HashMap<String, Object>>();
			tempData.clear();
			if (i < size - 1) {
				tempData = new ArrayList<HashMap<String, Object>>();
				for (int j = i * itemtCount; j < i * itemtCount + itemtCount; j++) {
					tempData.add(data.get(j));
				}
			} else {
				tempData = new ArrayList<HashMap<String, Object>>();
				for (int j = i * itemtCount; j < data.size(); j++) {
					tempData.add(data.get(j));
				}
			}
			EmojiAdapter adapter = new EmojiAdapter(mContext, tempData, line, columns);
			gridView.setAdapter(adapter);
			images.add(gridView);
		}
		mCircleView.chooseCircle(0);
		mViewPager.setAdapter(new ViewPAgerAdapter(mContext, images));

		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				mCircleView.chooseCircle(position);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
	}

	/**
	 * 表情容器 adapter
	 * 
	 * @author Zhengcw
	 * 
	 */
	private class ViewPAgerAdapter extends PagerAdapter {

		private Context mContext;
		private List<GridView> mGridViewList;

		public ViewPAgerAdapter(Context context, List<GridView> list) {
			this.mContext = context;
			this.mGridViewList = list;
		}

		@Override
		public int getCount() {
			return mGridViewList.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(mGridViewList.get(position));
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			GridView mGridView = mGridViewList.get(position);
			container.addView(mGridView);
			return mGridView;
		}

	}

	/**
	 * 表情类型选择栏 adapter
	 * 
	 * @author Zhengcw
	 * 
	 */
	private class ChooseTabAdapter extends BaseAdapter {
		private Context mContext;
		private ArrayList<EmojiInfo> list;

		public ChooseTabAdapter(Context context, ArrayList<EmojiInfo> emojiList) {
			this.mContext = context;
			this.list = emojiList;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		public void notifyAdapter() {
			notifyDataSetChanged();
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ImageView imageView;
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(R.layout.custom_emoji_item, null);
				imageView = (ImageView) convertView.findViewById(R.id.facedialog_ItemImage);
				LayoutParams params = new LayoutParams(AndroidUtil.dip2px(mContext, TAB_VIEW_WIDTH), AndroidUtil.dip2px(
						mContext, TAB_VIEW_HEIGTH));
				params.addRule(CENTER_IN_PARENT);
				params.topMargin = AndroidUtil.dip2px(mContext, 15);
				params.bottomMargin = AndroidUtil.dip2px(mContext, 15);
				imageView.setScaleType(ScaleType.CENTER_INSIDE);
				imageView.setLayoutParams(params);

				convertView.setTag(imageView);
			} else {
				imageView = (ImageView) convertView.getTag();
			}

			if (mTabIndex == position) {
				convertView.setBackgroundResource(R.drawable.emoji_choose_tab_pressed_bg);
			} else {
				convertView.setBackgroundResource(R.drawable.selector_emoji_tab_bg);
			}
			int id = (Integer) list.get(position).getTabIconId();
			imageView.setImageResource(id);

			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					if (mTabIndex != position) {
						mTabIndex = position;
						EmojiInfo info = list.get(position);
						mMsgType = info.getEmojiType();
						notifyAdapter();
						if (mMsgType == 1) {
							initData(info.getEmojiData(), info.getEmojiColumns(), info.getEmojiLines());
						} else {
							initDataForAnimation(info.getEmojiData(), info.getEmojiColumns(), info.getEmojiLines());
						}
					}
				}
			});
			return convertView;
		}
	}

	/**
	 * 
	 * 表情 adapter
	 * 
	 */
	private class EmojiAdapter extends BaseAdapter {
		private Context mContext;
		private ArrayList<HashMap<String, Object>> mList;
		private int itemlines;
		private int itemcolumns;
		private int itemWidth;
		private int margin;
		private int count;

		public EmojiAdapter(Context context, ArrayList<HashMap<String, Object>> data, int lines, int columns) {
			this.mContext = context;
			this.mList = data;
			this.itemlines = lines;
			this.itemcolumns = columns;
			itemWidth = AndroidUtil.dip2px(mContext, VIEW_PAGER_HEIGTH) / itemlines;
			margin = (AndroidUtil.dip2px(mContext, VIEW_PAGER_HEIGTH_INCLUD_MARGIN) - AndroidUtil.dip2px(mContext,
					VIEW_PAGER_HEIGTH)) / lines;
			count = itemcolumns * itemlines - 1;
		}

		@Override
		public int getCount() {
			return mList.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ImageView imageView;
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(R.layout.custom_emoji_item, null);
				imageView = (ImageView) convertView.findViewById(R.id.facedialog_ItemImage);
				LayoutParams params = new LayoutParams(itemWidth, itemWidth);
				params.addRule(CENTER_IN_PARENT);
				params.topMargin = margin;
				params.leftMargin = margin / 2;
				params.rightMargin = margin / 2;
				imageView.setScaleType(ScaleType.CENTER_INSIDE);
				imageView.setLayoutParams(params);

				convertView.setTag(imageView);
			} else {
				imageView = (ImageView) convertView.getTag();
			}

			HashMap<String, Object> map = mList.get(position);
			if (map != null) {
				int id = (Integer) map.get("imageItem");
				imageView.setImageResource(id);
				convertView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						if (mCallBack != null) {
							if (mMsgType != 1) {
								mCallBack.getEmojiContext(mMsgType, mList.get(position).get("textItem").toString(),
										mList.get(position).get("name").toString(),
										(Integer) mList.get(position).get("imageItem"));
							} else {
								if (position > 0 && position % count == 0) {
									mCallBack.deleteFuntion();
								} else {
									mCallBack.getEmojiContext(mMsgType, mList.get(position).get("textItem").toString(),
											"", (Integer) mList.get(position).get("imageItem"));
								}
							}
						}
					}
				});
			} else {
				imageView.setBackgroundColor(getResources().getColor(android.R.color.transparent));
			}

			return convertView;
		}
	}

	public void setListener(CallBack callBack) {
		this.mCallBack = callBack;
	}

	/**
	 * 
	 * 选择表情后的回调函数
	 * 
	 * @author Administrator
	 * 
	 */
	public interface CallBack {
		public void getEmojiContext(int MsgType, String MsgContext, String sourceName, int coverId);

		public void deleteFuntion();
	}
}
