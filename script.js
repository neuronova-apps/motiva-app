const menuButton = document.querySelector('.menu-button');
const mainNav = document.querySelector('.main-nav');
const year = document.querySelector('#year');
const reduceMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;

if (year) year.textContent = new Date().getFullYear();

if (menuButton && mainNav) {
  const closeMenu = () => {
    mainNav.classList.remove('open');
    menuButton.setAttribute('aria-expanded', 'false');
    menuButton.setAttribute('aria-label', 'Abrir menú de navegación');
  };

  menuButton.addEventListener('click', () => {
    const open = mainNav.classList.toggle('open');
    menuButton.setAttribute('aria-expanded', String(open));
    menuButton.setAttribute('aria-label', open ? 'Cerrar menú de navegación' : 'Abrir menú de navegación');
  });

  mainNav.querySelectorAll('a').forEach(link => link.addEventListener('click', closeMenu));
  document.addEventListener('keydown', event => {
    if (event.key === 'Escape') closeMenu();
  });
}

const revealItems = document.querySelectorAll('.reveal');
if (reduceMotion || !('IntersectionObserver' in window)) {
  revealItems.forEach(item => item.classList.add('visible'));
} else {
  const observer = new IntersectionObserver(entries => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        entry.target.classList.add('visible');
        observer.unobserve(entry.target);
      }
    });
  }, {threshold: .1});
  revealItems.forEach(item => observer.observe(item));
}

const categories = [
  'Filosofía',
  'Fe y espiritualidad',
  'Superación',
  'Motivación',
  'Momentos difíciles',
  'Amor y vínculos',
  'Familia',
  'Amistad',
  'Autoconocimiento',
  'Metas y logros',
  'Sabiduría y aprendizaje',
  'Paz y bienestar',
  'Gratitud',
  'Esperanza',
  'Calma',
  'Reflexión',
  'Humor e ingenio',
  'Literatura y autores',
  'Astrología'
];

const needs = [
  'Calma',
  'Esperanza',
  'Seguir adelante',
  'Reflexionar',
  'Enfocarme',
  'Agradecer',
  'Comprenderme',
  'Tomar decisión',
  'Aprender',
  'Recuperar confianza',
  'Conectar',
  'Aligerar',
  'Impulso',
  'Aceptar cambios',
  'Inspiración',
  'Asumir responsabilidad'
];

