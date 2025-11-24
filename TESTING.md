# Documentación de Pruebas - WayuuNext

## 📋 Índice
- [Resumen de Pruebas](#resumen-de-pruebas)
- [Estrategia de Testing](#estrategia-de-testing)
- [Configuración del Entorno](#configuración-del-entorno)
- [Tipos de Pruebas Implementadas](#tipos-de-pruebas-implementadas)
- [Ejecución de Pruebas](#ejecución-de-pruebas)
- [Resultados](#resultados)

---

## 🎯 Resumen de Pruebas

**Total de Pruebas Implementadas:** 49  
**Estado:** ✅ 49/49 Exitosas  
**Cobertura:** Autenticación JWT completa  
**Framework:** JUnit 5 + Mockito + Spring Boot Test  
**Base de Datos de Test:** H2 (in-memory)

### Distribución de Pruebas

| Tipo de Prueba | Clase | Cantidad | Estado |
|----------------|-------|----------|--------|
| **Pruebas Unitarias** | `AuthServiceTest` | 15 | ✅ |
| **Pruebas Unitarias** | `JwtAuthenticationFilterTest` | 12 | ✅ |
| **Pruebas de Integración** | `AuthControllerTest` | 10 | ✅ |
| **Pruebas de Integración** | `JwUtilTest` | 12 | ✅ |

---

## 🏗️ Estrategia de Testing

Seguimos la **Pirámide de Testing** con tres niveles:

```
         /\
        /  \       Pruebas E2E (futuro)
       /____\      
      /      \     Pruebas de Integración (22)
     /________\    
    /          \   Pruebas Unitarias (27)
   /____________\  
```

### 1. Pruebas Unitarias (27 tests)
- **Sin dependencias externas**: Usan mocks para simular dependencias
- **Rápidas**: Se ejecutan en milisegundos
- **Cobertura**: Lógica de negocio y filtros de seguridad

### 2. Pruebas de Integración (22 tests)
- **Con Spring Context**: Levantan el contexto completo de Spring Boot
- **Base de datos H2**: BD en memoria que se crea/destruye por test
- **Cobertura**: Endpoints HTTP, beans de Spring, integración de componentes

---

## ⚙️ Configuración del Entorno

### Dependencias de Testing

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-test</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>test</scope>
</dependency>
```

### Perfil de Test (application-test.properties)

```properties
# H2 en memoria
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# JPA con H2
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=false

# JWT
jwt.secret=badabun12
```

---

## 🧪 Tipos de Pruebas Implementadas

### 1. AuthServiceTest (15 pruebas unitarias)

**Objetivo:** Validar la lógica de autenticación sin dependencias externas

| # | Test | Validación |
|---|------|------------|
| 1 | `testLoginSuccess` | Login exitoso genera token |
| 2 | `testLoginFailUserNotFound` | Usuario inexistente rechazado |
| 3 | `testLoginFailWrongPassword` | Contraseña incorrecta rechazada |
| 4 | `testGetUsuarioByUsername` | Recuperación correcta de usuario |
| 5 | `testGetUsuarioByUsernameNotFound` | Usuario no encontrado maneja excepción |
| 6 | `testLoginWithNullUsername` | Username null rechazado |
| 7 | `testLoginWithNullPassword` | Password null rechazado |
| 8 | `testLoginWithEmptyUsername` | Username vacío rechazado |
| 9 | `testLoginWithEmptyPassword` | Password vacío rechazado |
| 10 | `testPasswordEncoderUsed` | PasswordEncoder usado correctamente |
| 11 | `testTokenGeneratedWithCorrectUser` | Token contiene usuario correcto |
| 12 | `testMultipleFailedAttempts` | Múltiples intentos fallidos rechazados |
| 13 | `testSuccessfulLoginAfterFailedAttempt` | Sistema permite reintentos |
| 14 | `testGetUsuarioReturnsCompleteObject` | Usuario completo recuperado |
| 15 | `testCaseSensitiveUsername` | Username case-sensitive validado |

**Técnicas:**
- Mocking con Mockito (`@Mock`, `@InjectMocks`)
- Stubbing (`when().thenReturn()`, `when().thenThrow()`)
- Verificación de invocaciones (`verify()`)

---

### 2. JwtAuthenticationFilterTest (12 pruebas unitarias)

**Objetivo:** Validar el filtro de autenticación JWT sin Spring Context

| # | Test | Validación |
|---|------|------------|
| 1 | `testValidTokenAuthentication` | Token válido autentica usuario |
| 2 | `testInvalidTokenRejection` | Token inválido rechazado |
| 3 | `testNoAuthorizationHeader` | Petición sin header manejada |
| 4 | `testNoBearerPrefix` | Header sin 'Bearer' rechazado |
| 5 | `testTokenExtraction` | Token extraído correctamente |
| 6 | `testExistingAuthenticationPreserved` | Autenticación existente preservada |
| 7 | `testExceptionPropagation` | Excepciones propagadas correctamente |
| 8 | `testEmptyHeader` | Header vacío manejado |
| 9 | `testBearerWithoutToken` | 'Bearer' sin token manejado |
| 10 | `testFilterChainContinues` | Cadena de filtros continúa siempre |
| 11 | `testAuthenticationDetails` | Detalles de request incluidos |
| 12 | `testTokenWithExtraSpaces` | Token con espacios procesado |

**Técnicas:**
- Mocking de interfaces Servlet (`HttpServletRequest`, `FilterChain`)
- Validación de SecurityContext
- Manejo de excepciones

---

### 3. AuthControllerTest (10 pruebas de integración)

**Objetivo:** Validar endpoints HTTP de autenticación con Spring Boot completo

| # | Test | Validación |
|---|------|------------|
| 1 | `testLoginSuccess` | POST /api/auth/login exitoso (HTTP 200) |
| 2 | `testLoginFailureInvalidCredentials` | Credenciales inválidas (HTTP 401) |
| 3 | `testLoginUserNotFound` | Usuario inexistente (HTTP 401) |
| 4 | `testLoginWithoutUsername` | Request sin username (HTTP 401) |
| 5 | `testLoginWithoutPassword` | Request sin password (HTTP 401) |
| 6 | `testLoginWithEmptyBody` | Body vacío (HTTP 401) |
| 7 | `testLoginWithInvalidJson` | JSON malformado (HTTP 400) |
| 8 | `testTokenGeneration` | Token formato correcto en respuesta |
| 9 | `testUserIdInResponse` | UserId retornado correctamente |
| 10 | `testLoginWithWrongPassword` | Contraseña incorrecta (HTTP 401) |

**Técnicas:**
- MockMvc para peticiones HTTP simuladas
- `@SpringBootTest` + `@AutoConfigureMockMvc`
- `@MockBean` para servicios
- JSON Path assertions

---

### 4. JwUtilTest (12 pruebas de integración)

**Objetivo:** Validar utilidad JWT con bean real de Spring

| # | Test | Validación |
|---|------|------------|
| 1 | `testGenerateToken` | Token JWT generado correctamente |
| 2 | `testValidateValidToken` | Token válido validado |
| 3 | `testValidateInvalidToken` | Token inválido rechazado |
| 4 | `testGetUsernameFromToken` | Username extraído correctamente |
| 5 | `testGenerateDifferentTokensForDifferentUsers` | Tokens únicos por usuario |
| 6 | `testValidateEmptyToken` | Token vacío rechazado |
| 7 | `testValidateNullToken` | Token null rechazado |
| 8 | `testTokenContainsCorrectInformation` | Token contiene datos correctos |
| 9 | `testGenerateMultipleTokensForSameUser` | Múltiples tokens válidos |
| 10 | `testValidateMalformedToken` | Token malformado rechazado |
| 11 | `testGeneratedTokenIsNotNull` | Token nunca es null |
| 12 | `testGetUsernameFromInvalidToken` | Excepción en token inválido |

**Técnicas:**
- `@SpringBootTest` para contexto completo
- `@Autowired` para inyección de beans reales
- Validación de estructura JWT (3 partes)

---

## 🚀 Ejecución de Pruebas

### Ejecutar todas las pruebas JWT
```bash
./mvnw test -Dtest="AuthControllerTest,JwUtilTest,JwtAuthenticationFilterTest,AuthServiceTest"
```

### Ejecutar solo pruebas unitarias
```bash
./mvnw test -Dtest="AuthServiceTest,JwtAuthenticationFilterTest"
```

### Ejecutar solo pruebas de integración
```bash
./mvnw test -Dtest="AuthControllerTest,JwUtilTest"
```

### Ejecutar todas las pruebas del proyecto
```bash
./mvnw test
```

### Ver logs detallados
```bash
./mvnw test -X
```

---

## 📊 Resultados

### Última Ejecución (24 de Noviembre, 2025)

```
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.example.wayuunext_app.service.AuthServiceTest
[INFO] Tests run: 15, Failures: 0, Errors: 0, Skipped: 0

[INFO] Running com.example.wayuunext_app.controller.AuthControllerTest
[INFO] Tests run: 10, Failures: 0, Errors: 0, Skipped: 0

[INFO] Running com.example.wayuunext_app.security.JwtAuthenticationFilterTest
[INFO] Tests run: 12, Failures: 0, Errors: 0, Skipped: 0

[INFO] Running com.example.wayuunext_app.security.JwUtilTest
[INFO] Tests run: 12, Failures: 0, Errors: 0, Skipped: 0

[INFO] Results:
[INFO] 
[INFO] Tests run: 49, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] BUILD SUCCESS
```

### Tiempo de Ejecución

| Clase de Test | Tiempo |
|---------------|--------|
| AuthServiceTest | ~2.1s |
| AuthControllerTest | ~5.5s |
| JwtAuthenticationFilterTest | ~0.3s |
| JwUtilTest | ~1.9s |
| **Total** | **~12.9s** |

### Logs Informativos

Cada prueba exitosa genera un log descriptivo:

```
✅ Test 1 EXITOSO: Login con credenciales válidas - Token generado correctamente para usuario 'testuser'
✅ Test 2 EXITOSO: Token válido validado correctamente - isValid=true
✅ Test 7 EXITOSO: Excepción en parsing de token propagada correctamente - Seguridad mantenida
✅ Test 10 EXITOSO: Contraseña incorrecta rechazada - HTTP 401 con campo de error
```

---

## 🔍 Cobertura de Funcionalidades

### ✅ Funcionalidades Probadas

- **Autenticación básica**: Login con username/password
- **Generación de JWT**: Tokens válidos con claims correctos
- **Validación de JWT**: Verificación de firma y expiración
- **Extracción de información**: Username desde token
- **Filtro de seguridad**: Interceptación de peticiones HTTP
- **Manejo de errores**: Credenciales inválidas, tokens malformados
- **Validaciones de entrada**: Campos nulos, vacíos, malformados
- **Respuestas HTTP**: Status codes correctos (200, 401, 400)
- **Seguridad**: Case-sensitivity, múltiples intentos, tokens únicos

### 🔄 Próximas Pruebas (Roadmap)

- [ ] **Pruebas de Expiración**: Tokens expirados rechazados
- [ ] **Pruebas de Refresh Token**: Renovación de tokens
- [ ] **Pruebas de Roles**: RBAC (Role-Based Access Control)
- [ ] **Pruebas de Performance**: Tiempo de respuesta bajo carga
- [ ] **Pruebas de Seguridad**: SQL Injection, XSS
- [ ] **Pruebas E2E**: Selenium/Cypress con frontend

---

## 🛠️ Tecnologías Utilizadas

| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| JUnit Jupiter | 5.10.5 | Framework de testing |
| Mockito | 5.x | Mocking framework |
| Spring Boot Test | 3.3.5 | Testing de integración |
| H2 Database | Latest | Base de datos en memoria |
| MockMvc | 6.x | Testing de controllers |
| AssertJ | 3.x | Assertions fluidas |

---

## 📚 Buenas Prácticas Implementadas

✅ **AAA Pattern**: Arrange, Act, Assert en cada test  
✅ **Test Isolation**: Cada test es independiente  
✅ **Descriptive Names**: `@DisplayName` con descripciones claras  
✅ **Fast Tests**: Uso de mocks y H2 para rapidez  
✅ **Clean Tests**: Un concepto por test  
✅ **Logging**: Logs informativos en tests exitosos  
✅ **No Database Dependency**: Tests unitarios sin BD  
✅ **Profile Separation**: Configuración específica para tests  

---

## 🎓 Aprendizajes y Notas

### ¿Por qué H2 y no PostgreSQL para tests?

1. **Velocidad**: 10-100x más rápido que BD externa
2. **Aislamiento**: Cada test empieza con BD limpia
3. **CI/CD Friendly**: No requiere infraestructura
4. **Portabilidad**: Funciona en cualquier máquina

### ¿Cuándo usar @Mock vs @MockBean?

- **@Mock**: Tests unitarios puros sin Spring
- **@MockBean**: Tests de integración con Spring Context

### ¿Por qué 27 unitarias vs 22 de integración?

Seguimos la pirámide: más tests unitarios (rápidos, baratos) y menos tests de integración (lentos, costosos).

---

## 📞 Contacto y Contribución

Para agregar nuevas pruebas o reportar issues:

1. Crea un archivo de test en `src/test/java/`
2. Usa `@DisplayName` descriptivo
3. Agrega logs informativos con `log.info()`
4. Actualiza esta documentación

---

**Última actualización:** 24 de Noviembre, 2025  
**Autor:** Morgan  
**Proyecto:** WayuuNext - Plataforma Educativa Wayúu
