-- country

CREATE SEQUENCE match_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE match
(
    id bigint DEFAULT nextval('match_seq') NOT NULL,
    createdAt timestamp with time zone,
    updatedAt timestamp with time zone,

    matchId varchar(255),
    marketId numeric,
    outcomeId varchar(255),
    specifiers varchar(255),

    PRIMARY KEY (id)
);