-- ============================================================
--  GYMANICE FITNESS APPLICATION
--  Database Schema  |  MySQL 8.0+
--  Architecture: Modular Monolith
--  Normalization: 3NF
-- ============================================================

CREATE DATABASE IF NOT EXISTS gymanice
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE gymanice;


-- ============================================================
-- SECTION 1 – USER MANAGEMENT MODULE
-- Tables: users, trainees, trainers, nutritionists
-- Pattern: Table-Per-Type (TPT) inheritance
-- ============================================================

CREATE TABLE users (
    user_id     INT          NOT NULL AUTO_INCREMENT,
    username    VARCHAR(50)  NOT NULL,
    email       VARCHAR(100) NOT NULL,
    password    VARCHAR(255) NOT NULL,          -- SHA-256 or bcrypt hash
    full_name   VARCHAR(100) NOT NULL,
    dob         DATE         NOT NULL,
    phone       VARCHAR(20),
    role        ENUM('TRAINEE', 'TRAINER', 'NUTRITIONIST') NOT NULL,
    is_verified TINYINT(1)   NOT NULL DEFAULT 0,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
                             ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (user_id),
    UNIQUE KEY uq_users_username (username),
    UNIQUE KEY uq_users_email    (email)
);

-- ------------------------------------------------------------

CREATE TABLE trainees (
    trainee_id   INT           NOT NULL,       -- shares PK with users
    height_cm    DECIMAL(5,2)  DEFAULT NULL,
    weight_kg    DECIMAL(5,2)  DEFAULT NULL,
    age          INT           DEFAULT NULL,
    fitness_goal VARCHAR(100)  DEFAULT NULL,

    PRIMARY KEY (trainee_id),
    CONSTRAINT fk_trainees_user
        FOREIGN KEY (trainee_id) REFERENCES users (user_id)
        ON DELETE CASCADE,
    CONSTRAINT chk_trainees_height CHECK (height_cm  IS NULL OR (height_cm  > 0 AND height_cm  < 300)),
    CONSTRAINT chk_trainees_weight CHECK (weight_kg  IS NULL OR (weight_kg  > 0 AND weight_kg  < 700)),
    CONSTRAINT chk_trainees_age    CHECK (age         IS NULL OR (age        > 0 AND age        < 120))
);

-- ------------------------------------------------------------

CREATE TABLE trainers (
    trainer_id     INT           NOT NULL,     -- shares PK with users
    credentials    VARCHAR(200)  DEFAULT NULL,
    specialization VARCHAR(100)  DEFAULT NULL,
    monthly_fee    DECIMAL(8,2)  NOT NULL DEFAULT 0.00,

    PRIMARY KEY (trainer_id),
    CONSTRAINT fk_trainers_user
        FOREIGN KEY (trainer_id) REFERENCES users (user_id)
        ON DELETE CASCADE,
    CONSTRAINT chk_trainers_fee CHECK (monthly_fee >= 59 AND monthly_fee <= 89)
);

-- ------------------------------------------------------------

CREATE TABLE nutritionists (
    nutritionist_id INT           NOT NULL,    -- shares PK with users
    credentials     VARCHAR(200)  DEFAULT NULL,
    specialization  VARCHAR(100)  DEFAULT NULL,
    monthly_fee     DECIMAL(8,2)  NOT NULL DEFAULT 0.00,

    PRIMARY KEY (nutritionist_id),
    CONSTRAINT fk_nutritionists_user
        FOREIGN KEY (nutritionist_id) REFERENCES users (user_id)
        ON DELETE CASCADE,
    CONSTRAINT chk_nutritionists_fee CHECK (monthly_fee >= 59 AND monthly_fee <= 89)
);


-- ============================================================
-- SECTION 2 – SUBSCRIPTION MODULE
-- Tables: subscription_plans, subscriptions
-- ============================================================

