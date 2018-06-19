package com.hangyjx.syygzapp.model.okhttp;

/**
 * 服务器请求地址
 * Created by
 * on 2016/11/11
 */
public class RequestUrl {
    public final static String GET_SMS_CODE_URL=DataPost.BASE_URL+"/app/mobile/getVerifyCode.json";//获取短信验证码地址
    public final static String REGISTER_URL=DataPost.BASE_URL+"/user/register.json";//注册地址
    public final static String LOGIN_URL="http://172.22.39.103:10901/v1/oauth2/selectUserKey";//登录地址
    public final static String LOGIN_SIGN="http://172.22.39.103:10901/v2/oauth2/loginMoveshield";//验签
    public final static String UPDATE_APP="http://172.22.21.74:10806/Define/updateApkFile";//下载
    public final static String SET_NAME_URL=DataPost.BASE_URL+"/user/register/setUserInfo.json";//设置头像和昵称地址
    public final static String USER_INFO=DataPost.BASE_URL+"/user/info.json";//用户信息
    public final static String USER_INFO1=DataPost.BASE_URL+"/user/info.json";//用户信息
    public final static String GUIDE_GET=DataPost.BASE_URL+"/app/startPage.json";//启动页


    public final static String SEND_HUATI_URL=DataPost.BASE_URL+"/dui/topic/publishTopic.json";//发布话题地址
    public final static String ADD_LINK_URL=DataPost.BASE_URL+"/dui/topic/getUrlResult.json";//添加链接地址
    public final static String UPLOAD_HUATI_FILE_URL=DataPost.BASE_URL+"/dui/topic/publishTopicResource.json";//发布话题资源地址
    public final static String SEND_GUANDIAN_URL=DataPost.BASE_URL+"/dui/viewpoint/publishViewpoint.json";//发布观点地址
    public final static String SEND_GUANDIAN_FILE_URL=DataPost.BASE_URL+"/dui/viewpoint/publishViewpointResource.json";//发布观点资源地址
    public final static String GUANDIAN_DETAIL_URL=DataPost.BASE_URL+"/dui/viewpoint/viewpointDetail.json";//观点详情地址
    public final static String GUANDIAN_DETAIL_REPLY_LIST_URL=DataPost.BASE_URL+"/dui/viewpoint/viewpointReplyList.json";//观点详情回复列表地址
    public final static String GUANDIAN_DETAIL_NIU_LIST_URL=DataPost.BASE_URL+"/dui/viewpoint/viewpointDingList.json";//观点详情回复列表地址
    public final static String GUANDIAN_DETAIL_DUI_LIST_URL=DataPost.BASE_URL+"/dui/viewpoint/viewpointDuiList.json";//观点详情回复列表地址
    public final static String GUANDIAN_DETAIL_SELECT_URL=DataPost.BASE_URL+"/dui/reply/publishReplyDingAndDui.json";//观点详情选择牛或怼地址
    public final static String GUANDIAN_DETAIL_REPLY_URL=DataPost.BASE_URL+"/dui/reply/publishReply.json";//观点详情回复地址
    public final static String SHARE_LIST_URL=DataPost.BASE_URL+"/dui/topic/getPosterList.json";//分享加载海报列表地址
    public final static String SHARE_GET_PIC_URL=DataPost.BASE_URL+"/dui/topic/shareMyTopic.json";//分享获取分享图片地址
    public static final String APP_PATCH_URL=DataPost.BASE_URL +"/android_patch";//app补丁包下载路径


    public final static String GETTOPICLIST_URL=DataPost.BASE_URL+"/dui/topic/getTopicList.json";//怼列表
    public final static String GETTOPICDETAIL_URL=DataPost.BASE_URL+"/dui/topic/getTopicDetail.json";//怼详情
    public final static String GETTOPICNEWDUI_URL=DataPost.BASE_URL+"/dui/topic/getTopicNewDui.json";//话题详情最新观点列表
    public final static String GETBANNERLIST_URL=DataPost.BASE_URL+"/dui/topic/getBannerList.json";//轮播
    public final static String SELECTCAMP_URL=DataPost.BASE_URL+"/dui/topic/selectCamp.json";//话题正营选择
    public final static String PUBLISHREPLY_URL=DataPost.BASE_URL+"/dui/reply/publishReply.json";//发布回复
    public final static String PUBLISHREPLYDINGANDDUI_URL=DataPost.BASE_URL+"/dui/reply/publishReplyDingAndDui.json";//观点的牛核对
    public final static String MESSAGE_LIST_URL=DataPost.BASE_URL+"/msg/dui/list.json";//消息列表
    public final static String ALL_READ=DataPost.BASE_URL+"/msg/dui/readAll.json";//全部已读
    public final static String TEAM_LIST=DataPost.BASE_URL+"/user/team/list.json";//主队列表
    public final static String MY_SETTING=DataPost.BASE_URL+"/user/setting.json";//个人设置
    public final static String CHANGE_NAME=DataPost.BASE_URL+"/user/change.json";//修改昵称
    public final static String FORGET_PASS=DataPost.BASE_URL+"/user/forgetPassword.json";//忘记密码-验证手机号
    public final static String CHANGE_PASS=DataPost.BASE_URL+"/user/forgetPassword/reset.json";//忘记密码-修改密码
    public final static String COMMIT_KEFU=DataPost.BASE_URL+"/app/csSubmit.json";//联系客服提交
    public final static String MY_ALL_HUATI=DataPost.BASE_URL+"/user/topic/list.json";//我的所有话题
    public final static String MY_ALL_GUANDIAN=DataPost.BASE_URL+"/user/viewpoint/list.json";//我的所有观点
    public final static String CHANGE_MY_TEAM=DataPost.BASE_URL+"/user/team/change.json";//主队维护
    public final static String SINGLE_MESSAGE_REDAER=DataPost.BASE_URL+"/msg/dui/read.json";//消息已读
    public final static String CHECK_VERSION=DataPost.BASE_URL+"/app/checkupdate.json";//检查更新
    public final static String NEW_MESSAGE_COUNT=DataPost.BASE_URL+"/msg/dui/unreadCount.json";//未读消息数
    public final static String GET_KEFU_NUMBER=DataPost.BASE_URL+"/app/global/data.json";//全局数据

    public final static String GET_TOPIC_VIEW_POINT_SCREEN_URL=DataPost.BASE_URL+"/dui/topic/getTopicViewpointScreen.json";//怼列表
    public final static String USER_DEVICE_SAVE=DataPost.BASE_URL+"/user/device/save.json";//怼列表

    public final static String PUSHMESSAGE_CHECK=DataPost.BASE_URL+"/user/pushmessage/check.json";//就等
    /**
     * 预制内容
     */
    public static final String DUI_CAMP_PRESET = DataPost.BASE_URL+"/dui/camp/preset.json";
}
