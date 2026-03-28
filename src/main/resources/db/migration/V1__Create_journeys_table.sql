CREATE TABLE IF NOT EXISTS journeys (
                                        session_id VARCHAR(255) PRIMARY KEY,
    vehicle_type VARCHAR(50) NOT NULL,
    destination VARCHAR(255) NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP
    );