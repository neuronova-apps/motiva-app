# Motiva

Motiva es una aplicación de Neuronova Apps orientada a ofrecer frases breves para reflexión, calma, inspiración y acompañamiento cotidiano mediante una experiencia web serena y accesible.

## Estado del proyecto

- **Web:** experiencia funcional en desarrollo activo.
- **Publicación:** disponible mediante GitHub Pages.
- **Android:** rama `android` separada en trabajo en progreso; no es una versión estable ni publicada.

## Alcance actual

Motiva organiza frases y reflexiones breves para exploración cotidiana. No constituye un servicio psicológico, terapéutico ni de atención en salud mental. Su contenido es de reflexión, inspiración y entretenimiento general.

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

## Tecnología

La versión web utiliza HTML5, CSS3, JavaScript en el navegador, contenido editorial organizado en estructuras JavaScript, GitHub Pages, recursos SVG/PNG y el módulo compartido de accesibilidad de Neuronova Apps. No requiere proceso de compilación.

## Accesibilidad

Motiva integra el núcleo de accesibilidad de Neuronova Apps, con opciones de tamaño de texto, alto contraste, espaciado, interlineado, lectura amigable, guía de lectura, resaltado de enlaces, reducción de movimiento y foco de teclado reforzado.

La superficie pública forma parte de la auditoría automática central del ecosistema. Estas medidas no equivalen a una certificación WCAG y la aplicación continúa sujeta a revisión manual.

## Privacidad

La política pública está disponible en https://neuronova-apps.github.io/motiva-app/privacy/.

Cualquier función futura que incorpore cuentas, analítica, sincronización o servicios externos deberá reflejarse previamente en esta política.

## Limitaciones conocidas

El banco público actual es todavía reducido y no existe personalización persistente, cuenta ni sincronización entre dispositivos. La aplicación no debe presentarse como herramienta clínica o terapéutica. La revisión manual completa de accesibilidad continúa pendiente y la rama Android no es una aplicación publicada.

## Roadmap

Las líneas previstas son ampliar el banco editorial, fortalecer categorías y necesidades, evaluar opciones de personalización compatibles con la política de privacidad y completar pruebas manuales de accesibilidad antes de ampliar afirmaciones públicas sobre conformidad.

## Desarrollo local

```bash
git clone https://github.com/neuronova-apps/motiva-app.git
cd motiva-app
python3 -m http.server 8000
```

Después abre `http://localhost:8000`. La rama `main` corresponde a la web pública y `android` mantiene el desarrollo móvil separado.

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

Motiva forma parte de Neuronova Apps y conserva una identidad visual propia dentro de un sistema común de navegación, accesibilidad, privacidad, documentación y publicación, manteniendo su repositorio independiente.

## Autoría

Proyecto personal e independiente desarrollado por Gabriel Berrospi dentro del ecosistema Neuronova Apps.

## Última revisión

2026-08-15
