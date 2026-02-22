const URLS = [
  "https://floney-run.shop/health-check",
  "https://floney-dev.store/health-check",
];

const SLACK_WEBHOOK_URL = process.env.SLACK_WEBHOOK_URL;

async function checkHealth(url) {
  try {
    const res = await fetch(url, {
      method: "GET",
      signal: AbortSignal.timeout(10000),
    });
    return { status: res.status, error: null };
  } catch (e) {
    return { status: null, error: e.message };
  }
}

async function sendSlack(message) {
  await fetch(SLACK_WEBHOOK_URL, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ text: message }),
    signal: AbortSignal.timeout(10000),
  });
}

export const handler = async () => {
  const now = new Date().toLocaleString("ko-KR", { timeZone: "Asia/Seoul" });
  const errors = [];

  for (const url of URLS) {
    const { status, error } = await checkHealth(url);
    if (status === 200) {
      console.log(`[OK] ${url} → ${status}`);
    } else {
      const msg = status ? `HTTP ${status}` : `연결 실패: ${error}`;
      console.log(`[FAIL] ${url} → ${msg}`);
      errors.push(`• *${url}*\n  └ ${msg}`);
    }
  }

  if (errors.length > 0) {
    await sendSlack(
      `<!channel> :rotating_light: *헬스체크 실패* \`${now}\`\n\n${errors.join("\n")}`
    );
  }

  return {
    statusCode: 200,
    body: JSON.stringify({ checked: URLS.length, errors: errors.length }),
  };
};
