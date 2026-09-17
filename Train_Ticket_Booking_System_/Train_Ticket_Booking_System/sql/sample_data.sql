USE train_booking;

INSERT INTO trains (train_number, train_name)
VALUES
('12345', 'Kolkata Rajdhani'),
('54321', 'Delhi Express');

INSERT INTO stations (name)
VALUES ('Delhi'), ('Kanpur'), ('Lucknow'), ('Kolkata');

INSERT INTO train_stations (train_id, station_id, station_order, arrival_time, departure_time)
VALUES
(1, 1, 1, NULL, '08:00:00'),
(1, 2, 2, '11:00:00', '11:15:00'),
(1, 3, 3, '14:00:00', '14:15:00'),
(1, 4, 4, '20:00:00', NULL),

(2, 4, 1, NULL, '08:30:00'),
(2, 3, 2, '14:30:00', '14:45:00'),
(2, 2, 3, '17:30:00', '17:45:00'),
(2, 1, 4, '21:00:00', NULL);

INSERT INTO coaches (train_id, coach_number, coach_type)
VALUES
(1, 'C1', 'AC'),
(2, 'C1', 'AC');

-- Six sample seats per coach.
INSERT INTO seats (coach_id, seat_number)
VALUES
(1,1),(1,2),(1,3),(1,4),(1,5),(1,6),
(2,1),(2,2),(2,3),(2,4),(2,5),(2,6);
