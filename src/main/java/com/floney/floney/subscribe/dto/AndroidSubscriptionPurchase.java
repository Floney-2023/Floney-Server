package com.floney.floney.subscribe.dto;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;
import com.google.api.services.androidpublisher.model.IntroductoryPriceInfo;
import com.google.api.services.androidpublisher.model.SubscriptionCancelSurveyResult;
import com.google.api.services.androidpublisher.model.SubscriptionPriceChange;

public class AndroidSubscriptionPurchase extends GenericJson {

    @Key
    private Boolean autoRenewing;
    @Key
    private String autoResumeTimeMillis;
    @Key
    private Integer cancelReason;
    @Key
    private SubscriptionCancelSurveyResult cancelSurveyResult;
    @Key
    private String countryCode;
    @Key
    private String developerPayload;
    @Key
    private String emailAddress;
    @Key
    private String expiryTimeMillis;
    @Key
    private String externalAccountId;
    @Key
    private String familyName;
    @Key
    private String givenName;
    @Key
    private IntroductoryPriceInfo introductoryPriceInfo;
    @Key
    private String kind;
    @Key
    private String linkedPurchaseToken;
    @Key
    private String obfuscatedExternalAccountId;
    @Key
    private String obfuscatedExternalProfileId;
    @Key
    private String orderId;
    @Key
    private int paymentState;
    @Key
    private String priceAmountMicros;
    @Key
    private SubscriptionPriceChange priceChange;
    @Key
    private String priceCurrencyCode;
    @Key
    private String profileId;
    @Key
    private String profileName;
    @Key
    private String promotionCode;
    @Key
    private Integer promotionType;
    @Key
    private Integer purchaseType;
    @Key
    private String startTimeMillis;
    @Key
    private String userCancellationTimeMillis;

    public AndroidSubscriptionPurchase() {
    }


    public Boolean getAutoRenewing() {
        return this.autoRenewing;
    }

    public AndroidSubscriptionPurchase setAutoRenewing(Boolean autoRenewing) {
        this.autoRenewing = autoRenewing;
        return this;
    }

    public Integer getCancelReason() {
        return this.cancelReason;
    }

    public AndroidSubscriptionPurchase setCancelReason(Integer cancelReason) {
        this.cancelReason = cancelReason;
        return this;
    }

    public SubscriptionCancelSurveyResult getCancelSurveyResult() {
        return this.cancelSurveyResult;
    }

    public AndroidSubscriptionPurchase setCancelSurveyResult(SubscriptionCancelSurveyResult cancelSurveyResult) {
        this.cancelSurveyResult = cancelSurveyResult;
        return this;
    }

    public String getCountryCode() {
        return this.countryCode;
    }

    public AndroidSubscriptionPurchase setCountryCode(String countryCode) {
        this.countryCode = countryCode;
        return this;
    }

    public String getDeveloperPayload() {
        return this.developerPayload;
    }

    public AndroidSubscriptionPurchase setDeveloperPayload(String developerPayload) {
        this.developerPayload = developerPayload;
        return this;
    }

    public String getEmailAddress() {
        return this.emailAddress;
    }

    public AndroidSubscriptionPurchase setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
        return this;
    }

    public String getExternalAccountId() {
        return this.externalAccountId;
    }

    public AndroidSubscriptionPurchase setExternalAccountId(String externalAccountId) {
        this.externalAccountId = externalAccountId;
        return this;
    }

    public String getFamilyName() {
        return this.familyName;
    }

    public AndroidSubscriptionPurchase setFamilyName(String familyName) {
        this.familyName = familyName;
        return this;
    }

    public String getGivenName() {
        return this.givenName;
    }

    public AndroidSubscriptionPurchase setGivenName(String givenName) {
        this.givenName = givenName;
        return this;
    }

    public IntroductoryPriceInfo getIntroductoryPriceInfo() {
        return this.introductoryPriceInfo;
    }

    public AndroidSubscriptionPurchase setIntroductoryPriceInfo(IntroductoryPriceInfo introductoryPriceInfo) {
        this.introductoryPriceInfo = introductoryPriceInfo;
        return this;
    }

    public String getKind() {
        return this.kind;
    }

    public AndroidSubscriptionPurchase setKind(String kind) {
        this.kind = kind;
        return this;
    }

    public String getLinkedPurchaseToken() {
        return this.linkedPurchaseToken;
    }

    public AndroidSubscriptionPurchase setLinkedPurchaseToken(String linkedPurchaseToken) {
        this.linkedPurchaseToken = linkedPurchaseToken;
        return this;
    }

    public String getObfuscatedExternalAccountId() {
        return this.obfuscatedExternalAccountId;
    }

    public AndroidSubscriptionPurchase setObfuscatedExternalAccountId(String obfuscatedExternalAccountId) {
        this.obfuscatedExternalAccountId = obfuscatedExternalAccountId;
        return this;
    }

    public String getObfuscatedExternalProfileId() {
        return this.obfuscatedExternalProfileId;
    }

    public AndroidSubscriptionPurchase setObfuscatedExternalProfileId(String obfuscatedExternalProfileId) {
        this.obfuscatedExternalProfileId = obfuscatedExternalProfileId;
        return this;
    }

    public String getOrderId() {
        return this.orderId;
    }

    public AndroidSubscriptionPurchase setOrderId(String orderId) {
        this.orderId = orderId;
        return this;
    }

    public int getPaymentState() {
        return this.paymentState;
    }

    public AndroidSubscriptionPurchase setPaymentState(Integer paymentState) {
        this.paymentState = paymentState;
        return this;
    }

    public SubscriptionPriceChange getPriceChange() {
        return this.priceChange;
    }

    public AndroidSubscriptionPurchase setPriceChange(SubscriptionPriceChange priceChange) {
        this.priceChange = priceChange;
        return this;
    }

    public String getPriceCurrencyCode() {
        return this.priceCurrencyCode;
    }

    public AndroidSubscriptionPurchase setPriceCurrencyCode(String priceCurrencyCode) {
        this.priceCurrencyCode = priceCurrencyCode;
        return this;
    }

    public String getProfileId() {
        return this.profileId;
    }

    public AndroidSubscriptionPurchase setProfileId(String profileId) {
        this.profileId = profileId;
        return this;
    }

    public String getProfileName() {
        return this.profileName;
    }

    public AndroidSubscriptionPurchase setProfileName(String profileName) {
        this.profileName = profileName;
        return this;
    }

    public String getPromotionCode() {
        return this.promotionCode;
    }

    public AndroidSubscriptionPurchase setPromotionCode(String promotionCode) {
        this.promotionCode = promotionCode;
        return this;
    }

    public Integer getPromotionType() {
        return this.promotionType;
    }

    public AndroidSubscriptionPurchase setPromotionType(Integer promotionType) {
        this.promotionType = promotionType;
        return this;
    }

    public Integer getPurchaseType() {
        return this.purchaseType;
    }

    public AndroidSubscriptionPurchase setPurchaseType(Integer purchaseType) {
        this.purchaseType = purchaseType;
        return this;
    }

    public String getExpiryTimeMillis() {
        return this.expiryTimeMillis;
    }

    public AndroidSubscriptionPurchase setStartTimeMillis(String startTimeMillis) {
        this.startTimeMillis = startTimeMillis;
        return this;
    }

    public AndroidSubscriptionPurchase set(String fieldName, Object value) {
        return (AndroidSubscriptionPurchase)super.set(fieldName, value);
    }

    public AndroidSubscriptionPurchase clone() {
        return (AndroidSubscriptionPurchase)super.clone();
    }
}
