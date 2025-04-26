CREATE TABLE appointments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    doctor_id BIGINT NOT NULL,
    patient_id BIGINT NOT NULL,
    date_time DATETIME NOT NULL,
    description VARCHAR(255),
    status VARCHAR(32) NOT NULL
);

INSERT INTO appointments (doctor_id, patient_id, date_time, description, status) VALUES
    (1, 100, NOW() + INTERVAL 1 DAY, 'Consulta inicial', 'SCHEDULED'),
    (2, 101, NOW() + INTERVAL 2 DAY, 'Retorno', 'SCHEDULED'),
    (1, 102, NOW() + INTERVAL 3 DAY, 'Exame de rotina', 'SCHEDULED');