// Selección de exhibición tomada del banco maestro.
// Solo se incluyen aquí entradas aptas para esta primera versión pública.
const quotes = [
  {
    id: 'FR000001',
    text: 'La claridad suele comenzar cuando dejamos de apresurar la respuesta.',
    author: 'Motiva',
    source: 'Colección original de la app',
    categories: ['Filosofía','Autoconocimiento','Sabiduría y aprendizaje','Reflexión'],
    need: 'Reflexionar'
  },
  {
    id: 'FR000002',
    text: 'Avanzar despacio sigue siendo avanzar cuando la dirección tiene sentido.',
    author: 'Motiva',
    source: 'Colección original de la app',
    categories: ['Superación','Metas y logros','Motivación'],
    need: 'Seguir adelante'
  },
  {
    id: 'FR000003',
    text: 'No todo merece una respuesta inmediata; algunas cosas se entienden mejor después de un poco de silencio.',
    author: 'Motiva',
    source: 'Colección original de la app',
    categories: ['Paz y bienestar','Filosofía','Autoconocimiento','Calma'],
    need: 'Calma'
  },
  {
    id: 'FR000004',
    text: 'Una meta se vuelve menos distante cuando la conviertes en una acción concreta.',
    author: 'Motiva',
    source: 'Colección original de la app',
    categories: ['Metas y logros','Motivación','Superación'],
    need: 'Enfocarme'
  },
  {
    id: 'FR000005',
    text: 'Cambiar de opinión después de comprender algo mejor también es una forma de crecimiento.',
    author: 'Motiva',
    source: 'Colección original de la app',
    categories: ['Autoconocimiento','Sabiduría y aprendizaje','Filosofía','Reflexión'],
    need: 'Reflexionar'
  },
  {
    id: 'FR000006',
    text: 'La constancia no exige hacerlo todo; exige volver a aquello que has decidido cuidar.',
    author: 'Motiva',
    source: 'Colección original de la app',
    categories: ['Superación','Metas y logros','Motivación'],
    need: 'Seguir adelante'
  },
  {
    id: 'FR000007',
    text: 'Una pausa consciente puede convertir un día acelerado en un momento que realmente alcanzas a vivir.',
    author: 'Motiva',
    source: 'Colección original de la app',
    categories: ['Paz y bienestar','Gratitud','Autoconocimiento','Calma'],
    need: 'Calma'
  },
  {
    id: 'FR000008',
    text: 'Agradecer una presencia es recordar que ningún vínculo valioso debería darse por hecho.',
    author: 'Motiva',
    source: 'Colección original de la app',
    categories: ['Amor y vínculos','Gratitud','Familia','Amistad'],
    need: 'Agradecer'
  },
  {
    id: 'FR000009',
    text: 'Conocerte mejor no significa tener todas las respuestas, sino reconocer con más claridad tus propias preguntas.',
    author: 'Motiva',
    source: 'Colección original de la app',
    categories: ['Autoconocimiento','Filosofía','Sabiduría y aprendizaje','Reflexión'],
    need: 'Comprenderme'
  },
  {
    id: 'FR000010',
    text: 'Tu progreso no pierde valor porque otra persona avance a un ritmo diferente.',
    author: 'Motiva',
    source: 'Colección original de la app',
    categories: ['Superación','Autoconocimiento','Motivación'],
    need: 'Recuperar confianza'
  },
  {
    id: 'FR000011',
    text: 'Cuando todo parece demasiado grande, vuelve a la siguiente tarea posible.',
    author: 'Motiva',
    source: 'Colección original de la app',
    categories: ['Motivación','Metas y logros','Superación'],
    need: 'Enfocarme'
  },
  {
    id: 'FR000012',
    text: 'Elegir también significa aceptar que no todos los caminos pueden recorrerse al mismo tiempo.',
    author: 'Motiva',
    source: 'Colección original de la app',
    categories: ['Filosofía','Autoconocimiento','Metas y logros','Reflexión'],
    need: 'Tomar decisión'
  },
  {
    id: 'FR000013',
    text: 'La serenidad puede nacer cuando dejamos de exigirnos controlar aquello que está fuera de nuestras manos.',
    author: 'Motiva',
    source: 'Colección original de la app',
    categories: ['Fe y espiritualidad','Paz y bienestar','Filosofía','Calma'],
    need: 'Calma'
  },
  {
    id: 'FR000014',
    text: 'Hay días en que creer es simplemente dar un paso más sin tener todas las respuestas.',
    author: 'Motiva',
    source: 'Colección original de la app',
    categories: ['Fe y espiritualidad','Esperanza','Superación'],
    need: 'Esperanza'
  },
  {
    id: 'FR000017',
    text: 'Cuidar un vínculo también es aprender a escuchar lo que la otra persona intenta decir.',
    author: 'Motiva',
    source: 'Colección original de la app',
    categories: ['Amor y vínculos','Familia','Amistad'],
    need: 'Conectar'
  },
  {
    id: 'FR000018',
    text: 'La experiencia enseña más cuando dejamos de defender el error y empezamos a observarlo.',
    author: 'Motiva',
    source: 'Colección original de la app',
    categories: ['Sabiduría y aprendizaje','Autoconocimiento','Filosofía','Reflexión'],
    need: 'Aprender'
  },
  {
    id: 'FR000019',
    text: 'A veces el plan perfecto consiste en aceptar que el plan necesitaba otro plan.',
    author: 'Motiva',
    source: 'Colección original de la app',
    categories: ['Humor e ingenio','Metas y logros','Motivación'],
    need: 'Aligerar'
  },
  {
    id: 'FR000020',
    text: 'Empezar con lo posible suele ser más útil que esperar a sentirte completamente preparado.',
    author: 'Motiva',
    source: 'Colección original de la app',
    categories: ['Motivación','Superación','Metas y logros'],
    need: 'Impulso'
  },
  {
    id: 'FR000022',
    text: 'El que lee mucho y anda mucho, ve mucho y sabe mucho.',
    author: 'Miguel de Cervantes Saavedra',
    source: 'Don Quijote de la Mancha · dominio público',
    categories: ['Sabiduría y aprendizaje','Literatura y autores','Filosofía'],
    need: 'Aprender'
  },
  {
    id: 'FR000024',
    text: 'Lo bueno, si breve, dos veces bueno.',
    author: 'Baltasar Gracián',
    source: 'Oráculo manual y arte de prudencia · dominio público',
    categories: ['Sabiduría y aprendizaje','Literatura y autores','Filosofía'],
    need: 'Reflexionar'
  },
  {
    id: 'FR000027',
    text: 'Ser bueno es el único modo de ser dichoso.',
    author: 'José Martí',
    source: 'Maestros ambulantes · dominio público',
    categories: ['Sabiduría y aprendizaje','Literatura y autores','Filosofía'],
    need: 'Asumir responsabilidad'
  },
  {
    id: 'FR000031',
    text: 'Pensar mejor comienza por revisar lo que damos por cierto.',
    author: 'Motiva',
    source: 'Colección original de la app',
    categories: ['Filosofía','Reflexión','Sabiduría y aprendizaje'],
    need: 'Reflexionar'
  }
];

