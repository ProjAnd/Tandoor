package tandoori.resturant.mobile.utils;


import tandoori.resturant.mobile.volly.AppController;

public class BaseUrl {
    public static final int handlerTime = 3000;
    public static final String OTP_DELIMITER = ":";
    public static final String MERCHANT_DETAILS = AppController.getInstance().useString("STATIC_RESOURCE_ENDPOINT") +
            AppController.getInstance().useString("MERCHANT_ID") + AppController.getInstance().useString("STATIC_RESOURCE_SUFFIX");
    public static String BASEURL = "http://api.restaurantbite.com/";
    //public static String BASE_URL = "http://server.avrsh.in/tiffinsorderonline/wp-json/rest/v1/";
    //    public static final String CATEGORIES_PRODUCTS = BASE_URL + "categories_products";
    //  public static final String CATEGORIES = BASE_URL + "categories";
    //  public static final String REGISTERUSER = BASE_URL + "user";
    public static final String LOGOUTUSER = BASEURL + "user/logout";
    // Delete Api
    public static final String REMOVECART = BASEURL + "api/v1/users/business/bucket?";
    public static final String REMOVECARTITEM = BASEURL + "api/v1/users/business/bucket/item?";

    //Get APIs
    public static final String DELETEADDRESS = BASEURL + "api/v1/users/address?";
    public static final String MERCHANT = BASEURL + "api/v1/merchants/config?";
    public static final String ORDERHISTORY = BASEURL + "api/v1/users/business/order/history?";
    public static final String GETADDRESS = BASEURL + "api/v1/users/addresses?";
    public static final String COUNTRY = BASEURL + "api/v1/enterprised/countries?";
    public static final String STATES = BASEURL + "api/v1/enterprised/countries/states?";
    public static final String DCI = BASEURL + "api/v1/users/business/bucket/dci?";
    public static final String PROFILE = BASEURL + "api/v1/users/profile?";
    //Post API
    public static final String LOGINUSER = BASEURL + "security/session/users/login";
    public static final String UPDATEQTY = BASEURL + "api/v1/users/business/bucket/update/item/qty?";
    public static final String CREATEANDUPDATE = BASEURL + "api/v1/users/address?";
    public static final String ADDTOCART = BASEURL + "api/v1/users/business/bucket/item?";
    public static final String REGISTER = BASEURL + "api/v1/users/register?";
    public static final String GETMERCHANTTOKEN = BASEURL + "security/session/merchants";
    public static final String OTP = BASEURL + "security/session/users/verify/otp";
    public static final String COUPON = BASEURL + "api/v1/users/business/bucket/apply_coupon?";
    public static final String REMOVECOUPON = BASEURL + "api/v1/users/business/bucket/remove_coupon?";
    public static final String PLACEORDER = BASEURL + "api/v1/users/business/order/payment/checkout?";
    public static final String UPDATE_SHIPPING_METHODS = BASEURL + "api/v1/users/business/bucket/update_shipping_method?";
    public static final String UPDATE_TIP = BASEURL + "api/v1/users/business/bucket/custom_taxrate?";
    public static final String PAYMENT_STATUS = BASEURL + "api/v1/users/business/order/payment/status?";
}