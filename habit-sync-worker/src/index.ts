export interface Env {
	DB: D1Database;
	API_KEY: string;
}

function checkAuth(request: Request, env: Env): boolean {
	const authHeader = request.headers.get("Authorization");
	return authHeader === `Bearer ${env.API_KEY}`;
}

function jsonResponse(data: unknown, status = 200): Response {
	return new Response(JSON.stringify(data), {
		status,
		headers: { "Content-Type": "application/json" },
	});
}

export default {
	async fetch(request: Request, env: Env): Promise<Response> {
		const url = new URL(request.url);

		if (!checkAuth(request, env)) {
			return jsonResponse({ error: "Unauthorized" }, 401);
		}

		if (url.pathname === "/api/habits" && request.method === "GET") {
			const { results } = await env.DB.prepare(
				"SELECT * FROM habits WHERE isDeleted = 0"
			).all();
			return jsonResponse(results);
		}

		if (url.pathname === "/api/logs" && request.method === "GET") {
			const { results } = await env.DB.prepare(
				"SELECT * FROM habit_logs WHERE isDeleted = 0"
			).all();
			return jsonResponse(results);
		}

		if (url.pathname === "/api/sync/habits" && request.method === "POST") {
			const habits = await request.json() as any[];
			for (const h of habits) {
				await env.DB.prepare(
					`INSERT INTO habits (id, name, icon, frequency, reminderTime, duration, category, createdAt, updatedAt, isDeleted)
					 VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
					 ON CONFLICT(id) DO UPDATE SET
						name=excluded.name, icon=excluded.icon, frequency=excluded.frequency,
						reminderTime=excluded.reminderTime, duration=excluded.duration,
						category=excluded.category, updatedAt=excluded.updatedAt, isDeleted=excluded.isDeleted
					 WHERE excluded.updatedAt > habits.updatedAt`
				).bind(h.id, h.name, h.icon, h.frequency, h.reminderTime, h.duration, h.category, h.createdAt, h.updatedAt, h.isDeleted ? 1 : 0).run();
			}
			return jsonResponse({ success: true });
		}

		if (url.pathname === "/api/sync/logs" && request.method === "POST") {
			const logs = await request.json() as any[];
			for (const l of logs) {
				await env.DB.prepare(
					`INSERT INTO habit_logs (id, habitId, date, updatedAt, isDeleted)
					 VALUES (?, ?, ?, ?, ?)
					 ON CONFLICT(id) DO UPDATE SET
						updatedAt=excluded.updatedAt, isDeleted=excluded.isDeleted
					 WHERE excluded.updatedAt > habit_logs.updatedAt`
				).bind(l.id, l.habitId, l.date, l.updatedAt, l.isDeleted ? 1 : 0).run();
			}
			return jsonResponse({ success: true });
		}

		return jsonResponse({ error: "Not found" }, 404);
	},
};
