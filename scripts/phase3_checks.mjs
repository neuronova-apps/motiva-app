import { chromium } from 'playwright';
import AxeBuilder from '@axe-core/playwright';
import { mkdir, writeFile } from 'node:fs/promises';

const baseURL = process.env.SMOKE_BASE_URL || 'http://127.0.0.1:4173/';
const artifactsDir = 'artifacts/phase3';
const blockingImpacts = new Set(['critical', 'serious']);
const browser = await chromium.launch({ headless: true });
const failures = [];

function assert(condition, message) { if (!condition) throw new Error(message); }
async function saveFailure(page, label, details) {
  await mkdir(artifactsDir, { recursive: true });
  await page.screenshot({ path: `${artifactsDir}/${label}.png`, fullPage: true }).catch(() => {});
  await writeFile(`${artifactsDir}/${label}.json`, JSON.stringify(details, null, 2), 'utf8').catch(() => {});
}
function summarizeViolation(v) { return { id:v.id, impact:v.impact, help:v.help, helpUrl:v.helpUrl, nodes:v.nodes.map(n => ({target:n.target,failureSummary:n.failureSummary})) }; }

async function runAxe(label, viewport, setup) {
  const context = await browser.newContext({ viewport, reducedMotion:'reduce' });
  const page = await context.newPage();
  try {
    const response = await page.goto(baseURL, { waitUntil:'domcontentloaded', timeout:30_000 });
    assert(response && response.status() < 400, `${label}: la página no cargó correctamente.`);
    await page.waitForTimeout(500);
    if (setup) await setup(page);
    const results = await new AxeBuilder({ page }).analyze();
    const blocking = results.violations.filter(v => blockingImpacts.has(v.impact));
    const advisory = results.violations.filter(v => !blockingImpacts.has(v.impact));
    console.log(`✓ axe ${label}: ${blocking.length} bloqueantes, ${advisory.length} informativas`);
    advisory.forEach(v => console.log(`  · ${v.impact || 'sin impacto'} ${v.id}: ${v.help}`));
    if (blocking.length) {
      const details = blocking.map(summarizeViolation);
      await saveFailure(page, `axe-${label}`, details);
      failures.push({label:`axe-${label}`,errors:details.map(v => `${v.impact} ${v.id}: ${v.help}`)});
      details.forEach(v => console.error(`  - ${v.impact} ${v.id}: ${v.help}`));
    }
  } catch (error) {
    await saveFailure(page, `axe-${label}-exception`, {error:error.message});
    failures.push({label:`axe-${label}`,errors:[error.message]});
  } finally { await context.close(); }
}

async function runFunctionalFlow() {
  const context = await browser.newContext({ viewport:{width:1280,height:800}, reducedMotion:'reduce' });
  const page = await context.newPage();
  try {
    await page.goto(baseURL, { waitUntil:'domcontentloaded', timeout:30_000 });
    const daily = page.locator('#dailyQuoteTitle');
    await daily.waitFor({state:'visible'});
    const before = (await daily.textContent()) || '';
    await page.locator('#newDailyQuote').click();
    assert(((await daily.textContent()) || '') !== before, 'Otra frase no cambió la frase diaria.');

    const need = page.locator('#needGrid .need-chip').first();
    await need.click();
    assert(await need.getAttribute('aria-pressed') === 'true', 'La necesidad elegida no quedó marcada como activa.');
    assert(await page.locator('#quoteList .category-card').count() >= 1, 'Elegir una necesidad no mostró frases relacionadas.');

    await page.locator('#categoryFilter').selectOption({label:'Calma'});
    await page.locator('#randomQuote').click();
    assert(await page.locator('#quoteList .category-card').count() === 1, 'Frase aleatoria no redujo la lista a una sola frase.');
    console.log('✓ funcional Motiva: frase diaria, necesidades y exploración aleatoria responden');
  } catch (error) {
    await saveFailure(page, 'functional-motiva', {error:error.message});
    failures.push({label:'functional-motiva',errors:[error.message]});
    console.error(`✗ funcional Motiva: ${error.message}`);
  } finally { await context.close(); }
}

await runAxe('home-desktop', {width:1440,height:900});
await runAxe('home-mobile-menu', {width:390,height:844}, async page => {
  const menu = page.locator('header .menu-button, header .menu').first();
  if ((await menu.count()) && (await menu.isVisible())) await menu.click();
});
await runFunctionalFlow();
await browser.close();
if (failures.length) { console.error(`\nFase 3 falló en ${failures.length} comprobación(es).`); process.exit(1); }
console.log('\nFase 3 superada: accesibilidad automática y flujo funcional principal verificados.');
