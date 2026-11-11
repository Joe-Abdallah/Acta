package com.csis231.springpostgrescrud.entity;
/**
 * Represents the direction of a vote — either an upvote or a downvote.
 *
 * Each constant carries a numeric type value (+1 or -1) that enables
 * arithmetic in the service layer. This value is
 * used when adjusting the cached voteCount on a Post or Comment.
 *
 * Stored as a string in the database via @Enumerated(EnumType.STRING),
 * so the DB column contains "UPVOTE" or "DOWNVOTE" rather than 0 or 1.
 */
public enum VoteType {

    /** An upvote. Contributes +1 to the vote count. */
    UPVOTE(1),

    /** A downvote. Contributes -1 to the vote count. */
    DOWNVOTE(-1);

    /**
     * The numeric direction of this vote type.
     * UPVOTE} = +1, DOWNVOTE = -1.
     * Used by the service layer to compute vote count.
     * post.setVoteCount(post.getVoteCount() + voteType.getType())
     */
    private final int type;

    /**
     * Constructs a VoteType with the given numeric direction.
     * Called once per constant when the enum class is loaded.
     *
     * @param type the numeric direction: +1 for upvote, -1 for downvote
     */
    VoteType(int type) {
        this.type = type;
    }

    /**
     * Returns the numeric direction of this vote type.
     *
     * @return +1 if this is UPVOTE, -1 if this is DOWNVOTE
     */
    public int getType() {
        return type;
    }
}