CREATE TABLE subscription_plans (
    plan_id          INT           NOT NULL AUTO_INCREMENT,
    trainer_id       INT           NOT NULL,
    plan_name        VARCHAR(100)  NOT NULL,
    price_sar        DECIMAL(8,2)  NOT NULL,
    duration_months  INT           NOT NULL DEFAULT 1,
    description      TEXT          DEFAULT NULL,
    is_active        TINYINT(1)    NOT NULL DEFAULT 1,
    created_at       TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (plan_id),
    CONSTRAINT fk_sp_trainer
        FOREIGN KEY (trainer_id) REFERENCES trainers (trainer_id)
        ON DELETE CASCADE,
    CONSTRAINT chk_sp_price    CHECK (price_sar BETWEEN 59 AND 89),
    CONSTRAINT chk_sp_duration CHECK (duration_months > 0)
);

-- ------------------------------------------------------------

CREATE TABLE subscriptions (
    subscription_id INT      NOT NULL AUTO_INCREMENT,
    trainee_id      INT      NOT NULL,
    trainer_id      INT      NOT NULL,
    plan_id         INT      DEFAULT NULL,
    start_date      DATE     NOT NULL,
    end_date        DATE     DEFAULT NULL,
    status          ENUM('ACTIVE', 'EXPIRED', 'CANCELLED') NOT NULL DEFAULT 'ACTIVE',
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (subscription_id),
    CONSTRAINT fk_sub_trainee
        FOREIGN KEY (trainee_id) REFERENCES trainees  (trainee_id) ON DELETE CASCADE,
    CONSTRAINT fk_sub_trainer
        FOREIGN KEY (trainer_id) REFERENCES trainers  (trainer_id) ON DELETE CASCADE,
    CONSTRAINT fk_sub_plan
        FOREIGN KEY (plan_id)    REFERENCES subscription_plans (plan_id) ON DELETE SET NULL
);


-- ============================================================
-- SECTION 3 – WORKOUT MANAGEMENT MODULE
-- Tables: exercises, workout_schedules, schedule_exercises,
--         exercise_performances
-- ============================================================

CREATE TABLE exercises (
    exercise_id   INT           NOT NULL AUTO_INCREMENT,
    name          VARCHAR(100)  NOT NULL,
    muscle_group  VARCHAR(50)   NOT NULL,
    description   TEXT          DEFAULT NULL,
    tutorial_link VARCHAR(255)  DEFAULT NULL,

    PRIMARY KEY (exercise_id),
    UNIQUE KEY uq_exercises_name (name)
);

-- ------------------------------------------------------------

CREATE TABLE workout_schedules (
    schedule_id   INT       NOT NULL AUTO_INCREMENT,
    trainer_id    INT       NOT NULL,
    trainee_id    INT       NOT NULL,
    schedule_date DATE      NOT NULL,
    notes         TEXT      DEFAULT NULL,
    created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (schedule_id),
    CONSTRAINT fk_ws_trainer
        FOREIGN KEY (trainer_id) REFERENCES trainers (trainer_id) ON DELETE CASCADE,
    CONSTRAINT fk_ws_trainee
        FOREIGN KEY (trainee_id) REFERENCES trainees (trainee_id) ON DELETE CASCADE
);

-- ------------------------------------------------------------
-- Junction table: WorkoutSchedule (M) <-> Exercise (M)
-- Stores per-schedule targets that may differ from defaults

CREATE TABLE schedule_exercises (
    schedule_id    INT NOT NULL,
    exercise_id    INT NOT NULL,
    target_sets    INT NOT NULL DEFAULT 3,
    target_reps    INT NOT NULL DEFAULT 10,
    target_weight  INT NOT NULL DEFAULT 0,      -- kg; 0 = bodyweight
    sort_order     INT NOT NULL DEFAULT 0,

    PRIMARY KEY (schedule_id, exercise_id),
    CONSTRAINT fk_se_schedule
        FOREIGN KEY (schedule_id) REFERENCES workout_schedules (schedule_id) ON DELETE CASCADE,
    CONSTRAINT fk_se_exercise
        FOREIGN KEY (exercise_id) REFERENCES exercises          (exercise_id) ON DELETE CASCADE,
    CONSTRAINT chk_se_sets CHECK (target_sets > 0),
    CONSTRAINT chk_se_reps CHECK (target_reps > 0)
);

-- ------------------------------------------------------------

