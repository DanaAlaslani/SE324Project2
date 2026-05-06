-- ============================================================
--  GYMANICE – Sample Data Seed
--  Password for all accounts: Test1234
--  SHA-256: 07480fb9e85b9396af06f006cf1c95024af2531c65fb505cfbd0add1e2f31573
-- ============================================================

USE gymanice;
SET FOREIGN_KEY_CHECKS = 0;

-- ── Users ────────────────────────────────────────────────────

INSERT INTO users (user_id, username, email, password, full_name, dob, phone, role, is_verified) VALUES
(1, 'ahmed_trainer',   'ahmed@gymanice.com',   '07480fb9e85b9396af06f006cf1c95024af2531c65fb505cfbd0add1e2f31573', 'Ahmed Ali',      '1988-03-10', '0512345001', 'TRAINER',      1),
(2, 'khalid_trainer',  'khalid@gymanice.com',  '07480fb9e85b9396af06f006cf1c95024af2531c65fb505cfbd0add1e2f31573', 'Khalid Hassan',  '1985-07-22', '0512345002', 'TRAINER',      1),
(3, 'sara_nutri',      'sara@gymanice.com',    '07480fb9e85b9396af06f006cf1c95024af2531c65fb505cfbd0add1e2f31573', 'Sara Hassan',    '1990-11-05', '0523456001', 'NUTRITIONIST', 1),
(4, 'roaa_nutri',      'roaa@gymanice.com',    '07480fb9e85b9396af06f006cf1c95024af2531c65fb505cfbd0add1e2f31573', 'Roaa Allaf',     '1992-04-18', '0523456002', 'NUTRITIONIST', 1),
(5, 'rima_trainee',    'rima@gymanice.com',    '07480fb9e85b9396af06f006cf1c95024af2531c65fb505cfbd0add1e2f31573', 'Rima Al-Saedi',  '2000-07-20', '0534567001', 'TRAINEE',      1),
(6, 'dana_trainee',    'dana@gymanice.com',    '07480fb9e85b9396af06f006cf1c95024af2531c65fb505cfbd0add1e2f31573', 'Dana Alaslani',  '1998-02-14', '0534567002', 'TRAINEE',      1);

-- ── Role tables ───────────────────────────────────────────────

INSERT INTO trainers (trainer_id, credentials, specialization, monthly_fee) VALUES
(1, 'NASM Certified, CPT',        'Strength & Conditioning', 79.00),
(2, 'CSCS, NSCA Certified',       'Weight Loss & Cardio',    89.00);

INSERT INTO nutritionists (nutritionist_id, credentials, specialization, monthly_fee) VALUES
(3, 'Registered Dietitian, MSc Nutrition', 'Sports Nutrition',  69.00),
(4, 'Clinical Nutritionist, BSc Dietetics','Weight Management', 79.00);

INSERT INTO trainees (trainee_id, height_cm, weight_kg, age, fitness_goal) VALUES
(5, 165.0, 72.0, 24, 'Lose Weight'),
(6, 170.0, 65.0, 26, 'Build Muscle');

-- ── Workout schedules ─────────────────────────────────────────

INSERT INTO workout_schedules (schedule_id, trainer_id, trainee_id, schedule_date, notes) VALUES
(1, 1, 5, '2026-05-05', 'Full Body Strength - Week 1'),
(2, 1, 5, '2026-05-12', 'Full Body Strength - Week 2'),
(3, 2, 6, '2026-05-06', 'Hypertrophy Program - Day 1'),
(4, 2, 6, '2026-05-13', 'Hypertrophy Program - Day 2');

-- ── Schedule exercises ────────────────────────────────────────

INSERT INTO schedule_exercises (schedule_id, exercise_id, target_sets, target_reps, target_weight, sort_order)
SELECT 1, exercise_id, 4, 10, 60, 1 FROM exercises WHERE name = 'Barbell Squat';
INSERT INTO schedule_exercises (schedule_id, exercise_id, target_sets, target_reps, target_weight, sort_order)
SELECT 1, exercise_id, 3, 12, 50, 2 FROM exercises WHERE name = 'Bench Press';
INSERT INTO schedule_exercises (schedule_id, exercise_id, target_sets, target_reps, target_weight, sort_order)
SELECT 1, exercise_id, 3,  6, 80, 3 FROM exercises WHERE name = 'Deadlift';
INSERT INTO schedule_exercises (schedule_id, exercise_id, target_sets, target_reps, target_weight, sort_order)
SELECT 1, exercise_id, 3, 60,  0, 4 FROM exercises WHERE name = 'Plank';

