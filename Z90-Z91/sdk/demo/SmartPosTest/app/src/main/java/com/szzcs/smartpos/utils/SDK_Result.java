package com.szzcs.smartpos.utils;

import android.content.Context;
import android.support.annotation.StringRes;

import com.szzcs.smartpos.R;
import com.zcs.sdk.SdkResult;
import com.zcs.sdk.card.CardInfoEntity;

/**
 * Created by yyzz on 2018/5/19.
 */

public class SDK_Result {

    public static String obtainCardInfo(Context context, CardInfoEntity... cardInfoEntitys) {
        StringBuilder stringBuilder = new StringBuilder();
        for (CardInfoEntity entity : cardInfoEntitys) {
            stringBuilder.append(obtainCardInfo(context, entity)).append("\n");
        }
        return stringBuilder.toString();
    }

    public static String obtainCardInfo(Context context, CardInfoEntity cardInfoEntity) {
        if (cardInfoEntity == null)
            return null;
        StringBuilder sb = new StringBuilder();
        sb.append("Resultcode:\t" + cardInfoEntity.getResultcode() + "\n")
                .append(cardInfoEntity.getCardExistslot() == null ? "" : "Card type:\t" + cardInfoEntity.getCardExistslot().name() + "\n")
                .append(cardInfoEntity.getCardNo() == null ? "" : "Card no:\t" + cardInfoEntity.getCardNo() + "\n")
                .append(cardInfoEntity.getRfCardType() == 0 ? "" : "Rf card type:\t" + cardInfoEntity.getRfCardType() + "\n")
                .append(cardInfoEntity.getRFuid() == null ? "" : "RFUid:\t" + new String(cardInfoEntity.getRFuid()) + "\n")
                .append(cardInfoEntity.getAtr() == null ? "" : "Atr:\t" + cardInfoEntity.getAtr() + "\n")
                .append(cardInfoEntity.getTk1() == null ? "" : "Track1:\t" + cardInfoEntity.getTk1() + "\n")
                .append(cardInfoEntity.getTk2() == null ? "" : "Track2:\t" + cardInfoEntity.getTk2() + "\n")
                .append(cardInfoEntity.getTk3() == null ? "" : "Track3:\t" + cardInfoEntity.getTk3() + "\n")
                .append(cardInfoEntity.getExpiredDate() == null ? "" : "expiredDate:\t" + cardInfoEntity.getExpiredDate() + "\n")
                .append(cardInfoEntity.getServiceCode() == null ? "" : "serviceCode:\t" + cardInfoEntity.getServiceCode());
        return sb.toString();
    }

