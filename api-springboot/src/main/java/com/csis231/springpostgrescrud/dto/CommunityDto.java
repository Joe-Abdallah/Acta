package com.csis231.springpostgrescrud.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * Data Transfer Object for Community
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommunityDto {

    /** The community's unique identifier.  */
    private Long id;

    /**
     * The community's unique name
     * Required on creation.
     */
    private String name;

    /**
     * An optional description of the community.
     * Optional on both creation and update
     */
    private String description;

    /** The timestamp when the community was created. */
    private Instant createdAt;

    /**
     * The ID of the client who created this community.
     */
    private Long creatorId;

    /**
     * The total number of members in this community.
     */
    private Integer memberCount;
}