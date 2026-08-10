(() => {
  'use strict';

  const STORAGE_KEY = 'motiva.selectionHistory.v1';
  const MAX_RECENT_IDS = 3;
  const MAX_RECENT_TONES = 2;

  // Metadatos mínimos para la colección pública actual.
  // El motor acepta los mismos campos del banco maestro y queda preparado
  // para sustituir esta capa por el dataset completo de producción.
  const metadataById = {
    FR000001: { tone: 'Reflexivo' },
    FR000002: { tone: 'Motivador' },
    FR000003: { tone: 'Sereno' },
    FR000004: { tone: 'Directo' },
    FR000005: { tone: 'Reflexivo' },
    FR000006: { tone: 'Motivador' },
    FR000007: { tone: 'Sereno' },
    FR000008: { tone: 'Cálido' },
    FR000009: { tone: 'Acompañante' },
    FR000010: { tone: 'Acompañante' },
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

  function normalizeQuote(quote) {
    const extra = metadataById[quote.id] || {};
    return Object.assign(quote, {
      tone: quote.tone || extra.tone || 'Acompañante',
      sensitivity: quote.sensitivity || extra.sensitivity || 'Normal',
      rights: quote.rights || extra.rights || 'Original',
      verification: quote.verification || extra.verification || 'Original',
      quality: Number(quote.quality || extra.quality || 5),
      editorialStatus: quote.editorialStatus || extra.editorialStatus || 'Aprobada',
      duplicate: quote.duplicate || extra.duplicate || 'No'
    });
  }

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

  function productionEligible(quote, mode = 'General') {
    if (!quote) return false;
    if (quote.editorialStatus !== 'Aprobada') return false;
    if (quote.duplicate !== 'No') return false;
    if (quote.quality < 4) return false;
    if (quote.rights === 'Por verificar') return false;
    if (quote.verification === 'C') return false;
    if (mode === 'General' && quote.sensitivity !== 'Normal') return false;
    if (mode === 'Contextual' && !['Normal', 'Emocional'].includes(quote.sensitivity)) return false;
    return true;
  }

  function randomItem(list) {
    if (!list.length) return null;
    return list[Math.floor(Math.random() * list.length)];
  }

  function eligiblePool(list, options = {}) {
    const mode = options.mode || 'General';
    const history = loadHistory();
    const excludedIds = new Set([...(options.excludeIds || []), ...history.ids]);
    const excludedTones = new Set([...(options.excludeTones || []), ...history.tones]);

    const base = list.map(normalizeQuote).filter(quote => productionEligible(quote, mode));

    // Primera elección: respeta simultáneamente los 3 IDs y 2 tonos recientes.
    let pool = base.filter(quote => !excludedIds.has(quote.id) && !excludedTones.has(quote.tone));

    // Si el filtro deja una colección pequeña sin salida, se relaja primero el tono.
    if (!pool.length) {
      pool = base.filter(quote => !excludedIds.has(quote.id));
    }

    // Último recurso: conserva los filtros editoriales y de seguridad, pero permite
    // reutilizar historial para no dejar al usuario sin contenido.
    if (!pool.length) {
      pool = base;
    }

    return pool;
  }

  function select(list, options = {}) {
    const pool = eligiblePool(list, options);
    const quote = randomItem(pool);
    if (quote) remember(quote);
    return quote;
  }

  function resetHistory() {
    try {
      localStorage.removeItem(STORAGE_KEY);
    } catch (_) {}
  }

  const api = {
    select,
    eligiblePool,
    remember,
    loadHistory,
    resetHistory,
    productionEligible,
    normalizeQuote,
    limits: { recentIds: MAX_RECENT_IDS, recentTones: MAX_RECENT_TONES }
  };

  window.MotivaSelectionEngine = api;

  // Integra el motor con la versión web actual sin cambiar su interfaz.
  if (typeof quotes !== 'undefined' && Array.isArray(quotes)) {
    quotes.forEach(normalizeQuote);
  }

  if (typeof randomFrom === 'function') {
    randomFrom = function motivaRandomFrom(list) {
      return api.select(list, { mode: 'General' });
    };
  }

  // La frase diaria se mantiene estable por fecha, pero se registra para evitar
  // que la selección interactiva la repita inmediatamente.
  if (typeof todayQuote !== 'undefined' && todayQuote) {
    normalizeQuote(todayQuote);
    remember(todayQuote);
  }
})();
