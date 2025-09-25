export function log(msg: string, context?: string) {
	const prefix = context ? `[${context}]` : '';
	console.log(new Date().toISOString(), prefix, msg);
}

export function createLogger(context: string) {
	return (msg: string) => log(msg, context);
}

/**
 * Formats time using CET/CEST.
 *
 * @param time
 * @returns
 */
export function formatPlannedTime(time: string): string {
	const sanitized = time.replace(/\[.*\]/, '');
	const date = new Date(sanitized);

	return new Intl.DateTimeFormat('de-DE', {
		hour: '2-digit',
		minute: '2-digit'
	}).format(date);
}

/**
 * Formats time, keeping UTC.
 *
 * @param time
 * @returns
 */
export function formatDate(time: string): string {
	const sanitized = time.replace(/\[.*\]/, '');
	const date = new Date(sanitized);

	return new Intl.DateTimeFormat('de-DE', {
		day: '2-digit',
		month: '2-digit',
		year: '2-digit',
		timeZone: 'UTC'
	}).format(date);
}
