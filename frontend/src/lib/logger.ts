export function log(msg: string, context?: string) {
	const prefix = context ? `[${context}]` : '';
	console.log(new Date().toISOString(), prefix, msg);
}

export function createLogger(context: string) {
	return (msg: string) => log(msg, context);
}
