# Motiva

Motiva es una aplicación de Neuronova Apps orientada a ofrecer frases breves para reflexión, calma, inspiración y acompañamiento cotidiano mediante una experiencia web serena y accesible.

## Estado del proyecto

- **Web:** experiencia funcional en desarrollo activo.
- **Publicación:** disponible mediante GitHub Pages.
- **Android:** existe una rama `android` separada para el desarrollo móvil. Se considera trabajo en progreso y no una versión estable o publicada.

## Funciones disponibles

- frase principal estable durante cada fecha local;
- colección pública actual de 22 frases;
- 19 categorías temáticas;
- 16 necesidades o intenciones de exploración;
- exploración por categoría y necesidad;
- selección aleatoria dentro del contenido disponible;
- atribución visible de autor o fuente cuando corresponde;
- pausa reflexiva breve;
- diseño responsive e integración con la accesibilidad central de Neuronova Apps.

Motiva no constituye un servicio psicológico, terapéutico ni de atención en salud mental. Sus contenidos son de reflexión, inspiración y entretenimiento general.

## Tecnología

La versión web utiliza:

- HTML5;
- CSS3;
- JavaScript en el navegador;
- contenido editorial organizado en estructuras JavaScript;
- GitHub Pages;
- recursos SVG/PNG para identidad y tarjetas sociales;
- módulo de accesibilidad compartido de Neuronova Apps.

No requiere proceso de compilación para ejecutar la web actual.

## Accesibilidad

Motiva integra el núcleo de accesibilidad de Neuronova Apps, con opciones de tamaño de texto, alto contraste, espaciado, interlineado, lectura amigable, guía de lectura, resaltado de enlaces, reducción de movimiento y foco de teclado reforzado.

La aplicación continúa sujeta a revisión manual y estas medidas no equivalen a una certificación WCAG.

## Privacidad

La política pública está disponible en:

https://neuronova-apps.github.io/motiva-app/privacy/

Cualquier función futura que incorpore cuentas, analítica, sincronización o servicios externos deberá reflejarse previamente en esta política.

## Desarrollo local

```bash
git clone https://github.com/neuronova-apps/motiva-app.git
cd motiva-app
python3 -m http.server 8000
```

Después abre `http://localhost:8000`.

La rama `main` corresponde a la web pública. La rama `android` mantiene el desarrollo móvil separado.

## Estructura principal

- `index.html`: interfaz principal;
- `script.js`: frase diaria, categorías y exploración;
- `styles.css`: estilos base;
- `hero-orbit.css`: sistema visual orbital;
- `ecosystem-alignment.css`: alineación con el sistema Neuronova;
- `privacy/`: política pública;
- `assets/social/`: tarjeta social de Motiva;
- recursos compartidos de accesibilidad cargados desde Neuronova Apps.

## Enlaces

- **Web:** https://neuronova-apps.github.io/motiva-app/
- **Privacidad:** https://neuronova-apps.github.io/motiva-app/privacy/
- **Repositorio:** https://github.com/neuronova-apps/motiva-app
- **Ecosistema:** https://neuronova-apps.github.io/

## Neuronova Apps

Motiva forma parte de **Neuronova Apps** y conserva una identidad visual propia dentro de un sistema común de navegación, accesibilidad, privacidad, documentación y publicación.

## Autoría

Proyecto personal e independiente desarrollado por Gabriel Berrospi dentro del ecosistema Neuronova Apps.
