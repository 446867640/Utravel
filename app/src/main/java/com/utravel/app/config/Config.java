package com.utravel.app.config;

import android.os.Environment;
import com.utravel.app.latte.Latte;

public class Config {
    public static final boolean ISDEBUG = true;
    public static final String DEBUG_BASE = "https://zhy-staging.igolife.net/api/v1/";
    public static final String RELEASE_BASE = "https://zhy.igolife.net/api/v1/";
    public static final String BASE = ISDEBUG ? DEBUG_BASE : RELEASE_BASE;
    public static final String AUTH_TOKEN = BASE + "auth_token";//登录
    public static final String SEND_SMS_URL = BASE + "sms_codes";//短信验证码
    public static final String REGISTER_URL = BASE + "member"; //注册
    public static final String DASHBOARD = BASE + "dashboard"; //个人中心
    public static final String PASSWORD = BASE + "password/reset"; //忘记密码
    public static final String CHANGE_PHONE = BASE + "mobile";
    public static final String UPLOAD_IMG_URL = BASE + "images";
    public static final String AVATAR = BASE + "avatar";
    public static final String CALCULATE_PROMOTIONS = BASE + "calculate_promotions"; //查看当前用户的预估收益
    public static final String SYSTEM_INVITATION_CODE = BASE + "get_system_invitation_code"; //系统邀请码
    public static final String WITHDRAWALS = BASE + "withdrawals"; //提现明细
    public static final String WITHDRAWALS_OVERVIEW = BASE + "withdrawals/overview"; //提现统计
    public static final String PRODUCT_CATEGORIES = BASE + "product_categories"; //商品分类
    public static final String PRODUCT_JD_SEARCH = BASE + "jingdong/products"; //京东商品搜索
    public static final String PRODUCT_JD_ORDER = BASE + "jingdong/orders"; //京东订单
    public static final String JD_PROMOTION_URL = BASE + "jingdong/promotion_url"; //拼多多商品搜索
    public static final String PRODUCT_PDD_SEARCH = BASE + "pinduoduo/goods_search"; //拼多多商品搜索
    public static final String PRODUCT_PDD_TOP = BASE + "pinduoduo/top_goods"; //拼多多热销榜商品
    public static final String PRODUCT_PDD_DETAIL = BASE + "pinduoduo/goods_detail"; //拼多多商品详情
    public static final String PRODUCT_PDD_COMMISSION_INFO = BASE + "pinduoduo/goods_commission_info"; //拼多多商品预估返余额数额
    public static final String PRODUCT_PDD_COMMISSION_URL = BASE + "pinduoduo/goods_promotion_url"; //拼多多推广链接
    public static final String PRODUCT_PDD_ORDER = BASE + "pinduoduo/orders"; //拼多多订单
    public static final String GET_RENZHENG_INFO = BASE + "certification_state";//查看个人认证情况
    public static final String PERSON_RENZHENG_URL = BASE + "identity_certification";//提交认证
    public static final String ALIPAY_USER_INFO1 = BASE + "alipay_user_info";//判断是否支付宝授权接口GET、换取授权访问令牌PATCH、解除支付宝保绑定接口DEL
    public static final String PUT_WX_CODE = BASE + "weixin_user_info";  //微信授权接口PUT
    public static final String ALIPAY_USER_INFO = BASE + "alipay_user_info/edit"; //支付宝授权所需参数接口GET
    public static final String CHANGE_PAY_PWD = BASE + "payment_password";//判断是否有支付密码GET、设置/重置支付密码PUT
    public static final String PUT_SHENFENZHENG = BASE + "payment_password/certified";//验证用户的身份证号码与已通过个人认证的身份是否一致接口PUT
    public static final String PUT_RESET_PAYPWD = BASE + "payment_password/reset"; //忘记支付密码重置接口PUT
    public static final String GET_SYSTEM_NOTIFICATIONS_LIST = BASE + "announcements"; //系统公告
    public static final String GET_PERSON_NOTIFICATIONS_LIST = BASE + "notifications"; //通知
    public static final String GET_NOTIFICATIONS_UNREAD = BASE + "notifications/unread_count"; //未读通知数
    public static final String PATCH_READ_NOTIFICATIONS = BASE + "notifications/"; //标记已读通知
    public static final String PATCH_ALLREAD_NOTIFICATIONS = BASE + "notifications/mark_all_as_read"; //标记全部已读通知
    public static final String OAUTH_WECHAT_LOGIN = BASE + "auth/wechat/callback"; //微信授权登陆
    public static final String GET_IN_OUT_DETAIL = BASE + "account_logs"; //收支明细接口GET
    public static final String BANNER_DATA = BASE + "ad_pictures";//banner轮播图
    public static final String PRODUCT_TB_ORDERS = BASE + "tbk/orders"; //淘客订单
    public static final String SHARE_PICTURE = BASE + "share_picture"; //二维码分享背景图接口GET
    public static final String YAOQINF_URL = BASE + "invitation_url"; //分享邀请用户注册URL接口GET
    public static final String GET_MY_TEAM_SEARCH = BASE + "downlines/search"; //我的团队搜索接口GET
    public static final String GET_MY_TEAM = BASE + "downlines"; //我的团队GET
    public static final String GET_FEED_READ = BASE + "feed_back_replies/";  //未读反馈信息已读接口PATCH
    public static final String GET_FEED = BASE + "feed_back_replies"; //未读反馈信息列表接口GET
    public static final String POST_FEED = BASE + "feed_backs"; //意见反馈信息提交接口POST
    public static final String TAOBAO_SEARCH = BASE + "tbk/goods_search"; //淘宝客搜索
    public static final String TAOBAO_ITEM_DETAIL = BASE + "tbk/goods_detail"; //淘宝客商品详情
    public static final String CALCULATE_DETAIL = BASE + "calculate_promotions/detail"; //预估收益明细
    public static final String INVITER = BASE + "inviter";//邀请我的人
    public static final String TREENODES = BASE + "inviter";//树状图
    public static final String GOODS_SEARCH = BASE + "products/search"; //商品搜索信息接口
    public static final String INVITATIONS = BASE + "invitations";//我邀请的人
    public static final String ADDRESS_INFO = BASE + "addresses";//收货地址
    public static final String GET_PRODUCTS = BASE + "products"; //首页商品列表接口GET
    public static final String GET_PRODUCTS_ID = BASE + "products/"; //首页商品详情接口GET
    public static final String SET_MOREN_ADDRESS = BASE + "addresses/"; //修改地址接口：修改地址信息PATCH、设置默认地址PATCH、删除地址DELETE
    public static final String AREAS_URL = BASE + "districts"; //省/市/区列表接口GET
    public static final String DING_DAN_DETAIL = BASE + "orders/new";//创建订单/去结算接口GET
    public static final String DING_DAN_LIST = BASE + "orders"; //订单列表接口GET、确认订单/去结算POST
    public static final String PUT_DINGDAN = BASE + "orders/"; //订单列表接口GET、确认订单/去结算POST
    public static final String GET_BANK_CARD = BASE + "bank_cards"; //银行卡列表接口
    public static final String SET_BANK_CARD = BASE + "bank_cards/"; //设置默认银行卡
    public static final String GET_PRODUCTS_CODE = BASE + "products/from_code"; //首页商品详情接口GET
    public static final String BALANCE_PAY = BASE + "balance_payments"; //我的余额支付接口POST
    public static final String WXPAY_PAY = BASE + "wxpay_payments"; //微信支付接口POST
    public static final String ALIPAY_PAY = BASE + "alipay_payments"; //支付宝支付接口POST
    public static final String PAY_TYPE = BASE + "payments/new"; //支付方式选择接口GET
    public static final String GET_SIGUNP = BASE + "signup_agreement"; //用户协议接口GET
    public static final String TREES = BASE + "trees"; //树状图商品列表接口
    public static final String TREES_ID = BASE + "trees/"; //树状图商品列表接口
    //*************************************响应码*************************************************
    public static final String ERROR401 = "您尚未登录，没有权限进行此项操作。请先登录。";
    public static final String ERROR_LOGIN_404 = "账户不存在";
    public static final String ERROR404 = "Not Found";
    public static final String TEAM_SEARCH_ERROR404 = "输入的手机号有误或团队成员不存在";
    public static final String CODE_ERROR404 = "无效的邀请码";
    public static final String YAOQINF_CODE_ERROR404 = "ResourceNotFound";
    //*************************************本地*************************************************
    //传入NewsActivity的key
    public static final String NEWS_KEY = "news";
    public static final String ANNOUNCEMENT = "announcement";
    public static final String SEARCH_DATA_KEY = "taobao_search";
    //分享商品接口数据
    public static final String SHARE_GOODS = BASE + "share_goods"; //商品详情数据
    //系统相机目录
    public static final String CAMERA_PHOTO_IDR = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath() + "/Camera/";
    //SD卡路径
    public static final String SDCARD_DIR = Environment.getExternalStorageDirectory().getPath();
    //PHOTO目录
    public static final String PHOTO_DIR = "/IGoPhoto/";
    public static final String PHOTO_AUTHORITIES = ".photpprovider";
    //淘客
    public static final String SIGN_METHOD_MD5 = "md5";
    public static final String SIGN_METHOD_HMAC = "hmac";
    public static final String CHARSET_UTF8 = "utf-8";
    //*************************************第三方*************************************************
    //阿里百川
    public static final String APPKEY_ALBC = "27888028";
    public static final String ADZONEID = "108323600067";
    public static final String PID = "mm_193690008_405550074_108323600067";
    public static final String SUB_PID = "mm_193690008_405550074_108323600067";
    //京东开普勒
    public static final String JD_KEPLER_APPKEY = "874a976d906a674382cf9f1c17777eaf";
    public static final String JD_KEPLER_KEYSECRET = "74f3b0e985354f8ebd9232c74cca8794";
    public static final String JD_KEPLER_MAIN_URL = "https://m.jd.com";
    public static final String JD_PIC_URL = "http://img10.360buyimg.com/n1/";
    //微信开放平台
    public static final String WE_CHAT_APP_ID = Config.WXSHARE_APP_ID;
    public static final String WE_CHAT_APP_SECRET = Config.WXSHARE_APP_WE_CHAT_APP_SECRET;
    //拼多多原始ID
    public static final String PINDUODUO_USER_NAME ="gh_0e7477744313";
    //I购优品原始ID
    public static final String IGOU_USER_NAME ="gh_00bc68e7159c";
    //淘客base的API
    public static final String BASE_TAOKE ="http://gw.api.taobao.com/router/rest";
    //腾讯Bugly
//    public static final String BUGLY_APP_ID = "bf9e8d1d00";
    //FIR版本更新url
//    public static final String FIR_TOKEN = "25446afbd1d19d063a9a5fa06372dec5";
//    public static final String FIR_APPID = "5d78ac6b23389f3620330b46";
//    public static final String FIR_API_URL ="https://api.fir.im/apps/latest/"+ FIR_APPID + "?api_token=" + FIR_TOKEN;
    //*************************************模板*********************************************
    public static final String APP = "Utravel";
    public static final String TAG_HX_YAOQINGMA = "invitation_code";
    //APP包名
    public static final String APP_PACKAGENAME = Latte.getApplicationContext().getPackageName();
    //APK服务器下载地址
    public static final String APK_DOWNLOAD_URL = "https://zhy.igolife.net/downloads/zhy-stable.apk";
    //APK目录
    public static final String APK_DIR = "/UtravelDownload/";
    //FIR版本更新url
    public static final String FIR_TOKEN = "25446afbd1d19d063a9a5fa06372dec5";
    public static final String FIR_APPID = "5dd256f123389f665807fbab";
    public static final String FIR_API_URL ="https://api.fir.im/apps/latest/"+ FIR_APPID + "?api_token=" + FIR_TOKEN;
    //腾讯Bugly
    public static final String BUGLY_APP_ID = "d3665f5ba1";
    //微信开放平台APP_ID（微信分享），平台可绑定小程序
    public static final String WX_APP_ID = "wx0708895ac6750af6";
    public static final String WXSHARE_APP_ID = "wx0708895ac6750af6";
    public static final String WXSHARE_APP_WE_CHAT_APP_SECRET = "e3709d3402d1a9e82cb386e59e4eac35";
    //极光推送
    public static final String JPUSH_APPKEY = "eece07732fcf3cb125caac4e";
    public static final String JPUSH_SECRET = "d2f1d5626941d04e96cf7175 ";
    //是否初始化tree_id
    public static final String IS_INIT_TREE_ID = "IS_INIT_TREE_ID";
}



