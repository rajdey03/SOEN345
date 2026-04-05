const { spawnSync } = require("child_process");
const path = require("path");

const flowName = process.argv[2] || "run-all";
const flowFile = path.join("e2e", `${flowName}.yaml`);

const stamp = Date.now();
const envValues = {
  TEST_EMAIL: process.env.TEST_EMAIL || `qa.${stamp}@example.com`,
  TEST_PHONE: process.env.TEST_PHONE || `+1514${String(stamp).slice(-7)}`,
  TEST_PASSWORD: process.env.TEST_PASSWORD || "password123",
  TEST_EVENT_TITLE: process.env.TEST_EVENT_TITLE || `QA-Event-${stamp}`,
  UPDATED_EVENT_TITLE:
    process.env.UPDATED_EVENT_TITLE || `QA-Event-${stamp}-Updated`,
  DEMO_ORGANIZER_ID:
    process.env.DEMO_ORGANIZER_ID || "44444444-4444-4444-4444-444444444444"
};

const args = ["test", flowFile];

for (const [key, value] of Object.entries(envValues)) {
  args.push("-e", `${key}=${value}`);
}

console.log(`Running Maestro flow: ${flowFile}`);
console.log("Using test data:");
for (const [key, value] of Object.entries(envValues)) {
  console.log(`- ${key}=${value}`);
}

const result = spawnSync("maestro", args, {
  stdio: "inherit",
  shell: true
});

if (result.error) {
  console.error("Failed to start Maestro. Make sure Maestro CLI is installed and on PATH.");
  console.error(result.error.message);
  process.exit(1);
}

process.exit(result.status ?? 1);