CREATE TABLE exercise_performances (
    performance_id INT       NOT NULL AUTO_INCREMENT,
    schedule_id    INT       NOT NULL,
    exercise_id    INT       NOT NULL,
    trainee_id     INT       NOT NULL,
    logged_date    DATE      NOT NULL,
    actual_sets    INT       NOT NULL,
    actual_reps    INT       NOT NULL,
    actual_weight  INT       NOT NULL DEFAULT 0,
    notes          TEXT      DEFAULT NULL,

    PRIMARY KEY (performance_id),
    CONSTRAINT fk_ep_schedule
        FOREIGN KEY (schedule_id) REFERENCES workout_schedules (schedule_id) ON DELETE CASCADE,
    CONSTRAINT fk_ep_exercise
        FOREIGN KEY (exercise_id) REFERENCES exercises          (exercise_id) ON DELETE CASCADE,
    CONSTRAINT fk_ep_trainee
        FOREIGN KEY (trainee_id)  REFERENCES trainees           (trainee_id)  ON DELETE CASCADE,
    CONSTRAINT chk_ep_sets CHECK (actual_sets >= 0),
    CONSTRAINT chk_ep_reps CHECK (actual_reps >= 0)
);


-- ============================================================
-- SECTION 4 – NUTRITION MANAGEMENT MODULE
-- Tables: meals, preset_meals, custom_meals,
--         ingredients, custom_meal_ingredients,
--         meal_plans, meal_plan_items
-- Pattern: Table-Per-Type for Meal hierarchy
-- ============================================================

CREATE TABLE meals (
    meal_id    INT           NOT NULL AUTO_INCREMENT,
    meal_name  VARCHAR(150)  NOT NULL,
    calories   DECIMAL(8,2)  NOT NULL,
    protein_g  DECIMAL(6,2)  NOT NULL DEFAULT 0.00,
    carbs_g    DECIMAL(6,2)  NOT NULL DEFAULT 0.00,
    fat_g      DECIMAL(6,2)  NOT NULL DEFAULT 0.00,
    meal_type  ENUM('PRESET', 'CUSTOM') NOT NULL,

    PRIMARY KEY (meal_id),
    CONSTRAINT chk_meals_calories CHECK (calories  >= 0),
    CONSTRAINT chk_meals_protein  CHECK (protein_g >= 0),
    CONSTRAINT chk_meals_carbs    CHECK (carbs_g   >= 0),
    CONSTRAINT chk_meals_fat      CHECK (fat_g     >= 0)
);

-- ------------------------------------------------------------

CREATE TABLE preset_meals (
    meal_id       INT       NOT NULL,           -- shares PK with meals
    category      ENUM('BREAKFAST', 'LUNCH', 'DINNER', 'SNACK') NOT NULL,
    prep_time_min INT       NOT NULL DEFAULT 0,
    recipe        TEXT      DEFAULT NULL,

    PRIMARY KEY (meal_id),
    CONSTRAINT fk_pm_meal
        FOREIGN KEY (meal_id) REFERENCES meals (meal_id) ON DELETE CASCADE
);

-- ------------------------------------------------------------

CREATE TABLE custom_meals (
    meal_id    INT  NOT NULL,                   -- shares PK with meals
    trainee_id INT  NOT NULL,
    notes      TEXT DEFAULT NULL,

    PRIMARY KEY (meal_id),
    CONSTRAINT fk_cm_meal
        FOREIGN KEY (meal_id)    REFERENCES meals    (meal_id)    ON DELETE CASCADE,
    CONSTRAINT fk_cm_trainee
        FOREIGN KEY (trainee_id) REFERENCES trainees (trainee_id) ON DELETE CASCADE
);

-- ------------------------------------------------------------

CREATE TABLE ingredients (
    ingredient_id    INT           NOT NULL AUTO_INCREMENT,
    name             VARCHAR(100)  NOT NULL,
    carbs_per_100g   DECIMAL(6,2)  NOT NULL DEFAULT 0.00,
    protein_per_100g DECIMAL(6,2)  NOT NULL DEFAULT 0.00,
    fat_per_100g     DECIMAL(6,2)  NOT NULL DEFAULT 0.00,
    fiber_per_100g   DECIMAL(6,2)  NOT NULL DEFAULT 0.00,

    PRIMARY KEY (ingredient_id),
    UNIQUE KEY uq_ingredients_name (name),
    CONSTRAINT chk_ing_carbs   CHECK (carbs_per_100g   >= 0),
    CONSTRAINT chk_ing_protein CHECK (protein_per_100g >= 0),
    CONSTRAINT chk_ing_fat     CHECK (fat_per_100g     >= 0),
    CONSTRAINT chk_ing_fiber   CHECK (fiber_per_100g   >= 0)
);