INSERT INTO schedule_exercises (schedule_id, exercise_id, target_sets, target_reps, target_weight, sort_order)
SELECT 2, exercise_id, 4, 10, 65, 1 FROM exercises WHERE name = 'Barbell Squat';
INSERT INTO schedule_exercises (schedule_id, exercise_id, target_sets, target_reps, target_weight, sort_order)
SELECT 2, exercise_id, 3, 12, 55, 2 FROM exercises WHERE name = 'Romanian Deadlift';
INSERT INTO schedule_exercises (schedule_id, exercise_id, target_sets, target_reps, target_weight, sort_order)
SELECT 2, exercise_id, 3, 10, 40, 3 FROM exercises WHERE name = 'Overhead Press';
INSERT INTO schedule_exercises (schedule_id, exercise_id, target_sets, target_reps, target_weight, sort_order)
SELECT 2, exercise_id, 3, 15, 10, 4 FROM exercises WHERE name = 'Lateral Raise';

INSERT INTO schedule_exercises (schedule_id, exercise_id, target_sets, target_reps, target_weight, sort_order)
SELECT 3, exercise_id, 4, 10, 70, 1 FROM exercises WHERE name = 'Bench Press';
INSERT INTO schedule_exercises (schedule_id, exercise_id, target_sets, target_reps, target_weight, sort_order)
SELECT 3, exercise_id, 4,  8,  0, 2 FROM exercises WHERE name = 'Pull-Up';
INSERT INTO schedule_exercises (schedule_id, exercise_id, target_sets, target_reps, target_weight, sort_order)
SELECT 3, exercise_id, 3, 10, 60, 3 FROM exercises WHERE name = 'Barbell Row';

INSERT INTO schedule_exercises (schedule_id, exercise_id, target_sets, target_reps, target_weight, sort_order)
SELECT 4, exercise_id, 4, 12, 80, 1 FROM exercises WHERE name = 'Deadlift';
INSERT INTO schedule_exercises (schedule_id, exercise_id, target_sets, target_reps, target_weight, sort_order)
SELECT 4, exercise_id, 3, 10, 50, 2 FROM exercises WHERE name = 'Overhead Press';
INSERT INTO schedule_exercises (schedule_id, exercise_id, target_sets, target_reps, target_weight, sort_order)
SELECT 4, exercise_id, 3, 12, 20, 3 FROM exercises WHERE name = 'Lateral Raise';

-- ── Performance logs ──────────────────────────────────────────

INSERT INTO exercise_performances (schedule_id, exercise_id, trainee_id, logged_date, actual_sets, actual_reps, actual_weight)
SELECT 1, exercise_id, 5, '2026-05-05', 4, 10, 58 FROM exercises WHERE name = 'Barbell Squat';
INSERT INTO exercise_performances (schedule_id, exercise_id, trainee_id, logged_date, actual_sets, actual_reps, actual_weight)
SELECT 1, exercise_id, 5, '2026-05-05', 3, 12, 48 FROM exercises WHERE name = 'Bench Press';
INSERT INTO exercise_performances (schedule_id, exercise_id, trainee_id, logged_date, actual_sets, actual_reps, actual_weight)
SELECT 1, exercise_id, 5, '2026-05-05', 3,  5, 75 FROM exercises WHERE name = 'Deadlift';

-- ── Meal plans ────────────────────────────────────────────────

INSERT INTO meal_plans (plan_id, plan_name, nutritionist_id, trainee_id, daily_target_cal, status) VALUES
(1, 'Rima Weight-Loss Plan',   3, 5, 1800.0, 'ACTIVE'),
(2, 'Dana Muscle-Gain Plan',   4, 6, 2400.0, 'ACTIVE');

