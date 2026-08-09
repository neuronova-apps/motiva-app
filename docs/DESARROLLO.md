# Desarrollo de Motiva

## Enfoque de desarrollo

Motiva separa la experiencia pública del trabajo editorial interno. La interfaz debe mantenerse sencilla aunque el banco maestro contenga una estructura compleja de clasificación y revisión.

## Componentes actuales

La versión web incorpora:

- portada clara y serena;
- frase del día;
- pausa reflexiva;
- exploración por necesidad;
- categorías;
- generador de frases de exhibición;
- atribución de autor y fuente cuando corresponde;
- módulo central de accesibilidad de Neuronova Apps.

## Selección de contenido

La primera versión pública utiliza una selección curada de frases aptas del banco maestro.

No se deben publicar automáticamente entradas con estados que indiquen revisión pendiente, descarte, dudas de derechos, duplicidad o sensibilidad que requiera evaluación adicional.

## Frase del día

La frase principal se determina a partir de la fecha local del usuario para mantenerse estable durante el día y cambiar al comenzar una nueva fecha.

## Arquitectura de contenido

El banco maestro contempla, entre otros, los siguientes campos:

- ID;
- frase;
- autor;
- fuente;
- categoría principal y secundarias;
- necesidad;
- tono;
- espiritualidad;
- longitud;
- derechos;
- verificación;
- calidad;
- estado editorial;
- duplicado;
- sensibilidad.

La aplicación pública debe utilizar únicamente los campos necesarios para la experiencia, mientras la información editorial permanece en el flujo interno.

## Accesibilidad

Motiva consume el módulo central de Neuronova Apps para ofrecer ampliación de texto, contraste, espaciado, lectura amigable para dislexia, guía de lectura, reducción de movimiento y foco reforzado.

## Pruebas prioritarias

1. estabilidad de la frase diaria;
2. correspondencia entre categoría, necesidad y frase;
3. atribución correcta;
4. exclusión de contenidos no aprobados;
5. legibilidad en dispositivos móviles;
6. funcionamiento con alto contraste y texto ampliado;
7. navegación completa mediante teclado.

## Evolución técnica

Una etapa futura podrá automatizar la exportación desde el banco maestro hacia un archivo de publicación, filtrando únicamente registros aprobados antes de que lleguen a la web o a la aplicación.

## Estado

Desarrollo activo.