const categoryTones = [
  'rgba(127,164,147,.18)',
  'rgba(184,174,201,.20)',
  'rgba(145,174,184,.18)',
  'rgba(217,201,168,.22)'
];

function dayIndex(length) {
  const now = new Date();
  const key = Number(`${now.getFullYear()}${String(now.getMonth() + 1).padStart(2,'0')}${String(now.getDate()).padStart(2,'0')}`);
  let hash = key;
  hash = ((hash << 5) - hash) + 17;
  return Math.abs(hash) % length;
}

function randomFrom(list) {
  if (!list.length) return null;
  return list[Math.floor(Math.random() * list.length)];
}

function quotesForCategory(category) {
  return quotes.filter(quote => quote.categories.includes(category));
}

function quotesForNeed(need) {
  return quotes.filter(quote => quote.need === need || quote.categories.includes(need));
}

function attribution(quote) {
  if (!quote) return '';
  return quote.author === 'Motiva' ? quote.source : `${quote.author} · ${quote.source}`;
}

// Frase del día: misma selección durante toda la fecha local.
const dailyQuote = document.querySelector('#dailyQuote');
const dailyCategory = document.querySelector('#dailyCategory');
const dailyAuthor = document.querySelector('#dailyAuthor');
const dailySource = document.querySelector('#dailySource');
const dailyDate = document.querySelector('#dailyDate');
const todayQuote = quotes[dayIndex(quotes.length)];

if (dailyQuote && todayQuote) {
  dailyQuote.textContent = todayQuote.text;
  dailyCategory.textContent = todayQuote.categories[0];
  dailyAuthor.textContent = todayQuote.author;
  dailySource.textContent = todayQuote.source;
  dailyDate.textContent = new Intl.DateTimeFormat('es-PE', {weekday:'long', day:'numeric', month:'long'}).format(new Date());
}

// Pausa reflexiva de 20 segundos.
const reflectButton = document.querySelector('#reflectButton');
const reflectionPrompt = document.querySelector('#reflectionPrompt');
const reflectionText = document.querySelector('#reflectionText');
let reflectionTimer = null;

reflectButton?.addEventListener('click', () => {
  const open = reflectionPrompt.hidden;
  reflectionPrompt.hidden = !open;
  reflectButton.setAttribute('aria-expanded', String(open));
  clearInterval(reflectionTimer);

  if (!open) return;
  let seconds = 20;
  reflectionText.textContent = `Lee la frase una vez más, sin prisa. Quedan ${seconds} segundos.`;
  reflectionTimer = setInterval(() => {
    seconds -= 1;
    if (seconds > 0) {
      reflectionText.textContent = `Lee la frase una vez más, sin prisa. Quedan ${seconds} segundos.`;
    } else {
      clearInterval(reflectionTimer);
      reflectionText.textContent = 'Pausa completa. ¿Qué palabra o idea te gustaría conservar hoy?';
    }
  }, 1000);
});

// Necesidades.
const needsGrid = document.querySelector('#needsGrid');
const momentNeed = document.querySelector('#momentNeed');
const momentQuote = document.querySelector('#momentQuote');
const momentAttribution = document.querySelector('#momentAttribution');
const anotherMoment = document.querySelector('#anotherMoment');
let currentNeed = 'Inspiración';

