package com.floney.floney.book.entity;

public enum ASSET {

    OUTCOME("지출"),
    INCOME("수입"),
    BANK("이체");
    private String kind;

    ASSET(String kind) {
        this.kind = kind;
    }

    public static boolean isOutcome(String asset){
        return OUTCOME.kind.equals(asset);
    }

    public static boolean isIncome(String asset){
        return INCOME.kind.equals(asset);
    }

}
