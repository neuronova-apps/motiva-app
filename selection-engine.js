(() => {
  'use strict';

  const STORAGE_KEY = 'motiva.selectionHistory.v1';
  const MAX_RECENT_IDS = 3;
  const MAX_RECENT_TONES = 2;

  // Metadatos temporales para la colección de exhibición actual.
  // Se ignoran automáticamente cuando se cargue MOTIVA_BANK.
  const demoMetadataById = {
    FR000001: { tone: 'Reflexivo' },
    FR000002: { tone: 'Motivador' },
    FR000003: { tone: 'Sereno' },
    FR000004: { tone: 'Directo' },
    FR000005: { tone: 'Reflexivo' },
    FR000006: { tone: 'Motivador' },
    FR000007: { tone: 'Sereno' },
    FR000008: { tone: 'Cálido' },
    FR000009: { tone: 'Acompañante' },
    FR000010: { tone: 'Motivador' },
    FR000011: { tone: 'Directo' },
    FR000012: { tone: 'Reflexivo' },
    FR000013: { tone: 'Sereno' },
    FR000014: { tone: 'Esperanzador' },
    FR000017: { tone: 'Cálido' },
    FR000018: { tone: 'Reflexivo' },
    FR000019: { tone: 'Ingenioso' },
    FR000020: { tone: 'Motivador' },
    FR000022: { tone: 'Inspirador', rights: 'Dominio público', verification: 'A' },
    FR000024: { tone: 'Ingenioso', rights: 'Dominio público', verification: 'A' },
    FR000027: { tone: 'Inspirador', rights: 'Dominio público', verification: 'A' },
    FR000031: { tone: 'Reflexivo' }
  };

  function hydrateBank(bank) {
    if (!bank || !Array.isArray(bank.q)) return [];
    return bank.q.map(row => ({
      id: row[0],
      text: row[1],
      author: bank.a[row[2]] || '',
      source: bank.s[row[3]] || '',
      category: bank.c[row[4]] || '',
      categories: [bank.c[row[4]] || ''].filter(Boolean),
      need: bank.n[row[5]] || '',
      tone: bank.t[row[6]] || '',
      sensitivity: bank.y[row[7]] || '',
      rights: bank.r[row[8]] || '',
      verification: bank.v[row[9]] || '',
      quality: Number(row[10] || 0),
      editorialStatus: bank.e[row[11]] || '',
      duplicate: bank.d[row[12]] || ''
    }));
  }

  function normalizeDemoQuote(quote) {
    const extra = demoMetadataById[quote.id] || {};
    return Object.assign(quote, {
      category: quote.category || quote.categories?.[0] || '',
      tone: quote.tone || extra.tone || 'Acompañante',
      sensitivity: quote.sensitivity || 'Normal',
      rights: quote.rights || extra.rights || 'Original',
      verification: quote.verification || extra.verification || 'Original',
      quality: Number(quote.quality || 5),
      editorialStatus: quote.editorialStatus || 'Aprobada',
      duplicate: quote.duplicate || 'No'
    });
  }

  const bankQuotes = hydrateBank(window.MOTIVA_BANK);
  const runtimeQuotes = bankQuotes.length
    ? bankQuotes
    : (typeof quotes !== 'undefined' && Array.isArray(quotes) ? quotes.map(normalizeDemoQuote) : []);

  function emptyHistory() {
    return { ids: [], tones: [] };
  }

  function loadHistory() {
    try {
      const parsed = JSON.parse(localStorage.getItem(STORAGE_KEY));
      if (!parsed || !Array.isArray(parsed.ids) || !Array.isArray(parsed.tones)) return emptyHistory();
      return {
        ids: parsed.ids.slice(0, MAX_RECENT_IDS),
        tones: parsed.tones.slice(0, MAX_RECENT_TONES)
      };
    } catch (_) {
      return emptyHistory();
    }
  }

  function saveHistory(history) {
    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(history));
    } catch (_) {
      // La selección continúa aunque el navegador bloquee almacenamiento local.
    }
  }

  function remember(quote) {
    if (!quote) return;
    const history = loadHistory();
    history.ids = [quote.id, ...history.ids.filter(id => id !== quote.id)].slice(0, MAX_RECENT_IDS);
    history.tones = [quote.tone, ...history.tones.filter(tone => tone !== quote.tone)].slice(0, MAX_RECENT_TONES);
    saveHistory(history);
  }

  // Replica la fórmula de "Motor de selección - QA":
  // Aprobada + No duplicado + derechos/verificación válidos + calidad >= 4
  // y sensibilidad Normal en General o distinta de Revisar en Contextual.
  function productionEligible(quote, mode = 'General') {
    if (!quote || !quote.id) return false;
    if (quote.editorialStatus !== 'Aprobada') return false;
    if (quote.duplicate !== 'No') return false;
    if (!quote.rights || quote.rights === 'Por verificar' || quote.rights === 'No utilizable') return false;
    if (!quote.verification || quote.verification === 'C') return false;
    if (Number(quote.quality) < 4) return false;

    if (mode === 'General') return quote.sensitivity === 'Normal';
    return quote.sensitivity !== 'Revisar';
  }

  function matchesOptionalFilters(quote, options = {}) {
    if (options.category && quote.category !== options.category) return false;
    if (options.need && quote.need !== options.need) return false;
    return true;
  }

  function basePool(list = runtimeQuotes, options = {}) {
    const mode = options.mode || 'General';
    return list
      .filter(Boolean)
      .filter(quote => productionEligible(quote, mode))
      .filter(quote => matchesOptionalFilters(quote, options));
  }

  function randomItem(list) {
    if (!list.length) return null;
    return list[Math.floor(Math.random() * list.length)];
  }

  function eligiblePool(list = runtimeQuotes, options = {}) {
    const history = options.ignoreHistory ? emptyHistory() : loadHistory();
    const excludedIds = new Set([...(options.excludeIds || []), ...history.ids]);
    const excludedTones = new Set([...(options.excludeTones || []), ...history.tones]);
    const base = basePool(list, options);

    // 1. Respeta simultáneamente los 3 IDs y 2 tonos recientes.
    let pool = base.filter(quote => !excludedIds.has(quote.id) && !excludedTones.has(quote.tone));

    // 2. Si el filtro deja una colección pequeña sin salida, relaja solo el tono.
    if (!pool.length) pool = base.filter(quote => !excludedIds.has(quote.id));

    // 3. Último recurso: mantiene todos los filtros editoriales y contextuales.
    if (!pool.length) pool = base;

    return pool;
  }

  function select(list = runtimeQuotes, options = {}) {
    const quote = randomItem(eligiblePool(list, options));
    if (quote && !options.doNotRemember) remember(quote);
    return quote;
  }

  function resetHistory() {
    try {
      localStorage.removeItem(STORAGE_KEY);
    } catch (_) {}
  }

  const api = {
    select,
    basePool,
    eligiblePool,
    remember,
    loadHistory,
    resetHistory,
    productionEligible,
    counts: {
      loaded: runtimeQuotes.length,
      general: basePool(runtimeQuotes, { mode: 'General' }).length,
      contextual: basePool(runtimeQuotes, { mode: 'Contextual' }).length
    },
    limits: { recentIds: MAX_RECENT_IDS, recentTones: MAX_RECENT_TONES }
  };

  window.MotivaSelectionEngine = api;

  if (!runtimeQuotes.length) return;

  if (bankQuotes.length && typeof quotesForCategory === 'function') {
    quotesForCategory = category => runtimeQuotes.filter(quote => quote.category === category);
  }

  if (bankQuotes.length && typeof quotesForNeed === 'function') {
    quotesForNeed = need => runtimeQuotes.filter(quote => quote.need === need);
  }

  if (typeof randomFrom === 'function') {
    randomFrom = list => api.select(list, { mode: 'General' });
  }

  // La frase diaria sigue estable por fecha local y se recuerda para evitar
  // una repetición inmediata en las selecciones interactivas.
  if (bankQuotes.length && typeof dayIndex === 'function') {
    const dailyPool = api.basePool(runtimeQuotes, { mode: 'General' });
    const productionToday = dailyPool[dayIndex(dailyPool.length)];
    if (productionToday) {
      const dailyQuoteElement = document.querySelector('#dailyQuote');
      const dailyCategoryElement = document.querySelector('#dailyCategory');
      const dailyAuthorElement = document.querySelector('#dailyAuthor');
      const dailySourceElement = document.querySelector('#dailySource');
      if (dailyQuoteElement) dailyQuoteElement.textContent = productionToday.text;
      if (dailyCategoryElement) dailyCategoryElement.textContent = productionToday.category;
      if (dailyAuthorElement) dailyAuthorElement.textContent = productionToday.author || 'Motiva';
      if (dailySourceElement) dailySourceElement.textContent = productionToday.source || '';
      remember(productionToday);
    }
  } else if (typeof todayQuote !== 'undefined' && todayQuote) {
    remember(normalizeDemoQuote(todayQuote));
  }
})();