function renderNeedQuote(need, forceDifferent = false) {
  currentNeed = need;
  const pool = quotesForNeed(need);
  const available = forceDifferent && pool.length > 1
    ? pool.filter(item => item.text !== momentQuote.textContent)
    : pool;
  const quote = randomFrom(available.length ? available : pool);

  momentNeed.textContent = need;
  if (quote) {
    momentQuote.textContent = quote.text;
    momentAttribution.textContent = attribution(quote);
  } else {
    momentQuote.textContent = 'Esta categoría forma parte del banco maestro y su colección pública se incorporará progresivamente.';
    momentAttribution.textContent = 'Motiva · contenido en preparación';
  }

  document.querySelectorAll('.need-chip').forEach(button => {
    const active = button.dataset.need === need;
    button.classList.toggle('active', active);
    button.setAttribute('aria-pressed', String(active));
  });
}

if (needsGrid) {
  needs.forEach(need => {
    const button = document.createElement('button');
    button.type = 'button';
    button.className = 'need-chip';
    button.dataset.need = need;
    button.setAttribute('aria-pressed', 'false');
    button.textContent = need;
    button.addEventListener('click', () => renderNeedQuote(need));
    needsGrid.appendChild(button);
  });
  renderNeedQuote('Calma');
}

anotherMoment?.addEventListener('click', () => renderNeedQuote(currentNeed, true));

// Categorías del banco maestro.
const categoryGrid = document.querySelector('#categoryGrid');
const categorySelect = document.querySelector('#categorySelect');

function chooseCategory(category) {
  if (categorySelect) categorySelect.value = category;
  renderGeneratedQuote(category);
  document.querySelector('#explorar')?.scrollIntoView({behavior: reduceMotion ? 'auto' : 'smooth'});
}

categories.forEach((category, index) => {
  if (categorySelect) {
    const option = document.createElement('option');
    option.value = category;
    option.textContent = category;
    categorySelect.appendChild(option);
  }

  if (categoryGrid) {
    const count = quotesForCategory(category).length;
    const button = document.createElement('button');
    button.type = 'button';
    button.className = 'category-card';
    button.style.setProperty('--tone', categoryTones[index % categoryTones.length]);
    button.innerHTML = `<span>${String(index + 1).padStart(2,'0')} · ${count ? `${count} en demo` : 'colección en desarrollo'}</span><strong>${category}</strong>`;
    button.setAttribute('aria-label', `Explorar categoría ${category}`);
    button.addEventListener('click', () => chooseCategory(category));
    categoryGrid.appendChild(button);
  }
});

// Explorador.
const generatedCategory = document.querySelector('#generatedCategory');
const generatedQuote = document.querySelector('#generatedQuote');
const generatedAuthor = document.querySelector('#generatedAuthor');
const generatedSource = document.querySelector('#generatedSource');
const generateQuote = document.querySelector('#generateQuote');
const nextGenerated = document.querySelector('#nextGenerated');
let currentCategory = categories[0];

function renderGeneratedQuote(category = currentCategory, forceDifferent = false) {
  currentCategory = category;
  const pool = quotesForCategory(category);
  const available = forceDifferent && pool.length > 1
    ? pool.filter(item => item.text !== generatedQuote.textContent)
    : pool;
  const quote = randomFrom(available.length ? available : pool);

  generatedCategory.textContent = category;
  if (quote) {
    generatedQuote.textContent = quote.text;
    generatedAuthor.textContent = quote.author;
    generatedSource.textContent = quote.source;
  } else {
    generatedQuote.textContent = 'Esta categoría ya existe en el banco maestro. Su colección pública se incorporará en una siguiente etapa.';
    generatedAuthor.textContent = 'Motiva';
    generatedSource.textContent = 'Colección en desarrollo';
  }
}

generateQuote?.addEventListener('click', () => renderGeneratedQuote(categorySelect.value));
nextGenerated?.addEventListener('click', () => renderGeneratedQuote(currentCategory, true));
categorySelect?.addEventListener('change', () => renderGeneratedQuote(categorySelect.value));
renderGeneratedQuote('Filosofía');
