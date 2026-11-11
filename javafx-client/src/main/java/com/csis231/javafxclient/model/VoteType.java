package com.csis231.javafxclient.model;

public enum VoteType {
    UPVOTE(1),
    DOWNVOTE(-1);

    private final int type;

    VoteType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}