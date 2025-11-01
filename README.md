# Sistema de Registro Distribuido con Replicación Consistente

## Descripción General

Este proyecto implementa un sistema distribuido de registrode registro de nombre y sincronizacion de datos entre diferentes instacias de docker. El sistema está compuesto por tres componentes principales: un cliente web (JavaScript), un servicio de registro (Registry Service) y un servicio de sincronización distribuida (HashSync), todos desplegados en contenedores Docker en AWS EC2.

## Componentes del Sistema

### 1. Cliente Web (Client)

**Tecnología**: HTML5 + JavaScript (Vanilla)

**Funcionalidad**:

- Envía peticiones POST al Registry Service
- Muestra respuestas y errores al usuario
- Soporta envío con botón o tecla Enter

**Endpoint Utilizado**:

```
http://ec2-54-159-20-80.compute-1.amazonaws.com:34003/registry
```

### 2. Registry Service

**Tecnología**: Spring Boot

**Responsabilidades**:

1. **Balanceo de Carga**: Implementa algoritmo Round-Robin para distribuir peticiones entre las 3 instancias de HashSync
2. **Proxy**: Actúa como intermediario entre el cliente y el cluster HashSync

**Implementación del Round-Robin**:

```
String[] apiUrls = apiConfiguration.getApiUrls();
int index = getNextApiIndex();
String selectedApi = apiUrls[index];

System.out.println("Round-robin seleccionó API " + (index + 1) + " (" + selectedApi + ")");
```

- Usa `AtomicInteger` para operaciones atómicas
- Garantiza distribución uniforme entre las 3 instancias

**Enpoint Expuesto**:

- `POST /registry`: Recibe registros del cliente y los reenvía a HashSync

### 3. HashSync Service

**Tecnología**: Spring Boot

**Responsabilidades**:

1. **Replicación Distribuida**: Sincroniza el estado entre las 3 instancias
2. **Consistencia**: Garantiza que todas las instancias tengan el mismo HashMap

**Arquitectura de Replicación con JGroups**:

JGroups proporciona un canal de comunicación multicast para replicación

**Sincronización de Estado**:

Los nodos nuevos se sincronizan con el estado existente mediante `getState()` y `setState()`:

**Endpoint Expuesto**:

- `POST /api/registry`: Recibe nuevos registros y los replica al cluster

## Despliegue en AWS EC2

### Infraestructura

El sistema está desplegado en EC2 con la siguiente configuración:

- **Cliente**: Se sirve estático o localmente
- **Registry Service**: `ec2-54-159-20-80.compute-1.amazonaws.com:34003`
- **HashSync Cluster**: `ec2-54-196-246-105.compute-1.amazonaws.com`
  - Instancia 1: Puerto 34000
  - Instancia 2: Puerto 34001
  - Instancia 3: Puerto 34002

### Flujo de Despliegue

1. **Build de Imágenes Docker**: Cada servicio se construye con su Dockerfile
2. **Push a Docker Hub**: Las imágenes se suben para facilitar el despliegue
3. **Ejecución en EC2**: Cada instancia se levanta con `docker run`
4. **Configuración de Puertos**: Se mapean puertos específicos para cada servicio

## Flujo de Operación Completo

### Escenario: Registrar un Nuevo Nombre

1. **Cliente → Registry Service**

   ```
   POST http://ec2-54-159-20-80.compute-1.amazonaws.com:34003/registry
   Body: {"key": "Juan"}
   ```

2. **Registry Service: Procesamiento**

   - Almacena localmente en `registry.put(key, key)`
   - Round-Robin selecciona instancia (ej: HashSync 1)

3. **Registry Service → HashSync**

   ```
   POST http://ec2-54-196-246-105.compute-1.amazonaws.com:34000/api/registry
   Body: {"key": "Juan"}
   ```

4. **HashSync: Replicación**

   - HashSync 1 recibe el registro
   - Crear timestamp para "Juan"
   - Envía mensaje vía JGroups al cluster
   - HashSync 2 y HashSync 3 reciben y actualizan su estado

5. **Consistencia Alcanzada**
   - Los 3 nodos HashSync tienen el mismo HashMap
   - Cada nodo puede responder con el estado actualizado


## Video del funcionamiento
- https://youtu.be/VEMk7NuxLHI
