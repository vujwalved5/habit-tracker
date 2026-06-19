CREATE TABLE IF NOT EXISTS habits (
	id TEXT PRIMARY KEY,
	name TEXT NOT NULL,
	icon TEXT,
	frequency TEXT,
	reminderTime TEXT,
	duration INTEGER,
	category TEXT,
	createdAt INTEGER,
	updatedAt INTEGER,
	isDeleted INTEGER DEFAULT 0
);

CREATE TABLE IF NOT EXISTS habit_logs (
	id TEXT PRIMARY KEY,
	habitId TEXT NOT NULL,
	date TEXT NOT NULL,
	updatedAt INTEGER,
	isDeleted INTEGER DEFAULT 0,
	UNIQUE(habitId, date)
);

CREATE INDEX IF NOT EXISTS idx_habit_logs_habitId ON habit_logs(habitId);
