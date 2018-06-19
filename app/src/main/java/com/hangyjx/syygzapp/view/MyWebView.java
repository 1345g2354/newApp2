package com.hangyjx.syygzapp.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hangyjx.syygzapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * This class serves as a WebView to be used in conjunction with a
 * VideoEnabledWebChromeClient. It makes possible: - To detect the HTML5 video
 * ended event so that the VideoEnabledWebChromeClient can exit full-screen.
 * 
 * Important notes: - Javascript is enabled by default and must not be disabled
 * with getSettings().setJavaScriptEnabled(false). - setWebChromeClient() must
 * be called before any loadData(), loadDataWithBaseURL() or loadUrl() method.
 * 
 * @author Cristian Perez (http://cpr.name)
 * 
 */
public class MyWebView extends WebView {

	public Context context;
	public int
			bac = 0,
			num = 0,
			num1 = 0;
	public View inflate;
	public ListView rec_view_comments;
	public boolean Scro2=false;
	public List<String> imgUrls;
	public int tiaozhuan = 0;
	public String
			urlAct,
			content_id = "",
			share_img_from_news="",
	        shareContent = "",
			shareTitle="",
	        wixinURL,
			currentUrl="";
	public boolean onNeedLoginBack=false,
	        isNeedLoginBu=false;
	public TextView txt_title;
	public int PicCount=0;
	public LinearLayout ll_listview;
	public View ll_refresh;

	private HashMap<String, Object> res;
	public Map<String, String> map;
	private boolean first;
	private boolean showOriginal;

	public RelativeLayout ll_bottom;
	public View head_2;
	public View inflatefoot;
	public ImageView add;
	public TextView activityRuleUrl;
	public String activityRuleUrl1;
	public String bAct;

	public ProgressBar progressBarGame;
	public String picId="";
	public float needZipSize=0;
	public boolean canShare=false;
	public String activityqq="";
	public List<String> picList=new ArrayList<>();

	private WebViewConnectActivityListener webViewConnectActivityListener;
	public class JavascriptInterface {
		@android.webkit.JavascriptInterface
		public void notifyVideoEnd() // Must match Javascript interface method
										// of VideoEnabledWebChromeClient
		{
			// This code is not executed in the UI thread, so we must force that
			// to happen
			new Handler(Looper.getMainLooper()).post(new Runnable() {
				@Override
				public void run() {
					if (videoEnabledWebChromeClient != null) {
						videoEnabledWebChromeClient.onHideCustomView();
					}
				}
			});
		}
	}

	private VideoEnabledWebChromeClient videoEnabledWebChromeClient;
	private boolean addedJavascriptInterface;

	public MyWebView(Context context) {
		super(context);
		this.context=context;
		addedJavascriptInterface = false;
	}

	public static ProgressBar progressBar;
	@SuppressWarnings("unused")
	public static boolean isAdd=false;
	public MyWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context=context;
		if(!isAdd){
			isAdd=true;
			progressBar = new ProgressBar(context, null,
					android.R.attr.progressBarStyleHorizontal);
			progressBar.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
					5, 0, 0));
		progressBar.setIndeterminate(false);
		progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.pregress_bg));
			Drawable drawable = getResources().getDrawable(R.drawable.pregress_bg);
//			drawable.setBounds(0,0,10,10);
			progressBar.setIndeterminateDrawable(drawable);
			addView(progressBar);
		}
		getSettings().setJavaScriptEnabled(true);
		addedJavascriptInterface = false;
		this.setWebViewClient(new MyWebViewClient());
		setWebChromeClient();
	}

	@SuppressWarnings("unused")
	public MyWebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		progressBar = new ProgressBar(context, null,
				android.R.attr.progressBarStyleHorizontal);
		progressBar.setMax(100);
		progressBar.setIndeterminate(false);
		progressBar.setProgressDrawable(getResources().getDrawable(R.drawable.pregress_bg));
		Drawable drawable = getResources().getDrawable(R.drawable.pregress_bg);
