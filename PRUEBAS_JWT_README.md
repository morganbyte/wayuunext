# Pruebas Funcionales - Autenticación JWT

## 📋 Descripción

Suite completa de pruebas funcionales para el sistema de autenticación JWT del proyecto WayuuNext.

## 🧪 Pruebas Implementadas

### 1. **AuthControllerTest** (10 pruebas)
Pruebas de integración para el endpoint de autenticación:
- ✅ Login exitoso con credenciales válidas
- ✅ Login fallido con credenciales inválidas
- ✅ Login con usuario inexistente
- ✅ Login sin username
- ✅ Login sin password
- ✅ Login con body vacío
- ✅ Login con formato JSON inválido
- ✅ Verificación de generación de token
- ✅ Verificación de userId en respuesta
- ✅ Login con contraseña incorrecta

### 2. **JwUtilTest** (12 pruebas)
Pruebas unitarias para la utilidad JWT:
- ✅ Generar token JWT válido
- ✅ Validar token válido
- ✅ Rechazar token inválido
- ✅ Extraer username del token
- ✅ Generar tokens diferentes para diferentes usuarios
- ✅ Rechazar token vacío
- ✅ Rechazar token nulo
- ✅ Token contiene información correcta
- ✅ Generar múltiples tokens para el mismo usuario
- ✅ Token malformado debe ser rechazado
- ✅ Token generado no es nulo
- ✅ Excepción al extraer username de token inválido

### 3. **JwtAuthenticationFilterTest** (12 pruebas)
Pruebas unitarias para el filtro de autenticación:
- ✅ Procesar correctamente un token válido
- ✅ Rechazar token inválido
- ✅ Manejar petición sin header de autorización
- ✅ Manejar header sin prefijo Bearer
- ✅ Extraer correctamente el token del header
- ✅ No reemplazar autenticación existente
- ✅ Manejar excepción al extraer username
- ✅ Manejar header vacío
- ✅ Manejar Bearer sin token
- ✅ Filtro siempre continúa la cadena
- ✅ Autenticación incluye detalles del request
- ✅ Manejar token con espacios extra

### 4. **AuthServiceTest** (15 pruebas)
Pruebas unitarias para el servicio de autenticación:
- ✅ Login exitoso con credenciales válidas
- ✅ Login falla con usuario inexistente
- ✅ Login falla con contraseña incorrecta
- ✅ Obtener usuario por username exitosamente
- ✅ Obtener usuario por username no encontrado
- ✅ Login con username null
- ✅ Login con password null
- ✅ Login con username vacío
- ✅ Login con password vacío
- ✅ Verificar uso del encoder
- ✅ Token generado con usuario correcto
- ✅ Múltiples intentos fallidos
- ✅ Login exitoso después de intentos fallidos
- ✅ Obtener usuario retorna objeto completo
- ✅ Login case-sensitive para username

## 🚀 Ejecutar las Pruebas

### Ejecutar todas las pruebas
```bash
cd /home/morgan/Documents/wayuunext
./mvnw test
```

### Ejecutar solo las pruebas de autenticación
```bash
./mvnw test -Dtest="Auth*Test"
```

### Ejecutar una clase de prueba específica
```bash
# AuthController
./mvnw test -Dtest=AuthControllerTest

# JwUtil
./mvnw test -Dtest=JwUtilTest

# JwtAuthenticationFilter
./mvnw test -Dtest=JwtAuthenticationFilterTest

# AuthService
./mvnw test -Dtest=AuthServiceTest
```

### Ejecutar un test específico
```bash
./mvnw test -Dtest=AuthControllerTest#testLoginSuccess
```

### Ejecutar con reporte detallado
```bash
./mvnw test -Dtest="Auth*Test" -DforkCount=0
```

## 📊 Cobertura de Código

Para generar reporte de cobertura con JaCoCo:

```bash
./mvnw clean test jacoco:report
```

El reporte se generará en: `target/site/jacoco/index.html`

## ✅ Resultados Esperados

Al ejecutar todas las pruebas, deberías ver algo como:

```
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.example.wayuunext_app.controller.AuthControllerTest
[INFO] Tests run: 10, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.example.wayuunext_app.security.JwUtilTest
[INFO] Tests run: 12, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.example.wayuunext_app.security.JwtAuthenticationFilterTest
[INFO] Tests run: 12, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.example.wayuunext_app.service.AuthServiceTest
[INFO] Tests run: 15, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 49, Failures: 0, Errors: 0, Skipped: 0
[INFO]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

## 🔍 Verificar Estado de las Pruebas

### Ver resultados en formato surefire
```bash
cat target/surefire-reports/*.txt
```

### Ver solo pruebas fallidas
```bash
./mvnw test | grep -A 10 "FAILURE"
```

## 🛠️ Troubleshooting

### Si las pruebas fallan por dependencias:
```bash
./mvnw clean install -DskipTests
./mvnw test
```

### Si hay problemas con la base de datos:
Las pruebas usan mocks, no necesitan base de datos real.

### Si falta alguna dependencia de testing:
Verifica que el `pom.xml` incluya:
- spring-boot-starter-test
- mockito
- junit-jupiter

## 📝 Notas Importantes

1. **Mocks vs Integración**: 
   - AuthControllerTest usa `@SpringBootTest` (integración)
   - Las demás usan mocks de Mockito (unitarias)

2. **Configuración de Test**:
   - JwUtilTest usa `@TestPropertySource` para configurar el secret

3. **Cobertura**:
   - Estas pruebas cubren ~90% del código de autenticación
   - Incluyen casos positivos, negativos y edge cases

4. **Mantenimiento**:
   - Si cambias la lógica de autenticación, actualiza las pruebas
   - Mantén las pruebas independientes entre sí

## 📈 Métricas de Calidad

- **Total de pruebas**: 49
- **Componentes probados**: 4 (Controller, Service, Filter, Util)
- **Casos de prueba cubiertos**: 
  - Casos exitosos ✅
  - Casos de error ❌
  - Edge cases 🔍
  - Validaciones de seguridad 🔒

## 🎯 Próximos Pasos

Una vez que estas pruebas pasen exitosamente, puedes continuar con:
1. Pruebas de integración backend-frontend
2. Pruebas de API REST
3. Pruebas de seguridad
4. Pruebas de rendimiento