-- ------------------------------------------------------------
-- Junction table: CustomMeal (M) <-> Ingredient (M)

CREATE TABLE custom_meal_ingredients (
    meal_id       INT           NOT NULL,
    ingredient_id INT           NOT NULL,
    amount_g      DECIMAL(7,2)  NOT NULL,

    PRIMARY KEY (meal_id, ingredient_id),
    CONSTRAINT fk_cmi_meal
        FOREIGN KEY (meal_id)       REFERENCES custom_meals (meal_id)        ON DELETE CASCADE,
    CONSTRAINT fk_cmi_ingredient
        FOREIGN KEY (ingredient_id) REFERENCES ingredients  (ingredient_id)  ON DELETE CASCADE,
    CONSTRAINT chk_cmi_amount CHECK (amount_g > 0)
);

-- ------------------------------------------------------------

CREATE TABLE meal_plans (
    plan_id           INT           NOT NULL AUTO_INCREMENT,
    plan_name         VARCHAR(150)  NOT NULL,
    nutritionist_id   INT           NOT NULL,
    trainee_id        INT           NOT NULL,
    daily_target_cal  DECIMAL(8,2)  NOT NULL,
    status            ENUM('DRAFT', 'ACTIVE', 'COMPLETED') NOT NULL DEFAULT 'DRAFT',
    created_at        TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
                                    ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (plan_id),
    CONSTRAINT fk_mp_nutritionist
        FOREIGN KEY (nutritionist_id) REFERENCES nutritionists (nutritionist_id) ON DELETE CASCADE,
    CONSTRAINT fk_mp_trainee
        FOREIGN KEY (trainee_id)      REFERENCES trainees       (trainee_id)      ON DELETE CASCADE,
    CONSTRAINT chk_mp_target CHECK (daily_target_cal > 0)
);

-- ------------------------------------------------------------
-- Junction table: MealPlan (M) <-> Meal (M)
-- day_number: 1=Monday ... 7=Sunday

CREATE TABLE meal_plan_items (
    plan_id    INT NOT NULL,
    meal_id    INT NOT NULL,
    day_number INT NOT NULL DEFAULT 1,
    sort_order INT NOT NULL DEFAULT 0,

    PRIMARY KEY (plan_id, meal_id, day_number),
    CONSTRAINT fk_mpi_plan
        FOREIGN KEY (plan_id) REFERENCES meal_plans (plan_id) ON DELETE CASCADE,
    CONSTRAINT fk_mpi_meal
        FOREIGN KEY (meal_id) REFERENCES meals       (meal_id) ON DELETE CASCADE,
    CONSTRAINT chk_mpi_day CHECK (day_number BETWEEN 1 AND 7)
);


-- ============================================================
-- SECTION 5 – PROGRESS TRACKING MODULE
-- Table: progress_records
-- BMI is a computed (GENERATED) column – never stored manually
-- ============================================================

CREATE TABLE progress_records (
    record_id            INT           NOT NULL AUTO_INCREMENT,
    trainee_id           INT           NOT NULL,
    record_date          DATE          NOT NULL,
    weight_kg            DECIMAL(5,2)  NOT NULL,
    height_cm            DECIMAL(5,2)  DEFAULT NULL,
    bmi                  DECIMAL(5,2)  GENERATED ALWAYS AS (
                             CASE
                                 WHEN height_cm IS NOT NULL AND height_cm > 0
                                 THEN ROUND(weight_kg / ((height_cm / 100) * (height_cm / 100)), 2)
                                 ELSE NULL
                             END
                         ) STORED,
    initial_weight_kg    DECIMAL(5,2)  DEFAULT NULL,
    target_weight_kg     DECIMAL(5,2)  DEFAULT NULL,
    progress_percent     DECIMAL(5,2)  DEFAULT NULL,
    predicted_completion DATE          DEFAULT NULL,
    notes                TEXT          DEFAULT NULL,

    PRIMARY KEY (record_id),
    CONSTRAINT fk_prog_trainee
        FOREIGN KEY (trainee_id) REFERENCES trainees (trainee_id) ON DELETE CASCADE,
    CONSTRAINT uq_prog_trainee_date
        UNIQUE (trainee_id, record_date),
    CONSTRAINT chk_prog_weight  CHECK (weight_kg > 0),
    CONSTRAINT chk_prog_progress CHECK (
        progress_percent IS NULL OR (progress_percent >= 0 AND progress_percent <= 100)
    )
);