-- Rima plan meals
INSERT INTO meal_plan_items (plan_id, meal_id, day_number, sort_order)
SELECT 1, meal_id, 1, 1 FROM meals WHERE meal_name = 'Oatmeal with Berries';
INSERT INTO meal_plan_items (plan_id, meal_id, day_number, sort_order)
SELECT 1, meal_id, 1, 2 FROM meals WHERE meal_name = 'Tuna Salad Wrap';
INSERT INTO meal_plan_items (plan_id, meal_id, day_number, sort_order)
SELECT 1, meal_id, 1, 3 FROM meals WHERE meal_name = 'Salmon & Vegetables';
INSERT INTO meal_plan_items (plan_id, meal_id, day_number, sort_order)
SELECT 1, meal_id, 2, 1 FROM meals WHERE meal_name = 'Scrambled Eggs & Toast';
INSERT INTO meal_plan_items (plan_id, meal_id, day_number, sort_order)
SELECT 1, meal_id, 2, 2 FROM meals WHERE meal_name = 'Grilled Chicken & Rice';
INSERT INTO meal_plan_items (plan_id, meal_id, day_number, sort_order)
SELECT 1, meal_id, 2, 3 FROM meals WHERE meal_name = 'Greek Yogurt & Nuts';

-- Dana plan meals
INSERT INTO meal_plan_items (plan_id, meal_id, day_number, sort_order)
SELECT 2, meal_id, 1, 1 FROM meals WHERE meal_name = 'Scrambled Eggs & Toast';
INSERT INTO meal_plan_items (plan_id, meal_id, day_number, sort_order)
SELECT 2, meal_id, 1, 2 FROM meals WHERE meal_name = 'Grilled Chicken & Rice';
INSERT INTO meal_plan_items (plan_id, meal_id, day_number, sort_order)
SELECT 2, meal_id, 1, 3 FROM meals WHERE meal_name = 'Beef Stir Fry';
INSERT INTO meal_plan_items (plan_id, meal_id, day_number, sort_order)
SELECT 2, meal_id, 1, 4 FROM meals WHERE meal_name = 'Protein Shake & Banana';
INSERT INTO meal_plan_items (plan_id, meal_id, day_number, sort_order)
SELECT 2, meal_id, 2, 1 FROM meals WHERE meal_name = 'Oatmeal with Berries';
INSERT INTO meal_plan_items (plan_id, meal_id, day_number, sort_order)
SELECT 2, meal_id, 2, 2 FROM meals WHERE meal_name = 'Quinoa & Black Beans';

-- ── Progress records ──────────────────────────────────────────

INSERT INTO progress_records (trainee_id, record_date, weight_kg, height_cm, initial_weight_kg, target_weight_kg, progress_percent) VALUES
(5, '2026-03-01', 72.0, 165.0, 72.0, 62.0,  0.0),
(5, '2026-03-15', 71.2, 165.0, 72.0, 62.0,  8.0),
(5, '2026-04-01', 70.0, 165.0, 72.0, 62.0, 20.0),
(5, '2026-04-15', 69.1, 165.0, 72.0, 62.0, 29.0),
(5, '2026-05-01', 68.3, 165.0, 72.0, 62.0, 37.0),
(6, '2026-03-10', 65.0, 170.0, 65.0, 78.0,  0.0),
(6, '2026-04-10', 66.4, 170.0, 65.0, 78.0, 10.8),
(6, '2026-05-01', 67.8, 170.0, 65.0, 78.0, 21.5);

-- ── Payments ──────────────────────────────────────────────────

INSERT INTO payments (trainee_id, trainer_id, payment_type, amount_sar, status, transaction_ref) VALUES
(5, 1, 'MADA',        299.00, 'COMPLETED', 'TXN-A1B2C3D4'),
(5, 3, 'APPLE_PAY',   199.00, 'COMPLETED', 'TXN-E5F6G7H8'),
(6, 2, 'CREDIT_CARD', 399.00, 'COMPLETED', 'TXN-I9J0K1L2'),
(6, 4, 'MADA',        199.00, 'COMPLETED', 'TXN-M3N4O5P6');

SET FOREIGN_KEY_CHECKS = 1;
