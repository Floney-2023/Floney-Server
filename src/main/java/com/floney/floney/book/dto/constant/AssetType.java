package com.floney.floney.book.dto.constant;

import com.floney.floney.common.exception.NotFoundCategoryException;
import lombok.Getter;

@Getter
public enum AssetType {

    OUTCOME("지출"),
    INCOME("수입"),
    BANK("이체");

    private String kind;

    AssetType(String kind) {
        this.kind = kind;
    }

    public static AssetType find(String name) {
        for (AssetType assetType : AssetType.values()) {
            if (assetType.kind.equals(name)) {
                return assetType;
            }
        }
        throw new NotFoundCategoryException();
    }

    public static boolean isAssetType(String name) {
        for (AssetType assetType : AssetType.values()) {
            if (assetType.kind.equals(name)) {
                return true;
            }
        }
        return false;
    }

}
