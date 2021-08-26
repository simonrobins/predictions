-- :name insert-user! :insert :one
-- :doc creates a new user record
INSERT INTO users (name, initials) VALUES (:name, :initials)

-- :name get-users :query :many
-- :doc retrieves a user record given the id
SELECT * FROM users

-- :name delete-user! :execute :affected
-- :doc deletes a user record given the id
DELETE FROM users WHERE id = :id


-- :name insert-team! :insert :one
-- :doc creates a new team record
INSERT INTO teams (name) VALUES (:name)

-- :name find-team :query :one
-- :doc retrieves a team record with a particular name
SELECT * FROM teams WHERE name = :name

-- :name get-teams :query :many
-- :doc retrieves all team records
SELECT * FROM teams

-- :name delete-team! :execute :affected
-- :doc deletes a team record given the id
DELETE FROM teams WHERE id = :id


-- :name insert-fixture! :insert :one
-- :doc creates a new fixture record
INSERT INTO fixtures (home_id, away_id) VALUES (:home-id, :away-id)

-- :name get-fixtures :query :many
-- :doc retrieves all fixture records
SELECT f.ID, t.NAME AS HOME_TEAM, t2.NAME AS AWAY_TEAM
    FROM FIXTURES f
  	INNER JOIN TEAMS t ON f.HOME_ID = t.ID
  	INNER JOIN TEAMS t2 ON f.AWAY_ID = t2.id

-- :name delete-fixture! :execute :affected
-- :doc deletes a fixture record given the id
DELETE FROM fixtures WHERE id = :id