//		drawable.setBounds(0,0,10,10);
		progressBar.setIndeterminateDrawable(drawable);
		progressBar.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				5, 0, 0));
		addView(progressBar);
		getSettings().setJavaScriptEnabled(true);
		addedJavascriptInterface = false;
	}

	/**
	 * Indicates if the video is being displayed using a custom view (typically
	 * full-screen)
	 * @return true it the video is being displayed using a custom view
	 *         (typically full-screen)
	 */
	public boolean isVideoFullscreen() {
		return videoEnabledWebChromeClient != null
				&& videoEnabledWebChromeClient.isVideoFullscreen();
	}

	@Override
	public boolean onInterceptHoverEvent(MotionEvent event) {
		return super.onInterceptHoverEvent(event);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		return super.dispatchTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return super.onTouchEvent(event);
	}

	/**
	 * Pass only a VideoEnabledWebChromeClient instance.
	 */



	@Override
	@SuppressLint("SetJavaScriptEnabled")
	public void setWebChromeClient(WebChromeClient client) {
		getSettings().setJavaScriptEnabled(true);
		if (client instanceof VideoEnabledWebChromeClient) {
			this.videoEnabledWebChromeClient = (VideoEnabledWebChromeClient) client;
		}
		super.setWebChromeClient(client);
	}

	@Override
	public void loadData(String data, String mimeType, String encoding) {
		addJavascriptInterface();
		super.loadData(data, mimeType, encoding);
	}

	@Override
	public void loadDataWithBaseURL(String baseUrl, String data,
									String mimeType, String encoding, String historyUrl) {
		addJavascriptInterface();
//		super.loadDataWithBaseURL(baseUrl, data, mimeType, encoding, historyUrl);
	}

	@Override
	public void loadUrl(String url) {
		addJavascriptInterface();
		super.loadUrl(url);
	}

	@Override
	public void loadUrl(String url, Map<String, String> additionalHttpHeaders) {
		addJavascriptInterface();
		super.loadUrl(url, additionalHttpHeaders);
	}

	private void addJavascriptInterface() {
		if (!addedJavascriptInterface) {
			// Add javascript interface to be called when the video ends (must
			// be done before page load)
			addJavascriptInterface(new JavascriptInterface(),
					"_VideoEnabledWebView"); // Must match Javascript interface
												// name of
												// VideoEnabledWebChromeClient
			addedJavascriptInterface = true;
		}
	}

	public void setWebViewConnectActivityListener(WebViewConnectActivityListener webViewConnectActivityListener){
		this.webViewConnectActivityListener=webViewConnectActivityListener;
	}

	public void setWebChromeClient(View nonVideoLayout, ViewGroup videoLayout, View loadingView){
		VideoEnabledWebChromeClient	webChromeClient=new

				VideoEnabledWebChromeClient(nonVideoLayout,
						videoLayout, loadingView, this) // See all
				{
					@Override
					public void onGeolocationPermissionsShowPrompt (String
																			origin, GeolocationPermissions.Callback callback){
						callback.invoke(origin, true, false);
						super.onGeolocationPermissionsShowPrompt(origin, callback);
					}
					@Override
					public void onReceivedTitle (WebView view, String title){
						super.onReceivedTitle(view, title);
						if (!TextUtils.isEmpty(title)) {
							if (txt_title != null) {
								if (TextUtils.isEmpty(txt_title.getText().toString())) {
									txt_title.setVisibility(View.VISIBLE);
									if (title.length() <= 10) {

										txt_title.setText(title);
									} else {
										String substring = title.substring(0, 10);
										txt_title.setText(substring + "...");
									}
								}
                                shareTitle=title;
							}
						}
					}

					@Override
					public void onProgressChanged (WebView view, int progress){
						if(progressBarGame!=null) {
							if (progressBarGame != null && progress == 100) {

								progressBarGame.setVisibility(View.GONE);
							} else {
								MyWebView.isAdd = false;
								if (progressBarGame != null && progressBarGame.getVisibility() == View.GONE)
									progressBarGame.setVisibility(View.VISIBLE);
								if (progressBarGame != null) {
									progressBarGame.setProgress(progress);
								}
							}
						}else if (progress >= 60 && !first) {
							first = true;
							bac = 0;
							num = 1;
							showOriginal = true;//当后台不传opentype这个字段是，默认为显示原生
							if (currentUrl.contains("openType=simple")) {        //不显示原生，不显示底部评论栏
								showOriginal = false;
								//simple用另外一个webView去打开

//                mRecyclerViewAdapter=new NewsDetailCommentAdapter1(NewsDetailActivity.this,rec_view_comments,all_comments,NewsDetailActivity.this);
////                        new NewsDetailCommentAdapter1()
//                rec_view_comments.setAdapter(mRecyclerViewAdapter);
								if(ll_bottom!=null) {
									ll_bottom.setVisibility(View.GONE);
								}
								if(head_2!=null) {
									head_2.setVisibility(View.GONE);
								}

							} else if (currentUrl.contains("openType=detail")) {  //显示原生，显示底部评论栏
								showOriginal = true;
								if(webViewConnectActivityListener!=null){
									webViewConnectActivityListener.postCommentList(1);
									//加载评论的RecyclyerView
									//请求点赞总数
									webViewConnectActivityListener.requestAssociate();
									//请求相关新闻
									webViewConnectActivityListener.requestAdmireCount();
								}
                               if(rec_view_comments!=null) {
								   rec_view_comments.setVisibility(View.VISIBLE);
							   }
								if(inflate!=null) {
									inflate.setVisibility(View.VISIBLE);
								}
								if(head_2!=null) {
									head_2.setVisibility(View.VISIBLE);
								}
								if(ll_bottom!=null) {
									ll_bottom.setVisibility(View.VISIBLE);
								}

							} else if (currentUrl.contains("openType=sbnc")) {    //不显示原生评论，显示底部评论栏
								showOriginal = false;
								if(ll_bottom!=null) {
									ll_bottom.setVisibility(View.VISIBLE);
								}
//                mRecyclerViewAdapter=new NewsDetailCommentAdapter1(NewsDetailActivity.this,rec_view_comments,all_comments,NewsDetailActivity.this);
////                        new NewsDetailCommentAdapter1()
//                rec_view_comments.setAdapter(mRecyclerViewAdapter);
								if(inflatefoot!=null) {
									inflatefoot.setVisibility(View.GONE);//j脚步局
								}
								if(head_2!=null) {
									head_2.setVisibility(View.GONE);//头布局2
								}
								if(inflate!=null) {
									inflate.setVisibility(View.GONE);//头布局1
								}
								if(rec_view_comments!=null) {
									rec_view_comments.setVisibility(View.GONE);//列表
								}
								if(ll_bottom!=null) {
									ll_bottom.setVisibility(View.VISIBLE);//底部评论
								}


							} else if (currentUrl.contains("openType=nbsc")) {    //显示原生，不显示底部评论栏
								showOriginal = true;
								if(webViewConnectActivityListener!=null){
									webViewConnectActivityListener.postCommentList(1);
									//加载评论的RecyclyerView
									//请求点赞总数
									webViewConnectActivityListener.requestAssociate();
									//请求相关新闻
									webViewConnectActivityListener.requestAdmireCount();
								}
                                if(rec_view_comments!=null) {
									rec_view_comments.setVisibility(View.VISIBLE);
									rec_view_comments.setVisibility(View.VISIBLE);
								}
								if(inflate!=null) {
									inflate.setVisibility(View.VISIBLE);
								}
								if(head_2!=null) {
									head_2.setVisibility(View.VISIBLE);
								}
								if(ll_bottom!=null) {
									ll_bottom.setVisibility(View.GONE);
								}
							} else {
//                mRecyclerViewAdapter=new NewsDetailCommentAdapter1(NewsDetailActivity.this,rec_view_comments,all_comments,NewsDetailActivity.this);
////                        new NewsDetailCommentAdapter1()
//                rec_view_comments.setAdapter(mRecyclerViewAdapter);
								if(webViewConnectActivityListener!=null) {
									webViewConnectActivityListener.postCommentList(1);
								}
							}
                           if(add!=null) {
							   if (canShare) {
								   add.setVisibility(View.VISIBLE);
							   } else {
								   add.setVisibility(View.GONE);
							   }
						   }

							if (currentUrl.contains("activityRuleUrl")) {
								if(activityRuleUrl!=null) {
									activityRuleUrl.setVisibility(View.VISIBLE);
								}
								String[] split = currentUrl.split("&");
								if (split != null) {
									for (String s : split) {
										if (s.contains("activityRuleUrl")) {
											int indexof = s.indexOf('=');
											activityRuleUrl1 = s.substring(indexof + 1);
										}
									}
								}

							} else {
								if(activityRuleUrl!=null) {
									activityRuleUrl.setVisibility(View.GONE);
								}
							}

                            if(webViewConnectActivityListener!=null) {
								if (showOriginal) {
									webViewConnectActivityListener.hideCommentAssociate(View.VISIBLE);
								} else {
									webViewConnectActivityListener.hideCommentAssociate(View.GONE);
								}
							}
						}
						super.onProgressChanged(view, progress);
					}
				}

		;
		webChromeClient
				.setOnToggledFullscreen(new VideoEnabledWebChromeClient.ToggledFullscreenCallback()

										{

											@Override
											public void toggledFullscreen ( boolean fullscreen){
												if (fullscreen) {
													WindowManager.LayoutParams attrs = ((Activity)context).getWindow()
															.getAttributes();
													attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
													attrs.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
													((Activity)context).getWindow().setAttributes(attrs);
													if (android.os.Build.VERSION.SDK_INT >= 14) {
														((Activity)context).getWindow()
																.getDecorView()
																.setSystemUiVisibility(
																		View.SYSTEM_UI_FLAG_LOW_PROFILE);
													}
													((Activity)context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
												} else {
													WindowManager.LayoutParams attrs = ((Activity)context).getWindow()
															.getAttributes();
													attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
													attrs.flags &= ~WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
													((Activity)context).getWindow().setAttributes(attrs);
													if (android.os.Build.VERSION.SDK_INT >= 14) {
														((Activity)context).getWindow().getDecorView()
																.setSystemUiVisibility(
																		View.SYSTEM_UI_FLAG_VISIBLE);
													}
													((Activity)context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
												}

											}
										}

				);
		this.setWebChromeClient(webChromeClient);
	}

	public void setWebChromeClient(){
		VideoEnabledWebChromeClient webChromeClient=new VideoEnabledWebChromeClient(this){
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				if(progressBar!=null) {
					if (newProgress == 100) {
						view.getSettings().setBlockNetworkImage(false);

						progressBar.setVisibility(View.GONE);
					} else {
						if (progressBar.getVisibility() == View.GONE){
							progressBar.setVisibility(View.VISIBLE);
						}
						progressBar.setProgress(newProgress);
					}
				}
				super.onProgressChanged(view, newProgress);
			}
		};
		this.setWebChromeClient(webChromeClient);
	}

	private class MyWebViewClient extends WebViewClient  {
		boolean isError = false;
		private String contentId, title, descript, miaoshu, taitou;
		private int commentCount, isDetail;
		private String third_source;
		private boolean isThirdPartyLoginSameUser;


		@Override
		public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
			// TODO Auto-generated method stub

			return super.shouldInterceptRequest(view, url);
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
				return true;
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			view.getSettings().setBlockNetworkImage(false); //将图片下载阻塞
			bac = 0;
			num = 1;

		}
		@Override
		public void onReceivedError(WebView view, int errorCode,
									String description, String failingUrl) {
			try{
				isError = true;
//				VideoEnabledWebView.this.setVisibility(View.GONE);
//				if(ll_listview!=null) {
//					ll_listview.setVisibility(View.GONE);
//				}
//				if(ll_refresh!=null) {
//					ll_refresh.setVisibility(View.VISIBLE);
//				}
			}catch (Exception e){

			}
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);

		}

	}

	private Handler mhandler = new Handler() {
		public void handleMessage(Message msg) {
//			switch (msg.what) {
//				case Constant.HTML_REQUEST_IDENTIFY://实名认证
//					BaseDataBean<UserBaseInfo> userCertState = SharedPreferencesUtil.getUserCertState(context);
//					if (userCertState != null) {
//						if (userCertState.data != null) {
//							int a = userCertState.data.certStatus;
//							if (a == 1) {
//								VideoEnabledWebView.this.loadUrl("javascript:needIdentity(" + String.valueOf(a) + ")");
//							} else {
//								if (!TextUtils.isEmpty(SharedPreferencesUtil.getLastMobile(context))) {
//									Intent intent = new Intent(context, UserCertificationActivityConcise.class);
//									intent.putExtra("the_mobilephone", SharedPreferencesUtil.getLastMobile(context));
//									Constant.HTML_REQUEST_ACTION = Constant.HTML_REQUEST_IDENTIFY;
//									context.startActivity(intent);
//								} else {
//									Intent intent = new Intent(context, UserCertificationActivity.class);
//									Constant.HTML_REQUEST_ACTION = Constant.HTML_REQUEST_IDENTIFY;
//									context.startActivity(intent);
//								}
//							}
//						} else {
//							if (!TextUtils.isEmpty(SharedPreferencesUtil.getLastMobile(context))) {
//								Intent intent = new Intent(context, UserCertificationActivityConcise.class);
//								intent.putExtra("the_mobilephone", SharedPreferencesUtil.getLastMobile(context));
//								Constant.HTML_REQUEST_ACTION = Constant.HTML_REQUEST_IDENTIFY;
//								context.startActivity(intent);
//							} else {
//								Intent intent = new Intent(context, UserCertificationActivity.class);
//								Constant.HTML_REQUEST_ACTION = Constant.HTML_REQUEST_IDENTIFY;
//								context.startActivity(intent);
//							}
//						}
//					}
//					break;
//				case Constant.HTML_REQUEST_NAV_BAR_TITLE:  //设置标题
//					if (!TextUtils.isEmpty((CharSequence) msg.obj)) {
//						String setTitle = (String) msg.obj;
//						if (setTitle.length() > 10) {
//							txt_title.setText(setTitle.substring(0, 10) + "...");
//						} else {
//							txt_title.setText((CharSequence) msg.obj);
//						}
//					}
//					break;
//				case Constant.HTML_NOT_LOGIN:  //
//					if (msg.obj.equals("HTML_REQUEST_IDENTIFY")) {
//						VideoEnabledWebView.this.loadUrl("javascript:needBindPhone(-1)");
//					} else if (msg.obj.equals("identity_not_login")) {
//						VideoEnabledWebView.this.loadUrl("javascript:needIdentity(-1)");
//					}
//					break;
//				case Constant.HTML_REQUEST_GETLOCATION:
//					if (MainActivity.mm.latitude != 0 && MainActivity.mm.longitude != 0) {
////                            function getLocationAndroid(latitude, longitude)
//
//						VideoEnabledWebView.this.loadUrl("javascript:getLocationAndroid(" + MainActivity.mm.latitude + ","+MainActivity.mm.longitude +")");
//					}
//					break;
//				case Constant.ON_ONEKEYSHARE_SHARE_SUCCESS:
//					Toast.makeText(context,"分享成功", Toast.LENGTH_SHORT).show();
//					break;
//			}
//			;
		}

		;
	};

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if(webViewConnectActivityListener!=null) {
				webViewConnectActivityListener.share((String) (msg.obj));
			}
		};
	};
	public interface WebViewConnectActivityListener{
		public void postCommentList(final int sort) ;
		public void requestAssociate();
		public void requestAdmireCount();
		public void hideCommentAssociate(int state);
		public void payUrl(String url);
		public void webViewPostRequest(final String url,
                                       final HashMap<String, Object> requestParams, final String requestTag);
		public void share(String string);
		public void orderPayNow(String orderNum, float totalprice1);
	}

	/**
	 * 显示未认证对话框
	 */
	public void showUnIdentiyDialog(String msg){
//		CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
//		customBuilder.setMessage(msg)
//				.setPositiveButton("去认证", new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						if (!TextUtils.isEmpty(SharedPreferencesUtil.getLastMobile(context))) {
//							Intent intent = new Intent(context, UserCertificationActivityConcise.class);
//							intent.putExtra("the_mobilephone", SharedPreferencesUtil.getLastMobile(context));
//							context.startActivity(intent);
//						} else {
//							Intent intent = new Intent(context, UserCertificationActivity.class);
//							context.startActivity(intent);
//						}
//						dialog.dismiss();
//					}
//				})
//				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						dialog.dismiss();
//					}
//				});
//		CustomDialog customDialog = customBuilder.create();
//		customDialog.setCancelable(true);
//		customDialog.setCanceledOnTouchOutside(true);
//		customDialog.show();

	}
}