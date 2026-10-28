# Roadmap de WayuuNext

App para aprender wayuunaiki y la cultura wayuu. Proyecto retomado el 28/10/2026.

**Objetivo:** demo sólida → aliados wayuu → piloto → convocatorias (ICANH, Ministerio de Cultura) en el primer trimestre de 2027 → licencia o convenio con instituciones.

---

## Fase 0: Poner orden (1-2 semanas)

### Seguridad
- [x] La API ya no envía la respuesta correcta (`correcta`) antes de contestar; se corrige solo en el servidor
- [x] `/api/tests/create` solo para rol `ADMIN`
- [x] Eliminado `/api/users/add`; el registro ya no permite fijar `id`, `progress` ni `role`
- [x] Eliminado `/api/progress/update` (el cliente podía enviar cualquier puntuación). El progreso se calcula en el servidor al evaluar y repetir un test aprobado no suma dos veces
- [x] Cada usuario solo consulta su propio progreso (`GET /api/progress` con su token)
- [x] Secretos fuera del repo: variables de entorno + `.env` (ver `.env.example`)
- [x] `jjwt` 0.9.1 → 0.12.6; eliminadas las dependencias `javax.*`
- [x] Tests de regresión de seguridad (`SecurityRulesTest`): 55/55 en verde
- [ ] El secreto JWT y la contraseña antiguos siguen en el historial de git: no reutilizarlos nunca
- [ ] Validación de datos en el registro (email válido, longitud de contraseña, usuario repetido → error claro)
- [ ] Manejo de token caducado en el frontend (si la API devuelve 401/403, volver al login)

### Limpieza
- [x] Borrados `node_modules`, `package.json` y `package-lock.json` de la raíz; dependencias movidas a `frontend/`
- [x] Borrado `LessonPage.js` (importaba archivos inexistentes)
- [x] Corregidos el logout (no borraba el token) y `axiosConfig.js`
- [x] `docker-compose.yml` + `Dockerfile` para base de datos y backend
- [x] README con instrucciones de arranque
- [ ] Centralizar la URL de la API en el frontend (hoy `http://localhost:8080` está repetida en varios archivos)
- [ ] Limpiar los `console.log` y los avisos de ESLint

## Fase 1: Que enseñe, no solo evalúe (3-5 semanas)
- [ ] Modelo de contenido: Unidad → Lección → Ejercicios
- [ ] Pantalla de lección: vocabulario con imagen, traducción y audio
- [ ] Tipos de ejercicio: emparejar, ordenar palabras, escuchar y elegir
- [ ] Progreso real: unidades que se desbloquean, porcentaje y racha de días
- [ ] Perfil con historial de resultados (`resultado_usuario` ya se guarda)
- [ ] Diccionario wayuunaiki-español con búsqueda
- [ ] Panel de administración para crear contenido sin tocar SQL

## Fase 2: Lista para enseñarla (2-3 semanas)
- [ ] Migrar el frontend de Create React App a Vite
- [ ] Diseño responsive pensado primero para el móvil
- [ ] PWA con modo sin conexión
- [ ] Despliegue público (frontend en Vercel; backend y base de datos en Railway, Render o Fly.io)
- [ ] Dominio propio y landing con el propósito del proyecto
- [ ] Analítica básica (usuarios, lecciones completadas)

## Fase 3: Comunidad y contenido (en paralelo desde ya)
- [ ] Contactar a 1-3 aliados wayuu (docentes de etnoeducación, Wayuunaiki para el Mundo, creadores de contenido)
- [ ] Revisar con ellos las 35 preguntas actuales
- [ ] Grabar audio de un hablante nativo para el vocabulario
- [ ] Acordar por escrito el reconocimiento y la retribución de su participación
- [ ] Piloto: 1 colegio o grupo durante un mes, con medición antes y después

## Fase 4: Convocatorias (primer trimestre de 2027)
- [ ] Vigilar los estímulos del ICANH (línea de lenguas indígenas) y del Ministerio de Cultura (enero-febrero)
- [ ] Dossier: problema, solución, demo, equipo, resultados del piloto y presupuesto
- [ ] Vídeo demo de 2-3 minutos
- [ ] Decidir la figura legal (persona natural, SAS o fundación)
- [ ] Próximo ciclo de Cultural Survival (cierra en octubre) y hackatones de impacto social

## Fase 5: Con tracción
- [ ] Tutor conversacional con IA apoyado en el diccionario validado
- [ ] Panel para docentes (licencia para colegios)
- [ ] Freemium, kit para turistas o marketplace de artesanía
- [ ] Propuesta a la Secretaría de Educación de La Guajira o al Ministerio de Educación
- [ ] Generalizar el motor para otras lenguas indígenas