    public static String obtainMsg(Context context, int resCode) {
        String msg = null;
        switch (resCode) {
            case SdkResult.SDK_ERROR:
                msg = appendMsg(context, resCode, R.string.SDK_ERROR);
                break;
            case SdkResult.SDK_PARAMERR:
                msg = appendMsg(context, resCode, R.string.SDK_PARAMERR);
                break;
            case SdkResult.SDK_TIMEOUT:
                msg = appendMsg(context, resCode, R.string.SDK_TIMEOUT);
                break;
            case SdkResult.SDK_RECV_DATA_ERROR:
                msg = appendMsg(context, resCode, R.string.SDK_RECV_DATA_ERROR);
                break;
            case SdkResult.SDK_ICC_BASE_ERR:
                msg = appendMsg(context, resCode, R.string.SDK_ICC_BASE_ERR);
                break;
            case SdkResult.SDK_ICC_ERROR:
                msg = appendMsg(context, resCode, R.string.SDK_ICC_ERROR);
                break;
            case SdkResult.SDK_ICC_PARAM_ERROR:
                msg = appendMsg(context, resCode, R.string.SDK_ICC_PARAM_ERROR);
                break;
            case SdkResult.SDK_ICC_NO_CARD:
                msg = appendMsg(context, resCode, R.string.SDK_ICC_NO_CARD);
                break;
            case SdkResult.SDK_ICC_NO_RESP:
                msg = appendMsg(context, resCode, R.string.SDK_ICC_NO_RESP);
                break;
            case SdkResult.SDK_ICC_COMM_ERR:
                msg = appendMsg(context, resCode, R.string.SDK_ICC_COMM_ERR);
                break;
            case SdkResult.SDK_ICC_RESP_ERR:
                msg = appendMsg(context, resCode, R.string.SDK_ICC_RESP_ERR);
                break;
            case SdkResult.SDK_ICC_NO_POWER_ON:
                msg = appendMsg(context, resCode, R.string.SDK_ICC_NO_POWER_ON);
                break;
            case SdkResult.SDK_RF_BASE_ERR:
                msg = appendMsg(context, resCode, R.string.SDK_RF_BASE_ERR);
                break;
            case SdkResult.SDK_RF_ERROR:
                msg = appendMsg(context, resCode, R.string.SDK_RF_ERROR);
                break;
            case SdkResult.SDK_RF_PARAM_ERROR:
                msg = appendMsg(context, resCode, R.string.SDK_RF_PARAM_ERROR);
                break;
            case SdkResult.SDK_RF_ERR_NOCARD:
                msg = appendMsg(context, resCode, R.string.SDK_RF_ERR_NOCARD);
                break;
            case SdkResult.SDK_RF_ERR_CARD_CONFLICT:
                msg = appendMsg(context, resCode, R.string.SDK_RF_ERR_CARD_CONFLICT);
                break;
            case SdkResult.SDK_RF_TIME_OUT:
                msg = appendMsg(context, resCode, R.string.SDK_RF_TIME_OUT);
                break;
            case SdkResult.SDK_RF_PROTOCOL_ERROR:
                msg = appendMsg(context, resCode, R.string.SDK_RF_PROTOCOL_ERROR);
                break;
            case SdkResult.SDK_RF_TRANSMISSION_ERROR:
                msg = appendMsg(context, resCode, R.string.SDK_RF_TRANSMISSION_ERROR);
                break;
            case SdkResult.SDK_MAG_BASE_ERR:
                msg = appendMsg(context, resCode, R.string.SDK_MAG_BASE_ERR);
                break;
            case SdkResult.SDK_MAG_ERROR:
                msg = appendMsg(context, resCode, R.string.SDK_MAG_ERROR);
                break;
            case SdkResult.SDK_MAG_PARAM_ERROR:
                msg = appendMsg(context, resCode, R.string.SDK_MAG_PARAM_ERROR);
                break;
            case SdkResult.SDK_MAG_NO_BRUSH:
                msg = appendMsg(context, resCode, R.string.SDK_MAG_NO_BRUSH);
                break;
            case SdkResult.SDK_PRN_BASE_ERR:
                msg = appendMsg(context, resCode, R.string.SDK_PRN_BASE_ERR);
                break;
            case SdkResult.SDK_PRN_ERROR:
                msg = appendMsg(context, resCode, R.string.SDK_PRN_ERROR);
                break;
            case SdkResult.SDK_PRN_PARAM_ERROR:
                msg = appendMsg(context, resCode, R.string.SDK_PRN_PARAM_ERROR);
                break;
            case SdkResult.SDK_PRN_STATUS_PAPEROUT:
                msg = appendMsg(context, resCode, R.string.SDK_PRN_STATUS_PAPEROUT);
                break;
            case SdkResult.SDK_PRN_STATUS_TOOHEAT:
                msg = appendMsg(context, resCode, R.string.SDK_PRN_STATUS_TOOHEAT);
                break;
            case SdkResult.SDK_PRN_STATUS_FAULT:
                msg = appendMsg(context, resCode, R.string.SDK_PRN_STATUS_FAULT);
                break;
            case SdkResult.SDK_PAD_BASE_ERR:
                msg = appendMsg(context, resCode, R.string.SDK_PAD_BASE_ERR);
                break;
            case SdkResult.SDK_PAD_ERR_NOPIN:
                msg = appendMsg(context, resCode, R.string.SDK_PAD_ERR_NOPIN);
                break;
            case SdkResult.SDK_PAD_ERR_CANCEL:
                msg = appendMsg(context, resCode, R.string.SDK_PAD_ERR_CANCEL);
                break;
            case SdkResult.SDK_PAD_ERR_EXCEPTION:
                msg = appendMsg(context, resCode, R.string.SDK_PAD_ERR_EXCEPTION);
                break;
            case SdkResult.SDK_PAD_ERR_TIMEOUT:
                msg = appendMsg(context, resCode, R.string.SDK_PAD_ERR_TIMEOUT);
                break;
            case SdkResult.SDK_PAD_ERR_NEED_WAIT:
                msg = appendMsg(context, resCode, R.string.SDK_PAD_ERR_NEED_WAIT);
                break;
            case SdkResult.SDK_PAD_ERR_DUPLI_KEY:
                msg = appendMsg(context, resCode, R.string.SDK_PAD_ERR_NEED_WAIT);
                break;
            case SdkResult.SDK_PAD_ERR_INVALID_INDEX:
                msg = appendMsg(context, resCode, R.string.SDK_PAD_ERR_NEED_WAIT);
                break;
            case SdkResult.SDK_PAD_ERR_NOTSET_KEY:
                msg = appendMsg(context, resCode, R.string.SDK_PAD_ERR_NEED_WAIT);
                break;
            default:
                msg = appendMsg(context, resCode, R.string.SDK_UNKNOWN_ERROR);
                break;
        }
        return msg;
    }

    public static String appendMsg(Context context, int code, @StringRes int id) {
        return appendMsg(code, context.getString(id));
    }

    public static String appendMsg(int code, String msg) {
        return appendMsg("Code", code + "", "Msg", msg);
    }

    public static String appendMsg(String... msg) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < msg.length; i += 2) {
            if (i == msg.length - 2) {
                sb.append(append(msg[i], msg[i + 1]));
                continue;
            }
            if (i == msg.length - 1) {
                sb.append(msg[i]);
                continue;
            }
            sb.append(append(msg[i], msg[i + 1]))
                    .append("\n");
        }
        return sb.toString();
    }

    public static String append(String title, String content) {
        return title + ":\t" + content;
    }
}