-- ============================================================
-- SECTION 6 – PAYMENT MODULE
-- Tables: payments
-- platform_fee and trainer_amount are GENERATED (5% rule)
-- ============================================================

CREATE TABLE payments (
    payment_id      INT              NOT NULL AUTO_INCREMENT,
    trainee_id      INT              NOT NULL,
    trainer_id      INT              NOT NULL,
    subscription_id INT              DEFAULT NULL,
    payment_type    ENUM('MADA', 'APPLE_PAY', 'CREDIT_CARD', 'STC_PAY') NOT NULL,
    amount_sar      DECIMAL(10,2)    NOT NULL,
    platform_fee    DECIMAL(10,2)    GENERATED ALWAYS AS
                        (ROUND(amount_sar * 0.05, 2)) STORED,
    trainer_amount  DECIMAL(10,2)    GENERATED ALWAYS AS
                        (ROUND(amount_sar * 0.95, 2)) STORED,
    status          ENUM('PENDING', 'COMPLETED', 'FAILED', 'REFUNDED')
                        NOT NULL DEFAULT 'PENDING',
    transaction_ref VARCHAR(100)     DEFAULT NULL,
    payment_date    TIMESTAMP        NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (payment_id),
    UNIQUE KEY uq_payments_ref (transaction_ref),
    CONSTRAINT fk_pay_trainee
        FOREIGN KEY (trainee_id)      REFERENCES trainees       (trainee_id)      ON DELETE RESTRICT,
    CONSTRAINT fk_pay_trainer
        FOREIGN KEY (trainer_id)      REFERENCES trainers        (trainer_id)      ON DELETE RESTRICT,
    CONSTRAINT fk_pay_subscription
        FOREIGN KEY (subscription_id) REFERENCES subscriptions   (subscription_id) ON DELETE SET NULL,
    CONSTRAINT chk_pay_amount CHECK (amount_sar > 0)
);


-- ============================================================
-- INDEXES (performance optimization)
-- ============================================================

-- User lookups
CREATE INDEX idx_users_role        ON users              (role);
CREATE INDEX idx_users_email       ON users              (email);

-- Workout queries
CREATE INDEX idx_ws_trainee_date   ON workout_schedules  (trainee_id, schedule_date);
CREATE INDEX idx_ws_trainer        ON workout_schedules  (trainer_id);
CREATE INDEX idx_ep_trainee_date   ON exercise_performances (trainee_id, logged_date);

-- Nutrition queries
CREATE INDEX idx_cm_trainee        ON custom_meals       (trainee_id);
CREATE INDEX idx_mp_trainee        ON meal_plans         (trainee_id);
CREATE INDEX idx_mp_nutritionist   ON meal_plans         (nutritionist_id);
CREATE INDEX idx_mpi_day           ON meal_plan_items    (day_number);

-- Progress queries
CREATE INDEX idx_prog_trainee_date ON progress_records   (trainee_id, record_date);

-- Payment queries
CREATE INDEX idx_pay_trainee       ON payments           (trainee_id);
CREATE INDEX idx_pay_trainer       ON payments           (trainer_id);
CREATE INDEX idx_pay_status        ON payments           (status);
CREATE INDEX idx_pay_date          ON payments           (payment_date);

-- Subscription queries
CREATE INDEX idx_sub_trainee       ON subscriptions      (trainee_id);
CREATE INDEX idx_sub_trainer       ON subscriptions      (trainer_id);
CREATE INDEX idx_sub_status        ON subscriptions      (status);
